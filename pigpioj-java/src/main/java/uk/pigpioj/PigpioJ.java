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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;

public class PigpioJ {
	public static final int PI_BAD_USER_GPIO = -2;
	public static final int PI_GPIO_IN_USE = -50;
	public static final int PI_I2C_WRITE_FAILED = -82;
	public static final int PI_I2C_READ_FAILED = -83;
	public static final int PI_BAD_POINTER = -90;
	public static final int PI_NOT_I2C_GPIO = -108;
	public static final int PI_BAD_I2C_WLEN = -109;
	public static final int PI_BAD_I2C_RLEN = -110;
	public static final int PI_BAD_I2C_CMD = -111;
	public static final int PI_BAD_I2C_BAUD = -112;
	
	private static final String LIB_NAME = "pigpioj";
	private static AtomicBoolean loaded = new AtomicBoolean();
	static void init() {
		synchronized (loaded) {
			if (!loaded.get()) {
				@SuppressWarnings("resource")
				InputStream is = PigpioJ.class.getResourceAsStream("/lib/lib" + LIB_NAME + ".so");
				if (is != null) {
					try {
						Path path = Files.createTempFile("lib" + LIB_NAME, ".so");
						path.toFile().deleteOnExit();
						Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
						Runtime.getRuntime().load(path.toString());
						loaded.set(true);
						System.out.println("Loaded '" + LIB_NAME + "' from classpath");
					} catch (Throwable t) {
						System.out.println("Error loading library from classpath, trying System.loadLibrary: " + t);
					} finally {
						try { is.close(); } catch (IOException e) { }
					}
				}
				if (! loaded.get()) {
					// Try load from the Java system library path (-Djava.library.path)
					try {
						System.loadLibrary(LIB_NAME);
						loaded.set(true);
						System.out.println("Loaded '" + LIB_NAME + "' from system library path");
					} catch (Throwable t) {
						System.out.println("Error loading library from system library path: " + t);
						t.printStackTrace();
					}
				}
			}
		}
	}
}
