package com.diozero.pigpioj;

import java.io.IOException;

public class PigpioSPI {
	static {
		PigpioJ.init();
	}
	
	/**
	 * This function returns a handle for the SPI device on the channel. Data will be
	 * transferred at baud bits per second. The flags may be used to modify the default
	 * behaviour of 4-wire operation, mode 0, active low chip select.
	 * An auxiliary SPI device is available on the A+/B+/Pi2/Zero and may be selected
	 * by setting the A bit in the flags. The auxiliary device has 3 chip selects and
	 * a selectable word size in bits.
	 * spiFlags consists of the least significant 22 bits.
	 * 
	 * 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
	 *  b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
	 * mm defines the SPI mode.
	 * Warning: modes 1 and 3 do not appear to work on the auxiliary device.
	 * Mode POL PHA
	 *  0    0   0
	 *  1    0   1
	 *  2    1   0
	 *  3    1   1
	 * px is 0 if CEx is active low (default) and 1 for active high.
	 * ux is 0 if the CEx gpio is reserved for SPI (default) and 1 otherwise.
	 * A is 0 for the standard SPI device, 1 for the auxiliary SPI. The auxiliary device
	 * is only present on the A+/B+/Pi2/Zero.
	 * W is 0 if the device is not 3-wire, 1 if the device is 3-wire. Standard SPI device only.
	 * nnnn defines the number of bytes (0-15) to write before switching the MOSI line
	 * to MISO to read data. This field is ignored if W is not set. Standard SPI device only.
	 * T is 1 if the least significant bit is transmitted on MOSI first, the default (0)
	 * shifts the most significant bit out first. Auxiliary SPI device only.
	 * R is 1 if the least significant bit is received on MISO first, the default (0) receives
	 * the most significant bit first. Auxiliary SPI device only.
	 * bbbbbb defines the word size in bits (0-32).
	 * The default (0) sets 8 bits per word. Auxiliary SPI device only.
	 * The other bits in flags should be set to zero
	 */
	public static native int spiOpen(int spiChan, int baud, int spiFlags) throws IOException;
	/** This functions closes the SPI device identified by the handle */
	public static native void spiClose(int handle) throws IOException;
	/**
	 * This function reads count bytes of data from the SPI device associated with the handle
	 * @return Returns the number of bytes transferred if OK
	 */
	public static native int spiRead(int handle, byte[] buf, int count) throws IOException;
	/**
	 * This function writes count bytes of data from buf to the SPI device associated with the handle
	 * @return Returns the number of bytes transferred if OK
	 */
	public static native int spiWrite(int handle, byte[] buf, int count) throws IOException;
	/**
	 * This function transfers count bytes of data from txBuf to the SPI device associated
	 * with the handle. Simultaneously count bytes of data are read from the device and
	 * placed in rxBuf
	 * @return Returns the number of bytes transferred if OK
	 */
	public static native int spiXfer(int handle, byte[] txBuf, byte[] rxBuf, int count) throws IOException;
}
