#!/usr/bin/env bash

source scripts/config.sh


EXPECTED_ARGS=0
E_BADARGS=65

source scripts/config.sh

LOG=DEBUG

if [ $# -ne $EXPECTED_ARGS ];
then
  echo "Usage: `basename $0`"
  exit $E_BADARGS
fi

echo "generating spots per entity from $SPOT"
cut -f 1,3 $SPOT | sort -u | sort -t'	' -nk2 | uniq > $TMP

echo "index spots per entity"
$JAVA $CLI.spot.ram.IndexEntityToSpotsCLI -input $1








