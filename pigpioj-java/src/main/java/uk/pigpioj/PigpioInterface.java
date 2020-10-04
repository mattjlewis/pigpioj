package uk.pigpioj;

import java.io.Closeable;

public interface PigpioInterface extends PigpioGpioInterface, PigpioI2CInterface, PigpioSpiInterface,
		PigpioWaveformInterface, PigpioSerialInterface, Closeable {
	@Override
	void close();
}
