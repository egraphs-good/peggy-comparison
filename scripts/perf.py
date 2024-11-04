from run_utils import run_peggy
import re
import subprocess
import os
from java_utils import analyze_java_file


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


def method_lengths(file_contents):
    """
    Returns a dictionary mapping the method signature to the length
    (in number of lines) of the method body.
    To keep from having to do any sophisticated parsing, it requires
    a very specific format. A method ends if there is a line with four spaces and a "}".

    Expected file format:
    public class <ClassName> {
        <access modifier> <ret_type> <method_name>(<args>) {
            <method_body: n lines>
        }

        <access modifier> <ret_type> <method_name>(<args>) {
        }
    }
    """
    # Dict: method name to len
    method_to_len = dict()

    # remove the class
    # file_contents = file_contents.strip()
    # file_contents = file_contents.split("\n")

    # handle multiple classes
    # get the location of all matches
    # then split around those
    # then do the below
    classname_regex = "class\s+([a-zA-z0-9]*).*\{.*"
    classes = re.split(classname_regex, file_contents)[1:]

    method_regex = (
        "[public|protected|private]?\s*([a-zA-z0-9_]+)\s+([a-zA-z0-9_]+)\(([^()]*)\)"
    )
    for classname, body in zip(classes[::2], classes[1::2]):
        methods = analyze_java_file(body)

        def sigtosig(sig):
            res = re.search(method_regex, sig.strip())
            ret_type = res.group(1)
            method_name = res.group(2)
            arg_types = [arg.split()[0] for arg in res.group(3).split(",") if arg]
            res = format_method(classname, ret_type, method_name, arg_types)
            return res

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
        return int(time.group(1))
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
    # better to use os or smth to combine filenames
    filename = location + classname + ".java"

    with open(filename) as f:
        lens = method_lengths(f.read())

    # Get all classes
    classnames = {class_name(method) for method in lens.keys()}
    lens = {method_name(method): len for method, len in lens.items()}

    csv = ""
    for classname in classnames:
        print(classname)

        peggy_output = run_peggy(classname, location, params, timeout=120)
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
                [escape_k, str(length)] + [str(times[col]) for col in columns[2:]]
            )
            csv += "\n"

    return csv


def perf_dir(results_dir, benchmark_dir):
    print("BENCHMARKING " + benchmark_dir)

    subprocess.call("javac " + benchmark_dir + "*.java", shell=True)
    results_file = results_dir + "perf_" + benchmark_dir.replace("/", "_") + ".csv"
    if os.path.exists(results_file):
        print("Results file " + results_file + " already exists. Exiting.")
        exit(1)

    # Create results dir if not exists
    if not os.path.exists(results_dir):
        os.makedirs(results_dir)

    with open(results_file, "w") as f:
        f.write(",".join(columns) + "\n")
    for filename in os.listdir(benchmark_dir):
        if filename.endswith(".java"):
            classname = os.path.splitext(filename)[0]
            perf = perf_file(benchmark_dir, classname)
            with open(results_file, "a") as f:
                f.write(perf)


if __name__ == "__main__":
    benchmark_dirs = [
        "/peggy-comparison/uninlined-spec/scimark/",
        "/peggy-comparison/inlined-spec/scimark/",
        "/peggy-comparison/benchmark/passing/",
        "/peggy-comparison/benchmark/failing/",
        "/peggy-comparison/uninlined-spec/compress/",
        "/peggy-comparison/inlined-spec/compress/",
    ]

    for benchmark_dir in benchmark_dirs:
        perf_dir("results/", benchmark_dir)
