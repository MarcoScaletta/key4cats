#!/bin/bash

echo "GENERATING SPECIFICATION REUSE"
source reuse-addOneCAT.sh addTwo doNothing

echo "VERIFYING SPECIFICATION REUSE"
source verify-addOneCAT-reuse.sh
