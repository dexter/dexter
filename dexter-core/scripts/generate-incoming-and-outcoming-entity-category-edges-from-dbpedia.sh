#!/usr/bin/env bash

source scripts/config.sh

rm $TMP 

echo "downloading the dbpedia entity-categories (lang = $LANG)"
wget -O $TMP.bz2 http://downloads.dbpedia.org/3.9/en/article_categories_$LANG.nt.bz2

echo "uncompressing"
bunzip2 $TMP.bz2

echo "extracting entity categories relationships"

grep subject $TMP | cut -d' ' -f1,3 | tr -d '<>'  | sed 's/http:\/\/dbpedia.org\/resource\///g' | tr ' ' '\t' > $TTMP

echo "generate edges"
$JAVA $CLI.categories.ExtractDbpediaCategoriesCLI --input $TTMP --output $TTTMP

echo "generate out entity categories"
awk -F'	' 'BEGIN{current=$1; incoming=""} {if ($1 == current) {incoming=incoming$2" "} else { print current"\t"incoming; current=$1; incoming=$2" "} } END {print current"\t"incoming}' $TTTMP > ${OUT_ENTITY_CATEGORIES/.gz/}

echo "compressing"
gzip ${OUT_ENTITY_CATEGORIES/.gz/}

echo "generate in entity-categories"

sort -nk2,2 -nk1,1 $TTTMP | awk -F'	' 'BEGIN{current=$2; incoming=""} {if ($2 == current) {incoming=incoming$1" "} else { print current"\t"incoming; current=$2; incoming=$1" "} } END {print current"\t"incoming}' > ${IN_ENTITY_CATEGORIES/.gz/}

echo "compressing"
gzip ${IN_ENTITY_CATEGORIES/.gz/}







