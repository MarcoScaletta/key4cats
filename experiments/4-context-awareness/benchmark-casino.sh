#!/bin/bash



echo "BENCHMARK placeBetCAT"

printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -b -s placeBetCAT -catsl casino.cats -java Casino
echo "BENCHMARK decideBetCAT"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -b -s decideBetCAT -catsl casino.cats -java Casino
echo "BENCHMARK oneRoundCAT"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -b -s oneRoundCAT -catsl casino.cats -java Casino
echo "BENCHMARK wrongRoundCAT"
printf "> EXPECTED OUTCOME: \033[31m FAIL\033[0m\n"
key4cats -b -s wrongRoundCAT -catsl casino.cats -java Casino
