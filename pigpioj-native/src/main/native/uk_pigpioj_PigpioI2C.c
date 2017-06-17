#include "uk_pigpioj_PigpioI2C.h"

#include "pigpioj_util.h"

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cOpen
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cOpen
  (JNIEnv* env, jclass clz, jint i2cBus, jint i2cAddr, jint i2cFlags) {
	return i2cOpen(i2cBus, i2cAddr, i2cFlags);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cClose
  (JNIEnv* env, jclass clz, jint handle) {
	return i2cClose(handle);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cWriteQuick
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cWriteQuick
  (JNIEnv* env, jclass clz, jint handle, jint bit) {
	return i2cWriteQuick(handle, bit);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cReadByte
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cReadByte
  (JNIEnv* env, jclass clz, jint handle) {
	return i2cReadByte(handle);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cWriteByte
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cWriteByte
  (JNIEnv* env, jclass clz, jint handle, jint bVal) {
	return i2cWriteByte(handle, bVal);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cReadByteData
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cReadByteData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg) {
	return i2cReadByteData(handle, i2cReg);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cWriteByteData
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cWriteByteData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg, jint bVal) {
	return i2cWriteByteData(handle, i2cReg, bVal);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cReadWordData
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cReadWordData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg) {
	return i2cReadWordData(handle, i2cReg);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cWriteWordData
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cWriteWordData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg, jint wVal) {
	return i2cWriteWordData(handle, i2cReg, wVal);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cProcessCall
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cProcessCall
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg, jint wVal) {
	return i2cProcessCall(handle, i2cReg, wVal);
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cReadBlockData
 * Signature: (II[B)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cReadBlockData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg, jbyteArray buf) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_read = i2cReadBlockData(handle, i2cReg, (char*)b);
	// mode = 0 - Copy back the content and free the buffer (b)
	(*env)->ReleaseByteArrayElements(env, buf, b, 0);

	return bytes_read;
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cWriteBlockData
 * Signature: (II[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cWriteBlockData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int rc = i2cWriteBlockData(handle, i2cReg, (char*)b, count);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseByteArrayElements(env, buf, b, JNI_ABORT);
	return rc;
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cReadI2CBlockData
 * Signature: (II[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cReadI2CBlockData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_read = i2cReadI2CBlockData(handle, i2cReg, (char*)b, count);
	// mode = 0 - Copy back the content and free the buffer (b)
	(*env)->ReleaseByteArrayElements(env, buf, b, 0);
	return bytes_read;
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cWriteI2CBlockData
 * Signature: (II[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cWriteI2CBlockData
  (JNIEnv* env, jclass clz, jint handle, jint i2cReg, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int rc = i2cWriteI2CBlockData(handle, i2cReg, (char*)b, count);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseByteArrayElements(env, buf, b, JNI_ABORT);

	return rc;
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cReadDevice
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cReadDevice
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int bytes_read = i2cReadDevice(handle, (char*)b, count);
	// mode = 0 - Copy back the content and free the buffer (b)
	(*env)->ReleaseByteArrayElements(env, buf, b, 0);
	return bytes_read;
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cWriteDevice
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_uk_pigpioj_PigpioI2C_i2cWriteDevice
  (JNIEnv* env, jclass clz, jint handle, jbyteArray buf, jint count) {
	jboolean is_copy;
	jbyte* b = (*env)->GetByteArrayElements(env, buf, &is_copy);
	int rc = i2cWriteDevice(handle, (char*)b, count);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseByteArrayElements(env, buf, b, JNI_ABORT);
	return rc;
}

/*
 * Class:     uk_pigpioj_PigpioI2C
 * Method:    i2cSwitchCombined
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_uk_pigpioj_PigpioI2C_i2cSwitchCombined
  (JNIEnv* env, jclass clz, jboolean setting) {
	i2cSwitchCombined(setting);
}
