#!/bin/bash

echo "Beginning peggy nightly script..."

# -x: before executing each command, print it
# -e: exit immediately upon first error
set -x -e
# if anything in a pipeline fails, fail the whole pipeline
set -o pipefail

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
DATA_DIR="$NIGHTLY_DIR/data"

# Make sure we're in the right place
cd $MYDIR
echo "Switching to nighly script directory: $MYDIR"

# Clean previous nightly run
# CAREFUL using -f

rm -rf $NIGHTLY_DIR
# Prepare output directories
mkdir -p "$NIGHTLY_DIR" "$NIGHTLY_DIR/data" "$OUTPUT_DIR"

# run setup.sh
source setup.sh

# run run.sh
source run.sh

# copy data directory to output
cp -r "$DATA_DIR" "$OUTPUT_DIR"
