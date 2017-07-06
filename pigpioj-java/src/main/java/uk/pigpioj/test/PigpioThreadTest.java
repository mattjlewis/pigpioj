package uk.pigpioj.test;

import uk.pigpioj.PigpioInterface;
import uk.pigpioj.PigpioJ;

public class PigpioThreadTest {
	private static final int THREAD_COUNT = 10;
	private static final int ITERATIONS = 10_000_000;
	
	public static void main(String[] args) {
		try (PigpioInterface pigpio_impl = PigpioJ.getImplementation()) {
			int version = pigpio_impl.getVersion();
			if (version < 0) {
				System.out.println("Error initialising piogpio " + version);
				return;
			}
			System.out.println("version: " + version);
			
			int led_pin = 12;
			
			for (int i=0; i<THREAD_COUNT; i++) {
				Runnable runnable = () -> onOffLoop(pigpio_impl, led_pin);
				Thread t = new Thread(runnable);
				t.setName("Test thread #" + i);
				t.start();
			}
		}
	}
	
	private static void onOffLoop(PigpioInterface pigpioImpl, int pin) {
		System.out.println("Thread " + Thread.currentThread().getName() + " started");
		int rc;
		for (int i=0; i<ITERATIONS; i++) {
			rc = pigpioImpl.write(pin, true);
			if (rc < 0) {
				System.out.println("Error turning on pin " + rc);
				break;
			}
			rc = pigpioImpl.write(pin, false);
			if (rc < 0) {
				System.out.println("Error turning off pin " + rc);
				break;
			}
		}
		System.out.println("Thread " + Thread.currentThread().getName() + " finished");
	}
}
