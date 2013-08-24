#!/usr/bin/env bash 

mvn install:install-file -Dfile=jdbm-2.4.jar -DgroupId=org.apache.jdbm -DartifactId=jdbm -Dversion=2.4 -Dpackaging=jar
