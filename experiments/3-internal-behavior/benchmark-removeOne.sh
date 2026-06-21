#!/bin/bash

echo "All the proofs should succeed."

echo "BENCHMARK removeOneOnceCAT"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -b -s removeOneOnceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy
echo "BENCHMARK removeOneTwiceCAT"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -b -s removeOneTwiceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy
echo "Verifying removeOneThriceCAT WITHOUT JVM warmup (warmup of JVM is not necessary)"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -s  removeOneThriceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy -b 1

