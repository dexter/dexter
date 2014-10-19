#!/usr/bin/env bash

VERSION="1.0.0"

# ram used 
XMX="-Xmx8000m"
# logging level
LOG=INFO
#project name, do not change
PROJECT_NAME=dexter-core
#LOG=DEBUG
# log frequency
LOGAT=1000
# command line package, do not change
CLI=it.cnr.isti.hpc.dexter.cli
# error code
E_BADARGS=65

# java command
JAVA="java $XMX -Dlogat=$LOGAT -Dlog=$LOG -cp ./target/$PROJECT_NAME-$VERSION-jar-with-dependencies.jar "

# data directory
DATA_DIR=data
# graph data folder 
GRAPH_DIR=$DATA_DIR/graph
# temporary file containing the labels and the redirects
TITLE_FILE=$DATA_DIR/title-redirect-id.tsv
# directory use for storing temporary files during files 
DEXTER_TMP_DIR=$DATA_DIR/tmp

# dump language
LANG=en
# json wikipedia dump path
WIKI_JSON_DUMP=../enwiki-latest-pages-articles.json.gz


#spot folder
SPOT_FOLDER=$DATA_DIR/spot

# spot file, contains the edges of the wikipedia graph, each line represent an edge
# <spot> <tab> <source entity> <tab> <target entity>
SPOT=$SPOT_FOLDER/spot-src-target.tsv


# contains the the document frequency for each spot, 
# how many documents contains the spot as anchor text or raw text
SPOT_DOC_FREQ=$SPOT_FOLDER/spots-doc-freq.tsv
# contains the metadata for each spot:
# the candidate entities for the spot, the document frequency
SPOT_FILE=$SPOT_FOLDER/spots.tsv.gz
# contains the perfect hashes for the spots
SPOT_HASHES=$SPOT_FOLDER/spot-hashes.tsv.gz

# plain incoming node file
IN_EDGES=$GRAPH_DIR/incoming-edges.tsv.gz
# plain outcoming node file
OUT_EDGES=$GRAPH_DIR/outcoming-edges.tsv.gz

#categories
WIKI_CATEGORIES=$GRAPH_DIR/categories.json.gz
IN_CATEGORIES=$GRAPH_DIR/incoming-categories.tsv.gz
OUT_CATEGORIES=$GRAPH_DIR/outcoming-categories.tsv.gz
IN_ENTITY_CATEGORIES=$GRAPH_DIR/incoming-entity-categories.tsv.gz
OUT_ENTITY_CATEGORIES=$GRAPH_DIR/outcoming-entity-categories.tsv.gz

#temporary files used by the scripts
TMP=$DEXTER_TMP_DIR/tmp
TTMP=$DEXTER_TMP_DIR/tmp1
TTTMP=$DEXTER_TMP_DIR/tmp2

mkdir -p $DEXTER_TMP_DIR

export TMPDIR=$DEXTER_TMP_DIR
export LC_ALL=C
