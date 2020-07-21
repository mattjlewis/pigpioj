package uk.pigpioj;

public class GpioPulse {
	/* uint32_t */
	private int gpioOn;
	/* uint32_t */
	private int gpioOff;
	/* uint32_t */
	private int usDelay;
	
	public GpioPulse() {
	}

	public GpioPulse(int gpioOn, int gpioOff, int usDelay) {
		this.gpioOn = gpioOn;
		this.gpioOff = gpioOff;
		this.usDelay = usDelay;
	}

	public int getGpioOn() {
		return gpioOn;
	}

	public void setGpioOn(int gpioOn) {
		this.gpioOn = gpioOn;
	}

	public int getGpioOff() {
		return gpioOff;
	}

	public void setGpioOff(int gpioOff) {
		this.gpioOff = gpioOff;
	}

	public int getUsDelay() {
		return usDelay;
	}

	public void setUsDelay(int usDelay) {
		this.usDelay = usDelay;
	}
}
