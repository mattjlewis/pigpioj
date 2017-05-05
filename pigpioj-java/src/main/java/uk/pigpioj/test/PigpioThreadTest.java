package uk.pigpioj.test;

import uk.pigpioj.PigpioGpio;

public class PigpioThreadTest {
	private static final int THREAD_COUNT = 10;
	private static final int ITERATIONS = 10_000_000;
	
	public static void main(String[] args) {
		int version = PigpioGpio.initialise();
		if (version < 0) {
			System.out.println("Error initialising piogpio " + version);
			return;
		}
		System.out.println("version: " + version);
		
		int led_pin = 12;
		
		for (int i=0; i<THREAD_COUNT; i++) {
			Runnable runnable = () -> onOffLoop(led_pin);
			Thread t = new Thread(runnable);
			t.setName("Test thread #" + i);
			t.start();
		}
	}
	
	private static void onOffLoop(int pin) {
		System.out.println("Thread " + Thread.currentThread().getName() + " started");
		int rc;
		for (int i=0; i<ITERATIONS; i++) {
			rc = PigpioGpio.write(pin, true);
			if (rc < 0) {
				System.out.println("Error turning on pin " + rc);
				break;
			}
			rc = PigpioGpio.write(pin, false);
			if (rc < 0) {
				System.out.println("Error turning off pin " + rc);
				break;
			}
		}
		System.out.println("Thread " + Thread.currentThread().getName() + " finished");
	}
}
