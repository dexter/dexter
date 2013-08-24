#!/usr/bin/env bash

source scripts/config.sh

echo "Indexing spots"
echo "delete old index"
rm -rf $SPOT_INDEX_DIR

echo "Joining spotfile $SPOT with the spot doc frequencies $SPOT_DOC_FREQ"
join -t '	' -1 1 -2 1  $SPOT $SPOT_DOC_FREQ > $TMP
echo "Indexing"
$JAVA it.cnr.isti.hpc.dexter.cli.spot.IndexSpotsCLI -input $TMP

rm $TMP

