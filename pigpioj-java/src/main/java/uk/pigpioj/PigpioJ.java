package uk.pigpioj;

public class PigpioJ {
	private static final String PIGPIOD_HOST = "PIGPIOD_HOST";
	private static final String PIGPIOD_PORT = "PIGPIOD_PORT";

	private static PigpioInterface pigpioImpl;

	public synchronized static PigpioInterface getImplementation() {
		if (pigpioImpl == null) {
			String pigpiod_hostname = getProperty(PIGPIOD_HOST);

			if (pigpiod_hostname != null) {
				PigpioSocket pigpiod = new PigpioSocket();

				String pigpiod_port_str = getProperty(PIGPIOD_PORT);
				int pigpiod_port = PigpioSocket.DEFAULT_PORT;
				if (pigpiod_port_str != null) {
					pigpiod_port = Integer.parseInt(pigpiod_port_str);
				}

				pigpiod.connect(pigpiod_hostname, pigpiod_port);
				pigpioImpl = pigpiod;
			} else {
				pigpioImpl = new PigpioJNI();
			}
		}

		return pigpioImpl;
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
