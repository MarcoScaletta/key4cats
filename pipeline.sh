#!/bin/bash
start=`date +%s`

time (source pipeline_functional_evaluation.sh)
time (source pipeline_reusability_evaluation.sh)

end=`date +%s`

echo "Total time of execution: $((end-start)) seconds"