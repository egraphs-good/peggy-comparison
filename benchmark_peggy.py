"""
Runs peggy on all Java files in the benchmark directory.
Explores parameters to determine how brittle the results are,
and outputs the decompiled result of each peggy run to a markdown file.
"""

import subprocess
import os

benchmark_dir = "benchmark/passing"
results_dir = "results"


class PeggyParams:
    # All params are str
    def __init__(self, axioms, optimization_level, tmp_folder, pb, eto):
        self.axioms = axioms
        self.optimization_level = optimization_level
        self.tmp_folder = tmp_folder
        self.pb = pb
        self.eto = eto

    def __str__(self):
        return (
            "- axioms: "
            + self.axioms
            + "\n"
            + "- optimization_level: "
            + self.optimization_level
            + "\n"
            + "- tmp_folder: "
            + self.tmp_folder
            + "\n"
            + "- pb: "
            + self.pb
            + "\n"
            + "- eto: "
            + self.eto
        )


def run_peggy_default(classname):
    run_peggy(
        classname,
        PeggyParams(
            axioms="axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            optimization_level="O1",
            tmp_folder="tmp",
            pb="glpk",
            eto="1",
        ),
    )


def run_peggy(classname, params: PeggyParams):
    command = [
        "java",
        "-Xmx2000m",
        "-cp",
        ".:peggy_1.0.jar:" + benchmark_dir,
        "peggy.optimize.java.Main",
    ]

    command.append("-axioms")
    if params.axioms:
        command.append(params.axioms)

    if params.optimization_level:
        command.append("-" + params.optimization_level)

    command.append(classname)

    if params.tmp_folder:
        command.append("-tmpFolder")
        command.append(params.tmp_folder)

    if params.pb:
        command.append("-pb")
        command.append(params.pb)

    if params.eto:
        command.append("-eto")
        command.append(params.eto)

    return subprocess.check_output(command, stderr=subprocess.STDOUT)


def benchmark_file(classname: str):
    filepath = benchmark_dir + "/" + classname + ".java"

    # Compile the file
    subprocess.call(["javac", filepath])

    # Write the original file
    with open("results/" + classname + ".md", "w") as md:
        md.write("# " + classname + "\n")
        md.write("## Original\n")
        md.write("```java\n")
        with open(benchmark_dir + "/" + classname + ".java", "r") as og:
            md.write(og.read())
        md.write("\n```\n")

    for eto_mul in range(1, 12):
        eto_val = 2**eto_mul
        # Run peggy on the file
        params = PeggyParams(
            axioms="axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            optimization_level="O2",
            tmp_folder="tmp",
            pb="glpk",
            eto=str(eto_val),
        )
        print("Running peggy on " + classname + " with params ")
        print(str(params))
        peggy_output = run_peggy(
            classname,
            params=params,
        )

        # Decompile the result using jd-cli
        # and capture the output
        decompiled = subprocess.check_output(
            ["./jd-cli", "optimized/" + classname + ".class"]
        )

        # Store the output in a markdown file
        with open("results/" + classname + ".md", "ab") as f:
            f.write(b"## Run \n")
            f.write(b"\n" + str(params).encode("utf-8") + b"\n\n")
            f.write(b"### Peggy output\n```\n")
            f.write(peggy_output)
            f.write(b"```\n")
            f.write(b"\n### Optimized")
            f.write(b"\n```java\n")
            f.write(decompiled)
            f.write(b"```\n")


if __name__ == "__main__":
    # Create results dir if not exists
    if not os.path.exists(results_dir):
        os.makedirs(results_dir)

    # Benchmark each file in `benchmark_dir`
    for filename in os.listdir(benchmark_dir):
        classname = os.path.splitext(filename)[0]
        benchmark_file(classname)
