#!/bin/bash

echo "Verifying full file specs-reuse.cats"
echo "> EXPECTED OUTCOME for addOne and addTwo: \033[32m SUCCESS\033[0m"
echo "> EXPECTED OUTCOME for doNothing: \033[31m FAIL\033[0m"
key4cats -catsl specs-reuse.cats -java SpecsReuse