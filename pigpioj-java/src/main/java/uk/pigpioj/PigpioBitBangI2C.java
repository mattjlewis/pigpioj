package uk.pigpioj;

public class PigpioBitBangI2C {
	/**
	 * <p>This function selects a pair of GPIO for bit banging I2C at a specified baud rate.
	 * Bit banging I2C allows for certain operations which are not possible with the standard I2C driver.</p>
	 * <ul>
	 * <li>baud rates as low as 50</li>
	 * <li>repeated starts</li>
	 * <li>clock stretching</li>
	 * <li>I2C on any pair of spare GPIO</li>
	 * </ul>
	 * <p><strong>NOTE</strong>: The GPIO used for SDA and SCL must have pull-ups to 3V3 connected. As a guide the hardware pull-ups on pins 3 and 5 are 1k8 in value.</p>
	 * @param sda 0-31
	 * @param scl 0-31
	 * @param baud 50-500000
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO, PI_BAD_I2C_BAUD, or PI_GPIO_IN_USE.
	 */
	public static native int bbI2COpen(int sda, int scl, int baud);
	
	/**
	 * This function stops bit banging I2C on a pair of GPIO previously opened with bbI2COpen.
	 * @param sda 0-31, the SDA GPIO used in a prior call to bbI2COpen
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_NOT_I2C_GPIO
	 */
	public static native int bbI2CClose(int sda);
	
	/**
	 * <p>This function executes a sequence of bit banged I2C operations. The operations to be performed are specified by the contents of inBuf which contains the concatenated command codes and associated data.</p>
	 * <p>The address, read, and write commands take a parameter P. Normally P is one byte (0-255). If the command is preceded by the Escape command then P is two bytes (0-65535, least significant byte first).</p>
	 * <p>The following command codes are supported: </p>
	 * <pre>
	 * Name     Cmd &amp; Data  Meaning
	 * End      0           No more commands
	 * Escape   1           Next P is two bytes
	 * Start    2           Start condition
	 * Stop     3           Stop condition
	 * Address  4 P         Set I2C address to P
	 * Flags    5 lsb msb   Set I2C flags to lsb + (msb &lt;&lt; 8)
	 * Read     6 P         Read P bytes of data
	 * Write    7 P ...     Write P bytes of data
	 * </pre>
	 * <p>The address and flags default to 0. The address and flags maintain their previous value until updated.</p>
	 * <p>No flags are currently defined.</p>
	 * <p>The returned I2C data is stored in consecutive locations of outBuf.</p>
	 * @param sda 0-31 (as used in a prior call to bbI2COpen)
	 * @param inBuf buffer the concatenated I2C commands, see below
	 * @param inLen size of command buffer
	 * @param outBuf buffer to hold returned data
	 * @param outLen size of output buffer
	 * @return 0 if OK (the number of bytes read), otherwise PI_BAD_USER_GPIO, PI_NOT_I2C_GPIO, PI_BAD_POINTER, PI_BAD_I2C_CMD, PI_BAD_I2C_RLEN, PI_BAD_I2C_WLEN, PI_I2C_READ_FAILED, or PI_I2C_WRITE_FAILED
	 */
	public static native int bbI2CZip(int sda, byte[] inBuf, int inLen, byte[] outBuf, int outLen);
}
