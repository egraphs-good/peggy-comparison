"""
Runs peggy on all Java files in the benchmark directory.
Explores parameters to determine how brittle the results are,
and outputs the decompiled result of each peggy run to a markdown file.
"""

import os
import subprocess

benchmark_dir = "benchmark"


def run_peggy_default(classname):
    run_peggy(
        classname,
        axioms="axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
        optimization_level="O1",
        tmp_folder="tmp",
        pb="glpk",
        eto="1",
    )


def run_peggy(classname, *, axioms, optimization_level, tmp_folder, pb, eto):
    command = [
        "java",
        "-Xmx2000m",
        "-cp",
        ".:peggy_1.0.jar:benchmark",
        "peggy.optimize.java.Main",
    ]

    command.append("-axioms")
    if axioms:
        command.append(axioms)

    if optimization_level:
        command.append("-" + optimization_level)

    command.append(classname)

    if tmp_folder:
        command.append("-tmpFolder")
        command.append(tmp_folder)

    if pb:
        command.append("-pb")
        command.append(pb)

    if eto:
        command.append("-eto")
        command.append(eto)

    subprocess.call(command)


def optimize_file(classname: str):
    filepath = benchmark_dir + "/" + classname + ".java"

    # Compile the file
    subprocess.call(["javac", filepath])

    for eto_mul in range(1, 4):
        eto_val = 10**eto_mul
        # Run peggy on the file
        # TODO: time peggy
        run_peggy(
            classname,
            axioms="axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            optimization_level="O2",
            tmp_folder="tmp",
            pb="glpk",
            eto=str(eto_val),
        )

        # Decompile the result using jd-cli
        # and capture the output
        decompiled = subprocess.check_output(
            ["./jd-cli", "optimized/" + classname + ".class"]
        )

        # Store the output in a markdown file
        with open("results/" + classname + ".md", "ab") as f:
            # TODO: write info about the params
            # TODO: only get the original method
            f.write(b"ETO val: " + str(eto_val).encode("utf-8") + b"\n")
            f.write(b"\n```java\n")
            f.write(decompiled)
            f.write(b"```\n")


if __name__ == "__main__":
    optimize_file("LoopStrengthReduction")
