#!/bin/bash




echo "Verifying rmOneCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmOneCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b 10

echo "Verifying rmTwoCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmTwoCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b 10

echo "Verifying rmThreeCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmThreeCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b 5

echo "Verifying rmFourCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmFourCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b 5

echo "Verifying rmFiveCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmFiveCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b 3

echo "Verifying rmSixCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmSixCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b 3

echo "Verifying rmSevenCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmSevenCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b  2

echo "Verifying rmEightCAT with JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmEightCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -b 1 -max 20000

echo "Verifying rmNineCAT NO JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmNineCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -max 40000

echo "Verifying rmTenCAT NO JVM WARM UP"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -s rmTenCAT -catsl hoare-logic-benchmark.cats -java HoareLogic -max 80000

python3 plots.py "$@"