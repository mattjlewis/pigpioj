package uk.pigpioj;

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

public class PigpioWaveform {
	/**
	 * This function clears all waveforms and any data added by calls to the gpioWaveAdd* functions.
	 * @return Returns 0 if OK.
	 */
	public static native int gpioWaveClear();
	
	/**
	 * This function starts a new empty waveform.
	 * You wouldn't normally need to call this function as it is automatically called after a
	 * waveform is created with the gpioWaveCreate function.
	 * @return Returns 0 if OK.
	 */
	public static native int gpioWaveAddNew();
	
	/**
	 * This function adds a number of pulses to the current waveform.
	 * The pulses are interleaved in time order within the existing waveform (if any).
	 * Merging allows the waveform to be built in parts, that is the settings for GPIO#1 can be added, and then GPIO#2 etc.
	 * If the added waveform is intended to start after or within the existing waveform then the first pulse should
	 * consist of a delay.
	 * @param pulses An array of pulses
	 * @return Returns the new total number of pulses in the current waveform if OK, otherwise PI_TOO_MANY_PULSES.
	 */
	public static native int gpioWaveAddGeneric(GpioPulse[] pulses);
	
	/**
	 * This function adds a waveform representing serial data to the existing waveform (if any).
	 * The serial data starts offset microseconds from the start of the waveform.
	 * NOTES:
	 * The serial data is formatted as one start bit, data_bits data bits, and stop_bits/2 stop bits.
	 * It is legal to add serial data streams with different baud rates to the same waveform.
	 * numBytes is the number of bytes of data in str.
	 * The bytes required for each character depend upon data_bits.
	 * For data_bits 1-8 there will be one byte per character.
	 * For data_bits 9-16 there will be two bytes per character.
	 * For data_bits 17-32 there will be four bytes per character.
	 * @param userGpio 0-31
	 * @param baud 50-1000000
	 * @param dataBits 1-32
	 * @param stopBits 2-8
	 * @param offset &gt;= 0
	 * @param str An array of chars (which may contain nulls)
	 * @return Returns the new total number of pulses in the current waveform if OK, otherwise PI_BAD_USER_GPIO,
	 * PI_BAD_WAVE_BAUD, PI_BAD_DATABITS, PI_BAD_STOPBITS, PI_TOO_MANY_CHARS, PI_BAD_SER_OFFSET, or PI_TOO_MANY_PULSES.
	 */
	public static native int gpioWaveAddSerial(int userGpio, int baud, int dataBits, int stopBits, int offset,
			byte[] str);
	
	/**
	 * This function creates a waveform from the data provided by the prior calls to the gpioWaveAdd* functions.
	 * Upon success a wave id greater than or equal to 0 is returned, otherwise PI_EMPTY_WAVEFORM, PI_TOO_MANY_CBS,
	 * PI_TOO_MANY_OOL, or PI_NO_WAVEFORM_ID.
	 * The data provided by the gpioWaveAdd* functions is consumed by this function.
	 * As many waveforms may be created as there is space available. The wave id is passed to gpioWaveTxSend to specify the waveform to transmit.
	 * Normal usage would be
	 * Step 1. gpioWaveClear to clear all waveforms and added data.
	 * Step 2. gpioWaveAdd* calls to supply the waveform data.
	 * Step 3. gpioWaveCreate to create the waveform and get a unique id
	 * Repeat steps 2 and 3 as needed.
	 * Step 4. gpioWaveTxSend with the id of the waveform to transmit.
	 * A waveform comprises one of more pulses. Each pulse consists of a GpioPulse structure.
	 * The fields specify
	 * 1) the GPIO to be switched on at the start of the pulse.
	 * 2) the GPIO to be switched off at the start of the pulse.
	 * 3) the delay in microseconds before the next pulse.
	 * Any or all the fields can be zero. It doesn't make any sense to set all the fields to zero (the pulse will be ignored).
	 * When a waveform is started each pulse is executed in order with the specified delay between the pulse and the next.
	 * @return Returns the new waveform id if OK, otherwise PI_EMPTY_WAVEFORM, PI_NO_WAVEFORM_ID, PI_TOO_MANY_CBS, or PI_TOO_MANY_OOL.
	 */
	public static native int gpioWaveCreate();
	
	/**
	 * Similar to gpioWaveCreate, this function creates a waveform but pads the consumed resources.
	 * Padded waves of equal dimension can be re-cycled efficiently allowing newly created waves to re-use the
	 * resources of deleted waves of the same dimension.
	 * Waveform data provided by gpioWaveAdd* and rawWaveAdd* functions are consumed by this function.
	 * A usage would be the creation of two waves where one is filled while the other is being transmitted.
	 * Each wave is assigned 50% of the resources. This buffer structure allows the transmission of infinite wave sequences.
	 * @param pctCB 0-100, the percent of all DMA control blocks to consume.
	 * @param pctBOOL 0-100, percent On-Off-Level (OOL) buffer to consume for wave output.
	 * @param pctTOOL 0-100, the percent of OOL buffer to consume for wave input (flags).
	 * @return Upon success a wave id greater than or equal to 0 is returned, otherwise PI_EMPTY_WAVEFORM,
	 * PI_TOO_MANY_CBS, PI_TOO_MANY_OOL, or PI_NO_WAVEFORM_ID.
	 */
	public static native int gpioWaveCreatePad(int pctCB, int pctBOOL, int pctTOOL);
	
