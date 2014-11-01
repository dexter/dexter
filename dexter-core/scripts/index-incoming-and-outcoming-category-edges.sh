#!/usr/bin/env bash

source scripts/config.sh

echo "index incoming and outcoming edges (in $OUT_CATEGORIES and $IN_CATEGORIES)"

echo "outcoming: "
$JAVA $CLI.graph.IndexOutcomingCategoryNodesCLI -input $OUT_CATEGORIES".gz"

echo "incoming"
$JAVA $CLI.graph.IndexIncomingCategoryNodesCLI -input $IN_CATEGORIES".gz"



