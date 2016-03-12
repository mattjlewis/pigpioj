package com.diozero.pigpioj;

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


/**
 * pigpio JNI wrapper, inspiration from this project: https://github.com/nkolban/jpigpio
 */
public class PigpioGpio {
	static {
		PigpioJ.init();
	}
	
	public static final int MODE_PI_INPUT = 0;
	public static final int MODE_PI_OUTPUT = 1;
	
	public static final int NO_EDGE = -1;
	public static final int RISING_EDGE = 0;
	public static final int FALLING_EDGE = 1;
	public static final int EITHER_EDGE = 2;
	
	public static final int PI_PUD_OFF = 0;
	public static final int PI_PUD_DOWN = 1;
	public static final int PI_PUD_UP = 2;
	
	/** Initialises the library, call before using the other library functions.
	 * Returns the pigpio version number if OK */
	public static native int initialise();
	/** Terminates the library */
	public static native void terminate();
	/** Sets the gpio mode, typically input or output */
	public static native int setMode(int gpio, int mode);
	/** Gets the gpio mode */
	public static native int getMode(int gpio);
	/** Sets or clears resistor pull ups or downs on the gpio */
	public static native int setPullUpDown(int gpio, int pud);
	/** Reads the gpio level, on or off */
	public static native int read(int gpio);
	/** Sets the gpio level, on or off */
	public static native int write(int gpio, boolean level);
	/** Returns the PWM duty cycle setting for the gpio
	 * For normal PWM the duty cycle will be out of the defined range for the gpio (see gpioGetPWMrange).
	 * If a hardware clock is active on the gpio the reported duty cycle will be 500000 (500k) out of 1000000 (1M).
	 * If hardware PWM is active on the gpio the reported duty cycle will be out of a 1000000 (1M).
	 * Normal PWM range defaults to 255 */
	public static native int getPWMDutyCycle(int gpio);
	/** Starts PWM on the gpio, duty cycle between 0 (off) and range (fully on). Range defaults to 255
	 * The gpioSetPWMrange function may be used to change the default range of 255 */
	public static native int setPWMDutyCycle(int gpio, int dutyCycle);
	/** Returns the duty cycle range used for the gpio if OK
	 * If a hardware clock or hardware PWM is active on the gpio the reported range will be 1000000 (1M) */
	public static native int getPWMRange(int gpio);
	/** Selects the duty cycle range to be used for the gpio.
	 * Subsequent calls to gpioPWM will use a duty cycle between 0 (off) and range (fully on)
	 * If PWM is currently active on the gpio its duty cycle will be scaled to reflect the new range
	 * The real range, the number of steps between fully off and fully on for each frequency, is given in the following table.
	 * 25,   50,  100,  125,  200,  250,  400,   500,   625,
	 * 800, 1000, 1250, 2000, 2500, 4000, 5000, 10000, 20000
	 * The real value set by gpioPWM is (dutycycle * real range) / range */
	public static native int setPWMRange(int gpio, int range);
	/** Returns the real range used for the gpio if OK
	 * If a hardware clock is active on the gpio the reported real range will be 1000000 (1M).
	 * If hardware PWM is active on the gpio the reported real range will be approximately 250M divided by the set PWM frequency */
	public static native int getPWMRealRange(int gpio);
	/** Returns the frequency (in hertz) used for the gpio if OK
	 * For normal PWM the frequency will be that defined for the gpio by gpioSetPWMfrequency.
	 * If a hardware clock is active on the gpio the reported frequency will be that set by gpioHardwareClock.
	 * If hardware PWM is active on the gpio the reported frequency will be that set by gpioHardwarePWM */
	public static native int getPWMFrequency(int gpio);
	/** Sets the frequency in hertz to be used for the gpio
	 * Returns the numerically closest frequency if OK
	 * The selectable frequencies depend upon the sample rate which may be 1, 2, 4, 5, 8, or 10 microseconds (default 5).
	 * Each gpio can be independently set to one of 18 different PWM frequencies.
	 * If PWM is currently active on the gpio it will be switched off and then back on at the new frequency.
	 * The frequencies for each sample rate are:
	 * Hertz
	 *      1: 40000 20000 10000 8000 5000 4000 2500 2000 1600
	 *          1250  1000   800  500  400  250  200  100   50
	 *      2: 20000 10000  5000 4000 2500 2000 1250 1000  800
	 *           625   500   400  250  200  125  100   50   25
	 *      4: 10000  5000  2500 2000 1250 1000  625  500  400
	 *           313   250   200  125  100   63   50   25   13
	 * sample
	 * rate
	 * (us) 5:  8000  4000  2000 1600 1000  800  500  400  320
	 *           250   200   160  100   80   50   40   20   10
	 *      8:  5000  2500  1250 1000  625  500  313  250  200
	 *           156   125   100   63   50   31   25   13    6
	 *     10:  4000  2000  1000  800  500  400  250  200  160
	 *           125   100    80   50   40   25   20   10    5 */
	public static native int setPWMFrequency(int gpio, int frequency);
	/** Returns the servo pulse width setting for the gpio */
	public static native int getServoPulseWidth(int gpio);
	/** Starts servo pulses on the gpio, 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise)
	 * The range supported by servos varies and should probably be determined by experiment.
	 * A value of 1500 should always be safe and represents the mid-point of rotation.
	 * You can DAMAGE a servo if you command it to move beyond its limits.
	 * The following causes an on pulse of 1500 microseconds duration to be transmitted
	 * on gpio 17 at a rate of 50 times per second. This will command a servo connected
	 * to gpio 17 to rotate to its mid-point
	 * OTHER UPDATE RATES:
	 * This function updates servos at 50Hz. If you wish to use a different update frequency
	 * you will have to use the PWM functions.
	 * PWM Hz    50   100  200  400  500
	 * 1E6/Hz 20000 10000 5000 2500 2000
	 * Firstly set the desired PWM frequency using gpioSetPWMfrequency.
	 * Then set the PWM range using gpioSetPWMrange to 1E6/frequency.
	 * Doing this allows you to use units of microseconds when setting the servo pulse width.
	 * E.g. If you want to update a servo connected to gpio25 at 400Hz
	 * gpioSetPWMfrequency(25, 400);
	 * gpioSetPWMrange(25, 2500);
	 * Thereafter use the PWM command to move the servo, e.g. gpioPWM(25, 1500) will set a 1500 us pulse */
	public static native int setServoPulseWidth(int gpio, int pulseWidth);
	/** Registers a function to be called (a callback) whenever the specified gpio interrupt occurs
	 * One function may be registered per gpio.
	 * The function is passed the gpio, the current level, and the current tick.
	 * The level will be PI_TIMEOUT if the optional interrupt timeout expires.
	 * The underlying Linux sysfs gpio interface is used to provide the interrupt services.
	 * The first time the function is called, with a non-NULL callback, the gpio is exported,
	 * set to be an input, and set to interrupt on the given edge and timeout.
	 * Subsequent calls, with a non-NULL callback, can vary one or more of the edge, timeout, or function.
	 * The ISR may be cancelled by passing a NULL callback, in which case the gpio is unexported.
	 * The tick is that read at the time the process was informed of the interrupt.
	 * This will be a variable number of microseconds after the interrupt occurred.
	 * Typically the latency will be of the order of 50 microseconds.
	 * The latency is not guaranteed and will vary with system load.
	 * The level is that read at the time the process was informed of the interrupt,
	 * or PI_TIMEOUT if the optional interrupt timeout expired.
	 * It may not be the same as the expected edge as interrupts happening in rapid
	 * succession may be missed by the kernel (i.e. this mechanism can not be used
	 * to capture several interrupts only a few microseconds apart) */
	public static native int setISRFunc(int gpio, int edge, int timeout, PigpioCallback callback);
	/** Returns the pigpio version */
	public static native int getVersion();
}
