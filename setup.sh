set -euxo pipefail

# get an absolute path to the current directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

image_name=peggy
# If you use a different name for the container, you must also update
# config.py.
container_name=peggy
conda_env_name=peggy

# Make the Docker image and container needed to run peggy (peggy needs
# Java 6).

# This step may take a while, around 10 minutes.
docker build -t $image_name .

# The container will have a volume corresponding to this directory.
docker run -d -v "$DIR:/peggy-comparison"  --name $container_name -i -t $image_name

# Create the python environment needed to run the benchmark scripts
conda create -n $conda_env_name python=3.13.0 matplotlib=3.9.2 pandas=2.2.3

conda init