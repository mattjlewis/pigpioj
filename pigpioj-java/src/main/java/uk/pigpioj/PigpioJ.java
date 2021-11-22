package uk.pigpioj;

public class PigpioJ {
	private static final String PIGPIOD_HOST = "PIGPIOD_HOST";
	private static final String PIGPIOD_PORT = "PIGPIOD_PORT";

	private static PigpioInterface autoDetectedPigpioImpl;

	public synchronized static PigpioInterface autoDetectedImplementation() {
		if (autoDetectedPigpioImpl == null) {
			String pigpiod_hostname = getProperty(PIGPIOD_HOST);

			if (pigpiod_hostname != null) {
				String pigpiod_port_str = getProperty(PIGPIOD_PORT);
				int pigpiod_port = PigpioSocket.DEFAULT_PORT;
				if (pigpiod_port_str != null) {
					pigpiod_port = Integer.parseInt(pigpiod_port_str);
				}

				autoDetectedPigpioImpl = newSocketImplementation(pigpiod_hostname, pigpiod_port);
			} else {
				autoDetectedPigpioImpl = new PigpioJNI();
			}
		}

		return autoDetectedPigpioImpl;
	}

	public static PigpioInterface newSocketImplementation(String hostname) {
		return newSocketImplementation(hostname, PigpioSocket.DEFAULT_PORT);
	}

	public static PigpioInterface newSocketImplementation(String hostname, int port) {
		PigpioSocket pigpiod = new PigpioSocket();
		pigpiod.connect(hostname, port);
		return pigpiod;
	}

	static String getProperty(String property) {
		// -D parameter takes priority over environment parameter
		String value = System.getProperty(property);
		if (value == null) {
			value = System.getenv(property);
		}
		return value;
	}
}
