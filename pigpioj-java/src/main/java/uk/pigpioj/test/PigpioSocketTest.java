package uk.pigpioj.test;

import uk.pigpioj.PigpioConstants;
import uk.pigpioj.PigpioSocket;

public class PigpioSocketTest {
	public static void main(String[] args) {
		try (PigpioSocket pigpiod = new PigpioSocket()) {
			pigpiod.connect("shirley.home", 8888);
			
			int delay = 500;
			int small_delay = 20;
			int gpio = 18;
			int blink_iterations = 3;
			int pwm_frequency = 50;

			sleep(delay);
			
			int version = pigpiod.getVersion();
			System.out.println("pigpio version: " + version);
			int hw_revision = pigpiod.getHardwareRevision();
			System.out.println("Hardware revision: " + Integer.toHexString(hw_revision));
			
			int mode = pigpiod.getMode(gpio);
			System.out.println("getMode(" + gpio + "): " + mode);
			
			if (pigpiod.setMode(gpio, PigpioConstants.MODE_PI_INPUT) < 0) {
				System.out.println("Error in setMode(" + gpio + ", " + PigpioConstants.MODE_PI_INPUT + ")");
			}
			
			mode = pigpiod.getMode(gpio);
			System.out.println("getMode(" + gpio + "): " + mode);
			
			if (pigpiod.setMode(gpio, PigpioConstants.MODE_PI_OUTPUT) < 0) {
				System.out.println("Error in setMode(" + gpio + ", " + PigpioConstants.MODE_PI_OUTPUT + ")");
			}
			
			mode = pigpiod.getMode(gpio);
			System.out.println("getMode(" + gpio + "): " + mode);
			
			int level;
			for (int i=0; i<blink_iterations; i++) {
				System.out.println("On");
				if (pigpiod.write(gpio, true) < 0) {
					System.out.println("Error in write(" + gpio + ", " + true + ")");
				}
				level = pigpiod.read(gpio);
				System.out.println("read(" + gpio + "): " + level);
				sleep(delay);
				
				System.out.println("Off");
				if (pigpiod.write(gpio, false) < 0) {
					System.out.println("Error in write(" + gpio + ", " + false + ")");
				}
				level = pigpiod.read(gpio);
				System.out.println("read(" + gpio + "): " + level);
				sleep(delay);
			}
			
			if (pigpiod.setPWMFrequency(gpio, pwm_frequency) < 0) {
				System.out.println("Error in setPWMFrequency(" + gpio + ", " + pwm_frequency + ")");
			}
			
			int real_range = pigpiod.getPWMRealRange(gpio);
			System.out.println("real_range: " + real_range);
			int pwm_range = pigpiod.getPWMRange(gpio);
			System.out.println("pwm_range: " + pwm_range);
			if (pigpiod.setPWMRange(gpio, real_range) < 0) {
				System.out.println("Error in setPWMRange()");
			}
			
			for (float f=0; f<=1; f+=0.02) {
				if (pigpiod.setPWMDutyCycle(gpio, Math.round(f * real_range)) < 0) {
					System.out.println("Error in setPWMDutyCycle");
				}
				sleep(small_delay);
			}
			
			for (float f=1; f>=0; f-=0.02) {
				if (pigpiod.setPWMDutyCycle(gpio, Math.round(f * real_range)) < 0) {
					System.out.println("Error in setPWMDutyCycle");
				}
				sleep(small_delay);
			}
		} catch (InterruptedException e) {
			System.err.println("Interrupted: " + e);
			e.printStackTrace(System.err);
		}
	}
	
	private static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// Ignore
		}
	}
}
