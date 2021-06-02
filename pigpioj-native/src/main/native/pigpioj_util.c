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
jclass i2cMessageClass;
jfieldID i2cMessageAddrField;
jfieldID i2cMessageFlagsField;
jfieldID i2cMessageLenField;

jobject listeners[MAX_GPIO_PINS];

#define SEC_IN_NANOSECS  1000000000ULL

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

	// Cache the I2CMessage class and fields on startup
	class_name = "uk/pigpioj/PiI2CMessage";
	jclass i2c_message_class = (*env)->FindClass(env, class_name);
	if ((*env)->ExceptionCheck(env) || i2c_message_class == NULL) {
		fprintf(stderr, "Error, could not find class '%s'\n", class_name);
		return JNI_ERR;
	}
	char* field_name = "addr";
	char* signature = "I";
	i2cMessageAddrField = (*env)->GetFieldID(env, i2c_message_class, field_name, signature);
	field_name = "flags";
	signature = "I";
	i2cMessageFlagsField = (*env)->GetFieldID(env, i2c_message_class, field_name, signature);
	field_name = "len";
	signature = "I";
	i2cMessageLenField = (*env)->GetFieldID(env, i2c_message_class, field_name, signature);
	(*env)->DeleteLocalRef(env, i2c_message_class);

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

jlong getEpochTimeMillis() {
	struct timeval tp;
	/*int rc = */gettimeofday(&tp, NULL);
	return tp.tv_sec * 1000ull + tp.tv_usec / 1000;
}

jlong getEpochTimeMillis2() {
	struct timespec ts;
	/*int rc = */clock_gettime(CLOCK_REALTIME, &ts);
	return ts.tv_sec * 1000ull + ts.tv_nsec / 1000000;
}

jlong getEpochTimeNanos() {
	struct timespec ts;
	/*int rc = */clock_gettime(CLOCK_REALTIME, &ts);
	return ts.tv_sec * SEC_IN_NANOSECS + ts.tv_nsec;
}

jlong getJavaTimeNanos() {
	struct timespec ts;
	/*int rc = */clock_gettime(CLOCK_MONOTONIC, &ts);
	return ts.tv_sec * 1000000000ull + ts.tv_nsec;
}

// See: http://stas-blogspot.blogspot.co.uk/2012/02/what-is-behind-systemnanotime.html
// http://hg.openjdk.java.net/jdk7/jdk7/hotspot/file/9b0ca45cd756/src/os/linux/vm/os_linux.cpp
jlong javaTimeNanos() {
	int supports_monotonic_clock = 1;
	if (supports_monotonic_clock) {
		struct timespec tp;
		/*int status = */clock_gettime(CLOCK_MONOTONIC, &tp);
		//assert(status == 0, "gettime error");
		jlong result = ((jlong) tp.tv_sec) * SEC_IN_NANOSECS + (jlong) tp.tv_nsec;
		return result;
	} else {
		struct timeval time;
		/*int status = */gettimeofday(&time, NULL);
		//assert(status != -1, "linux error");
		jlong usecs = ((jlong) time.tv_sec) * (1000 * 1000) + ((jlong) time.tv_usec);
		return 1000 * usecs;
	}
}
