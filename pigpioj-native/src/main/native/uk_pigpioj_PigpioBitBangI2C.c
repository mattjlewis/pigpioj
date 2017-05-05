#include "com_diozero_pigpioj_PigpioBitBangI2C.h"
#include "pigpioj_util.h"

/*
 * Class:     com_diozero_pigpioj_PigpioBitBangI2C
 * Method:    bbI2COpen
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioBitBangI2C_bbI2COpen
  (JNIEnv* env, jclass clz, jint sda, jint scl, jint baud) {
	return bbI2COpen(sda, scl, baud);
}

/*
 * Class:     com_diozero_pigpioj_PigpioBitBangI2C
 * Method:    bbI2CClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioBitBangI2C_bbI2CClose
  (JNIEnv* env, jclass clz, jint sda) {
	return bbI2CClose(sda);
}

/*
 * Class:     com_diozero_pigpioj_PigpioBitBangI2C
 * Method:    bbI2CZIP
 * Signature: (I[BI[BI)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioBitBangI2C_bbI2CZip
  (JNIEnv* env, jclass clz, jint sda, jbyteArray inBuf, jint inLen, jbyteArray outBuf, jint outLen) {
	jboolean is_copy;
	jbyte* in_buf = (*env)->GetByteArrayElements(env, inBuf, &is_copy);
	jbyte* out_buf = (*env)->GetByteArrayElements(env, outBuf, &is_copy);
	int bytes_transferred = bbI2CZip(sda, (char*)in_buf, inLen, (char*)out_buf, outLen);
	// mode = JNI_ABORT - No change hence free the buffer without copying back the possible changes
	(*env)->ReleaseByteArrayElements(env, inBuf, in_buf, JNI_ABORT);
	// mode = 0 - Copy back the content and free the buffer (outBuf)
	(*env)->ReleaseByteArrayElements(env, outBuf, out_buf, 0);
	return bytes_transferred;
}
