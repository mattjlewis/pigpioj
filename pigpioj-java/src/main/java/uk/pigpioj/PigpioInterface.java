package uk.pigpioj;

import java.io.Closeable;

public interface PigpioInterface extends PigpioGpioInterface, PigpioI2CInterface, PigpioSpiInterface, Closeable {
	@Override
	void close();
}
