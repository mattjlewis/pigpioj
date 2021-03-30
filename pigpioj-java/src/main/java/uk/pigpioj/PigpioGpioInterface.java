package uk.pigpioj;

public interface PigpioGpioInterface {
	int enableListener(int gpio, int edge, PigpioCallback callback);
	int disableListener(int gpio);
	
	/** @return the pigpio version */
	int getVersion();
	
	/** @return the Pi Hardware Revision identifier from /proc/cpuinfo */
	int getHardwareRevision();
	
	/** Gets the GPIO mode
	 * @param gpio GPIO
	 * @return GPIO mode
	 */
	int getMode(int gpio);
	
	/** Sets the GPIO mode, typically input or output
	 * @param gpio GPIO
	 * @param mode Mode
	 * @return Status
	 */
	int setMode(int gpio, int mode);
	
	/** Sets or clears resistor pull ups or downs on the GPIO
	 * @param gpio GPIO
	 * @param pud Pull up/down value
	 * @return Status
	 */
	int setPullUpDown(int gpio, int pud);
	
	/** Reads the GPIO level, on or off
	 * @param gpio GPIO
	 * @return Status
	 */
	int read(int gpio);
	
	/** Sets the GPIO level, on or off
	 * @param gpio GPIO
	 * @param level on/off
	 * @return Status
	 */
	int write(int gpio, boolean level);
	
	/** Returns the PWM duty cycle setting for the GPIO
	 * For normal PWM the duty cycle will be out of the defined range for the GPIO (see getPWMRange).
	 * If a hardware clock is active on the GPIO the reported duty cycle will be 500000 (500k) out of 1000000 (1M).
	 * If hardware PWM is active on the GPIO the reported duty cycle will be out of a 1000000 (1M).
	 * Normal PWM range defaults to 255
	 * @param gpio GPIO
	 * @return Status
	 */
	int getPWMDutyCycle(int gpio);
	
	/** Starts PWM on the GPIO, duty cycle between 0 (off) and range (fully on). Range defaults to 255
	 * The setPWMRange function may be used to change the default range of 255
	 * @param gpio GPIO
	 * @param dutyCycle New duty cycle value
	 * @return Status
	 */
	int setPWMDutyCycle(int gpio, int dutyCycle);
	
	/** Returns the duty cycle range used for the GPIO if OK
	 * If a hardware clock or hardware PWM is active on the GPIO the reported range will be 1000000 (1M)
	 * @param gpio GPIO
	 * @return Status
	 */
	int getPWMRange(int gpio);
	
	/** Selects the duty cycle range to be used for the GPIO.
	 * Subsequent calls to setPWMDutyCycle will use a duty cycle between 0 (off) and range (fully on)
	 * If PWM is currently active on the GPIO its duty cycle will be scaled to reflect the new range
	 * The real range, the number of steps between fully off and fully on for each frequency, is given in the following table.
	 * <pre>
	 * 25,   50,  100,  125,  200,  250,  400,   500,   625,
	 * 800, 1000, 1250, 2000, 2500, 4000, 5000, 10000, 20000
	 * </pre>
	 * The real value set by setPWMDutyCycle is <code>(duty cycle * real range) / range</code>
	 * @param gpio GPIO
	 * @param range Range
	 * @return Status
	 */
	int setPWMRange(int gpio, int range);
	
	/** Returns the real range used for the GPIO if OK
	 * If a hardware clock is active on the GPIO the reported real range will be 1000000 (1M).
	 * If hardware PWM is active on the GPIO the reported real range will be approximately 250M divided by the set PWM frequency
	 * @param gpio GPIO
	 * @return PWM Range
	 */
	int getPWMRealRange(int gpio);
	
	/**
	 * Returns the frequency (in hertz) used for the GPIO if OK
	 * For normal PWM the frequency will be that defined for the GPIO by setPWMFrequency.
	 * If a hardware clock is active on the GPIO the reported frequency will be that set by gpioHardwareClock.
	 * If hardware PWM is active on the GPIO the reported frequency will be that set by gpioHardwarePWM
	 * @param gpio GPIO
	 * @return PWM frequency
	 */
	int getPWMFrequency(int gpio);
	
	/**
	 * Sets the frequency in hertz to be used for the GPIO
	 * Returns the numerically closest frequency if OK
	 * The selectable frequencies depend upon the sample rate which may be 1, 2, 4, 5, 8, or 10 microseconds (default 5).
	 * Each GPIO can be independently set to one of 18 different PWM frequencies.
	 * If PWM is currently active on the GPIO it will be switched off and then back on at the new frequency.
	 * The frequencies for each sample rate are:
	 * <pre>
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
	 *           125   100    80   50   40   25   20   10    5
	 * </pre>
	 * @param gpio GPIO
	 * @param frequency Frequency
	 * @return Status
	 */
	int setPWMFrequency(int gpio, int frequency);
	
	/**
	 * Returns the servo pulse width setting for the GPIO
	 * @param gpio GPIO number
	 * @return 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise) if OK, otherwise PI_BAD_USER_GPIO or PI_NOT_SERVO_GPIO
	 */
	int getServoPulseWidth(int gpio);
	
