#include <jni.h>
#include <pigpio.h>

/* Java VM interface */
static JavaVM* globalJavaVM = NULL;

#define MAX_GPIO_PINS 50

jobject listeners[MAX_GPIO_PINS];

void callbackFunction(int gpio, int level, uint32_t tick) {
	if (gpio < 0 || gpio >= MAX_GPIO_PINS) {
		printf("PigpioGpio Native: Error: callbackFunction invalid pin number (%d); must be 0..%d.\n", gpio, MAX_GPIO_PINS-1);
		return;
	}

	// Verify that the listener object exists
	jobject listener = listeners[gpio];
	if (listener == NULL) {
		printf("PigpioGpio Native: Error: JNI callbackFunction no listener object found [gpio=%d]\n", gpio);
		return;
	}

	// Attach to the current JVM thread
	JNIEnv* env;
	(*globalJavaVM)->AttachCurrentThread(globalJavaVM, (void**)&env, NULL);

	// Get the listener object class
	jclass listener_class = (*env)->GetObjectClass(env, listener);
	if (listener_class == NULL) {
		printf("PigpioGpio Native: Error: JNI callbackFunction could not resolve class for listener object [gpio=%d]\n", gpio);
	} else {
		// Verify that the callback method exists
		jmethodID callback_method = (*env)->GetMethodID(env, listener_class, "callback", "(IZJ)V");
		if (callback_method == NULL) {
			printf("PigpioGpio Native: Error: callbackFunction could not get 'callback' method id [gpio=%d]\n", gpio);
		} else {
			// invoke the callback method in the callback interface
			(*env)->CallVoidMethod(env, listener, callback_method, gpio, level, tick);
		}
	}

	// Detach from the current JVM thread
	(*globalJavaVM)->DetachCurrentThread(globalJavaVM);
}

void setISRFunc(JNIEnv* env, jclass clz, unsigned gpio, unsigned edge, int timeout, gpioISRFunc_t f) {
	int rc = gpioSetISRFunc(gpio, edge, timeout, f);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioSetISRFunc");
	}
}

void throwException(JNIEnv* env, const char* exception, const char* message /*= NULL*/) {
	jclass clazz = (*env)->FindClass(env, exception);
	if (clazz != NULL) {
		(*env)->ThrowNew(env, clazz, message);
		(*env)->DeleteLocalRef(env, clazz);
	}
}

void throwIOException(JNIEnv* env, const char* message /*= NULL*/) {
	throwException(env, "java/io/IOException", message);
}

/* The VM calls this function upon loading the native library. */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved) {
	printf("JNI_OnLoad()\n");
	globalJavaVM = jvm;

	return JNI_VERSION_1_2;
}