	/**
	 * This function deletes the waveform with id wave_id.
	 * The wave is flagged for deletion. The resources used by the wave will only be reused when either of the following apply.
	 * - all waves with higher numbered wave ids have been deleted or have been flagged for deletion.
	 * - a new wave is created which uses exactly the same resources as the current wave (see the C source for gpioWaveCreate for details).
	 * Wave ids are allocated in order, 0, 1, 2, etc.
	 * @param waveId &gt;= 0, as returned by gpioWaveCreate
	 * @return Returns 0 if OK, otherwise PI_BAD_WAVE_ID.
	 */
	public static native int gpioWaveDelete(int waveId);
	
	/**
	 * This function transmits the waveform with id wave_id. The mode determines whether the waveform is sent once or
	 * cycles endlessly. The SYNC variants wait for the current waveform to reach the end of a cycle or finish before
	 * starting the new waveform.
	 * WARNING: bad things may happen if you delete the previous waveform before it has been synced to the new waveform.
	 * NOTE: Any hardware PWM started by gpioHardwarePWM will be cancelled.
	 * @param waveId &gt;= 0, as returned by gpioWaveCreate
	 * @param waveMode PI_WAVE_MODE_ONE_SHOT, PI_WAVE_MODE_REPEAT, PI_WAVE_MODE_ONE_SHOT_SYNC, PI_WAVE_MODE_REPEAT_SYNC
	 * @return Returns the number of DMA control blocks in the waveform if OK, otherwise PI_BAD_WAVE_ID, or PI_BAD_WAVE_MODE.
	 */
	public static native int gpioWaveTxSend(int waveId, int waveMode);

	/**
	 * This function transmits a chain of waveforms.
	 * NOTE: Any hardware PWM started by gpioHardwarePWM will be cancelled.
	 * The waves to be transmitted are specified by the contents of buf which contains an ordered list of wave_ids and
	 * optional command codes and related data.
	 * Each wave is transmitted in the order specified. A wave may occur multiple times per chain.
	 * A blocks of waves may be transmitted multiple times by using the loop commands. The block is bracketed by loop start and end commands. Loops may be nested.
	 * Delays between waves may be added with the delay command.
	 * The following command codes are supported:
	 * <pre>
	 * Name          Cmd &amp; Data  Meaning
	 * Loop Start    255 0       Identify start of a wave block
	 * Loop Repeat   255 1 x y   loop x + y*256 times
	 * Delay         255 2 x y   delay x + y*256 microseconds
	 * Loop Forever  255 3       loop forever
	 * </pre>
	 * If present Loop Forever must be the last entry in the chain.
	 * The code is currently dimensioned to support a chain with roughly 600 entries and 20 loop counters.
	 * @param buf The wave_ids and optional command codes
	 * @return Returns 0 if OK, otherwise PI_CHAIN_NESTING, PI_CHAIN_LOOP_CNT, PI_BAD_CHAIN_LOOP, PI_BAD_CHAIN_CMD,
	 * PI_CHAIN_COUNTER, PI_BAD_CHAIN_DELAY, PI_CHAIN_TOO_BIG, or PI_BAD_WAVE_ID.
	 */
	public static native int gpioWaveChain(byte[] buf);
	
	/**
	 * This function returns the id of the waveform currently being transmitted.
	 * @return Returns the waveform id or one of the following special values: PI_WAVE_NOT_FOUND (9998) - transmitted wave not found. PI_NO_TX_WAVE (9999) - no wave being transmitted.
	 */
	public static native int gpioWaveTxAt();
	
	/**
	 * This function checks to see if a waveform is currently being transmitted.
	 * @return Returns 1 if a waveform is currently being transmitted, otherwise 0.
	 */
	public static native int gpioWaveTxBusy();

	/**
	 * This function aborts the transmission of the current waveform.
	 * This function is intended to stop a waveform started in repeat mode.
	 * @return Returns 0 if OK.
	 */
	public static native int gpioWaveTxStop();

	/**
	 * This function returns the length in microseconds of the current waveform.
	 * @return The length in microseconds of the current waveform.
	 */
	
	public static native int gpioWaveGetMicros();
	/**
	 * This function returns the length in microseconds of the longest waveform created since gpioInitialise was called.
	 * @return The length in microseconds of the longest waveform created since gpioInitialise was called.
	 */
	public static native int gpioWaveGetHighMicros();
	
	/**
	 * This function returns the maximum possible size of a waveform in microseconds.
	 * @return The maximum possible size of a waveform in microseconds.
	 */
	public static native int gpioWaveGetMaxMicros();
	
	/**
	 * This function returns the length in pulses of the current waveform.
	 * @return The length in pulses of the current waveform.
	 */
	public static native int gpioWaveGetPulses();
	
	/**
	 * This function returns the length in pulses of the longest waveform created since gpioInitialise was called.
	 * @return The length in pulses of the longest waveform created since gpioInitialise was called.
	 */
	public static native int gpioWaveGetHighPulses();
	
	/**
	 * This function returns the length in pulses of the longest waveform created since gpioInitialise was called.
	 * @return The length in pulses of the longest waveform created since gpioInitialise was called.
	 */
	public static native int gpioWaveGetMaxPulses();
	
	/**
	 * This function returns the length in DMA control blocks of the current waveform.
	 * @return The length in DMA control blocks of the current waveform.
	 */
	public static native int gpioWaveGetCbs();
	
	/**
	 * This function returns the length in DMA control blocks of the longest waveform created since gpioInitialise was called.
	 * @return The length in DMA control blocks of the longest waveform created since gpioInitialise was called.
	 */
	public static native int gpioWaveGetHighCbs();
	
	/**
	 * This function returns the maximum possible size of a waveform in DMA control blocks.
	 * @return The maximum possible size of a waveform in DMA control blocks.
	 */
	public static native int gpioWaveGetMaxCbs();
}
