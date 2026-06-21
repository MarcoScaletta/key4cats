#!/bin/bash

echo "Load the generated proof obligation (PO) removeOneCAT.key"
echo "The PO should be correctly loaded"
printf "> EXPECTED OUTCOME: \033[32m SUCCESS\033[0m\n"

key4cats -po removeOneCAT.key
