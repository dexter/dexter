#!/usr/bin/env bash

source scripts/config.sh

EXPECTED_ARGS=0

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0`"
  exit $E_BADARGS
fi
TM_DIR="data/tagme"

mkdir -p $TM_DIR
TRAIN=$TM_DIR"/training.json.gz"
TEST=$TM_DIR"/test.json.gz"


$JAVA it.cnr.isti.hpc.dexter.cli.tagme.TrainCLI -training $TRAIN




