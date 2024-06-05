This repository runs the [peggy](https://goto.ucsd.edu/~mstepp/peggy/) equality saturation engine and compiler on several example files. Its purpose is to compare peggy's optimizations to [eggcc](https://github.com/egraphs-good/eggcc), another optimizing compiler using equality saturation, on similar example files.

For the original peggy project, please refer to the [peggy documentation](https://goto.ucsd.edu/~mstepp/peggy/). This is not the original peggy project.


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

# Comparison benchmark
This benchmark is qualitative, based on analysis of the optimized output of peggy on certain small example files. These files are in the `benchmark` directory.

Decompiled output for each of the benchmark Java files is stored in `results`. If you would like to re-generate these files, run `python3 benchmark_peggy.py`. In a Docker container, this took 24 min user time, 41 min total time.