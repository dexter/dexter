#!/usr/bin/env bash

source scripts/config.sh


echo "merging $SPOT and $SPOT_DOC_FREQ in $SPOT_FILE"

$JAVA $CLI.spot.WriteOneSpotPerLineCLI -input $SPOT -freq $SPOT_DOC_FREQ -output $SPOT_FILE

echo "results in $SPOT_FILE"



