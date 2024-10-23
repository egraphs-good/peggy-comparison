"""
Runs peggy on all Java files in the benchmark directory.
Explores parameters to determine how brittle the results are,
and outputs the decompiled result of each peggy run to a markdown file.
"""

import subprocess


class PeggyParams:
    # All params are str
    def __init__(self, axioms, optimization_level, tmp_folder, pb, eto):
        self.axioms = axioms
        self.optimization_level = optimization_level
        self.tmp_folder = tmp_folder
        self.pb = pb
        self.eto = eto
        self.glpkPath = '"/glpk-5.0/examples/glpsol"'
        self.activate = "livsr:binop:constant"

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
            + "\n"
            + "- glpkPath: "
            + self.glpkPath
            + "\n"
            + "- activate: "
            + self.activate
        )


# Runs peggy on a compiled class in the benchmark directory,
# with our default parameters
def run_peggy_default(classname, benchmark_dir):
    return run_peggy(
        classname,
        benchmark_dir,
        PeggyParams(
            axioms="axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            optimization_level="O2",
            tmp_folder="tmp",
            pb="glpk",
            eto="500",
        ),
    )


# Runs peggy on a compiled class in the benchmark directory
# with the given params
def run_peggy(classname, benchmark_dir, params: PeggyParams):
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

    if params.glpkPath:
        command.append("-glpkPath")
        command.append(params.glpkPath)

    if params.activate:
        command.append("-activate")
        command.append(params.activate)

    try:
        return subprocess.check_output(command, stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError as e:
        print(e)
