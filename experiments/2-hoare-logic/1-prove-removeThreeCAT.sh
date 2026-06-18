#!/bin/bash

echo "Verifying removeThreeCAT and all its dependencies: proof should succeed"
key4cats -f removeThreeCAT -catsl hoare-logic.cats -java HoareLogic -b
