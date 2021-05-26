# About

pigpioj is a Java wrapper around the excellent Raspberry Pi C library [pigpio](http://abyz.me.uk/rpi/pigpio/).
It supports two modes for interfacing with pigpio; JNI (the default) and Sockets.
Note that the primary driver for developing pigpioj was to provide an optimised hardware interface
library for the platform and device independent library [diozero](http://www.diozero.com).

Make sure pigpio is installed on your Raspberry Pi:

```shell
sudo apt update && sudo apt -y install pigpio pigpiod pigpio-tools
```

## JNI

The optimisations within pigpio (using /dev/mem) unfortunately requires root access.
Because of this all pigpioj applications that use the default JNI mode must be run as root.
In addition, the pigpio shared library must be installed on the Raspberry Pi; it can be installed by running:

```shell
sudo apt update && sudo apt -y install libpigpio1 libpigpiod-if2-1
```

In addition, the pigpiod daemon process must not be running:

```shell
sudo systemctl stop pigpiod.service
```

### Building the Native Library

Make sure that the pigpio development tools are installed:

```shell
sudo apt update && sudo apt -y install libpigpiod-if-dev
```

Copy the pigpioj-native source files to the Raspberry Pi and compile with `make`.
This will produce a libpigpioj.so shared object library that needs to be made available on the classpath at runtime.
On 32-bit ARM platforms, pigpioj will attempt to load libpigpioj-arm.so; on 64-bit ARM platforms it will
look for libpigpioj-aarch64.so.

At the time of writing, the Ubuntu 64-bit operating system for Raspberry Pi didn't include the
pigpio packages, hence must be built from source code.
Clone the [pigpio GitHub repository](https://github.com/joan2937/pigpio), and build and install it.

The shared library mode requires a small JNI shared object lib to also be loaded.
This library is packaged in the pgpioj JAR file and is loaded automatically when you
call `uk.pigpioj.PigpioJ.getImplementation()`.

## Sockets

By default pigpioj uses shared library local access; to switch to the remote socket mode you
must set the PIGPIOD_HOST variable, either via environment property or Java command line property.
Examples:

Command line:

```shell
java -DPIGPIOD_HOST=«your-pigpiod-host» -cp pigpioj-2.5.4.jar:<<your-app.jar>> <<your-main-class>>
```

Sockets mode is enabled by setting the `PIGPIOD_HOST` variable, either via the command
line or as an environment variable.

Command line:

```shell
java -DPIGPIOD_HOST=«raspberry-pi-hostname-or-ip-address» -cp pigpioj-2.5.4.jar:<<your-app.jar>> <<your-main-class>>
```

Environment variable:

```shell
export PIGPIOD_HOST=<raspberry-pi-hostname-or-ip-address>>
java -cp pigpioj-2.5.4.jar:<<your-app.jar>> <<your-main-class>>
```

The pigpiod daemon must be running on the Raspberry Pi for this to work.

```shell
sudo apt update && sudo apt -y install pigpio pigpio-tools pigpiod
sudo systemctl enable pigpiod.service
sudo systemctl start pigpiod.service
```

Note that you can also change the default pigpiod port if need be via the `PIGPIOD_PORT` property.

One key benefit of sockets mode is to enable remote communication - this means that you can use the exact
same APIs to run applications on another machine, including a any laptop or desktop that can run Java.

For security reasons, the pigpiod daemon process can be limited to allow only local connections.
Check the `ExecStart` parameter in `/etc/systemd/system/pigpiod.service.d/public.conf`.
If it has the `-l` option then it will only allow connections from the local machine, e.g.:

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
