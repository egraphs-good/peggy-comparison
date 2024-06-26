# We need old ubuntu so that the package manager includes Java 6.
FROM ubuntu:14.04.2

# Install dependencies: Java 6, build essential (to build glpk), wget (to download jd-cli)
RUN apt-get update && \
    apt-get -y install openjdk-6-jdk build-essential wget git

# Download the repository
RUN git clone https://github.com/egraphs-good/peggy-comparison.git


# Install GLPK. We just need the glpsol executable.
RUN wget "https://ftp.gnu.org/gnu/glpk/glpk-5.0.tar.gz" && \
    tar -xzvf glpk-5.0.tar.gz && cd glpk-5.0 && \
    ./configure && make install && \
    mv examples/glpsol /usr/bin/glpsol && \
    cd .. && rm -rf glpk-5.0 && rm glpk-5.0.tar.gz

# Install jd-cli for command line decompiling. We need an old version to work with Java 6.
RUN wget "https://github.com/intoolswetrust/jd-cli/releases/download/jd-cmd-0.9.2.Final/jd-cli-0.9.2-dist.tar.gz" && \
    tar -xzvf jd-cli-0.9.2-dist.tar.gz && \
    mv jd-cli peggy-comparison && mv jd-cli.jar peggy-comparison && \
    rm jd-cli-0.9.2-dist.tar.gz && rm jd-cli.bat

# Everything we need is in peggy-comparison
WORKDIR /peggy-comparison
