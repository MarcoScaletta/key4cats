#!/bin/bash


echo "BENCHMARK openAndWorkAndCloseCAT"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -b -s openAndWorkAndCloseCAT -catsl filework.cats -java FileWork

echo "BENCHMARK openAndWorkTwiceAndCloseCAT"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -b -s openAndWorkTwiceAndCloseCAT -catsl filework.cats -java FileWork

echo "BENCHMARK noOpenAndWorkAndCloseCAT"
echo "> EXPECTED OUTCOME: \033[31m FAIL\033[0m"
key4cats -b -s noOpenAndWorkAndCloseCAT -catsl filework.cats -java FileWork

echo "BENCHMARK openAndWorkAndNoCloseCAT"
echo "> EXPECTED OUTCOME: \033[31m FAIL\033[0m"
key4cats -b -s openAndWorkAndNoCloseCAT -catsl filework.cats -java FileWork


