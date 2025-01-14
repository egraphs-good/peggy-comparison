# TODO: remove timing?
set -euxo pipefail

# Build the docker container needed to run peggy (peggy needs
# Java 6). If you use a different name for the container, you must
# also update config.py.
time docker build -t peggy .
time docker start peggy

# Create the python environment needed to run the benchmark scripts
conda create -n peggy python=3.13.0 matplotlib=3.9.2 pandas=2.2.3
conda activate peggy

# Run the benchmarks
# TODO: don't produce output from script that's actually
# part of analyzing the results, only debugging output
time python3 scripts/benchmark_peggy.py &> output.txt