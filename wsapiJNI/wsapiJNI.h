#ifdef  _WINDOWS
  #include "jni.h"
#else
  #include <jni.h>
#endif

#include "wsdef.h"

/* Header for class wsapiJNI */

#ifndef _Included_wsapiJNI
#define _Included_wsapiJNI
#ifdef __cplusplus
extern "C" {
#endif

#define wsapiJNI_TIMEOUT_MIN 1L
#define wsapiJNI_TIMEOUT_MAX 60L
#define wsapiJNI_TIMEOUT_DEF 5L

#define wsapiJNI_SIDE_FRONT 1L
#define wsapiJNI_SIDE_REAR  2L
#define wsapiJNI_SIDE_BOTH  3L
#define wsapiJNI_SIDE_DEF   wsapiJNI_SIDE_BOTH

#define wsapiJNI_RES_100_DPI 100L
#define wsapiJNI_RES_200_DPI 200L
#define wsapiJNI_RES_300_DPI 300L
#define wsapiJNI_RES_600_DPI 600L
#define wsapiJNI_RES_DEF     wsapiJNI_RES_600_DPI

#define wsapiJNI_COL_BW   1L
#define wsapiJNI_COL_GRAY 8L
#define wsapiJNI_COL_RGB  24L
#define wsapiJNI_COL_DEF  wsapiJNI_COL_RGB

#define wsapiJNI_ROT_0    0L
#define wsapiJNI_ROT_90   90L
#define wsapiJNI_ROT_180  180L
#define wsapiJNI_ROT_270  270L
#define wsapiJNI_ROT_DEF  wsapiJNI_ROT_0

#define wsapiJNI_FD_LEVEL_MIN 0L
#define wsapiJNI_FD_LEVEL_MAX 100L
#define wsapiJNI_FD_LEVEL_DEF wsapiJNI_FD_LEVEL_MAX

#define wsapiJNI_DROP_NONE  4L
#define wsapiJNI_DROP_RED   0L
#define wsapiJNI_DROP_GREEN 1L
#define wsapiJNI_DROP_BLUE  2L
#define wsapiJNI_DROP_DEF   wsapiJNI_DROP_NONE

#define wsapiJNI_COMP_NONE 0L

//  Errors in wsapiJNI
#define wsapiJNI_ERROR_SUCCESS      0
#define wsapiJNI_ERROR_INTERNAL     -1
#define wsapiJNI_ERROR_BUSY         -2

//  Errors in wsapi
#define wsapiJNI_ERROR_TIMEOUT      E_ERR_DOCTIMEOUT
#define wsapiJNI_ERROR_IO				    E_ERR_IO		    /** Input/output error */
#define wsapiJNI_ERROR_NO_DEVICE    E_ERR_NO_DEVICE
#define wsapiJNI_ERROR_MEMORY       E_ERR_NO_MEM    /** Insufficient memory */
#define wsapiJNI_ERROR_DISCONNECTED E_ERR_NOCONNECT

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved);
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved);

/*
 * Class:     wsapiJNI
 * Method:    error
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_wsapiJNI_error
  (JNIEnv *, jobject);

/*
 * Class:     wsapiJNI
 * Method:    scan
 * Signature: ([Ljava/lang/Byte;LwsapiJNI/scanOption;)I
 */
JNIEXPORT jint JNICALL Java_wsapiJNI_scan
  (JNIEnv *, jobject, jobjectArray, jobject);

/*
 * Class:     wsapiJNI
 * Method:    getDeviceInfo
 * Signature: ([LwsapiJNI/DeviceInfo;)I
 */
JNIEXPORT jint JNICALL Java_wsapiJNI_getDeviceInfo
  (JNIEnv *, jobject, jobjectArray);

#ifdef __cplusplus
}
#endif
#endif
