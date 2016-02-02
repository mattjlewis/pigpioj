package com.diozero.pigpioj;

import java.io.IOException;

public class PigpioI2C {
	/** This returns a handle for the device at the address on the I2C bus
	 * For the SMBus commands the low level transactions are shown at the end of the function description. The following abbreviations are used.
	 * S      (1 bit) : Start bit
	 * P      (1 bit) : Stop bit
	 * Rd/Wr  (1 bit) : Read/Write bit. Rd equals 1, Wr equals 0.
	 * A, NA  (1 bit) : Accept and not accept bit.
	 *
	 * Addr   (7 bits): I2C 7 bit address.
	 * i2cReg (8 bits): Command byte, a byte which often selects a register.
	 * Data   (8 bits): A data byte.
	 * Count  (8 bits): A byte defining the length of a block operation.
	 *
	 * [..]: Data sent by the device. */
	public static native int i2cOpen(int i2cBus, int i2cAddr, int i2cFlags) throws IOException;
	/** This closes the I2C device associated with the handle */
	public static native int i2cClose(int handle) throws IOException;
}
