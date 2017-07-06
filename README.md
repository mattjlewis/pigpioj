# pigpioj

pigpioj is a Java wrapper for [pigpio](http://abyz.co.uk/rpi/pigpio/) on the Raspberry Pi that supports access locally via the libpigpio.so shared library as well as via a socket connection to the pigpiod daemon process.

By default pigpioj uses shared library local access; to switch to the remote socket mode you must set the PIGPIOD_HOST variable, either via environment property or Java command line property. Examples:

Environment variable:
```shell
export PIGPIOD_HOST=<your-pigpiod-host>
java -cp pigpioj-2.1.jar:<your-jar> <your-class>
```

Command line:
```shell
java -cp pigpioj-2.1.jar:<your-jar> -DPIGPIOD_HOST=<your-pigpiod-host> <your-class>
```

Note you can also change the default pigpiod port if need be via the PIGPIOD_PORT property. Remote access to the pigpiod daemon can be achieved by starting it without the "-l" flag. As there is no security in this remote socket protocol by default it is installed to listen on localhost only.

The shared library mode requires a small JNI shared object lib to also be loaded. This library is packaged in the pgpioj JAR file and should be loaded automatically when you call ```uk.pigpioj.PigpioJ.getImplementation()```.

Example code:

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

This library is used as a provider for the Raspberry Pi by the platform agnostic Device I/O Java library [diozero](http://www.diozero.com).
