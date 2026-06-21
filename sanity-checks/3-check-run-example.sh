#!/bin/bash

echo "Checking verification process for trivial example"

echo "Target single CAT: sanityCheckExampleCAT"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -s sanityCheckExampleCAT -catsl sanityCheckExample.cats -java SanityCheckExample

echo "Target sanityCheckExampleCAT and all dependencies"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -f sanityCheckExampleCAT -catsl sanityCheckExample.cats -java SanityCheckExample

echo "Target full file sanityCheckExample.cats"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"
key4cats -catsl sanityCheckExample.cats -java SanityCheckExample
