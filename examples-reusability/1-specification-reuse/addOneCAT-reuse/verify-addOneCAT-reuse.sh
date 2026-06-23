#!/bin/bash

echo "Verifying full file specs-file-addOne.cats"
printf "> EXPECTED OUTCOME for addOneCAT and addTwoCAT: \033[32m SUCCESS\033[0m\n"
printf "> EXPECTED OUTCOME for doNothingCAT: \033[31m FAIL\033[0m\n"
key4cats -catsl addOneCAT-reuse.cats -java SpecsReuse
