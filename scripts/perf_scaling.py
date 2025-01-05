import make_graphs
import os
import re
import subprocess
from java_utils import analyze_java_file
from run_utils import run_peggy
from typing import Dict


# For each java file in benchmark dir
# call perf)file
# For each file, perf_file
# runs peggy on the file
# counts the size of methods in the file
# returns data for method size, runtime of each
# the data is in a dictionary? Classname.methodname or whatever

params = {
    "axioms": "axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
    "optimization_level": "O2",
    "tmpFolder": "tmp",
    "pb": "glpk",
    "glpkPath": '"/glpk-5.0/examples/glpsol"',
    "activate": "livsr:binop:constant",
}


def format_method(classname, ret_type, methodname, arg_types):
    """
    Formats a method signature
    """
    return (
        classname + ": " + ret_type + " " + methodname + "(" + ",".join(arg_types) + ")"
    )


def method_name(signature):
    """
    Returns the method name from a signature
    """
    return signature.split()[2].split("(")[0]


def class_name(signature):
    """
    Returns the class name from a signature
    """
    return signature.split(":")[0]


def bytecode_line_counts(bytecode: str) -> Dict[str, int]:
    """
    Approximate code size of each method by the index of the
    last instruction of each method in the passed-in bytecode.

    Returns a dict mapping each method signature to its length.
    """
    bytecode_lines = bytecode.split("\n")
    method_to_len = dict()

    in_method = None
    for i, line in enumerate(bytecode_lines):
        if "Code:" in line:
            # previous line is the method signature
            in_method = bytecode_lines[i - 1]
        elif in_method and (line.strip() == "" or line.strip() == "}"):
            # end of method, previous instruction is the last
            linecount = int(bytecode_lines[i - 1].split(":")[0])
            method_to_len[in_method] = linecount
            in_method = None

    return method_to_len


def method_lengths_bytecode(filename) -> Dict[str, int]:
    """
    Returns a dictionary mapping the method signature to the length
    (in number of lines of bytecode) of the method body for each method
    in the file, including in inner classes.
    Inner classes are only qualified by their name, not by outer classes,
    so this doesn't work with duplicate class names.
    """
    # TODO: error handling
    bytecode: str = subprocess.check_output(["javap", "-c", filename]).decode("utf-8")

    # Split by class
    classname_regex = "class\s+([a-zA-z0-9]*).*\{.*"
    classes = re.split(classname_regex, bytecode)[1:]

    method_regex = (
        "[public|protected|private]?\s*([a-zA-z0-9_]+)\s+([a-zA-z0-9_]+)\(([^()]*)\);"
    )

    method_to_len = dict()
    for classname, body in zip(classes[::2], classes[1::2]):
        # Get length of each method in this class
        methods = bytecode_line_counts(body)

        def sigtosig(sig):
            # Convert signature from bytecode to signature that matches peggy log output
            res = re.search(method_regex, sig.strip())
            ret_type = res.group(1)
            method_name = res.group(2)
            arg_types = [arg.split()[0] for arg in res.group(3).split(",") if arg]
            res = format_method(classname, ret_type, method_name, arg_types)
            return res

        # Format method signature, including the class name,
        # and add to whole-file dict
        for method, line_count in methods.items():
            method_to_len[sigtosig(method)] = line_count

    return method_to_len


def get_time(portion, log):
    """
    `portion` is one of PEG2PEGTIME, PBTIME, ENGINETIME, Optimization took
    """
    regex = portion + " ([0-9]*)"

    time = re.search(regex, log)
    if time:
        try:
            return int(time.group(1))
        except Exception as e:
            print("Exception while parsing time")
            print(e)
            return -1
    else:
        return -1


def perf_from_output(output):
    # The first block will be about setting up peggy. Discard it.
    methods = output.split("- Processing method ")[1:]

    method_to_time = dict()
    for method in methods:
        # newlines are escaped in the subprocess output
        # remove surrounding <>
        method_name = method.split("\\n")[0][1:-1]

        # get times
        times = {
            portion: get_time(portion, method)
            for portion in [
                "PEG2PEGTIME",
                "PBTIME",
                "ENGINETIME",
                "Optimization took",
                "nodes:",
                "values:",
            ]
        }

        method_to_time[method_name] = times

    return method_to_time


columns = [
    "benchmark_dir",
    "name",
    "length",
    "PEG2PEGTIME",
    "PBTIME",
    "ENGINETIME",
    "Optimization took",
    "nodes:",
    "values:",
]


def perf_file(location, classname):
    # TODO: better to use os or smth to combine filenames
    filename = location + classname + ".java"

    lens = method_lengths_bytecode(filename)

    # Get all classes
    classnames = {class_name(method) for method in lens.keys()}
    lens = {method_name(method): len for method, len in lens.items()}

    csv = ""
    for classname in classnames:
        print(classname)

        peggy_output = run_peggy(classname, location, params, timeout=1800)
        if not peggy_output:
            print("peggy failed")
            continue
        print("PEGGY OUTPUT:")
        print(peggy_output)
        print("END PEGGY OUTPUT")

        method_to_time = perf_from_output(str(peggy_output))

        for method, times in method_to_time.items():
            # just use method names because sometimes the signatures look a little different
            # (this is also a little hacky)
            name = method_name(method)
            length = lens[name] if name in lens else -1
            escape_k = '"' + method + '"'
            csv += ",".join(
                [location, escape_k, str(length)]
                + [str(times[col]) for col in columns[3:]]
            )
            csv += "\n"

    return csv


def perf_dir(benchmark_dir, results_file):
    print("BENCHMARKING " + benchmark_dir)

    subprocess.call("javac " + benchmark_dir + "*.java", shell=True)

    for filename in os.listdir(benchmark_dir):
        if filename.endswith(".java"):
            try:
                classname = os.path.splitext(filename)[0]
                perf = perf_file(benchmark_dir, classname)
                with open(results_file, "a") as f:
                    f.write(perf)
            except Exception as e:
                print("Unexpected exception")
                print(e)


def benchmark_dirs(
    dirs, results_filename, time_vs_lines_filename, time_vs_nodes_filename
):
    # benchmark the directories
    with open(results_filename, "w") as f:
        f.write(",".join(columns) + "\n")

    for benchmark_dir in dirs:
        perf_dir(benchmark_dir, results_filename)

    # make the graphs


if __name__ == "__main__":
    lens = method_lengths_bytecode("java/benchmark/Benchmark.class")
    for k, v in lens.items():
        print(k, v)
