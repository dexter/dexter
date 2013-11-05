#!/usr/bin/env bash

source scripts/config.sh


echo "extracting spots for categories"
$JAVA it.cnr.isti.hpc.dexter.cli.spot.ExtractSpotsCLI --input $WIKI_CATEGORIES --output $TMP
echo "sorting categories spots by text and target category"
cat $TMP | sort -t'	' -k1,1 -k3,3n  | uniq > $TTMP
echo "filtering edges"
cat $TTMP | awk -F'	' '{if ($2 > 0 && $3 > 0) print $2"	"$3}'  | sort -nk2,2 -nk1,1 | uniq > $TMP
echo "generating incoming categories file in $IN_CATEGORIES"
awk -F'	' 'BEGIN{current=$2; incoming=""} {if ($1 != $2) if ($2 == current) {incoming=incoming$1" "} else { print current"\t"incoming; current=$2; incoming=$1" "} } END {print current"\t"incoming}' $TMP  > $TTMP

#first line is empty, i need to remove it 
sed 1d $TTMP > $TTTMP

echo "compressing"
gzip $TTTMP
mv $TTTMP.gz $IN_CATEGORIES

echo "sorting by source"
sort -nk1,1 -nk2,2 $TMP > $TTMP
echo "generating outcoming categories file in $OUT_CATEGORIES"
awk -F'	' 'BEGIN{current=$1; incoming=""} {if ($1 != $2) if ($1 == current) {incoming=incoming$2" "} else { print current"\t"incoming; current=$1; incoming=$2" "} } END {print current"\t"incoming}' $TTMP  > $TMP

sed 1d $TMP > $TTMP
gzip $TTMP 
echo "compressing"
mv $TTMP.gz $OUT_CATEGORIES
rm $TMP

