#!/bin/bash



echo "BENCHMARK placeBetCAT"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -b -s placeBetCAT -catsl casino.cats -java Casino
echo "BENCHMARK decideBetCAT"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -b -s decideBetCAT -catsl casino.cats -java Casino
echo "BENCHMARK oneRoundCAT"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -b -s oneRoundCAT -catsl casino.cats -java Casino
echo "BENCHMARK wrongRoundCAT"
echo "> EXPECTED OUTCOME: \033[31m FAIL\033[0m"
key4cats -b -s wrongRoundCAT -catsl casino.cats -java Casino
