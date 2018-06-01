package uk.pigpioj.test;

import static uk.pigpioj.PigpioBitBangSerial.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.pigpioj.PigpioConstants;
import uk.pigpioj.PigpioInterface;
import uk.pigpioj.PigpioJ;

/**
 * @author 9903286
 */
public class PigpioTestBBSerial {
	
	/*
	 * This is a test made for the bitbanged serial provided by pigpio.
	 * It will check through all the different baud rates in the bauds array, test them "numTest" times and return their success rate.
	 * (NOTE: For this test to work, connect rxPin to txPin on the Raspberry PI)
	 */
	
	public static Random rand = new Random();
	
	public static final int numTests = 10;

	public static final int[] bauds = {50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 250000};
	public static final int data_bits = 8;
	public static final int stop_bits = 2;
	
	//Settings for string generation
	public static final int minLength = 10;
	public static final int maxLength = 30;
	
	//GPIO pin settings
	public static final int txPin = 17;
	public static final int rxPin = 18;
	
	//Where we store results
	public static List<Integer> numSuccesses = new ArrayList<>();
	
	//Main function
	public static void main(String[] args) throws InterruptedException {
		try (PigpioInterface pigpio_impl = PigpioJ.getImplementation()) {
			System.out.println("PIGPIO Version: " + pigpio_impl.getVersion());
			System.out.println("\nStarting bit banged serial tests...");
			long ms = System.currentTimeMillis();
			
			pigpio_impl.setMode(rxPin, PigpioConstants.MODE_PI_INPUT);
			pigpio_impl.setMode(txPin, PigpioConstants.MODE_PI_OUTPUT);
			
			sleep(100);
			
			for(int i = 0; i < bauds.length; i++) {
				numSuccesses.add(i, 0); //Set successes to 0 in our scoreboard
				serialReadClose(rxPin); //Close previously open rxPin
				serialReadOpen(rxPin, bauds[i], data_bits); //Open rxPin on the new baud rate
				
				while(serialRead(rxPin, 1024).length > 0); //Empty all the data from pigpio's buffer. (If this is infinite rxPin and txPin is probably wrong!) 
				
				System.out.println();
				for(int j = 0; j < numTests; j++) {
					//Print progress bar
					int percent = (int) (((float) j/(float) numTests)*100f);
					System.out.print("\rBaudrate " + bauds[i] + " test:\t<");
					for(int p = 0; p < percent; p++) {
						System.out.print("=");
					}
					for(int u = 0; u < (100-percent); u++) {
						System.out.print("-");
					}
					System.out.print("> " + percent + "% (" + j + "/" + numTests + ")");
					
					String str = generateString(rand.nextInt(maxLength-minLength)+minLength);
					float strBits = (str.getBytes().length*8);
					float time = strBits/((float) bauds[i]);
					long sleeptime = (long) ((time*1.5) * 1000); //Add 1.5 to time since there can be other factors slowing the baud even more
					if(sleeptime < 10) sleeptime = 10;
					
					//TX
					waveClear(); //Clear any previously added data from wave
					waveAddSerial(txPin, bauds[i], data_bits, stop_bits, 0, str.getBytes().length, str.getBytes()); //Add string to our wave
					sleep(10); //Wait for data to be added, for some reason this seems to be needed
					int wave_id = waveCreate(); //Create our new wave
					waveTXSend(wave_id, 2); //Send our wave with mode PI_WAVE_MODE_ONE_SHOT_SYNC, so we know when it's done
					
					sleep(sleeptime); //We need to wait for the bits to actually be transmitted, calculated depending on baudrate
					
					//RX
					byte[] recvBytes = serialRead(rxPin, str.length()); //This should contain our test string "str".
					String recvStr = new String(recvBytes, StandardCharsets.UTF_8);
					
					//Logic
					boolean isCorrect = str.equals(recvStr);
					if(isCorrect) {
						numSuccesses.set(i, numSuccesses.get(i)+1);
					}
				}
				//Print progress bar
				System.out.print("\rBaudrate " + bauds[i] + " test:\t<");
				for(int p = 0; p < 100; p++) {
					System.out.print("=");
				}
				System.out.print("> " + 100 + "% (" + numTests + "/" + numTests + ")");
			}
			
			serialReadClose(rxPin);
			waveClear();
			System.out.println("\n\nCalculating final score: ");
			System.out.println("Baudrate:\t\tSuccess rate:\t\tTests:");
			for(int i = 0; i < bauds.length; i++) {
				
				int baud = bauds[i];
				int successes = numSuccesses.get(i);
				
				int percent = (int) (((float) successes / (float) numTests)*100f);
				
				System.out.println(baud + "\t\t\t" + percent + "%\t\t\t" + successes + "/" + numTests);
			}
			System.out.println("Time taken: " + ((float) (System.currentTimeMillis()-ms)/1000f) + " seconds...");
			
		}
	}
	
	/**
	 * Generates a random string with length <code>length</code>.
	 * 
	 * @param length
	 * @return random string
	 */
	public static String generateString(int length) {
		String returnString = "";
		for(int i = 0; i < length; i++) {
			char c = (char) (rand.nextInt(26) + 'a');
			returnString += (rand.nextBoolean() ? (""+c).toUpperCase() : c);
		}
		return returnString;
	}
	
	/**
	 * Pretty much does Thread.sleep(), but didn't want to bother with exceptions.
	 * @param millis
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
