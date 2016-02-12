package com.diozero.pigpioj;

/*
 * #%L
 * pigpio Java wrapper
 * %%
 * Copyright (C) 2016 diozero
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.io.IOException;

public class PigpioI2C {
	static {
		PigpioJ.init();
	}
	
	/** This returns a handle for the device at the address on the I2C bus
	 * For the SMBus commands the low level transactions are shown at the end of the function description.
	 * The following abbreviations are used.
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
	public static native void i2cClose(int handle) throws IOException;
	/** This reads a single byte from the device associated with handle */
	public static native int i2cReadByte(int handle) throws IOException;
	/** This sends a single byte to the device associated with handle */
	public static native void i2cWriteByte(int handle, int bVal) throws IOException;
	/** This reads a single byte from the specified register of the device associated with handle */
	public static native int i2cReadByteData(int handle, int i2cReg) throws IOException;
	/** This writes a single byte to the specified register of the device associated with handle */
	public static native void i2cWriteByteData(int handle, int i2cReg, int bVal) throws IOException;
	/** This reads a single 16 bit word from the specified register of the device associated with handle */
	public static native int i2cReadWordData(int handle, int i2cReg) throws IOException;
	/** This writes a single 16 bit word to the specified register of the device associated with handle */
	public static native void i2cWriteWordData(int handle, int i2cReg, int wVal) throws IOException;
	/** This writes 16 bits of data to the specified register of the device associated with handle and reads 16 bits of data in return */
	public static native int i2cProcessCall(int handle, int i2cReg, int wVal) throws IOException;
	/**
	 * This reads a block of up to 32 bytes from the specified register of the device associated with handle.
	 * The amount of returned data is set by the device
	 * @return Returns the number of bytes read (>0) if OK
	 */
	public static native int i2cReadBlockData(int handle, int i2cReg, byte[] buf) throws IOException;
	/** This writes up to 32 bytes to the specified register of the device associated with handle */
	public static native void i2cWriteBlockData(int handle, int i2cReg, byte[] buf, int count) throws IOException;
	/**
	 * This reads count bytes from the specified register of the device associated with handle
	 * @param count The count may be 1-32
	 * @return Returns the number of bytes read (>0) if OK
	 */
	public static native int i2cReadI2CBlockData(int handle, int i2cReg, byte[] buf, int count) throws IOException;
	/** This writes 1 to 32 bytes to the specified register of the device associated with handle */
	public static native void i2cWriteI2CBlockData(int handle, int i2cReg, byte[] buf, int count) throws IOException;
	/** This reads count bytes from the raw device into buffer. Returns count (>0) if OK */
	public static native int i2cReadDevice(int handle, byte[] buffer, int count) throws IOException;
	/** This writes count bytes from buffer to the raw device */
	public static native void i2cWriteDevice(int handle, byte[] buffer, int count) throws IOException;
	/** This sets the I2C (i2c-bcm2708) module "use combined transactions" parameter on or off */
	public static native void i2cSwitchCombined(boolean setting) throws IOException;
}
