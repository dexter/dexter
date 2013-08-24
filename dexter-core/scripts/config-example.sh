#!/usr/bin/env bash

VERSION="0.0.4-SNAPSHOT"
XMX="-Xmx8000m"
LOG=INFO
PROJECT_NAME=dexter-core
##LOG=DEBUG
LOGAT=1000
CLI=it.cnr.isti.hpc.dexter.cli
E_BADARGS=65
JAVA="java $XMX -Dlogat=$LOGAT -Dlog=$LOG -cp ./target/$PROJECT_NAME-$VERSION-jar-with-dependencies.jar "

DATA_DIR=data
GRAPH_DIR=$DATA_DIR/graph
TITLE_FILE=$DATA_DIR/title-redirect-id.tsv
DEXTER_TMP_DIR=$DATA_DIR/tmp

mkdir -p $DEXTER_TMP_DIR

WIKI_JSON_DUMP=../enwiki-latest-pages-articles.json.gz
TMP=$DEXTER_TMP_DIR/tmp
TTMP=$DEXTER_TMP_DIR/tmp1
TTTMP=$DEXTER_TMP_DIR/tmp2


SPOT_FOLDER=$DATA_DIR/spot

SPOT=$SPOT_FOLDER/spot-src-target.tsv
SPOT_DOC_FREQ=$SPOT_FOLDER/spots-doc-freq.tsv
SPOT_INDEX_DIR=$DATA_DIR/spot-index
SPOT_FILE=$SPOT_FOLDER/spots.tsv.gz
SPOT_HASHES=$SPOT_FOLDER/spot-hashes.tsv.gz

MW_DIR=$DATA_DIR/wikiminer


IN_EDGES=$GRAPH_DIR/incoming-edges.tsv.gz
OUT_EDGES=$GRAPH_DIR/outcoming-edges.tsv.gz

BENCHMARK_DIR=$DATA_DIR/benchmark

export TMPDIR=$DEXTER_TMP_DIR



export LC_ALL=C
