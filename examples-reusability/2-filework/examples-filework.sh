#!/bin/bash



echo "BENCHMARK onlyOpenCAT: proof should succeed"
key4cats -b -s onlyOpenCAT -catsl filework.cats -java FileWork

echo "BENCHMARK openAndWorkAndCloseCAT: proof should succeed"
key4cats -b -s openAndWorkAndCloseCAT -catsl filework.cats -java FileWork

echo "BENCHMARK openAndWorkTwiceAndCloseCAT: proof should succeed"
key4cats -b -s openAndWorkTwiceAndCloseCAT -catsl filework.cats -java FileWork

echo "BENCHMARK noOpenAndWorkAndCloseCAT: proof should fail"
key4cats -b -s noOpenAndWorkAndCloseCAT -catsl filework.cats -java FileWork

echo "BENCHMARK openAndWorkAndNoCloseCAT : proof should fail"
key4cats -b -s openAndWorkAndNoCloseCAT -catsl filework.cats -java FileWork


