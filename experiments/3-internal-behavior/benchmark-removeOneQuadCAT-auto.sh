#!/bin/bash

echo "Verifying removeOneQuadCAT WITHOUT JVM warmup"
echo "\033[1;31m !! The following proof can take VERY LONG (>1.5h minutes originally) !!\033[0m"
echo "\033[1;31m !! JVM warmup for this CAT is unnecessary and NOT RECOMMENDED !!\033[0m"
key4cats -s removeOneQuadCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy -max 40000
