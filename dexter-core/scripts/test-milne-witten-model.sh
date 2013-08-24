#!/usr/bin/env bash

source scripts/config.sh

EXPECTED_ARGS=0

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0`"
  exit $E_BADARGS
fi

mkdir -p $MW_DIR
TRAIN=$MW_DIR"/training.json.gz"
TEST=$MW_DIR"/test.json.gz"


$JAVA it.cnr.isti.hpc.dexter.cli.milnewitten.TestCLI -test $TEST



