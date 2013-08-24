#!/usr/bin/env bash

source scripts/config.sh

echo "index incoming and outcoming edges (in $OUT_EDGES and $IN_EDGES)"

echo "outcoming: "
$JAVA it.cnr.isti.hpc.dexter.cli.graph.IndexOutcomingNodesCLI -input $OUT_EDGES

echo "incoming"
$JAVA it.cnr.isti.hpc.dexter.cli.graph.IndexIncomingNodesCLI -input $IN_EDGES



