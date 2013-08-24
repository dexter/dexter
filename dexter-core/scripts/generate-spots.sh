#!/usr/bin/env bash

source scripts/config.sh

mkdir -p $SPOT_FOLDER


echo "extracting spots "
$JAVA it.cnr.isti.hpc.dexter.cli.spot.ExtractSpotsCLI --input $WIKI_JSON_DUMP --output $TMP
echo "sorting spots by text and target entity"
cat $TMP | sort -t'	' -k1,1 -k3,3n  | uniq > $SPOT
