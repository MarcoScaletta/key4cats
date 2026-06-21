#!/bin/bash

echo "Verifying full file specs-reuse.cats"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -s removeOneTwiceMatchOnceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy