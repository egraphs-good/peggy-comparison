This repository performs qualitative and quantitative benchmarks on the [peggy](https://goto.ucsd.edu/~mstepp/peggy/) equality saturation engine and compiler. Its purpose is to compare peggy's performance to [eggcc](https://github.com/egraphs-good/eggcc), another optimizing compiler using equality saturation, on similar example files.

For the original peggy project, please refer to the [peggy documentation](https://goto.ucsd.edu/~mstepp/peggy/). This is not the original peggy project.

# Overview

There are two benchmarks.

## Optimization capabilities (OC) (qualitative)

This benchmark produces a comparison of 


## Performance and scaling (PS) (quantitative)


# Dockerfile Setup
The Dockerfile will clone this repository and install several dependencies.

First, build the image:

```
docker build -t peggy .
```

Now run the image.
```
docker run -it peggy
```

# Running the benchmarks

TODO: don't run it in the docker container. 

Run `python3 benchmark_peggy.py` in the Docker container.

TODO: approx time??

This will produce several outputs in the `results` directory:

- `oc/<params>`: A directory corresponding to the set of params passed to peggy on which it was run.
- `oc/<params>/params.json`: These are the params with which Peggy was run to produce the output in this directory.
- `oc/<params>/peggy_log.txt`: This is the captured output from running Peggy on `Benchmark.java`.
- `oc/<params>/Benchmark.java`: This is the decompiled optimized benchmark produced by running Peggy.
- `ps/perf.csv`: This is a csv containing the results of the performance benchmark on peggy.
- `ps/time_vs_lines.png`: A graph plotting the solver time of Peggy vs. the number of lines of its input.
- `ps/time_vs_nodes.png`: A graph plotting the solver time of Peggy vs. the number of nodes in the solver formulation.
