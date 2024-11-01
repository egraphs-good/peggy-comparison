"""
Runs peggy on all Java files in the benchmark directory.
Explores parameters to determine how brittle the results are,
and outputs the decompiled result of each peggy run to a markdown file.
"""

import subprocess


# Runs peggy on a compiled class in the benchmark directory,
# with our default parameters
def run_peggy_default(classname, benchmark_dir, timeout=60):
    return run_peggy(
        classname,
        benchmark_dir,
        {
            "axioms": "axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            "optimization_level": "O2",
            "tmpFolder": "tmp",
            "pb": "glpk",
            "eto": "500",
            "glpkPath": '"/glpk-5.0/examples/glpsol"',
            "activate": "livsr:binop:constant",
        },
        timeout=timeout,
    )


# Runs peggy on a compiled class in the benchmark directory
# with the given params
def run_peggy(classname, benchmark_dir, params, timeout=60):
    command = [
        "java",
        "-Xmx2000m",
        "-cp",
        ".:peggy_1.0.jar:" + benchmark_dir,
        "peggy.optimize.java.Main",
    ]

    command.append("-" + params["optimization_level"])
    command.append(classname)

    addl = [
        ["-" + key, value]
        for key, value in params.items()
        if key != "optimization_level"
    ]
    addl = [item for sublist in addl for item in sublist]
    command.append(addl)
    print(addl)

    try:
        return subprocess.check_output(
            command, stderr=subprocess.STDOUT, timeout=timeout
        )
    except subprocess.CalledProcessError as e:
        print("Command failed")
        print(" ".join(e.cmd))
        print(e.output)
        return None
    except subprocess.TimeoutExpired as e:
        print("Command timed out")
        print(" ".join(command))
        return None
