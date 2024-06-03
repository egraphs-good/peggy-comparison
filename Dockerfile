# We need old ubuntu so that the package manager includes Java 6.
FROM ubuntu:14.04.2

# Install dependencies: Java 6, build essential (to build glpk), wget (to download jd-cli)
RUN apt-get update && \
    apt-get -y install openjdk-6-jdk build-essential wget

# Download the repository
# FIXME: we need to make the repo public for this to work
RUN git clone git@github.com:egraphs-good/peggy-comparison.git

# Install GLPK. We just need the glpsol executable.
RUN wget "https://ftp.gnu.org/gnu/glpk/glpk-5.0.tar.gz" && \
    unzip glpk-5.0.tar.gz && cd glpk-5.0 && \
    ./configure && make install && \
    mv examples/glpsol /usr/bin/glpsol && cd glpk-5.0 && rm -rf glpk-5.0

# Install jd-cli for command line decompiling. We need an old version to work with Java 6.
RUN wget "https://github.com/intoolswetrust/jd-cli/releases/download/jd-cmd-0.9.2.Final/jd-cli-0.9.2-dist.tar.gz" && tar -xzvf jd-cli-0.9.2-dist.tar.gz 
