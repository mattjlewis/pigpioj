package uk.pigpioj.test;

import uk.pigpioj.PigpioInterface;
import uk.pigpioj.PigpioJ;

public class ServoTest {
	private static final float TOWERPRO_SG90_MIN_MS = 0.6f;
	private static final float TOWERPRO_SG90_MAX_MS = 2.5f;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.format("Usage: %s <gpio>%n", ServoTest.class.getName());
			System.exit(1);
		}
		
		int pin_number = Integer.parseInt(args[0]);
		
		try (PigpioInterface pigpio_impl = PigpioJ.getImplementation()) {
			test(pigpio_impl, pin_number, TOWERPRO_SG90_MIN_MS, TOWERPRO_SG90_MAX_MS);
		}
	}
	
	private static void test(PigpioInterface pigpioImpl, int gpio, float min, float max) {
		pigpioImpl.setPWMFrequency(gpio, 50);
		pigpioImpl.setPWMRange(gpio, pigpioImpl.getPWMRealRange(gpio));
		
		{
			int pwm_freq = pigpioImpl.getPWMFrequency(gpio);
			int range = pigpioImpl.getPWMRange(gpio);
			int real_range = pigpioImpl.getPWMRealRange(gpio);
			pigpioImpl.setPWMRange(gpio, pigpioImpl.getPWMRealRange(gpio));
			int new_pwm_freq = pigpioImpl.getPWMFrequency(gpio);
			int new_range = pigpioImpl.getPWMRange(gpio);
			int new_real_range = pigpioImpl.getPWMRealRange(gpio);
			// pwm_freq=800, range=255, real_range=250, new_pwm_freq=800, new_range=250, new_real_range=250
			System.out.format("pwm_freq=%d, range=%d, real_range=%d, new_pwm_freq=%d, new_range=%d, new_real_range=%d%n",
					Integer.valueOf(pwm_freq), Integer.valueOf(range), Integer.valueOf(real_range),
					Integer.valueOf(new_pwm_freq), Integer.valueOf(new_range), Integer.valueOf(new_real_range));
		}
		
		int min_us = Math.round(min * 1000);
		int max_us = Math.round(max * 1000);
		int mid_us = (min_us + max_us) / 2;
		System.out.format("min_us=%d, mid_us=%d, max_us=%d%n", Integer.valueOf(min_us),
				Integer.valueOf(mid_us), Integer.valueOf(max_us));
		
		System.out.format("Mid (%dus)%n", Integer.valueOf(mid_us));
		pigpioImpl.setServoPulseWidth(gpio, mid_us);
		sleepMillis(1000);
		
		System.out.format("Mid (%dus) to Max (%dus)%n", Integer.valueOf(mid_us), Integer.valueOf(max_us));
		for (int i=mid_us; i<max_us; i+=5) {
			pigpioImpl.setServoPulseWidth(gpio, i);
			sleepMillis(10);
		}
		System.out.format("Max (%dus) to Min (%dus)%n", Integer.valueOf(max_us), Integer.valueOf(min_us));
		for (int i=max_us; i>min_us; i-=5) {
			pigpioImpl.setServoPulseWidth(gpio, i);
			sleepMillis(10);
		}
		System.out.format("Min (%dus) to Mid (%dus)%n", Integer.valueOf(min_us), Integer.valueOf(mid_us));
		for (int i=min_us; i<mid_us; i+=5) {
			pigpioImpl.setServoPulseWidth(gpio, i);
			sleepMillis(10);
		}
		
		int range = pigpioImpl.getPWMRange(gpio);
		int real_range = pigpioImpl.getPWMRealRange(gpio);
		int sample_rate_us = 1_000_000/pigpioImpl.getPWMFrequency(gpio)/pigpioImpl.getPWMRange(gpio);
		int min_dc = min_us / sample_rate_us;
		int mid_dc = mid_us / sample_rate_us;
		int max_dc = max_us / sample_rate_us;
		System.out.format("freq=%d, range=%d, real_range=%d, sample_rate_us=%d, min_dc=%d, mid_dc=%d, max_dc=%d%n",
				Integer.valueOf(pigpioImpl.getPWMFrequency(gpio)), Integer.valueOf(range), Integer.valueOf(real_range),
				Integer.valueOf(sample_rate_us), Integer.valueOf(min_dc), Integer.valueOf(mid_dc), Integer.valueOf(max_dc));
		
		System.out.format("Mid DC (%d)%n", Integer.valueOf(mid_dc));
		pigpioImpl.setPWMDutyCycle(gpio, mid_dc);
		sleepMillis(1000);
		
		System.out.format("Mid DC (%d) to Max DC (%d)%n", Integer.valueOf(mid_dc), Integer.valueOf(max_dc));
		for (int i=mid_dc; i<max_dc; i++) {
			pigpioImpl.setPWMDutyCycle(gpio, i);
			sleepMillis(10);
		}
		System.out.format("Max DC (%d) to Min DC (%d)%n", Integer.valueOf(max_dc), Integer.valueOf(min_dc));
		for (int i=max_dc; i>min_dc; i--) {
			pigpioImpl.setPWMDutyCycle(gpio, i);
			sleepMillis(10);
		}
		System.out.format("Min DC (%d) to Mid DC (%d)%n", Integer.valueOf(min_dc), Integer.valueOf(mid_dc));
		for (int i=min_dc; i<mid_dc; i++) {
			pigpioImpl.setPWMDutyCycle(gpio, i);
			sleepMillis(10);
		}
	}
	
	public static void sleepMillis(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) { }
	}
}
