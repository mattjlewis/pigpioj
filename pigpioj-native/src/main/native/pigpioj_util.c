#include "pigpioj_util.h"
#include <time.h>

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


/* The VM calls this function upon loading the native library. */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved) {
	globalJavaVM = jvm;

	return JNI_VERSION_1_8;
}
