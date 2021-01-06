# About

pigpioj is a Java wrapper around the excellent Raspberry Pi C library [pigpio](http://abyz.me.uk/rpi/pigpio/).
It supports two modes for interfacing with pigpio; JNI (the default) and Sockets.
Note that the primary driver for developing pigpioj was to provide an optimised hardware interface
library for the platform and device independent library [diozero](http://www.diozero.com).

Please make 

## JNI

The optimisations within pigpio (using /dev/mem) unfortunately requires root access.
Because of this all pigpioj applications that use the default JNI mode must be run as root.
In addition, the pigpio shared library must be installed on the Raspberry Pi; it can be installed by running:
```
sudo apt update && sudo apt -y install libpigpio1 libpigpiod-if2-1
```

### Building the Native Library

Make sure that the pigpio development tools are installed:
```
sudo apt update && sudo apt -y install libpigpiod-if-dev
```

Copy the pigpioj-native source files to the Raspberry Pi and compile with `make`.
This will produce a libpigpioj.so shared object library that needs to be made available on the classpath at runtime.
On 32-bit ARM platforms, pigpioj will attempt to load libpigpioj-arm.so; on 64-bit ARM platforms it will
look for libpigpioj-aarch64.so.

The Ubuntu 64-bit operating system didn't include the pigpio packages, hence must be built from source code.
Clone the [pigpio GitHub repository](https://github.com/joan2937/pigpio), and build and install it.

## Sockets

Sockets mode is enabled by running with the command line flag `PIGPIOD_HOST`, e.g.
```
java -DPIGPIOD_HOST=<<raspberry-pi-hostname-or-ip-address>> -cp pigpioj-2.5.3.jar:<<your_app.jar>> <<your_main_class>>
```

The pigpiod daemon must be running on the Raspberry Pi for this to work.

```
sudo apt update && sudo apt -y install pigpio pigpio-tools pigpiod
sudo systemctl enable pigpiod.service
sudo systemctl start pigpiod.service
```

One key benefit of sockets mode is to enable remote communication - this means that you can use the exact
same APIs to run applications on another machine, including a any laptop or desktop that can run Java.

For security reasons, the pigpiod daemon process can be limited to allow only local connections.
Check the `ExecStart` parameter in `/etc/systemd/system/pigpiod.service.d/public.conf`.
If it has the `-l` option then it will only allow connections from the local machine, e.g.:
```
ExecStart=/usr/bin/pigpiod -l
```

To change this behaviour, simply remove the `-l` parameter from this file and restart the process:
```
sudo systemctl restart pigpiod.service
```
