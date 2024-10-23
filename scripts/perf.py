from run_utils import PeggyParams, run_peggy_default
import re
import subprocess


# For each java file in benchmark dir
# call perf)file
# For each file, perf_file
# runs peggy on the file
# counts the size of methods in the file
# returns data for method size, runtime of each
# the data is in a dictionary? Classname.methodname or whatever


def format_method(classname, ret_type, methodname, arg_types):
    """
    Formats a method signature
    """
    return (
        classname + ": " + ret_type + " " + methodname + "(" + ",".join(arg_types) + ")"
    )


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
    file_contents = file_contents.strip()
    file_contents = file_contents.split("\n")

    classname_regex = "public\s+class\s+([a-zA-z0-9]*)\s+\{"
    classname = re.match(classname_regex, file_contents[0].strip()).group(1)

    # yes, this is hacky
    methods = "\n".join(file_contents).split("\n    }")
    methods = methods[:-1]

    # kind of crude
    method_regex = "[a-z]*\s+([a-zA-z0-9_]*)\s+([a-zA-z0-9_]*)\(([^()]*)\)"

    for method in methods:
        method_sig = re.search(method_regex, method)

        method_body = method[method_sig.start() :]
        ret_type = method_sig.group(1)
        method_name = method_sig.group(2)
        arg_types = [arg.split()[0] for arg in method_sig.group(3).split(",")]

        method_sig = format_method(classname, ret_type, method_name, arg_types)
        print("SIG")
        print(method_sig)
        print("END")

        length = method_body.count("\n")

        method_to_len[method_sig] = length

    return method_to_len


def get_time(portion, log):
    """
    `portion` is one of PEG2PEGTIME, PBTIME, ENGINETIME, Optimization took
    """
    regex = portion + " ([0-9]*)"
    return int(re.search(regex, log).group(1))


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
            for portion in ["PEG2PEGTIME", "PBTIME", "ENGINETIME", "Optimization took"]
        }

        method_to_time[method_name] = times

    return method_to_time


def perf_file(location, classname):
    # better to use os or smth to combine filenames
    filename = location + classname + ".java"

    subprocess.call(["javac", filename])

    with open(filename) as f:
        lens = method_lengths(f.read())
        print(lens)

    peggy_output = run_peggy_default(classname, location)
    method_to_time = perf_from_output(str(peggy_output))

    columns = [
        "name",
        "length",
        "PEG2PEGTIME",
        "PBTIME",
        "ENGINETIME",
        "Optimization took",
    ]
    csv = ",".join(columns) + "\n"
    for k, v in lens.items():
        escape_k = '"' + k + '"'
        csv += ",".join(
            [escape_k, str(v)] + [str(method_to_time[k][col]) for col in columns[2:]]
        )
        csv += "\n"

    with open("results/perf/" + classname + ".csv", "w") as f:
        f.write(csv)


perf_file("/peggy-comparison/benchmark/passing/", "LoopInvariantCodeMotion")