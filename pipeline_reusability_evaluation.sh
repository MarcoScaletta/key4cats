#!/bin/bash
echo "========================================================================="
echo "REUSABILITY EVALUATION"
echo "========================================================================="
echo "example 1: specification reuse"
cd examples-reusability/1-specification-reuse/greater-than
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "example greater-than-specs-reuse.sh"
time (source greater-than-specs-reuse.sh)
echo "-------------------------------------------------------------------------"
cd ../removeOne
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "example removeOne-specs-reuse.sh"
time (source removeOne-specs-reuse.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
echo "example 1: specification reuse"
cd ../../2-filework
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "example examples-filework.sh"
time (source examples-filework.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
cd ../../..