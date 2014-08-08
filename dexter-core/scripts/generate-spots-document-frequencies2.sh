#!/usr/bin/env bash

source scripts/config.sh


echo "Generates spots frequencies in $SPOT_DOC_FREQ"

# produce a file in $SPOT_DOC_FREQ containing 
# <spot> \t <df(spot)>
# where df(spot) is the document frequency of the text in wikipedia collection 
$JAVA $CLI.spot.GenerateSpotDocumentFrequency2CLI -input $SPOT -dump $WIKI_JSON_DUMP -output $TMP





