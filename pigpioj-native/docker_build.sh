#!/bin/sh

docker run --rm -w /pigpioj-native \
  -v "$(pwd):/pigpioj-native" diozero/pigpioj-cc sh -c ./cc_build.sh

if [ $? -eq 0 ]; then
  cp -R lib/linux-aarch64/libpigpioj.so ../pigpioj-java/src/main/resources/lib/libpigpioj-aarch64.so
  cp -R lib/linux-armv7/libpigpioj.so ../pigpioj-java/src/main/resources/lib/libpigpioj-armv7.so
  cp -R lib/linux-armv6/libpigpioj.so ../pigpioj-java/src/main/resources/lib/libpigpioj-armv6.so
  cp -R lib/linux-armv6/libpigpioj.so ../pigpioj-java/src/main/resources/lib/libpigpioj-arm.so
fi
