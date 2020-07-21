package uk.pigpioj;

import java.io.Closeable;

public interface PigpioInterface
		extends PigpioGpioInterface, PigpioI2CInterface, PigpioSpiInterface, PigpioWaveformInterface, Closeable {
	@Override
	void close();
}
