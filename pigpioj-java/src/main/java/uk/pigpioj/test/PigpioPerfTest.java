package com.diozero.pigpioj.test;

/*
 * #%L
 * pigpio Java wrapper
 * %%
 * Copyright (C) 2016 diozero
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.io.IOException;

import com.diozero.pigpioj.PigpioGpio;

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
			if (version < 0) {
				throw new IOException("Error in PigpioGpio.initialise()");
			}
			
			int rc = PigpioGpio.setMode(pin, PigpioGpio.MODE_PI_OUTPUT);
			if (rc < 0) {
				throw new IOException("Error in PigpioGpio.setMode()");
			}

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
