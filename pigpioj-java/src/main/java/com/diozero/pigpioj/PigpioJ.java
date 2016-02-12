package com.diozero.pigpioj;

public class PigpioJ {
	private static Boolean loaded = Boolean.FALSE;
	
	public static void init() {
		synchronized (loaded) {
			if (!loaded.booleanValue()) {
				System.loadLibrary("pigpioj");
				loaded = Boolean.TRUE;
			}
		}
	}
}
