#include "uk_pigpioj_PigpioSerial.h"

#include "pigpioj_util.h"

/*
 * Class:     uk_pigpioj_PigpioSerial
 * Method:    serOpen
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSerial_serOpen
  (JNIEnv* env, jclass clz, jstring sertty, jint baud, jint serFlags) {
	const char* s = (*env)->GetStringUTFChars(env, sertty, 0);
	int rc = serOpen((char*)s, baud, serFlags);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseStringUTFChars(env, sertty, s);
	return rc;
}

/*
 * Class:     uk_pigpioj_PigpioSerial
 * Method:    serClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSerial_serClose
  (JNIEnv* env, jclass clz, jint handle) {
	return serClose(handle);
}

/*
 * Class:     uk_pigpioj_PigpioSerial
 * Method:    serWriteByte
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSerial_serWriteByte
  (JNIEnv* env, jclass clz, jint handle, jint bVal) {
	return serWriteByte(handle, bVal);
}

/*
 * Class:     uk_pigpioj_PigpioSerial
 * Method:    serReadByte
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSerial_serReadByte
  (JNIEnv* env, jclass clz, jint handle) {
	return serReadByte(handle);
}

/*
 * Class:     uk_pigpioj_PigpioSerial
 * Method:    serWrite
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSerial_serWrite
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int rc = serWrite(handle, (char*)b, count);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseByteArrayElements(env, buf, b, JNI_ABORT);
	return rc;
}

/*
 * Class:     uk_pigpioj_PigpioSerial
 * Method:    serRead
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSerial_serRead
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_read = serRead(handle, (char*)b, count);
	// mode = 0 - Copy back the content and free the buffer (b)
	(*env)->ReleaseByteArrayElements(env, buf, b, 0);

	return bytes_read;
}

/*
 * Class:     uk_pigpioj_PigpioSerial
 * Method:    serDataAvailable
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioSerial_serDataAvailable
  (JNIEnv* env, jclass clz, jint handle) {
	return serDataAvailable(handle);
}
