#!/usr/bin/env bash

source scripts/config.sh

EXPECTED_ARGS=2

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` wikipedia-dump  training-size"
  exit $E_BADARGS
fi

mkdir -p $MW_DIR
DUMP=$1
SIZE=$2
TRAIN=$MW_DIR"/training.json.gz"
TEST=$MW_DIR"/test.json.gz"


$JAVA it.cnr.isti.hpc.dexter.cli.milnewitten.GenerateMilneWittenTrainingAndTestSetCLI -input $DUMP -training $TRAIN -test $TEST -size $SIZE



