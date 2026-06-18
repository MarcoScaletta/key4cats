#!/bin/bash

echo "Checking env variable KEY4CATs"
if [[ -z "${KEY4CATs}" ]]; then
  echo "> NOT OK: Env. Variable KEY4CATs not defined"
  exit 1
else
  echo "> OK: env var KEY4CATs is ${KEY4CATs}"
fi

