package uk.pigpioj;

public interface PigpioI2CInterface {
	/** <p>This returns a handle for the device at the address on the I2C bus.</p>
	 * <p>For the SMBus commands the low level transactions are shown at the end of the function description.</p>
	 * <p>The following abbreviations are used:</p>
	 * <pre>
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
	 * [..]: Data sent by the device.
	 * </pre>
	 * @param i2cBus I2C bus number
	 * @param i2cAddr I2C device address
	 * @param i2cFlags I2C flags
	 * @return File descriptor of the I2C device opened
	 */
	int i2cOpen(int i2cBus, int i2cAddr, int i2cFlags);
	
	/**
	 * This closes the I2C device associated with the handle
	 * @param handle File descriptor from {@link uk.pigpioj.PigpioI2C#i2cOpen i2cOpen}
	 * @return Status
	 */
	int i2cClose(int handle);
	
	/**
	 * This sends a single bit (in the Rd/Wr bit) to the device associated with handle.
	 * @param handle >=0, as returned by a call to i2cOpen
	 * @param bit 0-1, the value to write
	 * @return 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED
	 */
	int i2cWriteQuick(int handle, int bit);
	
	/**
	 * This reads a single byte from the device associated with handle
	 * @param handle >=0, as returned by a call to i2cOpen
	 * @return the byte read (>=0) if OK, otherwise PI_BAD_HANDLE, or PI_I2C_READ_FAILED. 
	 */
	int i2cReadByte(int handle);
	
	/**
	 * This sends a single byte to the device associated with handle.
	 * @param handle >=0, as returned by a call to i2cOpen
	 * @param bVal 0-0xFF, the value to write
	 * @return 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
	 */
	int i2cWriteByte(int handle, int bVal);
	
	/* This reads a single byte from the specified register of the device associated with handle */
	int i2cReadByteData(int handle, int i2cReg);
	
	/* This writes a single byte to the specified register of the device associated with handle */
	int i2cWriteByteData(int handle, int i2cReg, int bVal);
	
	/* This reads a single 16 bit word from the specified register of the device associated with handle */
	int i2cReadWordData(int handle, int i2cReg);
	
	/* This writes a single 16 bit word to the specified register of the device associated with handle */
	int i2cWriteWordData(int handle, int i2cReg, int wVal);
	
	/* This writes 16 bits of data to the specified register of the device associated with handle and reads 16 bits of data in return */
	int i2cProcessCall(int handle, int i2cReg, int wVal);
	
	/**
	 * This reads a block of up to 32 bytes from the specified register of the device associated with handle.
	 * The amount of returned data is set by the device
	 * @param handle File descriptor from i2cOpen
	 * @param i2cReg I2C register
	 * @param buf Buffer for data read
	 * @return Returns the number of bytes read (&gt;0) if OK
	 */
	int i2cReadBlockData(int handle, int i2cReg, byte[] buf);
	
	/* This writes up to 32 bytes to the specified register of the device associated with handle */
	int i2cWriteBlockData(int handle, int i2cReg, byte[] buf, int count);
	
	/**
	 * This reads count bytes from the specified register of the device associated with handle
	 * @param handle File descriptor from i2cOpen
	 * @param i2cReg I2C register
	 * @param buf Buffer for data read
	 * @param count The count may be 1-32
	 * @return Returns the number of bytes read (&gt;0) if OK
	 */
	int i2cReadI2CBlockData(int handle, int i2cReg, byte[] buf, int count);
	
	/* This writes 1 to 32 bytes to the specified register of the device associated with handle */
	int i2cWriteI2CBlockData(int handle, int i2cReg, byte[] buf, int count);
	
	/* This reads count bytes from the raw device into buffer. Returns count (>0) if OK */
	int i2cReadDevice(int handle, byte[] buffer, int count);
	
	/* This writes count bytes from buffer to the raw device */
	int i2cWriteDevice(int handle, byte[] buffer, int count);
}
