#!/usr/bin/env bash

source scripts/config.sh


EXPECTED_ARGS=1
E_BADARGS=65

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` {arg}"
  exit $E_BADARGS
fi

echo "get entities for spot <$1>"
$JAVA it.cnr.isti.hpc.dexter.cli.spot.GetSpotCommonnessCLI --spot $1

