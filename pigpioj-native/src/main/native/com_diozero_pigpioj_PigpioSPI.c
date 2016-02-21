#include "com_diozero_pigpioj_PigpioSPI.h"
#include "pigpioj_util.h"

/*
 * Class:     com_diozero_pigpioj_PigpioSPI
 * Method:    spiOpen
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioSPI_spiOpen
  (JNIEnv* env, jclass clz, jint spiChan, jint baud, jint spiFlags) {
	return spiOpen(spiChan, baud, spiFlags);
}

/*
 * Class:     com_diozero_pigpioj_PigpioSPI
 * Method:    spiClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioSPI_spiClose
  (JNIEnv* env, jclass clz, jint handle) {
	return spiClose(handle);
}

/*
 * Class:     com_diozero_pigpioj_PigpioSPI
 * Method:    spiRead
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioSPI_spiRead
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_read = spiRead(handle, (char*)b, count);
	jint mode = 0;
	(*env)->ReleaseByteArrayElements(env, buf, b, mode);
	return bytes_read;
}

/*
 * Class:     com_diozero_pigpioj_PigpioSPI
 * Method:    spiWrite
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioSPI_spiWrite
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_written = spiWrite(handle, (char*)b, count);
	jint mode = 0;
	(*env)->ReleaseByteArrayElements(env, buf, b, mode);
	return bytes_written;
}

/*
 * Class:     com_diozero_pigpioj_PigpioSPI
 * Method:    spiXfer
 * Signature: (I[B[BI)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioSPI_spiXfer
  (JNIEnv* env, jclass clz, jint handle, jbyteArray txBuf, jbyteArray rxBuf, jint count) {
	jboolean is_copy;
	jbyte* tx = (*env)->GetByteArrayElements(env, txBuf, &is_copy);
	jbyte* rx = (*env)->GetByteArrayElements(env, rxBuf, &is_copy);
	int bytes_transferred = spiXfer(handle, (char*)tx, (char*)rx, count);
	jint mode = 0;
	(*env)->ReleaseByteArrayElements(env, txBuf, tx, mode);
	mode = 0;
	(*env)->ReleaseByteArrayElements(env, rxBuf, rx, mode);
	return bytes_transferred;
}
