package com.diozero.pigpioj;

import java.io.IOException;

public class PigpioInputTest implements PigpioCallback {
	public static final int RISING_EDGE = 0;
	public static final int FALLING_EDGE = 1;
	public static final int EITHER_EDGE = 2;
	
	public static final int PI_PUD_OFF = 0;
	public static final int PI_PUD_DOWN = 1;
	public static final int PI_PUD_UP = 2;

	public static void main(String[] args) {
		int pin = 12;
		int timeout = -1;
		int delay_s = 20;
		try {
			int version = PigpioGpio.initialise();
			System.out.println("version: " + version);
			
			PigpioGpio.setMode(pin, PigpioGpio.MODE_PI_INPUT);
			PigpioGpio.setPullUpDown(pin, PI_PUD_UP);
			PigpioGpio.setISRFunc(pin, EITHER_EDGE, timeout, new PigpioInputTest());
			System.out.println("Sleeping for " + delay_s + "s");
			Thread.sleep(delay_s*1000);
			
			PigpioGpio.setISRFunc(pin, EITHER_EDGE, timeout, null);
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

	@Override
	public void callback(int pin, boolean value, long time) {
		System.out.println("Callback(" + pin + ", " + value + ", " + time + ")");
	}
}
