#!/bin/sh

LIB_NAME=pigpioj
JAVA_PROJECT=../${LIB_NAME}-java

docker run --rm -w /${LIB_NAME} -v "$(pwd):/${LIB_NAME}" diozero/pigpioj-cc sh -c ./cc_build.sh

if [ $? -eq 0 ]; then
  cp -R lib/* ${JAVA_PROJECT}/src/main/resources/lib/.
fi
