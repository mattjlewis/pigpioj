#include "pigpioj_util.h"

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>

/* Java VM interface */
static JavaVM* globalJavaVM = NULL;

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


/* The VM calls this function upon loading the native library. */
jint JNI_OnLoad(JavaVM* jvm, void* reserved) {
	globalJavaVM = jvm;

	return JNI_VERSION_1_8;
}
