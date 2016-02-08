#include <pigpio.h>
#include <jni.h>

/* Returns a global reference to VM */
JavaVM* getGlobalJavaVM();

void throwException(JNIEnv* env, const char* exception, const char* message /*= NULL*/);
void throwIOException(JNIEnv* env, const char* message /*= NULL*/);
