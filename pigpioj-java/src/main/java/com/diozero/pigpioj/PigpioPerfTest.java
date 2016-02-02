package com.diozero.pigpioj;

import java.io.IOException;

public class PigpioPerfTest {
	private static final int DEFAULT_ITERATIONS = 5_000_000;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: " + PigpioPerfTest.class.getName() + " <pin-number> [<iterations>]");
			System.exit(1);
		}
		
		final int pin = Integer.parseInt(args[0]);
		final int iterations = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_ITERATIONS;
		
		try {
			int version = PigpioGpio.initialise();
			System.out.println("version: " + version);
			
			PigpioGpio.setMode(pin, PigpioGpio.MODE_PI_OUTPUT);

			for (int j=0; j<5; j++) {
				long start_nano = System.nanoTime();
				for (int i=0; i<iterations; i++) {
					PigpioGpio.write(pin, true);
					PigpioGpio.write(pin, false);
				}
				long duration_ns = (System.nanoTime() - start_nano);
				System.out.format("Duration for %d iterations: %.4fs%n",
						Integer.valueOf(iterations), Float.valueOf(((float)duration_ns) / 1000 / 1000 / 1000));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PigpioGpio.terminate();
		}
	}
}
