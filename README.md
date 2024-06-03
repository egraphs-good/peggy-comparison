This repository runs the [peggy](https://goto.ucsd.edu/~mstepp/peggy/) equality saturation engine on compiler on several example files. Its purpose is to compare peggy's optimizations to [eggcc](https://github.com/egraphs-good/eggcc), another optimizing compiler using equality saturation, on similar example files.

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
