#!/bin/bash

echo "Beginning peggy nightly script..."

# -x: before executing each command, print it
# -e: exit immediately upon first error
set -x -e
# if anything in a pipeline fails, fail the whole pipeline
set -o pipefail

# run setup.sh
source setup.sh

# determine physical directory of this script
src="${BASH_SOURCE[0]}"
while [ -L "$src" ]; do
  dir="$(cd -P "$(dirname "$src")" && pwd)"
  src="$(readlink "$src")"
  [[ $src != /* ]] && src="$dir/$src"
done
MYDIR="$(cd -P "$(dirname "$src")" && pwd)"

# Absolute directory paths
NIGHTLY_DIR="$MYDIR/nightly"
OUTPUT_DIR="$NIGHTLY_DIR/output"
DATA_DIR="$MYDIR/nightly/data"

# Make sure we're in the right place
cd $MYDIR
echo "Switching to nighly script directory: $MYDIR"

# Clean previous nightly run
# CAREFUL using -f

rm -rf $NIGHTLY_DIR
# Prepare output directories
mkdir -p "$NIGHTLY_DIR" "$NIGHTLY_DIR/data" "$NIGHTLY_DIR/data/llvm" "$OUTPUT_DIR"



# This is the uploading part, copied directly from Herbie's nightly script.
DIR="$OUTPUT_DIR"
B=$(git rev-parse --abbrev-ref HEAD)
C=$(git rev-parse HEAD | sed 's/\(..........\).*/\1/')
RDIR="$(date +%s):$(hostname):$B:$C"

# Upload the artifact!
if [ "$LOCAL" == "" ]; then
  nightly-results publish --name "$RDIR" "$DIR"
fi