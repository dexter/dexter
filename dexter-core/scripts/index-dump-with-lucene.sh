#!/usr/bin/env bash

source scripts/config.sh

EXPECTED_ARGS=0

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0`"
  exit $E_BADARGS
fi
DUMP=$1
PROGRESS_INFO_FREQ=100000
 
echo "indexing dump ($WIKI_JSON_DUMP)"
$JAVA $CLI.index.IndexWikipediaOnLuceneCLI -input $WIKI_JSON_DUMP 
