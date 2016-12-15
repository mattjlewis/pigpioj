#include "com_diozero_pigpioj_PigpioGpio.h"
#include "pigpioj_util.h"
#include <sys/time.h>

#define MAX_GPIO_PINS 50

jobject listeners[MAX_GPIO_PINS];

unsigned long long getEpochTime() {
	struct timeval tp;
	gettimeofday(&tp, NULL);
	return (unsigned long long)(tp.tv_sec) * 1000 + (unsigned long long)(tp.tv_usec) / 1000;
}

void callbackFunction(int gpio, int level, uint32_t tick) {
	// Attach to the current JVM thread
	JavaVM* vm = getGlobalJavaVM();
	JNIEnv* env;
	(*vm)->AttachCurrentThread(vm, (void**)&env, NULL);

	// Get the Java nano time as early as possible
	// TODO Can these class/method references be cached on start-up?
	jclass clz = (*env)->FindClass(env, "java/lang/System");
	jlong nano_time = -1;
	if (clz != NULL) {
		jmethodID method_id = (*env)->GetStaticMethodID(env, clz, "nanoTime", "()J");
		if (method_id != NULL) {
			nano_time = (*env)->CallStaticLongMethod(env, clz, method_id);
		}
	}

	// Now get the UNIX epoch time
	unsigned long long epoch_time = getEpochTime();

	if (gpio < 0 || gpio >= MAX_GPIO_PINS) {
		printf("PigpioGpio Native: Error: callbackFunction invalid pin number (%d); must be 0..%d.\n", gpio, MAX_GPIO_PINS-1);
		// Detach from the current JVM thread
		(*vm)->DetachCurrentThread(vm);
		return;
	}

	// Verify that the listener object exists
	jobject listener = listeners[gpio];
	if (listener == NULL) {
		printf("PigpioGpio Native: Error: JNI callbackFunction no listener object found [gpio=%d]\n", gpio);
		// Detach from the current JVM thread
		(*vm)->DetachCurrentThread(vm);
		return;
	}

	// Get the listener object class
	jclass listener_class = (*env)->GetObjectClass(env, listener);
	if (listener_class == NULL) {
		printf("PigpioGpio Native: Error: JNI callbackFunction could not resolve class for listener object [gpio=%d]\n", gpio);
	} else {
		// Verify that the callback method exists
		jmethodID callback_method = (*env)->GetMethodID(env, listener_class, "callback", "(IZJJ)V");
		if (callback_method == NULL) {
			printf("PigpioGpio Native: Error: callbackFunction could not get 'callback' method id [gpio=%d]\n", gpio);
		} else {
			// invoke the callback method in the callback interface
			jlong j_epoch_time = (jlong)epoch_time;
			jlong j_nano_time = (jlong)nano_time;
			(*env)->CallVoidMethod(env, listener, callback_method, gpio, level, j_epoch_time, j_nano_time);
		}
	}

	// Detach from the current JVM thread
	(*vm)->DetachCurrentThread(vm);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    initialise
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_initialise
  (JNIEnv* env, jclass clz) {
	int i;
	for (i = 0; i < MAX_GPIO_PINS; i++) {
		listeners[i] = NULL;
	}

	return gpioInitialise();
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    terminate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_diozero_pigpioj_PigpioGpio_terminate
  (JNIEnv* env, jclass clz) {
	gpioTerminate();
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getVersion
  (JNIEnv* env, jclass clz) {
	return gpioVersion();
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getMode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getMode
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioGetMode(gpio);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setMode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_setMode
  (JNIEnv* env, jclass clz, jint gpio, jint mode) {
	return gpioSetMode(gpio, mode);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPullUpDown
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPullUpDown
  (JNIEnv* env, jclass clz, jint gpio, jint pud) {
	return gpioSetPullUpDown(gpio, pud);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    read
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_read
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioRead(gpio);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    write
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_write
  (JNIEnv* env, jclass clz, jint gpio, jboolean value) {
	return gpioWrite(gpio, value);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMDutyCycle
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMDutyCycle
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioGetPWMdutycycle(gpio);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPWMDutyCycle
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPWMDutyCycle
  (JNIEnv* env, jclass clz, jint gpio, jint dutyCycle) {
	return gpioPWM(gpio, dutyCycle);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMRange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMRange
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioGetPWMrange(gpio);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPWMRange
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPWMRange
  (JNIEnv* env, jclass clz, jint gpio, jint range) {
	return gpioSetPWMrange(gpio, range);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMRealRange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMRealRange
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioGetPWMrealRange(gpio);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getPWMFrequency
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getPWMFrequency
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioGetPWMfrequency(gpio);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setPWMFrequency
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_setPWMFrequency
  (JNIEnv* env, jclass clz, jint gpio, jint frequency) {
	return gpioSetPWMfrequency(gpio, frequency);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    getServoPulseWidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_getServoPulseWidth
  (JNIEnv* env, jclass clz, jint gpio) {
	return gpioGetServoPulsewidth(gpio);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setServoPulseWidth
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_setServoPulseWidth
  (JNIEnv* env, jclass clz, jint gpio, jint pulseWidth) {
	return gpioServo(gpio, pulseWidth);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    setISRFunc
 * Signature: (IIILorg/diozero/internal/provider/pigpio/impl/PigpioCallback;)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_setISRFunc
  (JNIEnv* env, jclass clz, jint gpio, jint edge, jint timeout, jobject listener) {
	if (listener == NULL) {
		gpioSetISRFunc(gpio, edge, timeout, NULL);
		if (listeners[gpio] != NULL) {
			(*env)->DeleteGlobalRef(env, listeners[gpio]);
			listeners[gpio] = NULL;
		}

		return 0;
	}

	// Validate the listener object has the appropriate method signature
	jclass listener_class = (*env)->GetObjectClass(env, listener);
	if (listener_class == NULL) {
		printf("PigpioGpio Native: Error: setISRFunc(%d) could not get listener class\n", gpio);
		throwRuntimeException(env, "Error in setISRFunc, could not get class for listener object");
		return -1;
	}

	// Verify that the object has a 'void callback(int, boolean, long, long)' method
	jmethodID callback_method = (*env)->GetMethodID(env, listener_class, "callback", "(IZJJ)V");
	if (callback_method == NULL) {
		printf("PigpioGpio Native: Error: setISRFunc(%d) could not get callback method id\n", gpio);
		throwIllegalArgumentException(env, "Error in setISRFunc, could not get listener method id callback(IZJJ)V");
		return -1;
	}

	// Remove the previous listener if there was one
	if (listeners[gpio] != NULL) {
		gpioSetISRFunc(gpio, edge, timeout, NULL);
		(*env)->DeleteGlobalRef(env, listeners[gpio]);
		listeners[gpio] = NULL;
	}

	// Register the new listener and pigpio callback
	listeners[gpio] = (*env)->NewGlobalRef(env, listener);
	return gpioSetISRFunc(gpio, edge, timeout, callbackFunction);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    noiseFilter
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_noiseFilter
  (JNIEnv* env, jclass clz, jint gpio, jint steadyMs, jint activeMs) {
	return gpioNoiseFilter(gpio, steadyMs, activeMs);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    glitchFilter
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_glitchFilter
  (JNIEnv* env, jclass clz, jint gpio, jint steadyMs) {
	return gpioGlitchFilter(gpio, steadyMs);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    hardwareClock
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_hardwareClock
  (JNIEnv* env, jclass clz, jint gpio, jint clockFreq) {
	return gpioHardwareClock(gpio, clockFreq);
}

/*
 * Class:     com_diozero_pigpioj_PigpioGpio
 * Method:    hardwarePwm
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_diozero_pigpioj_PigpioGpio_hardwarePwm
  (JNIEnv* env, jclass clz, jint gpio, jint pwmFreq, jint pwmDuty) {
	return gpioHardwarePWM(gpio, pwmFreq, pwmDuty);
}
