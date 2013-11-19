#!/usr/bin/env bash

source scripts/config.sh

EXPECTED_ARGS=2
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` in/out id/name"
  exit $E_BADARGS
fi

DIR=$1
NAME=$2 
echo "$1 category links for $2"
$JAVA it.cnr.isti.hpc.dexter.cli.graph.GetNeighbourCategoryNodesCLI -input $NAME -direction $DIR



