package uk.pigpioj.test;

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

import uk.pigpioj.PigpioConstants;
import uk.pigpioj.PigpioInterface;
import uk.pigpioj.PigpioJ;

public class PigpioPerfTest {
	private static final int DEFAULT_ITERATIONS = 5_000_000;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: " + PigpioPerfTest.class.getName() + " <pin-number> [<iterations>]");
			System.exit(1);
		}

		final int pin = Integer.parseInt(args[0]);
		final int iterations = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_ITERATIONS;

		try (PigpioInterface pigpio_impl = PigpioJ.autoDetectedImplementation()) {
			int version = pigpio_impl.getVersion();
			System.out.println("version: " + version);
			if (version < 0) {
				throw new IOException("Error in pigpio_impl.getVersion()");
			}

			int rc = pigpio_impl.setMode(pin, PigpioConstants.MODE_PI_OUTPUT);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setMode()");
			}

			for (int j = 0; j < 5; j++) {
				long start_ms = System.currentTimeMillis();
				for (int i = 0; i < iterations; i++) {
					pigpio_impl.write(pin, true);
					pigpio_impl.write(pin, false);
				}
				long duration_ms = (System.currentTimeMillis() - start_ms);
				double frequency = iterations / (duration_ms / 1000.0);

				System.out.format("Duration for %d iterations: %.4fs, frequency: %.0f%n", Integer.valueOf(iterations),
						Float.valueOf((duration_ms) / 1000f), Double.valueOf(frequency));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
