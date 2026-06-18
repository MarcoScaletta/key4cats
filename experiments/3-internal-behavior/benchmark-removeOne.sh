#!/bin/bash

echo "All the proofs should succeed."

echo "BENCHMARK removeOneOnceCAT"
key4cats -b -s removeOneOnceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy
echo "BENCHMARK removeOneTwiceCAT"
key4cats -b -s removeOneTwiceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy
echo "Verifying removeOneThriceCAT WITHOUT JVM warmup (warmup of JVM is not necessary)"
key4cats -s  removeOneThriceCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy -b 1

