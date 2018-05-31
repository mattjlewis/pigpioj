package uk.pigpioj;

/**
 * @author 9903286
 */
public class PigpioBitBangSerial {
	
	/* ------------------------------ RX ------------------------------ */
	
	/**
	 * This function opens a GPIO pin for bit bang reading of serial data.<br>
	 * The serial data is returned in a cyclic buffer and is read using {@link PigpioBitBangSerial#serialRead(int, int) serialRead}.<br>
	 * It is the caller's responsibility to read data from the cyclic buffer in a timely fashion.
	 * 
	 * @param gpio 0-31
	 * @param baud 50-250000
	 * @param data_bits 1-32
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO, PI_BAD_WAVE_BAUD, PI_BAD_DATABITS or PI_GPIO_IN_USE.
	 */
	public static native int serialReadOpen(int gpio, int baud, int data_bits);
	
	/**
	 * This function configures the level logic for bit bang serial reads.<br>
	 * The GPIO must be opened for bit bang reading of serial data using
	 * {@link PigpioBitBangSerial#serialReadOpen(int, int, int) serialReadOpen}
	 * prior to calling this function.
	 * 
	 * @param gpio 0-31
	 * @param invert true or false
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO, PI_GPIO_IN_USE, PI_NOT_SERIAL_GPIO or PI_BAD_SER_INVERT.
	 */
	public static native int serialReadInvert(int gpio, boolean invert);
	
	/**
	 * This function copies up to <code>maxSize</code> of bytes from pigpio's
	 * buffer, puts them in an array<br>
	 * and then returns that array.<br>
	 * <br>
	 * The bytes returned for each character depend upon the number of data_bits
	 * specified in the function
	 * {@link PigpioBitBangSerial#serialReadOpen(int, int, int) serialReadOpen}.<br>
	 * For data_bits 1-8 there will be one byte per character.<br>
	 * For data_bits 9-16 there will be two bytes per character.<br>
	 * For data_bits 17-32 there will be four bytes per character.
	 * 
	 * @param gpio 0-31
	 * @param maxSize
	 * @return byteArray
	 */
	public static native byte[] serialRead(int gpio, int maxSize);
	
	/**
	 * This function closes a GPIO for bit bang reading of serial data.
	 * 
	 * @param gpio 0-31
	 * @return 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_NOT_SERIAL_GPIO.
	 */
	public static native int serialReadClose(int gpio);
	
	/* ------------------------------ TX ------------------------------ */
	
	/**
	 * This function clears all waveforms and any data added by calls to the waveAdd* (such as {@link PigpioBitBangSerial#waveAddSerial(int, int, int, int, int, int, byte[]) waveAddSerial}) functions. 
	 * @return 0 if OK.
	 */
	public static native int waveClear();
	
	/**
	 * This function adds a waveform representing serial data to the existing waveform (if any).<br>
	 * The serial data starts offset microseconds from the start of the waveform. 
	 * 
	 * @param gpio 0-31
	 * @param baud 50-1000000
	 * @param data_bits 1-32
	 * @param stop_bits 2-8
	 * @param offset >= 0
	 * @param numBytes >= 1
	 * @param bytes to add to waveform
	 * @return Returns the new total number of pulses in the current waveform if OK, otherwise PI_BAD_USER_GPIO, PI_BAD_WAVE_BAUD, PI_BAD_DATABITS, PI_BAD_STOPBITS, PI_TOO_MANY_CHARS, PI_BAD_SER_OFFSET, or PI_TOO_MANY_PULSES. 
	 */
	public static native int waveAddSerial(int gpio, int baud, int data_bits, int stop_bits, int offset, int numBytes, byte[] bytes);
	
	/**
	 * This function creates a waveform from the data provided by the prior calls to the waveAdd* (such as {@link PigpioBitBangSerial#waveAddSerial(int, int, int, int, int, int, byte[]) waveAddSerial}) functions.<br>
	 * 
	 * @return WAVE_ID if OK, otherwise PI_EMPTY_WAVEFORM, PI_NO_WAVEFORM_ID, PI_TOO_MANY_CBS, or PI_TOO_MANY_OOL.
	 */
	public static native int waveCreate();
	
	/**
	 * This function transmits the waveform with id wave_id. The mode determines whether the waveform is sent once or cycles endlessly. The SYNC variants wait for the current waveform to reach the end of a cycle or finish before starting the new waveform.<br>
	 * WARNING: bad things may happen if you delete the previous waveform before it has been synced to the new waveform.
	 * 
	 * @param wave_id >=0, as returned by {@link PigpioBitBangSerial#waveCreate() waveCreate()}
	 * @param wave_mode PI_WAVE_MODE_ONE_SHOT, PI_WAVE_MODE_REPEAT, PI_WAVE_MODE_ONE_SHOT_SYNC, PI_WAVE_MODE_REPEAT_SYNC (0-3)
	 * @return The number of DMA control blocks in the waveform if OK, otherwise PI_BAD_WAVE_ID, or PI_BAD_WAVE_MODE.
	 */
	public static native int waveTXSend(int wave_id, int wave_mode);
	
	/**
	 * This function deletes the waveform with id wave_id.
	 * 
	 * @param wave_id >=0, as returned by {@link PigpioBitBangSerial#waveCreate() waveCreate()}
	 * @return Returns 0 if OK, otherwise PI_BAD_WAVE_ID.
	 */
	public static native int waveDelete(int wave_id);
	
}
