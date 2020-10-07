#include "pigpioj_util.h"

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>

static jint JNI_VERSION = JNI_VERSION_1_8;
/* Java VM interface */
static JavaVM* globalJavaVM = NULL;

jclass gpioPulseClass;
jfieldID gpioOnFieldId;
jfieldID gpioOffFieldId;
jfieldID usDelayFieldId;
jclass pigpioCallbackClass;
jmethodID callbackMethodId;

jobject listeners[MAX_GPIO_PINS];

/* The VM calls this function upon loading the native library. */
jint JNI_OnLoad(JavaVM* jvm, void* reserved) {
	// Obtain the JNIEnv from the VM and confirm JNI_VERSION
	JNIEnv* env;
	if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION) != JNI_OK) {
		return JNI_ERR;
	}

	/*
	 * https://stackoverflow.com/questions/10617735/in-jni-how-do-i-cache-the-class-methodid-and-fieldids-per-ibms-performance-r
	 * Class IDs must be registered as global references to maintain the viability
	 * of any associated Method ID / Field IDs. If this isn't done and the class
	 * is unloaded from the JVM, on class reload, the Method IDs / Field IDs may
	 * be different. If the Class ID is registered as a global reference, the
	 * associated Method IDs and Field IDs do not need to be registered as global
	 * references. Registering a Class ID as a global reference prevents the
	 * associated Java class from unloading, therefore stabilizing the Method ID
	 * / Field ID values. Global references, including the Class ID,s should be
	 * removed in JNI_OnUnload().
	 */

	// Temporary local reference holder
	jclass temp_local_class_ref;
	char* class_name;

	// STEP 1/3 : Load the class id
	class_name = "uk/pigpioj/GpioPulse";
	temp_local_class_ref = (*env)->FindClass(env, class_name);
	if ((*env)->ExceptionCheck(env) || temp_local_class_ref == NULL) {
		fprintf(stderr, "PigpioUtil: Error looking up class %s\n", class_name);
		return JNI_ERR;
	}

	// STEP 2/3 : Assign the ClassId as a Global Reference
	gpioPulseClass = (jclass) (*env)->NewGlobalRef(env, temp_local_class_ref);

	// STEP 3/3 : Delete the no longer needed local reference
	(*env)->DeleteLocalRef(env, temp_local_class_ref);

	class_name = "uk/pigpioj/PigpioCallback";
	temp_local_class_ref = (*env)->FindClass(env, class_name);
	if ((*env)->ExceptionCheck(env) || temp_local_class_ref == NULL) {
		fprintf(stderr, "PigpioUtil: Error looking up class %s\n", class_name);
		return JNI_ERR;
	}
	pigpioCallbackClass = (jclass) (*env)->NewGlobalRef(env, temp_local_class_ref);
	(*env)->DeleteLocalRef(env, temp_local_class_ref);

	// Now look up field / method id references

	gpioOnFieldId = (*env)->GetFieldID(env, gpioPulseClass, "gpioOn", "I");
	if ((*env)->ExceptionOccurred(env) || gpioOnFieldId == NULL) {
		fprintf(stderr, "PigpioUtil: Error: Unable to get gpioOn fieldId in GpioPulse class\n");
		return JNI_ERR;
	}
	gpioOffFieldId = (*env)->GetFieldID(env, gpioPulseClass, "gpioOff", "I");
	if ((*env)->ExceptionOccurred(env) || gpioOffFieldId == NULL) {
		fprintf(stderr, "PigpioUtil: Error: Unable to get gpioOff fieldId in GpioPulse class\n");
		return JNI_ERR;
	}
	usDelayFieldId = (*env)->GetFieldID(env, gpioPulseClass, "usDelay", "I");
	if ((*env)->ExceptionOccurred(env) || usDelayFieldId == NULL) {
		fprintf(stderr, "PigpioUtil: Error: Unable to get usDelay fieldId in GpioPulse class\n");
		return JNI_ERR;
	}

	callbackMethodId = (*env)->GetMethodID(env, pigpioCallbackClass, "callback", "(IZJJ)V");
	if ((*env)->ExceptionOccurred(env) || gpioOffFieldId == NULL) {
		fprintf(stderr, "PigpioUtil: Error: Unable to get callback methodId in PigpioCallback class\n");
		return JNI_ERR;
	}

	globalJavaVM = jvm;

	return JNI_VERSION;
}

// According to http://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/invocation.html#JNI_OnUnload
// The VM calls JNI_OnUnload when the class loader containing the native library is garbage collected.
void JNI_OnUnload(JavaVM *jvm, void *reserved) {
	JNIEnv* env;
	if ((*jvm)->GetEnv(jvm, (void **) &env, JNI_VERSION) != JNI_OK) {
		// Something is wrong but nothing we can do about this :(
		return;
	}
	(*env)->DeleteGlobalRef(env, gpioPulseClass);
	int gpio;
	for (gpio=0; gpio<MAX_GPIO_PINS; gpio++) {
		jobject listener = listeners[gpio];
		if (listener != NULL) {
			gpioSetISRFunc(gpio, 0, 0, NULL);
			(*env)->DeleteGlobalRef(env, listeners[gpio]);
			listeners[gpio] = NULL;
		}
	}
}

JavaVM* getGlobalJavaVM() {
	return globalJavaVM;
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

void throwRuntimeException(JNIEnv* env, const char* message /*= NULL*/) {
	throwException(env, "java/lang/RuntimeException", message);
}

void throwIllegalArgumentException(JNIEnv* env, const char* message /*= NULL*/) {
	throwException(env, "java/lang/IllegalArgumentException", message);
}

jlong getEpochTime() {
	struct timeval tp;
	gettimeofday(&tp, NULL);
	return ((jlong) tp.tv_sec) * 1000 + tp.tv_usec / 1000;
}

jlong getEpochTime2() {
	struct timespec ts;
	clock_gettime(CLOCK_REALTIME, &ts);
	return ((jlong) ts.tv_sec) * 1000 + ts.tv_nsec / 1000 / 1000;
}

jlong getJavaNanoTime() {
	struct timespec ts;
	/*int rc = */clock_gettime(CLOCK_MONOTONIC, &ts);
	return ((jlong) ts.tv_sec) * (1000 * 1000 * 1000) + ((jlong) ts.tv_nsec);
}
