This repository performs qualitative and quantitative benchmarks on the [peggy](https://goto.ucsd.edu/~mstepp/peggy/) equality saturation engine and compiler. Its purpose is to compare peggy's performance to [eggcc](https://github.com/egraphs-good/eggcc), another optimizing compiler using equality saturation, on similar example files.

For the original peggy project, please refer to the [peggy documentation](https://goto.ucsd.edu/~mstepp/peggy/). This is not the original peggy project.

# Overview

There are two benchmarks:

- **optimization capabilities (OC) (qualitative)**: This benchmark produces a comparison of 

- **performance and scaling (PS) (quantitative)**: This benchmark...


# Running the benchmarks

## Installation

You will need [Docker](https://docs.docker.com/get-started/get-docker/) and the docker daemon must be running.

Optionally, if you would like to use the run script without modification, you will need conda. Otherwise, see the run script for the required Python version and packages.

## Running

The script `run.sh` creates a Docker container for peggy to run in,  Simply run:

```./run.sh```


NOTE: It's hard to stop the benchmark file because it runs subprocesses in docker? think the best way is to kill it?

TODO: approx time??

This will produce several outputs in the `results` directory:

- `oc/<params>`: A directory corresponding to the set of params passed to peggy on which it was run.
- `oc/<params>/params.json`: These are the params with which Peggy was run to produce the output in this directory.
- `oc/<params>/peggy_log.txt`: This is the captured output from running Peggy on `Benchmark.java`.
- `oc/<params>/Benchmark.java`: This is the decompiled optimized benchmark produced by running Peggy.
- `ps/perf.csv`: This is a csv containing the results of the performance benchmark on peggy.
- `ps/time_vs_lines.png`: A graph plotting the solver time of Peggy vs. the number of lines of its input.
- `ps/time_vs_nodes.png`: A graph plotting the solver time of Peggy vs. the number of nodes in the solver formulation.
