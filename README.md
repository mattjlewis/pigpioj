# About

pigpioj is a Java wrapper around the excellent Raspberry Pi C library [pigpio](http://abyz.me.uk/rpi/pigpio/).
It supports two modes for interfacing with pigpio; JNI (the default) and Sockets.
Note that the primary driver for developing pigpioj was to provide an optimised hardware interface
library for the platform and device independent library [diozero](http://www.diozero.com).

Make sure pigpio is installed on your Raspberry Pi:

```shell
sudo apt update && sudo apt -y install pigpio pigpiod pigpio-tools
```

pigpioj has two modes of operation:

1. JNI
1. Remote sockets

## JNI

The JNI mode requires a small system library to be loaded at runtime that allows Java to invoke the
pigpio C interface. This library is packaged in the pigpioj JAR file and is loaded automatically
when `uk.pigpioj.PigpioJ.getImplementation()` is called.

pigpioj comes bundled with native libraries that are compiled for AArch64, ARMv7 and ARMv7 CPU
architectures which are bundled within the pigpioj JAR file itself. At startup, pigpioj will detect
the CPU architecture and load the appropriate JNI library.

The optimisations within pigpio (using /dev/mem) unfortunately requires root access.
Because of this all pigpioj applications that use the default JNI mode must be run as root.
In addition, the pigpio shared library must be installed on the Raspberry Pi; it can be installed by
running:

```shell
sudo apt update && sudo apt -y install libpigpio1 libpigpiod-if2-1
```

In addition, the pigpiod daemon process must not be running:

```shell
sudo systemctl stop pigpiod.service
```

The pigpiod daemon process can be disabled from automatically starting on a reboot:

```shell
sudo systemctl disable pigpiod.service
```

### Running on Ubuntu

At the time of writing, the Ubuntu 64-bit operating system for Raspberry Pi didn't include the
pigpio packages, hence must be built from source code.
Clone the [pigpio GitHub repository](https://github.com/joan2937/pigpio), build and install it.

```
sudo apt update && sudo apt -y install git make gcc
git clone https://github.com/joan2937/pigpio.git --depth=1
cd pigpio
make
sudo make install
```

### PWM / PCM / I2S / pigpio Conflicts

As per [this pigpio issue](https://github.com/joan2937/pigpio/issues/87), by default pigpio uses the
Pi's PCM hardware to time DMA transfers. pigpio can be configured to use the PWM hardware instead of
the PCM hardware by calling `gpioCfgClock` before `gpioInitialise`. To enable this in pigpioj, the
following variables must be set, either via command line or as environment variables:

* PIGPIO_CLOCK_CFG_MICROS
* PIGPIO_CLOCK_CFG_PERIPHERAL

### Building the Native Library

If you need to compile the JNI library yourself you can do so either using a physical Raspberry Pi,
or via the pigpioj Docker based cross compiler.

#### Building via Docker

Clone pigpioj, build the Docker image and initiate the docker build process:

```
git clone git@github.com:mattjlewis/pigpioj.git
cd pigpioj/pigpioj-native/docker
docker build -t diozero/pigpioj-cc .
cd ..
./docker_build.sh
```

Once successful, this will result in the following folder structure:

```
lib
├── linux-aarch64
│   └── libpigpioj.so
├── linux-armv6
│   └── libpigpioj.so
└── linux-armv7
    └── libpigpioj.so
```

#### Building on a Raspberry Pi

Make sure that the pigpio development tools are installed:

```shell
sudo apt update && sudo apt -y install libpigpiod-if-dev
```

Unfortunately `pigpio.h` is not included in any of the Raspberry Pi OS packages, first of all run
this command in the Pi user's home directory (the Makefile assumes the header files are in
`/home/pi/pigpio`):

```
sudo apt update && sudo apt -y install git make gcc
git clone https://github.com/joan2937/pigpio.git --depth=1
cd pigpio
make
```

Copy the pigpioj-native source files to the Raspberry Pi and compile with `make`.

```
git clone git@github.com:mattjlewis/pigpioj.git
cd pigpioj/pigpioj-native/src/main/native
make
```

This will produce a libpigpioj.so shared object library that needs to be made available on the
classpath at runtime.

## Sockets

By default pigpioj uses JNI mode; to switch to the remote socket mode you must set the `PIGPIOD_HOST`
variable, either via environment property or Java command line property. Examples:

Command line:

```shell
java -DPIGPIOD_HOST=«your-pigpiod-host» -cp pigpioj-2.5.11.jar:«your-app.jar» «your-main-class»
```

Environment variable:

```shell
export PIGPIOD_HOST=<your-pigpiod-host»
java -cp pigpioj-2.5.11.jar:«your-app.jar» «your-main-class»
```

The pigpiod daemon must be running on the Raspberry Pi for this to work.

```shell
sudo apt update && sudo apt -y install pigpio pigpio-tools pigpiod
sudo systemctl enable pigpiod.service
sudo systemctl start pigpiod.service
```

Note that you can also change the default pigpiod port if need be via the `PIGPIOD_PORT` property.

One key benefit of sockets mode is to enable remote communication - this means that you can use the
exact same APIs to run applications on another machine, including a any laptop or desktop that can
run Java.

For security reasons, the pigpiod daemon process can be limited to allow only local connections.
Check the `ExecStart` parameter in `/etc/systemd/system/pigpiod.service.d/public.conf`. If it has the `-l`
option then it will only allow connections from the local machine, e.g.:

```
ExecStart=/usr/bin/pigpiod -l
```

To change this behaviour, simply remove the `-l` parameter from this file and restart the process:

```shell
sudo systemctl restart pigpiod.service
```

If the file doesn't exist, run the `raspi-config` application and choose "3 Interface Options",
"P8 Remote GPIO" and select "Yes". This will make the appropriate changes and create that file if
it didn't already exist.

## Example Application

```java
import uk.pigpioj.*;

public class PigpioTest {
	public static void main(String[] args) throws InterruptedException {
		int gpio = 16;
		try (PigpioInterface pigpio_impl = PigpioJ.getImplementation()) {
			pigpio_impl.setMode(gpio, PigpioConstants.MODE_PI_OUTPUT);
			pigpio_impl.write(gpio, true);
			Thread.sleep(1000);
			pigpio_impl.write(gpio, false);
			Thread.sleep(1000);
		}
	}
}
```

This library is used as a provider for the Raspberry Pi by the platform agnostic
diozero Java library [diozero](http://www.diozero.com).
