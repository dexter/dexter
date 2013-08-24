#!/usr/bin/env bash

source scripts/config.sh

mkdir -p $SPOT_FOLDER/ram
$JAVA it.cnr.isti.hpc.dexter.cli.spot.ram.GenerateSpotsMinimalPerfectHashCLI  -output $SPOT_HASHES
echo "uncompressing spot file $SPOT_FILE "
zcat ${SPOT_FILE/.gz/} > $TTMP

echo "uncompressing hash file $SPOT_HASHES"
gunzip $SPOT_HASHES


SPOT_HASHES=${SPOT_HASHES/.gz/} 

echo "paste the spot file with the hashes"
paste $SPOT_HASHES  $TTMP > $TMP
echo "sorting the file by hash (output in $TTMP)"
sort -nk1 $TMP > $TTMP
cut -f 2,3,4,5,6 $TTMP > $TMP
echo "index spot file and generate offsets"
$JAVA it.cnr.isti.hpc.dexter.cli.spot.ram.IndexSpotFileAndGenerateOffsetsCLI -input $TMP
echo "index offsets using eliasfano"
$JAVA it.cnr.isti.hpc.dexter.cli.spot.ram.IndexOffsetsUsingEliasFanoCLI

echo "delete tmp files"
rm $TMP $TTMP $SPOT_HASHES







