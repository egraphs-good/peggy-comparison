import config
import make_graphs
import os
import re
import subprocess
from collections import defaultdict
from run_utils import run_peggy, Result, ResultType
from typing import Dict


params = {
    # TODO: hard-coded path
    "axioms": "peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml",
    "tmpFolder": "tmp",
    "pb": "glpk",
    "activate": "livsr:binop:constant",
}
optimization_level = "O2"


def format_method(classname, ret_type, methodname, arg_types):
    """
    Formats a method signature
    """
    return (
        classname + ": " + ret_type + " " + methodname + "(" + ",".join(arg_types) + ")"
    )


def get_method_name(signature):
    """
    Returns the method name from a signature
    """
    return signature.split()[2].split("(")[0]


def get_class_name(signature):
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
        elif in_method and (
            line.strip() == ""
            or line.strip() == "}"
            or line.strip() == "Exception table:"
        ):
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
    if not filename.endswith(".java"):
        raise ValueError("Method lengths requires a Java file.")

    class_filename = f"{filename[:-5]}.class"
    if not os.path.exists(class_filename):
        raise ValueError(
            f"Class file {class_filename} does not exist. Did you compile the Java file?"
        )

    bytecode = subprocess.check_output(["javap", "-c", class_filename]).decode("utf-8")

    # Split by class
    classname_regex = r"class\s+([a-zA-z0-9]*).*\{.*"
    classes = re.split(classname_regex, bytecode)[1:]

    method_regex = (
        r"[public|protected|private]?\s*([a-zA-z0-9_]+)\s+([a-zA-z0-9_]+)\(([^()]*)\);"
    )

    method_to_len = dict()
    for classname, body in zip(classes[::2], classes[1::2]):
        # Get length of each method in this class
        methods = bytecode_line_counts(body)

        def sigtosig(sig):
            # Convert signature from bytecode to signature that matches peggy log output
            res = re.search(method_regex, sig.strip())
            if res:
                # TODO: could handle better
                ret_type = res.group(1)
                method_name = res.group(2)
                arg_types = [arg.split()[0] for arg in res.group(3).split(",") if arg]
                res = format_method(classname, ret_type, method_name, arg_types)
                return res
            else:
                print(f"Failed to parse method signature: {sig}")
                return None

        # Format method signature, including the class name,
        # and add to whole-file dict
        for method, line_count in methods.items():
            formatted_sig = sigtosig(method)
            if formatted_sig:
                method_to_len[formatted_sig] = line_count

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
        # TODO: this likely indicates a timeout or failure
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


def perf_file(location, filename, output_filename):
    """
    Run peggy on all classes in a file.
    Return csv-formatted results with the runtime and formulation size for each
    method.
    """

    filename = os.path.join(location, filename)

    lens = method_lengths_bytecode(filename)

    # Get all classes
    classname_to_methods = defaultdict(list)
    for method in lens.keys():
        class_name = get_class_name(method)
        classname_to_methods[class_name].append(method)

    lens = {get_method_name(method): len for method, len in lens.items()}

    csv = ""
    for classname, methods in classname_to_methods.items():
        print(classname)
        print(methods)

        # Run one method at a time to get a more precise timeout
        for method in methods:
            methods_to_exclude = [
                f"<{other_method}>"
                for other_method in methods
                if other_method != method
            ]
            methods_to_exclude.append(f"<{classname}: void <init>()>")

            params["exclude"] = "::".join(methods_to_exclude)

            # TODO: hard-coded parameters
            peggy_result = run_peggy(
                classname,
                location,
                params,
                optimization_level=optimization_level,
                # TODO: adjust this timeout
                timeout=1800,
                container_name=config.docker_containername,
            )

            with open(output_filename, "ab") as f:
                match peggy_result:
                    case Result(ResultType.FAILURE, output):
                        # TODO: write this to an output/log file instead
                        with open(output_filename, "a") as f:
                            f.write("Peggy failed")
                    case Result(ResultType.TIMEOUT, output):
                        with open(output_filename, "a") as f:
                            f.write("Peggy timed out")

                f.write(b"PEGGY OUTPUT:")
                f.write(peggy_result.output)
                f.write(b"END PEGGY OUTPUT")

                method_to_time = perf_from_output(str(peggy_result.output))
                if len(method_to_time) > 1:
                    # TODO: can check that the methods are being skipped in the output
                    raise RuntimeError(
                        f"Methods not excluded properly. Tried to exclude all except \
                                       {method} but got {method_to_time.keys()}."
                    )
                if len(method_to_time) == 0:
                    print(
                        f"WARNING: method {method} not processed by Peggy. Skipping..."
                    )
                    continue

                method, times = method_to_time.popitem()

                # TODO: this logic is too complicated
                # If the time is -1 but result is SUCCESS, it's still a failure
                times = {
                    col: (
                        time
                        if time != -1
                        else (
                            peggy_result.result.name
                            if peggy_result.result != ResultType.SUCCESS
                            else ResultType.FAILURE.name
                        )
                    )
                    for col, time in times.items()
                }

                # just use method names because sometimes the signatures look a little different
                # (this is also a little hacky)
                # TODO: fix that
                name = get_method_name(method)
                length = lens[name] if name in lens else -1
                escape_k = '"' + method + '"'
                csv += ",".join(
                    [location, escape_k, str(length)]
                    + [str(times[col]) for col in columns[3:]]
                )
                csv += "\n"

    return csv


def perf_dir(benchmark_dir, results_file, output_filename):
    print("BENCHMARKING " + benchmark_dir)

    output = subprocess.check_output(
        "docker exec peggy javac " + benchmark_dir + "*.java", shell=True
    ).decode("utf-8")

    print(output)

    for filename in os.listdir(benchmark_dir):
        if filename.endswith(".java"):
            perf = perf_file(benchmark_dir, filename, output_filename)
            with open(results_file, "a") as f:
                f.write(perf)


def benchmark_dirs(
    dirs,
    results_filename,
    time_vs_lines_filename,
    time_vs_nodes_filename,
    output_filename,
):
    # benchmark the directories
    with open(results_filename, "w") as f:
        f.write(",".join(columns) + "\n")

    for benchmark_dir in dirs:
        perf_dir(benchmark_dir, results_filename, output_filename)

    # make the graphs
    make_graphs.make_graphs(
        results_filename,
        time_vs_lines_filename=time_vs_lines_filename,
        time_vs_nodes_filename=time_vs_nodes_filename,
    )
