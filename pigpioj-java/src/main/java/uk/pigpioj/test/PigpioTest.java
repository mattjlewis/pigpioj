package uk.pigpioj.test;

import uk.pigpioj.PigpioConstants;
import uk.pigpioj.PigpioInterface;
import uk.pigpioj.PigpioJ;

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
