package uk.pigpioj;

public interface PigpioInterface extends PigpioGpioInterface, PigpioI2CInterface, PigpioSpiInterface,
		PigpioWaveformInterface, PigpioSerialInterface, AutoCloseable {
	@Override
	void close();
}
