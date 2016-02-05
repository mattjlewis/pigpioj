#include <jni.h>
#include <pigpio.h>

void callbackFunction(int gpio, int level, uint32_t tick);
void setISRFunc(JNIEnv* env, jclass clz, unsigned gpio, unsigned edge, int timeout, gpioISRFunc_t f);
void throwException(JNIEnv* env, const char* exception, const char* message /*= NULL*/);
void throwIOException(JNIEnv* env, const char* message /*= NULL*/);
/* The VM calls this function upon loading the native library. */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved);
/* This function is called when the native library gets unloaded by the VM. */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* jvm, void* reserved);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    initialise
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_initialise
  (JNIEnv* env, jclass clz);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    terminate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_terminate
  (JNIEnv* env, jclass clz);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setMode
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_setMode
  (JNIEnv* env, jclass clz, jint gpio, jint mode);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getMode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getMode
  (JNIEnv* env, jclass clz, jint gpio);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPullUpDown
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPullUpDown
  (JNIEnv* env, jclass clz, jint gpio, jint pud);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    read
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_diozero_pigpioj_PigpioGpio_read
  (JNIEnv* env, jclass clz, jint gpio);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    write
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_write
  (JNIEnv* env, jclass clz, jint gpio, jboolean value);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPWMDutyCycle
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPWMDutyCycle
  (JNIEnv* env, jclass clz, jint gpio, jint dutyCycle);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMDutyCycle
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMDutyCycle
  (JNIEnv* env, jclass clz, jint gpio);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPWMRange
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPWMRange
  (JNIEnv* env, jclass clz, jint gpio, jint range);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMRange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMRange
  (JNIEnv* env, jclass clz, jint gpio);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMRealRange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMRealRange
  (JNIEnv* env, jclass clz, jint gpio);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPWMFrequency
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPWMFrequency
  (JNIEnv* env, jclass clz, jint gpio, jint frequency);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMFrequency
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMFrequency
  (JNIEnv* env, jclass clz, jint gpio);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setServoPulseWidth
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_setServoPulseWidth
  (JNIEnv* env, jclass clz, jint gpio, jint pulseWidth);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getServoPulseWidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getServoPulseWidth
  (JNIEnv* env, jclass clz, jint gpio);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setISRFunc
 * Signature: (IIILorg/diozero/internal/provider/pigpio/impl/PigpioCallback;)V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_setISRFunc
  (JNIEnv* env, jclass clz, jint gpio, jint edge, jint timeout, jobject listener);
/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getVersion
  (JNIEnv* env, jclass clz);
