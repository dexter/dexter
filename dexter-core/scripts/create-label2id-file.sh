#!/usr/bin/env bash

source scripts/config.sh

# 
# if [ $# -ne $EXPECTED_ARGS ]
# then
#   echo "Usage: `basename $0` wikipedia-json-dump.json"
#   exit $E_BADARGS
# fi

echo "export titles and redirects from the dump ($WIKI_JSON_DUMP)"
$JAVA it.cnr.isti.hpc.dexter.cli.label.ExportArticlesIdCLI -input $WIKI_JSON_DUMP -output $TITLE_FILE
echo "sort by id"
sort -t"	" -nk3 $TITLE_FILE > $TMP
mv  $TMP  $TITLE_FILE
echo "indexing id -> label"
$JAVA it.cnr.isti.hpc.dexter.cli.label.IndexIdToLabelCLI -input $TITLE_FILE
echo "sort by title"
sort -k1 $TITLE_FILE > $TMP
mv  $TMP $TITLE_FILE
echo "adding redirect"
$JAVA  it.cnr.isti.hpc.dexter.cli.label.AddRedirectIdCLI -input $TITLE_FILE -output $TMP
mv  $TMP  $TITLE_FILE
echo "sort title and redirect"
sort -k1 $TITLE_FILE > $TMP
mv  $TMP  $TITLE_FILE
echo "indexing label -> id"
$JAVA it.cnr.isti.hpc.dexter.cli.label.IndexLabelToIdCLI -input $TITLE_FILE
#rm $TITLE_FILE
