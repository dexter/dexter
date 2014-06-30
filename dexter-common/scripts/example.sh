#!/usr/bin/env bash
source ./scripts/config.sh

EXPECTED_ARGS=2 

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` input1 input2"
  exit $E_BADARGS
fi

echo "hello world param $1, param $2"
