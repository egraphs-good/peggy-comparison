"""
Runs peggy on all Java files in the benchmark directory.
Explores parameters to determine how brittle the results are,
and outputs the decompiled result of each peggy run to a markdown file.
"""

import subprocess
import os
import shutil
import run_utils


# Creates a results file containing optimized code with different params
# for the class
def benchmark_file(classname: str, benchmark_dir: str, results_dir: str):
    filepath = benchmark_dir + "/" + classname + ".java"

    # Compile the file
    subprocess.call(["javac", filepath])

    for eto_mul in range(1, 12):
        eto_val = 2**eto_mul
        # Run peggy on the file
        params = {
            "axioms": "axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            "optimization_level": "O2",
            "tmp_folder": "tmp",
            "pb": "glpk",
            "eto": str(eto_val),
        }
        print("Running peggy on " + classname + " with params ")
        print(str(params))
        peggy_output = run_utils.run_peggy(
            classname,
            params=params,
        )

        param_dir = str(hash(str(params)))

        # Copy the optimized class file to the results dir for these params
        os.mkdir("results/" + param_dir)
        shutil.copy(
            "optimized/" + classname + ".class",
            "results/" + param_dir + "/" + classname + ".class",
        )

        # Decompile the result using jd-cli
        # and capture the output
        decompiled = subprocess.check_output(
            ["./jd-cli", "optimized/" + classname + ".class"]
        )

        # Store the output in a markdown file
        with open(results_dir + param_dir + "/" + classname + ".md", "a") as f:
            f.write("# " + classname + "\n")
            f.write("## Original\n")
            f.write("```java\n")
            with open(benchmark_dir + "/" + classname + ".java", "r") as og:
                f.write(og.read())
            f.write("\n```\n")

        with open(results_dir + param_dir + "/" + classname + ".md", "ab") as f:
            f.write(b"## Run \n")
            f.write(b"\n" + str(params).encode("utf-8") + b"\n\n")
            f.write(b"### Peggy output\n```\n")
            f.write(peggy_output)
            f.write(b"```\n")
            f.write(b"\n### Optimized")
            f.write(b"\n```java\n")
            f.write(decompiled)
            f.write(b"```\n")
