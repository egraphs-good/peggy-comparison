This repository performs qualitative and quantitative benchmarks on the [Peggy](https://goto.ucsd.edu/~mstepp/peggy/) equality saturation engine and compiler. Its purpose is to compare Peggy's performance to [eggcc](https://github.com/egraphs-good/eggcc), another optimizing compiler using equality saturation, on similar example files.

For the original Peggy project, please refer to the [Peggy documentation](https://goto.ucsd.edu/~mstepp/peggy/). This is not the original Peggy project.

# Overview

There are two benchmarks:

- **optimization capabilities (OC) (qualitative)**: This benchmark produces a comparison of optimized output at different numbers of iterations of Peggy's equality saturation engine.

- **performance and scaling (PS) (quantitative)**: This benchmark... TODO


# Running the benchmarks

## Installation

You will need [Docker](https://docs.docker.com/get-started/get-docker/) and the docker daemon must be running.

If you would like to use the setup script without modification, you will also need conda. Otherwise, if you would like to use a different Python environment, see the setup script for the required Python version and packages.

To setup, run `./setup.sh`. This may take a while (10 minutes).


## Running

Make sure the Docker daemon is running. Then, run:

```
docker start peggy
conda activate peggy
python3 scripts/benchmark_peggy.py
```

TODO: benchmark shouldn't produce relevant output, this should be captured instead

Note that this script runs subprocesses in the Docker container. If you want to interrupt the script, you may also want to `docker stop peggy` to stop subprocesses in the container.

TODO: approx time??

This will produce several outputs in the `results` directory:

- `oc/<params>`: A directory corresponding to the set of params passed to peggy on which it was run.

NOTE: this isn't correct rn
- `oc/<params>/params.json`: These are the params with which Peggy was run to produce the output in this directory.
- `oc/<params>/peggy_log.txt`: This is the captured output from running Peggy on `Benchmark.java`.
- `oc/<params>/Benchmark.java`: This is the decompiled optimized benchmark produced by running Peggy.
- `ps/perf.csv`: This is a csv containing the results of the performance benchmark on peggy.
- `ps/time_vs_lines.png`: A graph plotting the solver time of Peggy vs. the number of lines of its input.
- `ps/time_vs_nodes.png`: A graph plotting the solver time of Peggy vs. the number of nodes in the solver formulation.
