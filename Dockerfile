# We need old ubuntu so that the package manager includes Java 6.
FROM ubuntu:14.04

# Install dependencies: Java 6, build essential (to build glpk), wget (to download jd-cli)
RUN apt-get update && \
    apt-get -y install openjdk-6-jdk build-essential wget git 

# Install GLPK.
RUN wget "https://ftp.gnu.org/gnu/glpk/glpk-5.0.tar.gz" && \
    tar -xzvf glpk-5.0.tar.gz && cd glpk-5.0 && \
    ./configure && make install && \
    # We copy the executable to bin because Peggy expects it to be at this location.
    # Passing -glpkPath pointing to solver in the example directory doesn't seem to work.
    # Peggy fails due to not finding glpsol at "/usr/bin/glpsol".
    cp /glpk-5.0/examples/glpsol /usr/bin/glpsol

# Download the repository
RUN git clone https://github.com/egraphs-good/peggy-comparison.git

# Install jd-cli for command line decompiling. We need an old version to work with Java 6.
RUN wget "https://github.com/intoolswetrust/jd-cli/releases/download/jd-cmd-0.9.2.Final/jd-cli-0.9.2-dist.tar.gz" && \
    tar -xzvf jd-cli-0.9.2-dist.tar.gz && \
    mv jd-cli peggy-comparison && mv jd-cli.jar peggy-comparison && \
    rm jd-cli-0.9.2-dist.tar.gz && rm jd-cli.bat

# We are going to run /usr under a non-root, 
# and glpk dumps files under /usr, so we just give everyone every permission
RUN chmod -R 777 /usr

# Everything we need will be in peggy-comparison
WORKDIR /peggy-comparison
