#!/bin/bash

echo "Verifying removeThreeCAT and all its dependencies"
echo "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m"
key4cats -f removeThreeCAT -catsl hoare-logic.cats -java HoareLogic -b
