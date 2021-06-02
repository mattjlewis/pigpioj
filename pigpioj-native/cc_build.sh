#!/bin/sh

cd src/main/native
LIB_DIR=../../../lib

# aarch64
make clean && make CROSS_PREFIX=aarch64-linux-gnu- ARCH=armv8-a
if [ $? -eq 0 ]; then
  TARGET=${LIB_DIR}/linux-aarch64 && mkdir -p ${TARGET} && mv libpigpioj.so ${TARGET}/.
fi

# armv7
make clean && make CROSS_PREFIX=arm-linux-gnueabihf- ARCH=armv7 CC_CFLAGS="-mfpu=vfp -mfloat-abi=hard"
if [ $? -eq 0 ]; then
  TARGET=${LIB_DIR}/linux-armv7 && mkdir -p ${TARGET} && mv libpigpioj.so ${TARGET}/.
fi

# Finally build armv6 to be extra sure that PATH has no reference to the Pi armv6 cross compiler
OLD_PATH=${PATH}
PATH=${PI_GCC_TARGET_DIR}/bin:${PATH} && make clean && make CROSS_PREFIX=arm-linux-gnueabihf- ARCH=armv6 CC_CFLAGS="-mfpu=vfp -mfloat-abi=hard"
if [ $? -eq 0 ]; then
  TARGET=${LIB_DIR}/linux-armv6 && mkdir -p ${TARGET} && mv libpigpioj.so ${TARGET}/.
fi
PATH=${OLD_PATH}

cd ../../..
