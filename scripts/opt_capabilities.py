"""
Runs peggy on all Java files in the benchmark directory.
Explores parameters to determine how brittle the results are,
and outputs the decompiled result of each peggy run to a markdown file.
"""

import subprocess
import os
import shutil
import run_utils
import config


# Creates a results file containing optimized code with different params
# for the class
def benchmark_file(
    classname: str,
    benchmark_dir: str,
    results_dir: str,
    container_name: str,
    compile=True,
):
    filepath = benchmark_dir + "/" + classname + ".java"

    if compile:
        try:
            subprocess.check_output(
                [
                    "docker",
                    "exec",
                    "-it",
                    config.docker_containername,
                    "javac",
                    filepath,
                ],
                stderr=subprocess.STDOUT
            )
        except subprocess.CalledProcessError as e:
            print("Error compiling " + classname)
            print(e.output)
            raise e

    # TODO: choose a good set - 250, 500, 1000?
    for eto_val in [500]:
        # Run peggy on the file
        params = {
            "axioms": "peggy/axioms/java_arithmetic_axioms.xml:peggy/axioms/java_operator_axioms.xml:peggy/axioms/java_operator_costs.xml:peggy/axioms/java_util_axioms.xml",
            "tmpFolder": "tmp",
            "pb": "glpk",
            "eto": str(eto_val),
        }
        print("Running peggy on " + classname + " with params ")
        print(str(params))

        # TODO: might want to distinguish between failures?
        peggy_output = run_utils.run_peggy(
            classname,
            optimization_level="O2",
            params=params,
            benchmark_dir=benchmark_dir,
            container_name=container_name,
            timeout=600,
        ).output

        param_dir = str(eto_val)

        # Copy the optimized class file to the results dir for these params
        # TODO: hardcoded path
        if not os.path.exists(os.path.join(results_dir, param_dir)):
            os.makedirs(os.path.join(results_dir, param_dir))

        shutil.copy(
            os.path.join("optimized", classname + ".class"),
            os.path.join(results_dir, param_dir, classname + ".class"),
        )

        # Decompile the result using jd-cli
        # and capture the output
        decompiled = subprocess.check_output(
            # TODO: hardcoded path
            [
                "./jd-cli",
                "optimized/" + classname + ".class",
            ]
        )

        # Store the output in a markdown file
        if not os.path.exists(os.path.join(results_dir, param_dir, classname + ".md")):
            with open(
                os.path.join(results_dir, param_dir, classname + ".md"), "x"
            ) as f:
                f.write("# " + classname + "\n")
                f.write("## Original\n")
                f.write("```java\n")
                with open(benchmark_dir + "/" + classname + ".java", "r") as og:
                    f.write(og.read())
                f.write("\n```\n")

        with open(os.path.join(results_dir, param_dir, classname + ".md"), "ab") as f:
            f.write(b"## Run \n")
            f.write(b"\n" + str(params).encode("utf-8") + b"\n\n")
            f.write(b"### Peggy output\n```\n")
            f.write(peggy_output)
            f.write(b"```\n")
            f.write(b"\n### Optimized")
            f.write(b"\n```java\n")
            f.write(decompiled)
            f.write(b"```\n")
