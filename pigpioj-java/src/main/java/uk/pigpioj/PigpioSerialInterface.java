package uk.pigpioj;

public interface PigpioSerialInterface {
	/**
	 * This function opens a serial device at a specified baud rate and with specified flags.
	 * The device name must start with /dev/tty or /dev/serial.
	 * 
	 * The baud rate must be one of 50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200, or 230400.
	 * No flags are currently defined. This parameter should be set to zero.
	 * 
	 *  @param sertty the serial device to open [char*]
	 *  @param baud the baud rate in bits per second, see below [unsigned]
	 *  @param serFlags 0 [unsigned]
	 *  @return Returns a handle (>=0) if OK, otherwise PI_NO_HANDLE, or PI_SER_OPEN_FAILED.
	 */
	int serOpen(String sertty, int baud, int serFlags);
	
	/**
	 * This function closes the serial device associated with handle. 
	 * 
	 * @param handle >=0, as returned by a call to serOpen [unsigned]
	 * @return Returns 0 if OK, otherwise PI_BAD_HANDLE.
	 */
	int serClose(int handle);

	/**
	 * This function writes bVal to the serial port associated with handle. 
	 * @param handle >=0, as returned by a call to serOpen [unsigned]
	 * @param bVal Unsigned byte value to write [unsigned]
	 * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
	 */
	int serWriteByte(int handle, int bVal);

	/**
	 * This function reads a byte from the serial port associated with handle.
	 * If no data is ready PI_SER_READ_NO_DATA is returned.
	 * @param handle >=0, as returned by a call to serOpen [unsigned]
	 * @return Returns the read byte (>=0) if OK, otherwise PI_BAD_HANDLE, PI_SER_READ_NO_DATA, or PI_SER_READ_FAILED.
	 */
	int serReadByte(int handle);

	/**
	 * This function writes count bytes from buf to the the serial port associated with handle. 
	 * 
	 * @param handle >=0, as returned by a call to serOpen [unsigned]
	 * @param buf the array of bytes to write [char*]
	 * @param count the number of bytes to write [unsigned]
	 * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
	 */
	int serWrite(int handle, byte[] buf, int count);

	/**
	 * This function reads up count bytes from the the serial port associated with handle and writes them to buf.
	 * If no data is ready zero is returned.
	 * 
	 * @param handle >=0, as returned by a call to serOpen [unsigned]
	 * @param buf an array to receive the read data [char*]
	 * @param count the maximum number of bytes to read [unsigned]
	 * @return Returns the number of bytes read (>0=) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
	 */
	int serRead(int handle, byte[] buf, int count);
	
	/**
	 * This function returns the number of bytes available to be read from the device associated with handle.
	 * @param handle >=0, as returned by a call to serOpen [unsigned]
	 * @return Returns the number of bytes of data available (>=0) if OK, otherwise PI_BAD_HANDLE.
	 */
	int serDataAvailable(int handle);
}
