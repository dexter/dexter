#!/usr/bin/env bash

export MAVEN_OPTS="-Xmx4000m -XX:PermSize=128m -XX:-UseGCOverheadLimit"

mvn jetty:run
