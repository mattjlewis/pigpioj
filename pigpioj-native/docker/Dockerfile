FROM diozero/diozero-cc

ARG HOME_DIR=/home/${RUN_AS_USER}

RUN sudo apt-get -y install python3-distutils
WORKDIR ${HOME_DIR}
RUN git clone https://github.com/joan2937/pigpio.git --depth 1
WORKDIR ${HOME_DIR}/pigpio

# aarch64
RUN make clean && make CROSS_PREFIX=aarch64-linux-gnu- CFLAGS="-march=armv8-a" prefix=/usr \
		&& sudo make install prefix=/usr
RUN sudo cp libpigpio.so /usr/lib/aarch64-linux-gnu/.

# armv7
RUN make clean && make CROSS_PREFIX=arm-linux-gnueabihf- CFLAGS="-march=armv7" prefix=/usr \
		&& sudo make install prefix=/usr
RUN sudo cp libpigpio.so /usr/lib/arm-linux-gnueabihf/.

# armv6 - building this last out of paranoia to ensure PATH isn't modified
# FIXME Work out why -I/usr/include is required!
RUN PATH=${PI_GCC_TARGET_DIR}/bin:${PATH} \
		&& make clean && make CROSS_PREFIX=arm-linux-gnueabihf- \
			CFLAGS="-march=armv6 -mfpu=vfp -mfloat-abi=hard" prefix=${PI_GCC_TARGET_DIR}/arm-linux-gnueabihf \
		&& sudo make install prefix=${PI_GCC_TARGET_DIR}/arm-linux-gnueabihf
RUN sudo cp libpigpio.so ${PI_GCC_TARGET_DIR}/arm-linux-gnueabihf/lib/.

WORKDIR ${HOME_DIR}
RUN sudo rm -rf pigpio
