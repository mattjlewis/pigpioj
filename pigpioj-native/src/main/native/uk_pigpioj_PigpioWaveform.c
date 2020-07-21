#include "uk_pigpioj_PigpioWaveform.h"

#include "pigpioj_util.h"

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveClear
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveClear
  (JNIEnv *env, jclass clz) {
	return gpioWaveClear();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveAddNew
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveAddNew
  (JNIEnv *env, jclass clz) {
	return gpioWaveAddNew();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveAddGeneric
 * Signature: (I[Luk/pigpioj/GpioPulse;)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveAddGeneric
  (JNIEnv *env, jclass clz, jobjectArray pulses) {
	jclass gpio_pulse_class = (*env)->FindClass(env, "uk/pigpioj/GpioPulse");
	if ((*env)->ExceptionOccurred(env)) {
		fprintf(stderr, "PigpioWaveform: Error: Unable to get GpioPulse class\n");
		return -1;
	}
	jfieldID gpio_on_field_id = (*env)->GetFieldID(env, gpio_pulse_class, "gpioOn", "I");
	if ((*env)->ExceptionOccurred(env)) {
		fprintf(stderr, "PigpioWaveform: Error: Unable to get gpioOn fieldId in GpioPulse class\n");
		return -1;
	}
	jfieldID gpio_off_field_id = (*env)->GetFieldID(env, gpio_pulse_class, "gpioOff", "I");
	if ((*env)->ExceptionOccurred(env)) {
		fprintf(stderr, "PigpioWaveform: Error: Unable to get gpioOff fieldId in GpioPulse class\n");
		return -1;
	}
	jfieldID us_delay_field_id = (*env)->GetFieldID(env, gpio_pulse_class, "usDelay", "I");
	if ((*env)->ExceptionOccurred(env)) {
		fprintf(stderr, "PigpioWaveform: Error: Unable to get usDelay fieldId in GpioPulse class\n");
		return -1;
	}

	int array_length = (*env)->GetArrayLength(env, pulses);
	// Convert pulses to gpioPulse_t
	gpioPulse_t pulse_array[array_length];
	int i;
	for (i=0; i<array_length; i++) {
		jobject element = (*env)->GetObjectArrayElement(env, pulses, i);
		if ((*env)->ExceptionOccurred(env)) {
			fprintf(stderr, "PigpioWaveform: Error: Unable to get object array element %d\n", i);
			return -1;
		}

		pulse_array[i].gpioOn = (uint32_t) (*env)->GetIntField(env, element, gpio_on_field_id);
		pulse_array[i].gpioOff = (uint32_t) (*env)->GetIntField(env, element, gpio_off_field_id);
		pulse_array[i].usDelay = (uint32_t) (*env)->GetIntField(env, element, us_delay_field_id);

		(*env)->DeleteLocalRef(env, element);
	}
	return gpioWaveAddGeneric(array_length, pulse_array);
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveAddSerial
 * Signature: (IIIII[C)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveAddSerial
  (JNIEnv *env, jclass clz, jint gpio, jint baud, jint dataBits, jint stopBits, jint offset, jbyteArray str) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, str, &is_copy);
	int rc = gpioWaveAddSerial(gpio, baud, dataBits, stopBits, offset, (*env)->GetArrayLength(env, str), (char*) b);
	(*env)->ReleaseByteArrayElements(env, str, b, JNI_ABORT);
	return rc;
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveCreate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveCreate
  (JNIEnv *env, jclass clz) {
	return gpioWaveCreate();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveCreatePad
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveCreatePad
  (JNIEnv *env, jclass clz, jint pctCB, jint pctBOOL, jint pctTOOL) {
	return gpioWaveCreatePad(pctCB, pctBOOL, pctTOOL);
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveDelete
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveDelete
  (JNIEnv *env, jclass clz, jint waveId) {
	return gpioWaveDelete(waveId);
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveTxSend
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveTxSend
  (JNIEnv *env, jclass clz, jint waveId, jint waveMode) {
	return gpioWaveTxSend(waveId, waveMode);
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveChain
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveChain
  (JNIEnv *env, jclass clz, jbyteArray buf) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int rc = gpioWaveChain((char*) b, (*env)->GetArrayLength(env, buf));
	(*env)->ReleaseByteArrayElements(env, buf, b, JNI_ABORT);
	return rc;
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveTxAt
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveTxAt
  (JNIEnv *env, jclass clz) {
	return gpioWaveTxAt();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveTxBusy
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveTxBusy
  (JNIEnv *env, jclass clz) {
	return gpioWaveTxBusy();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveTxStop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveTxStop
  (JNIEnv *env, jclass clz) {
	return gpioWaveTxStop();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetMicros
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetMicros
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetMicros();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetHighMicros
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetHighMicros
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetHighMicros();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetMaxMicros
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetMaxMicros
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetMaxMicros();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetPulses
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetPulses
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetPulses();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetHighPulses
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetHighPulses
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetHighPulses();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetMaxPulses
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetMaxPulses
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetMaxPulses();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetCbs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetCbs
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetCbs();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetHighCbs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetHighCbs
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetHighCbs();
}

/*
 * Class:     uk_pigpioj_PigpioWaveform
 * Method:    gpioWaveGetMaxCbs
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioWaveform_gpioWaveGetMaxCbs
  (JNIEnv *env, jclass clz) {
	return gpioWaveGetMaxCbs();
}
