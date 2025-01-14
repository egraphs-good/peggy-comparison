import subprocess
from enum import Enum
from dataclasses import dataclass
import os
from typing import List

DEBUG = True


class ResultType(Enum):
    SUCCESS = 0
    TIMEOUT = 1
    FAILURE = 2


@dataclass
class Result:
    result: ResultType
    output: str


# Runs peggy on a compiled class in the benchmark directory,
# with our default parameters
def run_peggy_default(classname, benchmark_dir, timeout=60):
    return run_peggy(
        classname,
        benchmark_dir,
        {
            "axioms": "axioms/java_arithmetic_axioms.xml:axioms/java_operator_axioms.xml:axioms/java_operator_costs.xml:axioms/java_util_axioms.xml",
            "tmpFolder": "tmp",
            "pb": "glpk",
            "eto": "500",
            "glpkPath": '"/glpk-5.0/examples/glpsol"',
            "activate": "livsr:binop:constant",
        },
        optimization_level="O2",
        timeout=timeout,
    )


# Runs peggy on a compiled class in the benchmark directory
# with the given params
def run_peggy(
    classname,
    benchmark_dir,
    params,
    optimization_level,
    timeout=60,
    container_name=None,
):
    """
    Runs peggy, within a docker container if specified,
    on a compiled class in the benchmark directory with the given params.
    The class must have been compiled with the version of JPathLikeava in the container.
    The container must be running.
    """
    command = []
    if container_name:
        command = [
            "docker",
            "exec",
            "-it",
            container_name,
        ]

    command.extend(
        [
            "java",
            "-Xmx2000m",
            "-cp",
            # TODO: this path is hard-coded.
            "/peggy-comparison/peggy/peggy_1.0.jar:"
            + os.path.join("/peggy-comparison", benchmark_dir),
            "peggy.optimize.java.Main",
        ]
    )

    command.append("-" + optimization_level)
    command.append(classname)

    addl = [
        ["-" + key, value]
        for key, value in params.items()
        if key != "optimization_level"
    ]
    addl = [item for sublist in addl for item in sublist]
    command.extend(addl)

    try:
        output = subprocess.check_output(
            command, stderr=subprocess.STDOUT, timeout=timeout
        )
        return Result(ResultType.SUCCESS, output)
    except subprocess.CalledProcessError as e:
        if DEBUG:
            print(f"Command failed\n{" ".join(command)}")
        return Result(ResultType.FAILURE, e.output)
    except subprocess.TimeoutExpired as e:
        if DEBUG:
            print(f"Command timed out\n{" ".join(command)}")
        return Result(ResultType.TIMEOUT, e.output)
