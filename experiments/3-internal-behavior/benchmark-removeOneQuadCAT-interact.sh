#!/bin/bash

echo "Verifying removeOneQuadCAT WITHOUT JVM warmup"
printf "\033[1;31m !! The following proof can take VERY LONG (>1.5h minutes originally) !!\033[0m\n"
printf "\033[1;31m !! JVM warmup for this CAT is unnecessary and NOT RECOMMENDED !!\033[0m\n"
echo "The proof will be done interactively (execute \"source benchmark-removeOneQuadCAT-auto.sh\" otherwise)"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -i -s removeOneQuadCAT -catsl removeOneCaseStudy.cats -java RemoveOneCaseStudy -max 40000
