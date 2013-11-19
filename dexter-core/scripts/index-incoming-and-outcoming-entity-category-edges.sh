#!/usr/bin/env bash

source scripts/config.sh

echo "index incoming and outcoming edges (in $OUT_EDGES and $IN_EDGES)"

echo "outcoming: "
$JAVA $CLI.graph.IndexOutcomingEntityCategoryNodesCLI -input $OUT_ENTITY_CATEGORIES

echo "incoming"
$JAVA $CLI.graph.IndexIncomingEntityCategoryNodesCLI -input $IN_ENTITY_CATEGORIES



