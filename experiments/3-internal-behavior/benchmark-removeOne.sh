#!/bin/bash

echo "All the proofs should succeed."

echo "BENCHMARK removeOneOnceCAT"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -b -s removeOneOnceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy
echo "BENCHMARK removeOneTwiceCAT"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -b -s removeOneTwiceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy
echo "Verifying removeOneThriceCAT WITHOUT JVM warmup (warmup of JVM is not necessary)"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s  removeOneThriceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy -b 1

