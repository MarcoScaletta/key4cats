#!/bin/bash

echo "Checking verification process for trivial example: proof should succeed."
key4cats -s sanityCheckExampleCAT -catsl sanityCheckExample.cats -java SanityCheckExample
key4cats -f sanityCheckExampleCAT -catsl sanityCheckExample.cats -java SanityCheckExample
key4cats -catsl sanityCheckExample.cats -java SanityCheckExample
