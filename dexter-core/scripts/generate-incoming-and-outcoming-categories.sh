#!/usr/bin/env bash

source scripts/config.sh


echo "extracting spots for categories"
$JAVA $CLI.categories.ExtractCategoryEdgesCLI --input $WIKI_CATEGORIES --output $TMP
echo "done in $TMP"

