#!/bin/bash

echo "Verifying removeThreeCAT and all its dependencies"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -f removeThreeCAT -catsl hoare-logic.cats -java HoareLogic -b
