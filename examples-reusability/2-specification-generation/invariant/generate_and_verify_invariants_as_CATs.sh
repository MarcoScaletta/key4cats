#!/bin/bash

INVARIANT="x>1"

printf "> EXPECTED OUTCOME for invariant (x>1)\n"
printf "> \t addOne: \033[32m SUCCESS\033[0m\n"
printf "> \t removeOne: \033[31m FAIL\033[0m\n"

source generate_invariants_as_CATs.sh "$INVARIANT" addOne removeOne

source verify_invariants_as_CATs.sh