#include <pigpio.h>
#include <jni.h>

/* Returns a global reference to VM */
JavaVM* getGlobalJavaVM();

void throwException(JNIEnv* env, const char* exception, const char* message /*= NULL*/);
void throwIOException(JNIEnv* env, const char* message /*= NULL*/);
void throwRuntimeException(JNIEnv* env, const char* message /*= NULL*/);
void throwIllegalArgumentException(JNIEnv* env, const char* message /*= NULL*/);
jlong getEpochTimeMillis();
jlong getEpochTimeMillis2();
jlong getEpochTimeNanos();
jlong getJavaTimeNanos();

/* The VM calls this function upon loading the native library. */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved);

#define MAX_GPIO_PINS 50
