FROM diozero/diozero-cc

ARG HOME_DIR=/home/${RUN_AS_USER}

RUN sudo apt-get -y install python3-distutils
RUN git clone https://github.com/joan2937/pigpio.git --depth 1
WORKDIR ${HOME_DIR}/pigpio

# aarch64
RUN make clean && make CROSS_PREFIX=aarch64-linux-gnu- ARCH=armv8-a CFLAGS="-march=armv8-a" PREFIX=/usr \
		&& sudo make install PREFIX=/usr
RUN sudo cp libpigpio.so /usr/lib/aarch64-linux-gnu/.
# armv7
RUN make clean && make CROSS_PREFIX=arm-linux-gnueabihf- ARCH=armv7 CFLAGS="-march=armv8-a" PREFIX=/usr \
		&& sudo make install PREFIX=/usr
RUN sudo cp libpigpio.so /usr/lib/arm-linux-gnueabihf/.
# armv6 - building this last out of paranoia to ensure PATH isn't modified
RUN PATH=${PI_GCC_TARGET_DIR}/bin:${PATH} \
		&& make clean && make CROSS_PREFIX=arm-linux-gnueabihf- ARCH=armv6 \
			CFLAGS="-march=armv6 -mfpu=vfp -mfloat-abi=hard -I${PI_GCC_TARGET_DIR}/arm-linux-gnueabihf/include -I/usr/include" \
			PREFIX=${PI_GCC_TARGET_DIR} \
		&& sudo make install DESTDIR=$PI_GCC_TARGET_DIR/arm-linux-gnueabihf
RUN sudo cp libpigpio.so $PI_GCC_TARGET_DIR/arm-linux-gnueabihf/lib/.
