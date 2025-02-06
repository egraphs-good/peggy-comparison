This repository performs qualitative and quantitative benchmarks on the [Peggy](https://goto.ucsd.edu/~mstepp/peggy/) equality saturation engine and compiler. Its purpose is to compare Peggy's performance to [eggcc](https://github.com/egraphs-good/eggcc), another optimizing compiler using equality saturation, on similar example files.

For the original Peggy project, please refer to the [Peggy documentation](https://goto.ucsd.edu/~mstepp/peggy/). This is not the original Peggy project.

# Overview

There are two benchmarks:

- **optimization capabilities (OC) (qualitative)**: This benchmark produces a comparison of optimized output at different numbers of iterations of Peggy's equality saturation engine.

- **performance and scaling (PS) (quantitative)**: This benchmark evaluates how Peggy's performance (measured as optimization time) scales with the size of the input program.


# Running the benchmarks

## Installation

You will need [Docker](https://docs.docker.com/get-started/get-docker/) and the docker daemon must be running.

If you would like to use the setup script without modification, you will also need conda. Otherwise, if you would like to use a different Python environment, see the setup script for the required Python version and packages.

To setup, run `./setup.sh`. This may take a while (10 minutes).


## Running

Make sure the Docker daemon is running. Then, run:

```
source run.sh
```

This may take several hours.
Note that this script runs subprocesses in the Docker container. If you want to interrupt the script, you may also want to `docker stop peggy` to stop subprocesses in the container.


This will produce several outputs in the `results` directory:

- `oc/<eto>`: A directory corresponding to the eto (number of engine iterations) passed to peggy on which it was run.
- `oc/<eto>/Benchmark.md`: This is the captured output from running Peggy on `Benchmark.java` and the decompiled result.
- `oc/<eto>/Benchmark.class`: This is the optimized result from running Peggy on `Benchmark.java`.
- `ps/perf.csv`: This is a csv containing the results of the performance benchmark on peggy.
- `ps/time_vs_lines.png`: A graph plotting the solver time of Peggy vs. the number of lines of Java bytecode of its input.
- `ps/time_vs_nodes.png`: A graph plotting the solver time of Peggy vs. the number of nodes in the solver formulation.
- `ps/output.txt`: The output produced by Peggy from running the performance benchmark. Useful to see the causes of failures.
