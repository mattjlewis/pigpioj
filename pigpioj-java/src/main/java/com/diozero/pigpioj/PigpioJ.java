package com.diozero.pigpioj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PigpioJ {
	private static Boolean loaded = Boolean.FALSE;
	
	public static void init() {
		synchronized (loaded) {
			if (!loaded.booleanValue()) {
				try {
					Path path = Files.createTempFile("libpigpioj", ".so");
					path.toFile().deleteOnExit();
					Files.copy(PigpioJ.class.getResourceAsStream("/lib/libpigpioj.so"), path, StandardCopyOption.REPLACE_EXISTING);
					System.load(path.toString());
					loaded = Boolean.TRUE;
				} catch (IOException e) {
					System.out.println("Error loading library from classpath: " + e);
					e.printStackTrace();
					
					// Try load the usual way...
					System.loadLibrary("libpigpioj.so");
				}
			}
		}
	}
}
