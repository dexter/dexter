#!/usr/bin/env bash

source scripts/config.sh


echo "extracting categories"

$JAVA $CLI.categories.ExtractCategoriesCLI -input $WIKI_JSON_DUMP -output $WIKI_CATEGORIES

echo "categories in $WIKI_CATEGORIES"



