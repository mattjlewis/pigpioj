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

public class PigpioOutputTest {
	public static void main(String[] args) {
		int pin = 18;
		try (PigpioInterface pigpio_impl = PigpioJ.autoDetectedImplementation()) {
			int version = pigpio_impl.getVersion();
			System.out.println("version: " + version);
			if (version < 0) {
				throw new IOException("Error in pigpio_impl.initialise()");
			}

			int rc = pigpio_impl.setMode(pin, PigpioConstants.MODE_PI_OUTPUT);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setMode()");
			}

			for (int i = 0; i < 5; i++) {
				System.out.println("on");
				rc = pigpio_impl.write(pin, true);
				if (rc < 0) {
					throw new IOException("Error in pigpio_impl.write()");
				}
				Thread.sleep(500);

				System.out.println("off");
				rc = pigpio_impl.write(pin, false);
				if (rc < 0) {
					throw new IOException("Error in pigpio_impl.write()");
				}
				Thread.sleep(500);
			}
			int pwm_max = 255;

			System.out.println("pwm full on");
			rc = pigpio_impl.setPWMDutyCycle(pin, pwm_max);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setPWMDutyCycle()");
			}
			Thread.sleep(500);

			System.out.println("pwm full off");
			rc = pigpio_impl.setPWMDutyCycle(pin, 0);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setPWMDutyCycle()");
			}
			Thread.sleep(500);

			System.out.println("pwm 25% off");
			rc = pigpio_impl.setPWMDutyCycle(pin, Math.round(pwm_max * .25f));
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setPWMDutyCycle()");
			}
			Thread.sleep(500);

			System.out.println("pwm 50%");
			rc = pigpio_impl.setPWMDutyCycle(pin, Math.round(pwm_max * .50f));
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setPWMDutyCycle()");
			}
			Thread.sleep(500);

			System.out.println("pwm 75%");
			rc = pigpio_impl.setPWMDutyCycle(pin, Math.round(pwm_max * .75f));
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setPWMDutyCycle()");
			}
			Thread.sleep(500);

			System.out.println("pwm full off");
			rc = pigpio_impl.setPWMDutyCycle(pin, 0);
			if (rc < 0) {
				throw new IOException("Error in pigpio_impl.setPWMDutyCycle()");
			}
			Thread.sleep(500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
