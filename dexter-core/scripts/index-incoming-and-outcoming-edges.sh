#!/usr/bin/env bash

source scripts/config.sh

echo "index incoming and outcoming edges (in $OUT_EDGES and $IN_EDGES)"

echo "outcoming: "
$JAVA $CLI.graph.IndexOutcomingNodesCLI -input $OUT_EDGES

echo "incoming"
$JAVA $CLI.graph.IndexIncomingNodesCLI -input $IN_EDGES



