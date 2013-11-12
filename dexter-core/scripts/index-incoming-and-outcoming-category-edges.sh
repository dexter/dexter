#!/usr/bin/env bash

source scripts/config.sh

echo "index incoming and outcoming edges (in $OUT_EDGES and $IN_EDGES)"

echo "outcoming: "
$JAVA $CLI.graph.IndexOutcomingCategoryNodesCLI -input $OUT_CATEGORIES

echo "incoming"
$JAVA $CLI.graph.IndexIncomingCategoryNodesCLI -input $IN_CATEGORIES