	/** Starts servo pulses on the GPIO, 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise)
	 * The range supported by servos varies and should probably be determined by experiment.
	 * A value of 1500 should always be safe and represents the mid-point of rotation.
	 * You can DAMAGE a servo if you command it to move beyond its limits.
	 * The following causes an on pulse of 1500 microseconds duration to be transmitted
	 * on GPIO 17 at a rate of 50 times per second. This will command a servo connected
	 * to GPIO 17 to rotate to its mid-point
	 * OTHER UPDATE RATES:
	 * This function updates servos at 50Hz. If you wish to use a different update frequency
	 * you will have to use the PWM functions.
	 * <pre>
	 * PWM Hz    50   100  200  400  500
	 * 1E6/Hz 20000 10000 5000 2500 2000
	 * </pre>
	 * Firstly set the desired PWM frequency using setPWMFrequency.
	 * Then set the PWM range using setPWMRange to 1E6/frequency.
	 * Doing this allows you to use units of microseconds when setting the servo pulse width.
	 * E.g. If you want to update a servo connected to GPIO25 at 400Hz
	 * <pre>
	 * setPWMFrequency(25, 400);
	 * setPWMRange(25, 2500);
	 * </pre>
	 * Thereafter use the PWM command to move the servo, e.g. setPWMDutyCycle(25, 1500) will set a 1500 us pulse
	 * @param gpio GPIO number 0-31 
	 * @param pulseWidth Pulse width in microseconds, accepted values: 0, 500-2500 
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_PULSEWIDTH
	 */
	int setServoPulseWidth(int gpio, int pulseWidth);
		
	/**
	 * Sets a noise filter on a GPIO.
	 * Level changes on the GPIO are ignored until a level which has been stable for steady
	 * microseconds is detected. Level changes on the GPIO are then reported for active
	 * microseconds after which the process repeats.
	 * Note, level changes before and after the active period may be reported. Your software
	 * must be designed to cope with such reports.
	 * @param gpio  GPIO (0-31)
	 * @param steadyMs 0-300000
	 * @param activeMs 0-1000000
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_BAD_FILTER
	 */
	int noiseFilter(int gpio, int steadyMs, int activeMs);
	
	/**
	 * Sets a glitch filter on a GPIO.
	 * Level changes on the GPIO are not reported unless the level has been stable for at least steady
	 * microseconds. The level is then reported. Level changes of less than steady microseconds are ignored.
	 * Note, each (stable) edge will be time-stamped steady microseconds after it was first detected.
	 * @param gpio 0-31
	 * @param steadyUs 0-300000
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_BAD_FILTER
	 */
	int glitchFilter(int gpio, int steadyUs);
	
	/**
	 * Starts a hardware clock on a GPIO at the specified frequency. Frequencies above 30MHz are unlikely to work.
	 * The same clock is available on multiple GPIO. The latest frequency setting will be used by all GPIO which share a clock.
	 * The GPIO must be one of the following:
	 * <pre>
	 * 4   clock 0  All models
	 * 5   clock 1  All models but A and B (reserved for system use)
	 * 6   clock 2  All models but A and B
	 * 20  clock 0  All models but A and B
	 * 21  clock 1  All models but A and Rev.2 B (reserved for system use)
	 * 32  clock 0  Compute module only
	 * 34  clock 0  Compute module only
	 * 42  clock 1  Compute module only (reserved for system use)
	 * 43  clock 2  Compute module only
	 * 44  clock 1  Compute module only (reserved for system use)
	 * </pre>
	 * @param gpio see description
	 * @param clockFreq 0 (off) or 4689-250000000 (250M)
	 * @return 0 if OK, otherwise PI_BAD_GPIO, PI_NOT_HCLK_GPIO, PI_BAD_HCLK_FREQ,or PI_BAD_HCLK_PASS
	 */
	int hardwareClock(int gpio, int clockFreq);
	
	/**
	 * Starts hardware PWM on a GPIO at the specified frequency and dutycycle.
	 * Frequencies above 30MHz are unlikely to work.
	 * NOTE: Any waveform started by gpioWaveTxSend, or gpioWaveChain will be cancelled.
	 * This function is only valid if the pigpio main clock is PCM.
	 * The main clock defaults to PCM but may be overridden by a call to gpioCfgClock.
	 * The same PWM channel is available on multiple GPIO.
	 * The latest frequency and dutycycle setting will be used by all GPIO which share a PWM channel.
	 * The GPIO must be one of the following.
	 * <pre>
	 * 12  PWM channel 0  All models but A and B
	 * 13  PWM channel 1  All models but A and B
	 * 18  PWM channel 0  All models
	 * 19  PWM channel 1  All models but A and B
	 * 40  PWM channel 0  Compute module only
	 * 41  PWM channel 1  Compute module only
	 * 45  PWM channel 1  Compute module only
	 * 52  PWM channel 0  Compute module only
	 * 53  PWM channel 1  Compute module only
	 * </pre>
	 * The actual number of steps between off and fully on is the integral part of 250 million divided by PWMfreq.
	 * The actual frequency set is 250 million / steps.
	 * There will only be a million steps for a PWMfreq of 250. Lower frequencies will have more steps and
	 * higher frequencies will have fewer steps. PWMduty is automatically scaled to take this into account.
	 * @param gpio see description
	 * @param pwmFreq 0 (off) or 1-125000000 (125M)
	 * @param pwmDuty 0 (off) to 1000000 (1M)(fully on)
	 * @return 0 if OK, otherwise PI_BAD_GPIO, PI_NOT_HPWM_GPIO, PI_BAD_HPWM_DUTY, PI_BAD_HPWM_FREQ, or PI_HPWM_ILLEGAL
	 */
	int hardwarePwm(int gpio, int pwmFreq, int pwmDuty);
}
