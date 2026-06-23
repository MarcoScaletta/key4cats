#!/bin/bash

echo "Checking command key4cats"
if ! command -v key4cats; then
  echo "\t > NOT OK: Command key4cats not found (did you use \"source\"?)"
  return
else
  echo "> OK: key4cats exists, executing \"key4cats -h\""
  key4cats -h
fi
