#include "uk_pigpioj_PigpioBitBangSerial.h"

#include <stdlib.h>
#include "pigpioj_util.h"

//Author: 9903286
//RX
/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    serialReadOpen
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_serialReadOpen
  (JNIEnv* env, jclass clz, jint gpio, jint baud, jint data_bits) {
	return gpioSerialReadOpen(gpio, baud, data_bits);
}

/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    serialReadInvert
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_serialReadInvert
  (JNIEnv* env, jclass clz, jint gpio, jboolean invert) {
	return gpioSerialReadInvert(gpio, invert);
}
/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    serialRead
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_uk_pigpioj_PigpioBitBangSerial_serialRead
  (JNIEnv* env, jclass clz, jint gpio, jint maxSize) {
	char *buf = (char*) malloc(maxSize);
	int bytes = gpioSerialRead(gpio, buf, maxSize);
	
	jbyteArray result = (*env)->NewByteArray(env, bytes);
	(*env)->SetByteArrayRegion(env, result, 0, bytes, (jbyte *)buf);
	
	free(buf);
	
	return result;
}

/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    serialReadClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_serialReadClose
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioSerialReadClose(gpio);
}

//TX
/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    waveClear
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_waveClear
  (JNIEnv* env, jclass clz) {
	return gpioWaveClear();
}

/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    waveAddSerial
 * Signature: (IIIIII[B)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_waveAddSerial
  (JNIEnv* env, jclass clz, jint gpio, jint baud, jint data_bits, jint stop_bits, jint offset, jint numBytes, jbyteArray bytes) {
	jboolean is_copy;
	jbyte* strBytes = (*env)->GetByteArrayElements(env, bytes, &is_copy);
	int result = gpioWaveAddSerial(gpio, baud, data_bits, stop_bits, offset, numBytes, (char*) strBytes);
	(*env)->ReleaseByteArrayElements(env, bytes, strBytes, 0);
	return result;
}

/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    waveCreate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_waveCreate
  (JNIEnv* env, jclass clz) {
	return gpioWaveCreate();
}

/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    waveTXSend
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_waveTXSend
  (JNIEnv* env, jclass clz, jint wave_id, jint wave_mode) {
	return gpioWaveTxSend(wave_id, wave_mode);
}

/*
 * Class:     uk_pigpioj_PigpioBitBangSerial
 * Method:    waveDelete
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioBitBangSerial_waveDelete
  (JNIEnv* env, jclass clz, jint wave_id) {
	return gpioWaveDelete(wave_id);
}


















