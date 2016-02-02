package com.diozero.pigpioj;

import java.io.IOException;

public class PigpioOutputTest {
	public static void main(String[] args) {
		int pin = 18;
		try {
			int version = PigpioGpio.initialise();
			System.out.println("version: " + version);
			
			PigpioGpio.setMode(pin, PigpioGpio.MODE_PI_OUTPUT);

			for (int i=0; i<5; i++) {
				System.out.println("on");
				PigpioGpio.write(pin, true);
				Thread.sleep(500);
				
				System.out.println("off");
				PigpioGpio.write(pin, false);
				Thread.sleep(500);
			}
			int pwm_max = 255;
			
			System.out.println("pwm full on");
			PigpioGpio.setPWMDutyCycle(pin, pwm_max);
			Thread.sleep(500);
			
			System.out.println("pwm full off");
			PigpioGpio.setPWMDutyCycle(pin, 0);
			Thread.sleep(500);
			
			System.out.println("pwm 25% off");
			PigpioGpio.setPWMDutyCycle(pin, (int)(pwm_max*.25));
			Thread.sleep(500);
			
			System.out.println("pwm 50%");
			PigpioGpio.setPWMDutyCycle(pin, (int)(pwm_max*.50));
			Thread.sleep(500);
			
			System.out.println("pwm 75%");
			PigpioGpio.setPWMDutyCycle(pin, (int)(pwm_max*.75));
			Thread.sleep(500);
			
			System.out.println("pwm full off");
			PigpioGpio.setPWMDutyCycle(pin, 0);
			Thread.sleep(500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PigpioGpio.terminate();
		}
	}
}
