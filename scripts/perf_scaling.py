import config
import make_graphs
import os
import re
import json
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
    # Previously we use the byte offset of the .class file as the number of lines
    # which is inaccurate
    linecount = 0
    for i, line in enumerate(bytecode_lines):
        if line.endswith('Code:'):
            # previous line is the method signature
            in_method = bytecode_lines[i - 1]
            linecount = 0
        elif in_method and (
            line.strip() == ""
            or line.strip() == "}"
            or line.strip() == "Exception table:"
        ):
            method_to_len[in_method] = linecount
            in_method = None
        linecount += 1

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
        r"(public|protected|private|static|\s)*(([a-zA-z0-9_]+)\s)*([a-zA-z0-9_]+)\(([^()]*)\);"
    )

    method_to_len = dict()
    for classname, body in zip(classes[::2], classes[1::2]):
        # Get length of each method in this class
        methods = bytecode_line_counts(body)

        def sigtosig(sig):
            # Convert signature from bytecode to signature that matches peggy log output
            res = re.findall(method_regex, sig.strip())
            if len(res) > 0:
                res = res[0]
            else:
                print('WARNING: skipping', sig.strip())
            if res:
                ret_type = res[-3] if len(res) >= 3 and res[-3] != '' else 'void'
                method_name = res[-2] if res[-2] != classname else '<init>'
                arg_types = [arg.split()[0] for arg in res[-1].split(",") if arg]
                print(arg_types, method_name, classname, ret_type)
                res = format_method(classname, ret_type, method_name, arg_types)
                return res
            else:
                print(f"Failed to parse method signature: {sig}", flush=True)
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
            print("Exception while parsing time, skipping...", flush=True)
            print(e, flush=True)
            return -1
    else:
        # This indicates a timeout or failure
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
    # "nodes:",
    # "values:",
    "EGGCC_compiletime",
    "EGGCC_extracttime",
]


def perf_file(peggy_location, eggcc_location, filename, output_filename):
    """
    Run peggy and eggcc on all classes in a file.
    Return csv-formatted results with the runtime and formulation size for each
    method.
    """

    peggy_filename = os.path.join(peggy_location, filename)

    lens = method_lengths_bytecode(peggy_filename)

    # Get all classes
    classname_to_methods = defaultdict(list)
    for method in lens.keys():
        class_name = get_class_name(method)
        classname_to_methods[class_name].append(method)

    # print(list(lens.keys()))
    # lens = {get_method_name(method): len for method, len in lens.items()}
    assert(len(classname_to_methods) == 1)
    csv = ""
    for classname, methods in classname_to_methods.items():

        ###################
        # eggcc
        ###################
        eggcc_filename = filename.lower()[:-len('.java')] + '.bril'
        eggcc_filename = os.path.join(eggcc_location, eggcc_filename)
        try:
            output = subprocess.check_output(
                f'eggcc/target/release/eggcc {eggcc_filename} --run-data-out /tmp/profile.json',
                shell=True
            ).decode('utf-8')
        except Exception as e:
            print("Exception while running eggcc, skipping...", flush=True)
            print(e, flush=True)
            continue
            
        with open('/tmp/profile.json') as f:
            eggcc_data = json.load(f)
            secs = eggcc_data["eggcc_compile_time"]["secs"]
            nanos = eggcc_data["eggcc_compile_time"]["nanos"]
            eggcc_compile_time = secs * 1e3 + nanos / 1e6

            secs = eggcc_data["eggcc_extraction_time"]["secs"]
            nanos = eggcc_data["eggcc_extraction_time"]["nanos"]
            eggcc_extraction_time = secs * 1e3 + nanos / 1e6

        ###################
        # Peggy
        ###################
        print(f"Running Peggy on {classname}...", flush=True)

        # TODO: hard-coded parameters
        peggy_result = run_peggy(
            classname,
            peggy_location,
            params,
            optimization_level=optimization_level,
            timeout=config.ps_per_method_timeout_seconds,
            container_name=config.docker_containername,
        )

        with open(output_filename, "ab") as f:
            match peggy_result:
                case Result(ResultType.FAILURE, _output):
                    f.write(b"\nPeggy failed\n")
                case Result(ResultType.TIMEOUT, _output):
                    f.write(b"\nPeggy timed out\n")

            f.write(b"PEGGY OUTPUT:\n")
            if peggy_result.output:
                f.write(peggy_result.output)
            f.write(b"END PEGGY OUTPUT\n")

        method_to_time = perf_from_output(str(peggy_result.output))
        
        assert(len(method_to_time) > 0)

        total_length = 0
        total_times = {}
        for (method, times) in method_to_time.items():

            # TODO: this logic is too complicated
            # If the time is -1 but result is SUCCESS, it's still a failure
            times = {
                col: (
                    time
                    if time != -1 and peggy_result.result == ResultType.SUCCESS
                    else (
                        peggy_result.result.name
                        if peggy_result.result != ResultType.SUCCESS
                        else ResultType.FAILURE.name
                    )
                )
                for col, time in times.items()
            }

            assert(method in lens)
            length = lens[method]

            total_length += length

            def combine_time(t1, t2):
                if 'TIMEOUT' in [t1, t2]:
                    return 'TIMEOUT'
                elif 'FAILURE' in [t1, t2]:
                    return 'FAILURE'
                else:
                    return t1 + t2

            total_times = {
                col: combine_time(time, total_times.get(col, 0))
                for col, time in times.items()
            }

        csv += ",".join(
            [peggy_location, filename, str(total_length)]
            + [str(total_times[col]) for col in columns[3:-2]]
            + [str(eggcc_compile_time), str(eggcc_extraction_time)]
        )
        csv += "\n"

    return csv


def perf_dir(peggy_benchmark_dir, eggcc_benchmark_dir, results_file, output_filename):
    print("BENCHMARKING " + peggy_benchmark_dir, flush=True)

    output = subprocess.check_output(
        "docker exec peggy javac " + peggy_benchmark_dir + "*.java", shell=True
    ).decode("utf-8")

    for filename in os.listdir(peggy_benchmark_dir):
        if filename.endswith(".java"):
            perf = perf_file(peggy_benchmark_dir, eggcc_benchmark_dir, filename, output_filename)
            with open(results_file, "a") as f:
                f.write(perf)


def benchmark_dirs(
    dirs,
    results_filename,
    time_vs_lines_filename,
    ratio_vs_lines_filename,
    output_filename,
):
    subprocess.run("cd eggcc && cargo build --release", shell=True)
    # benchmark the directories
    with open(results_filename, "w") as f:
        f.write(",".join(columns) + "\n")

    for benchmark_dir in dirs:
        perf_dir(benchmark_dir[0], benchmark_dir[1], results_filename, output_filename)

    # make the graphs
    make_graphs.make_graphs(
        results_filename,
        time_vs_lines_filename=time_vs_lines_filename,
        ratio_vs_lines_filename=ratio_vs_lines_filename,
    )
