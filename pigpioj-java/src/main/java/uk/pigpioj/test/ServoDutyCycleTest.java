package uk.pigpioj.test;

import uk.pigpioj.PigpioInterface;
import uk.pigpioj.PigpioJ;

public class ServoDutyCycleTest {
    private static final float TOWERPRO_SG90_MIN_MS = 0.6f;
    private static final float TOWERPRO_SG90_MAX_MS = 2.5f;
    private static final int PWM_FREQUENCY = 50;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.format("Usage: %s <gpio>%n", ServoPulseWidthTest.class.getName());
            System.exit(1);
        }

        int pin_number = Integer.parseInt(args[0]);

        try (PigpioInterface pigpio_impl = PigpioJ.autoDetectedImplementation()) {
            test(pigpio_impl, pin_number, PWM_FREQUENCY, TOWERPRO_SG90_MIN_MS, TOWERPRO_SG90_MAX_MS);
        }
    }

    private static void test(PigpioInterface pigpioImpl, int gpio, int frequency, float min, float max) {
        pigpioImpl.setPWMFrequency(gpio, frequency);

        int range = pigpioImpl.getPWMRealRange(gpio);
        pigpioImpl.setPWMRange(gpio, range);

        float mid = (min+max) / 2;
        int period_ms = Float.valueOf(1.0f / frequency * 1000).intValue();
        int min_dc = Float.valueOf(dutyCyle(min, period_ms) * range).intValue();
        int mid_dc = Float.valueOf(dutyCyle(mid, period_ms) * range).intValue();
        int max_dc = Float.valueOf(dutyCyle(max, period_ms) * range).intValue();
        System.out.format("freq=%d, range=%d, mid=%f, period_ms=%d min_dc=%d, mid_dc=%d, max_dc=%d%n",
                pigpioImpl.getPWMFrequency(gpio), range, mid,
                period_ms, min_dc, mid_dc, max_dc);

        System.out.format("Mid DC (%d)%n", mid_dc);
        pigpioImpl.setPWMDutyCycle(gpio, mid_dc);
        sleepMillis(1000);

        System.out.format("Mid DC (%d) to Max DC (%d)%n", mid_dc, max_dc);
        for (int i = mid_dc; i < max_dc; i++) {
            pigpioImpl.setPWMDutyCycle(gpio, i);
            sleepMillis(10);
        }
        System.out.format("Max DC (%d) to Min DC (%d)%n", max_dc, min_dc);
        for (int i = max_dc; i > min_dc; i--) {
            pigpioImpl.setPWMDutyCycle(gpio, i);
            sleepMillis(10);
        }
        System.out.format("Min DC (%d) to Mid DC (%d)%n", min_dc, mid_dc);
        for (int i = min_dc; i < mid_dc; i++) {
            pigpioImpl.setPWMDutyCycle(gpio, i);
            sleepMillis(10);
        }
    }

    private static float dutyCyle(float ontime_ms, float period_ms) {
        return ontime_ms / period_ms;
    }

    private static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}