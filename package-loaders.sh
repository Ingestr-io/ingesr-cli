#!/bin/bash

mkdir -p target/classes/loader-templates

(cd src/loader-templates/java-maven/ && zip -r ../../../target/classes/loader-templates/bootstrap-java-maven.zip ./)