/* This function is called when the native library gets unloaded by the VM. */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* jvm, void* reserved) {
	printf("JNI_OnUnLoad()\n");
	gpioTerminate();
	globalJavaVM = NULL;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    initialise
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_initialise
  (JNIEnv* env, jclass clz) {
	int i;
	for (i = 0; i < MAX_GPIO_PINS; i++) {
		listeners[i] = NULL;
	}

	int version = gpioInitialise();
	if (version < 0) {
		throwIOException(env, "Error calling gpioInitialise");
		return 0;
	}

	return version;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    terminate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_terminate
  (JNIEnv* env, jclass clz) {
	gpioTerminate();
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    setMode
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_setMode
  (JNIEnv* env, jclass clz, jint gpio, jint mode) {
	int rc = gpioSetMode(gpio, mode);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioSetMode");
	}
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    getMode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_getMode
  (JNIEnv* env, jclass clz, jint gpio) {
	int mode = gpioGetMode(gpio);
	if (mode < 0) {
		throwIOException(env, "Error calling gpioGetMode");
		return 0;
	}
	return mode;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    setPullUpDown
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_setPullUpDown
  (JNIEnv* env, jclass clz, jint gpio, jint pud) {
	int rc = gpioSetPullUpDown(gpio, pud);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioSetPullUpDown");
	}
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    read
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_read
  (JNIEnv* env, jclass clz, jint gpio) {
	int value = gpioRead(gpio);
	if (value < 0) {
		throwIOException(env, "Error calling gpioRead");
		return 0;
	}
	return value;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    write
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_write
  (JNIEnv* env, jclass clz, jint gpio, jboolean value) {
	int rc = gpioWrite(gpio, value);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioWrite");
	}
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    setPWMDutyCycle
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_setPWMDutyCycle
  (JNIEnv* env, jclass clz, jint gpio, jint dutyCycle) {
	int rc = gpioPWM(gpio, dutyCycle);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioPWM");
	}
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    getPWMDutyCycle
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_getPWMDutyCycle
  (JNIEnv* env, jclass clz, jint gpio) {
	int duty_cycle = gpioGetPWMdutycycle(gpio);
	if (duty_cycle < 0) {
		throwIOException(env, "Error calling gpioGetPWMdutycycle");
		return 0;
	}
	return duty_cycle;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    setPWMRange
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_setPWMRange
  (JNIEnv* env, jclass clz, jint gpio, jint range) {
	int rc = gpioSetPWMrange(gpio, range);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioSetPWMrange");
	}
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    getPWMRange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_getPWMRange
  (JNIEnv* env, jclass clz, jint gpio) {
	int range = gpioGetPWMrange(gpio);
	if (range < 0) {
		throwIOException(env, "Error calling gpioGetPWMrange");
		return 0;
	}
	return range;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    getPWMRealRange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_getPWMRealRange
  (JNIEnv* env, jclass clz, jint gpio) {
	int range = gpioGetPWMrealRange(gpio);
	if (range < 0) {
		throwIOException(env, "Error calling gpioGetPWMrealRange");
		return 0;
	}
	return range;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    setPWMFrequency
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_setPWMFrequency
  (JNIEnv* env, jclass clz, jint gpio, jint frequency) {
	int rc = gpioSetPWMfrequency(gpio, frequency);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioSetPWMfrequency");
	}
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    getPWMFrequency
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_getPWMFrequency
  (JNIEnv* env, jclass clz, jint gpio) {
	int frequency = gpioGetPWMfrequency(gpio);
	if (frequency < 0) {
		throwIOException(env, "Error calling gpioGetPWMfrequency");
		return 0;
	}
	return frequency;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    setServoPulseWidth
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_setServoPulseWidth
  (JNIEnv* env, jclass clz, jint gpio, jint pulseWidth) {
	int rc = gpioServo(gpio, pulseWidth);
	if (rc < 0) {
		throwIOException(env, "Error calling gpioServo");
	}
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    getServoPulseWidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_getServoPulseWidth
  (JNIEnv* env, jclass clz, jint gpio) {
	int pulsewidth = gpioGetServoPulsewidth(gpio);
	if (pulsewidth < 0) {
		throwIOException(env, "Error calling gpioGetServoPulsewidth");
		return 0;
	}
	return pulsewidth;
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    setISRFunc
 * Signature: (IIILorg/diozero/internal/provider/pigpio/impl/PigpioCallback;)V
 */
JNIEXPORT void JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_setISRFunc
  (JNIEnv* env, jclass clz, jint gpio, jint edge, jint timeout, jobject listener) {
	if (gpio < 0 || gpio >= MAX_GPIO_PINS) {
		printf("PigpioGpio Native: Error: setISRFunc(%d) pin number must be 0..%d\n", gpio, MAX_GPIO_PINS-1);
		throwIOException(env, "Error in setISRFunc, invalid pin number");
		return;
	}

	if (listener == NULL) {
		setISRFunc(env, clz, gpio, edge, timeout, NULL);
		if (listeners[gpio] != NULL) {
			(*env)->DeleteGlobalRef(env, listeners[gpio]);
			listeners[gpio] = NULL;
		}

		return;
	}

	// Validate the listener object has the appropriate method signature
	jclass listener_class = (*env)->GetObjectClass(env, listener);
	if (listener_class == NULL) {
		printf("PigpioGpio Native: Error: setISRFunc(%d) could not get listener class\n", gpio);
		throwIOException(env, "Error in setISRFunc, could not get listener class");
		return;
	}

	// Verify that the object has a 'void callback(int, boolean, long)' method
	jmethodID callback_method = (*env)->GetMethodID(env, listener_class, "callback", "(IZJ)V");
	if (callback_method == NULL) {
		printf("PigpioGpio Native: Error: setISRFunc(%d) could not get callback method id\n", gpio);
		throwIOException(env, "Error in setISRFunc, could not get callback method id");
		return;
	}

	// Remove the previous listener if there was one
	if (listeners[gpio] != NULL) {
		setISRFunc(env, clz, gpio, edge, timeout, NULL);
		(*env)->DeleteGlobalRef(env, listeners[gpio]);
		listeners[gpio] = NULL;
	}

	// Register the new listener and pigpio callback
	listeners[gpio] = (*env)->NewGlobalRef(env, listener);
	setISRFunc(env, clz, gpio, edge, timeout, callbackFunction);
}

/*
 * Class:     org_diozero_internal_provider_pigpio_impl_PigpioGpio
 * Method:    getVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_diozero_internal_provider_pigpio_impl_PigpioGpio_getVersion
  (JNIEnv* env, jclass clz) {
	return gpioVersion();
}
