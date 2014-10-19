#!/usr/bin/env bash

export MAVEN_OPTS="-Xmx3000m -XX:PermSize=128m -XX:-UseGCOverheadLimit"

mvn jetty:run
