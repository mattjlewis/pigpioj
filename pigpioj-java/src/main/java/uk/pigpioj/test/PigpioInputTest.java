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
import uk.pigpioj.PigpioGpio;

public class PigpioInputTest implements PigpioCallback {
	public static void main(String[] args) {
		int pin = 12;
		int timeout = -1;
		int delay_s = 20;
		try {
			int version = PigpioGpio.initialise();
			System.out.println("version: " + version);
			if (version < 0) {
				throw new IOException("Error in PigpioGpio.initialise()");
			}
			
			int rc = PigpioGpio.setMode(pin, PigpioConstants.MODE_PI_INPUT);
			if (rc < 0) {
				throw new IOException("Error in PigpioGpio.setMode()");
			}
			rc = PigpioGpio.setPullUpDown(pin, PigpioConstants.PI_PUD_UP);
			if (rc < 0) {
				throw new IOException("Error in PigpioGpio.setPullUpDown()");
			}
			rc = PigpioGpio.setISRFunc(pin, PigpioConstants.EITHER_EDGE, timeout, new PigpioInputTest());
			if (rc < 0) {
				throw new IOException("Error in PigpioGpio.setISRFunc()");
			}
			System.out.println("Sleeping for " + delay_s + "s");
			Thread.sleep(delay_s*1000);
			
			rc = PigpioGpio.setISRFunc(pin, PigpioConstants.EITHER_EDGE, timeout, null);
			if (rc < 0) {
				throw new IOException("Error in PigpioGpio.setISRFunc()");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PigpioGpio.terminate();
		}
	}

	@Override
	public void callback(int pin, boolean value, long epochTime, long nanoTime) {
		System.out.println("Callback(" + pin + ", " + value + ", " + epochTime + ", " + nanoTime + ")");
	}
}
