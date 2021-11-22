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

import uk.pigpioj.PigpioCallback;
import uk.pigpioj.PigpioConstants;
import uk.pigpioj.PigpioInterface;
import uk.pigpioj.PigpioJ;

public class PigpioInputTest implements PigpioCallback {
	public static void main(String[] args) {
		int pin = 12;
		int delay_s = 20;

		try (PigpioInterface pigpio_impl = PigpioJ.autoDetectedImplementation()) {
			int version = pigpio_impl.getVersion();
			System.out.println("version: " + version);
			if (version < 0) {
				throw new IOException("Error in pigpio_impl.initialise()");
			}

			int rc = pigpio_impl.setMode(pin, PigpioConstants.MODE_PI_INPUT);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setMode()");
			}
			rc = pigpio_impl.setPullUpDown(pin, PigpioConstants.PI_PUD_UP);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setPullUpDown()");
			}
			rc = pigpio_impl.enableListener(pin, PigpioConstants.EITHER_EDGE, new PigpioInputTest());
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setISRFunc()");
			}
			System.out.println("Sleeping for " + delay_s + "s");
			Thread.sleep(delay_s * 1000);

			rc = pigpio_impl.disableListener(pin);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setISRFunc()");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void callback(int pin, boolean value, long epochTime, long nanoTime) {
		System.out.println("Callback(" + pin + ", " + value + ", " + epochTime + ", " + nanoTime + ")");
	}
}
