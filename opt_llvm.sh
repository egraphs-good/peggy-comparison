#!/bin/bash

java -Xmx400m -cp .:peggy_1.0.jar peggy.optimize.llvm.Main $@
