package com.diozero.pigpioj;

public interface PigpioCallback {
	void callback(int pin, boolean value, long time);
}
