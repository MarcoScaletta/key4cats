#!/bin/bash

echo "========================================================================="
echo "FUNCTIONAL EVALUATION"
echo "========================================================================="
echo "experiment 1: translation"
cd experiments/1-translation/
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "experiment 1-translation.sh"
time (source 1-translation.sh)
echo "-------------------------------------------------------------------------"
echo "experiment 2-load_and_prove.sh"
time (source 2-load_and_prove.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
echo "experiment 2: Hoare Logic"
cd ../2-hoare-logic
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "experiment 1-prove-removeThreeCAT.sh"
time (source 1-prove-removeThreeCAT.sh)
echo "-------------------------------------------------------------------------"
echo "experiment 2-simple-CATs-benchmark.sh"
time (source 2-simple-CATs-benchmark.sh -no-show)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
echo "experiment 3: Internal Behavior"
cd ../3-internal-behavior
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "experiment benchmark-removeOne.sh"
time (source benchmark-removeOne.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
echo "experiment 4: context-awareness"
cd ../4-context-awareness
echo "In folder: $(pwd)"
echo "-------------------------------------------------------------------------"
echo "experiment benchmark-casino.sh"
time (source benchmark-casino.sh)
echo "-------------------------------------------------------------------------"
echo "========================================================================="
cd ../../..