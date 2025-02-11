set -euxo pipefail

# check that we have javap
javap --help


docker --version
export DOCKER_ENABLE_DEPRECATED_PULL_SCHEMA_1_IMAGE=1

# get an absolute path to the current directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

image_name=peggy
# If you use a different name for the container, you must also update
# config.py.
container_name=peggy

docker ps

# remove any docker images named peggy
docker rm -f $container_name || true

# Make the Docker image and container needed to run peggy (peggy needs
# Java 6).

# This step may take a while, around 10 minutes.
docker build -t $image_name .

# The container will have a volume corresponding to this directory.
docker run --user $(id -u):$(id -g) -d -v "$DIR:/peggy-comparison"  --name $container_name -i -t $image_name


# remove any local jd-cli installation
rm -rf jd-cli-0.9.2-dist.tar.gz jd-cli-0.9.2 || true
wget "https://github.com/intoolswetrust/jd-cli/releases/download/jd-cmd-0.9.2.Final/jd-cli-0.9.2-dist.tar.gz"
tar -xzvf jd-cli-0.9.2-dist.tar.gz

rm -rf jd-cli-0.9.2-dist.tar.gz