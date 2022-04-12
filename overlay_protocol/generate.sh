#!/bin/sh

# On first error, exit. Also, print commands.
set -ex

# Absolute path to the folder containing script
DIR=$(readlink -f "$0")

pushd "$DIR"

flatc --java --gen-object-api --gen-all -o java/src/main/java ./flatbuffers/all.fbs
flatc --rust --rust-module-root-file --gen-all -o rust/src/generated ./flatbuffers/all.fbs

popd
