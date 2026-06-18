#!/bin/bash



echo "BENCHMARK placeBetCAT: proof should succeed"
key4cats -b -s placeBetCAT -catsl casino.cats -java Casino
echo "BENCHMARK decideBetCAT: proof should succeed"
key4cats -b -s decideBetCAT -catsl casino.cats -java Casino
echo "BENCHMARK oneRoundCAT: proof should succeed"
key4cats -b -s oneRoundCAT -catsl casino.cats -java Casino
echo "BENCHMARK wrongRoundCAT: proof should fail"
key4cats -b -s wrongRoundCAT -catsl casino.cats -java Casino
