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
