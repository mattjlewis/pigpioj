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

import com.diozero.pigpioj.PigpioGpio;

public class ServoTest {
	private static final float TOWERPRO_SG5010_MIN_MS = 1;
	private static final float TOWERPRO_SG5010_MAX_MS = 2;
	private static final float TOWERPRO_SG90_MIN_MS = 0.6f;
	private static final float TOWERPRO_SG90_MAX_MS = 2.5f;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.format("Usage: %s <BCM pin number>%n", ServoTest.class.getName());
			System.exit(1);
		}
		
		int pin_number = Integer.parseInt(args[0]);
		
		PigpioGpio.initialise();
		
		test(pin_number, TOWERPRO_SG90_MIN_MS, TOWERPRO_SG90_MAX_MS);
		
		PigpioGpio.terminate();
	}
	
	private static void test(int pinNumber, float min, float max) {
		PigpioGpio.setPWMFrequency(pinNumber, 50);
		PigpioGpio.setPWMRange(pinNumber, PigpioGpio.getPWMRealRange(pinNumber));
		
		{
			int pwm_freq = PigpioGpio.getPWMFrequency(pinNumber);
			int range = PigpioGpio.getPWMRange(pinNumber);
			int real_range = PigpioGpio.getPWMRealRange(pinNumber);
			PigpioGpio.setPWMRange(pinNumber, PigpioGpio.getPWMRealRange(pinNumber));
			int new_pwm_freq = PigpioGpio.getPWMFrequency(pinNumber);
			int new_range = PigpioGpio.getPWMRange(pinNumber);
			int new_real_range = PigpioGpio.getPWMRealRange(pinNumber);
			// pwm_freq=800, range=255, real_range=250, new_pwm_freq=800, new_range=250, new_real_range=250
			System.out.format("pwm_freq=%d, range=%d, real_range=%d, new_pwm_freq=%d, new_range=%d, new_real_range=%d%n",
					Integer.valueOf(pwm_freq), Integer.valueOf(range), Integer.valueOf(real_range),
					Integer.valueOf(new_pwm_freq), Integer.valueOf(new_range), Integer.valueOf(new_real_range));
		}
		
		int min_us = (int)(min * 1000);
		int max_us = (int)(max * 1000);
		int mid_us = (min_us + max_us) / 2;
		System.out.format("min_us=%d, mid_us=%d, max_us=%d%n", Integer.valueOf(min_us),
				Integer.valueOf(mid_us), Integer.valueOf(max_us));
		
		System.out.format("Mid (%dus)%n", Integer.valueOf(mid_us));
		PigpioGpio.setServoPulseWidth(pinNumber, mid_us);
		sleepMillis(1000);
		
		System.out.format("Mid (%dus) to Max (%dus)%n", Integer.valueOf(mid_us), Integer.valueOf(max_us));
		for (int i=mid_us; i<max_us; i+=5) {
			PigpioGpio.setServoPulseWidth(pinNumber, i);
			sleepMillis(10);
		}
		System.out.format("Max (%dus) to Min (%dus)%n", Integer.valueOf(max_us), Integer.valueOf(min_us));
		for (int i=max_us; i>min_us; i-=5) {
			PigpioGpio.setServoPulseWidth(pinNumber, i);
			sleepMillis(10);
		}
		System.out.format("Min (%dus) to Mid (%dus)%n", Integer.valueOf(min_us), Integer.valueOf(mid_us));
		for (int i=min_us; i<mid_us; i+=5) {
			PigpioGpio.setServoPulseWidth(pinNumber, i);
			sleepMillis(10);
		}
		
		int range = PigpioGpio.getPWMRange(pinNumber);
		int real_range = PigpioGpio.getPWMRealRange(pinNumber);
		int sample_rate_us = 1_000_000/PigpioGpio.getPWMFrequency(pinNumber)/PigpioGpio.getPWMRange(pinNumber);
		int min_dc = min_us / sample_rate_us;
		int mid_dc = mid_us / sample_rate_us;
		int max_dc = max_us / sample_rate_us;
		System.out.format("freq=%d, range=%d, real_range=%d, sample_rate_us=%d, min_dc=%d, mid_dc=%d, max_dc=%d%n",
				Integer.valueOf(PigpioGpio.getPWMFrequency(pinNumber)), Integer.valueOf(range), Integer.valueOf(real_range),
				Integer.valueOf(sample_rate_us), Integer.valueOf(min_dc), Integer.valueOf(mid_dc), Integer.valueOf(max_dc));
		
		System.out.format("Mid DC (%d)%n", Integer.valueOf(mid_dc));
		PigpioGpio.setPWMDutyCycle(pinNumber, mid_dc);
		sleepMillis(1000);
		
		System.out.format("Mid DC (%d) to Max DC (%d)%n", Integer.valueOf(mid_dc), Integer.valueOf(max_dc));
		for (int i=mid_dc; i<max_dc; i++) {
			PigpioGpio.setPWMDutyCycle(pinNumber, i);
			sleepMillis(10);
		}
		System.out.format("Max DC (%d) to Min DC (%d)%n", Integer.valueOf(max_dc), Integer.valueOf(min_dc));
		for (int i=max_dc; i>min_dc; i--) {
			PigpioGpio.setPWMDutyCycle(pinNumber, i);
			sleepMillis(10);
		}
		System.out.format("Min DC (%d) to Mid DC (%d)%n", Integer.valueOf(min_dc), Integer.valueOf(mid_dc));
		for (int i=min_dc; i<mid_dc; i++) {
			PigpioGpio.setPWMDutyCycle(pinNumber, i);
			sleepMillis(10);
		}
	}
	
	public static void sleepMillis(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) { }
	}
}
