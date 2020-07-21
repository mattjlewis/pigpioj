#include "uk_pigpioj_PigpioSPI.h"

#include "pigpioj_util.h"

/*
 * Class:     uk_pigpioj_PigpioSPI
 * Method:    spiOpen
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSPI_spiOpen
  (JNIEnv* env, jclass clz, jint spiChan, jint baud, jint spiFlags) {
	return spiOpen(spiChan, baud, spiFlags);
}

/*
 * Class:     uk_pigpioj_PigpioSPI
 * Method:    spiClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSPI_spiClose
  (JNIEnv* env, jclass clz, jint handle) {
	return spiClose(handle);
}

/*
 * Class:     uk_pigpioj_PigpioSPI
 * Method:    spiRead
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSPI_spiRead
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_read = spiRead(handle, (char*)b, count);
	// mode = 0 - Copy back the content and free the buffer (b)
	(*env)->ReleaseByteArrayElements(env, buf, b, 0);
	return bytes_read;
}

/*
 * Class:     uk_pigpioj_PigpioSPI
 * Method:    spiWrite
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSPI_spiWrite
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint offset, jint length) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_written = spiWrite(handle, (char*) &b[offset], length);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseByteArrayElements(env, buf, b, JNI_ABORT);
	return bytes_written;
}

/*
 * Class:     uk_pigpioj_PigpioSPI
 * Method:    spiXfer
 * Signature: (I[B[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSPI_spiXfer
  (JNIEnv* env, jclass clz, jint handle, jbyteArray txBuf, jbyteArray rxBuf, jint count) {
	jboolean is_copy;
	jbyte* tx = (*env)->GetByteArrayElements(env, txBuf, &is_copy);
	jbyte* rx = (*env)->GetByteArrayElements(env, rxBuf, &is_copy);
	int bytes_transferred = spiXfer(handle, (char*)tx, (char*)rx, count);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseByteArrayElements(env, txBuf, tx, JNI_ABORT);
	// mode = 0 - Copy back the content and free the buffer (rx)
	(*env)->ReleaseByteArrayElements(env, rxBuf, rx, 0);
	return bytes_transferred;
}
