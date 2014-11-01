#!/usr/bin/env bash

source scripts/config.sh

echo "index incoming and outcoming category edges (in $OUT_ENTITY_CATEGORIES and $IN_ENTITY_CATEGORIES)"

echo "outcoming: "
$JAVA $CLI.graph.IndexOutcomingEntityCategoryNodesCLI -input $OUT_ENTITY_CATEGORIES".gz"

echo "incoming"
$JAVA $CLI.graph.IndexIncomingEntityCategoryNodesCLI -input $IN_ENTITY_CATEGORIES".gz"



