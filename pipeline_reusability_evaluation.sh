#!/bin/bash
echo "========================================================================="
echo "REUSABILITY EVALUATION"
echo "========================================================================="
echo "example 1: specification reuse"
cd examples-reusability/1-specification-reuse/addOneCAT-reuse
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "example generate-and-verify-addOneCAT-reuse.sh"
time (source generate-and-verify-addOneCAT-reuse.sh)
echo "-------------------------------------------------------------------------"
cd ../removeOne
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "example removeOne-specs-reuse.sh"
time (source removeOne-specs-reuse.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="

echo "example 3: specification generation"
cd ../../2-specification-generation/invariant
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "example generate_and_verify_invariants_as_CATs.sh"
time (source generate_and_verify_invariants_as_CATs.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
echo "example 3: specifying and verifying protocols"
cd ../../3-filework
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "example examples-filework.sh"
time (source examples-filework.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
cd ../../