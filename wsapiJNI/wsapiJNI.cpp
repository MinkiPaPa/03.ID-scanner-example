// wsapiJNI.cpp : Defines the entry point for the DLL application.
/*

  This code is not freeware but you can modify it!
  Use of this code is at you own risk.
  Do not duplicate or distribute without written permission from Voim Technologies Inc.

*/

#ifdef  _WINDOWS
  #include "stdafx.h"
#endif

#include <stdio.h>

#include "wsapiJNI.h"

#include <malloc.h>

#ifdef _WINDOWS
  #include <string>
#else
  #include <string.h>
  #include <mutex>
  #include <dlfcn.h>
  #include <libgen.h>
  #include <limits.h>
  #include <unistd.h>

  #define HMODULE void*
#endif

HMODULE     wsapi_handle = NULL;
WSHANDLE    ws_handle = NULL;

#ifdef _WINDOWS
#else
  std::mutex  exclusive_device;
#endif

WSHANDLE (*wsapi_open)(unsigned int devicetype, meta_notify_cb cb_notify) = 0;
void (*wsapi_close)(WSHANDLE handle) = 0;
int (*wsapi_write)(WSHANDLE handle, char* szValue, int length) = 0;
size_t (*wsapi_read)(WSHANDLE handle, char* szValue, int* length) = 0;

int isOnline = 0;

char TXT_MODEL_NAME[] = "IDS600";

void UnloadProc()
{
  if (wsapi_handle != 0)
  {
#ifdef  _WINDOWS
    FreeLibrary(wsapi_handle);
#else
    dlclose(wsapi_handle);
#endif
  }

  wsapi_open  = 0;
  wsapi_close = 0;
  wsapi_write = 0;
  wsapi_read  = 0;
}

int OnLoadProc(char* szPath)
{
  int result = 0;

  if (wsapi_handle == NULL)
  {
#ifdef  _WINDOWS
    char szWsApiPath[MAX_PATH];
    strcpy(szWsApiPath, szPath);
    strcat(szWsApiPath, "\\");
    strcat(szWsApiPath, "wsapi.dll");

    wsapi_handle = LoadLibrary(szWsApiPath);
    int error = 0;
    if ((error = GetLastError()) != NULL)
    {
        fprintf (stderr, "%d\n", error);
#else
    char szWsApiPath[PATH_MAX];
    strcpy(szWsApiPath, szPath);
    strcat(szWsApiPath, "/");
    strcat(szWsApiPath, "libwsapi.so");

    wsapi_handle = dlopen(szWsApiPath, RTLD_NOW);
    char *error = NULL;
    if ((error = dlerror()) != NULL)
    {
        fprintf (stderr, "%s\n", error);
#endif
    }
    else
    {
#ifdef  _WINDOWS
      wsapi_open  = (WSHANDLE (*)(unsigned int, meta_notify_cb))GetProcAddress(wsapi_handle, "wsOpen");;
      wsapi_close = (void (*)(WSHANDLE))GetProcAddress(wsapi_handle, "wsClose");;
      wsapi_write = (int (*)(WSHANDLE, char*, int))GetProcAddress(wsapi_handle, "wsWrite");;
      wsapi_read  = (size_t (*)(WSHANDLE, char*, int*))GetProcAddress(wsapi_handle, "wsRead");;
#else
      wsapi_open  = (WSHANDLE (*)(unsigned int, meta_notify_cb))dlsym(wsapi_handle, "wsOpen");;
      wsapi_close = (void (*)(WSHANDLE))dlsym(wsapi_handle, "wsClose");;
      wsapi_write = (int (*)(WSHANDLE, char*, int))dlsym(wsapi_handle, "wsWrite");;
      wsapi_read  = (size_t (*)(WSHANDLE, char*, int*))dlsym(wsapi_handle, "wsRead");;
#endif

      if ((wsapi_open  == 0) ||
          (wsapi_close == 0) ||
          (wsapi_write == 0) ||
          (wsapi_read  == 0))
      {
        UnloadProc();
      }
      else
      {
#ifdef  _WINDOWS
        SetLastError(0);
#else
        dlerror();    /* Clear any existing error */
#endif
        result = JNI_VERSION_1_4;
      }
    }
  }

  return result;
}

void OnUnloadProc()
{
  if (wsapi_handle != 0)
  {
    if (ws_handle != NULL)
    {
      if (wsapi_close != NULL)
      {
        wsapi_close(ws_handle);
      }
    }

    UnloadProc();
  }
}

#ifdef _WINDOWS
  char *dirname(char* path)
  {
    char *result = strrchr(path, '\\');
    if (result != NULL)
      *result = NULL;
    return  result;
  }
#endif

void getSelfPath(char* szSelfPath)
{
#ifdef _WINDOWS
  #ifndef GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS
    #define GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS        0x00000004
    #define GET_MODULE_HANDLE_EX_FLAG_UNCHANGED_REFCOUNT  0x00000002

    HMODULE hKernel32 = ::GetModuleHandleA("KERNEL32.DLL");

    typedef BOOL (WINAPI *procGetModuleHandleEx)(DWORD, LPCTSTR, HMODULE*);
    procGetModuleHandleEx GetModuleHandleEx = (procGetModuleHandleEx)GetProcAddress(hKernel32,
    #ifdef UNICODE
      "GetModuleHandleExW");
    #else
      "GetModuleHandleExA");
    #endif // !UNICODE
  #endif

  HMODULE hm = NULL;

  if (!GetModuleHandleEx(GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS |
          GET_MODULE_HANDLE_EX_FLAG_UNCHANGED_REFCOUNT,
          (LPCSTR) &getSelfPath,
          &hm))
  {
    int ret = GetLastError();
    fprintf(stderr, "GetModuleHandle returned %d\n", ret);
  }

  GetModuleFileName(hm, szSelfPath, MAX_PATH);
#else
  //  get self path
  Dl_info info;
  info.dli_fname  = NULL;
  info.dli_fbase  = NULL;
  info.dli_sname  = NULL;
  info.dli_saddr  = NULL;

  dladdr((void*)JNI_OnLoad, &info);

  strcpy(szSelfPath, info.dli_fname);
#endif
  dirname(szSelfPath);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
  jint result = wsapiJNI_ERROR_INTERNAL;

#ifdef _WINDOWS
  char szSelfPath[MAX_PATH+1];
#else
  char szSelfPath[PATH_MAX];
#endif

  getSelfPath(szSelfPath);
  if (OnLoadProc(szSelfPath) > 0)
  {
    result = JNI_VERSION_1_4;
  }

  return result;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved)
{
  OnUnloadProc();
}

void Notify(WSHANDLE handle, int event)
{
  switch( event )
  {
  case 1:
    isOnline = 1;
    break;
  case 2:
    isOnline = 0;
    break;
  default:
    fprintf( stderr, "\nDevice Open Error[%d]\n", event );
  }

  fprintf( stderr, "%s %s\n", TXT_MODEL_NAME, (isOnline == 1 ? "ON" : "OFF" ) ); fflush( stderr );
}

int open()
{
  int result = wsapiJNI_ERROR_INTERNAL;

#ifdef	_WINDOWS  
	isOnline = 1;
#else
	isOnline = 0;
#endif	
  
  if (wsapi_handle == 0)
  {
    fprintf (stderr, "wsapi not loaded\n");
  }
  else
  {
    ws_handle = wsapi_open(WS_DEVICE_ISV, Notify);
    if (ws_handle != 0)
    {
      result = wsapiJNI_ERROR_SUCCESS;
    }
  }

  return result;
}

int close()
{
  int result = wsapiJNI_ERROR_INTERNAL;

  if (wsapi_handle == 0)
  {
    fprintf (stderr, "wsapi not loaded\n");
  }
  else
  {
    if (ws_handle == NULL)
    {
      fprintf (stderr, "ws not opened\n");
    }
    else
    {
      wsapi_close(ws_handle);
      ws_handle = 0;
      result = 1;
    }
  }

  return result;
}

int write(const char* outValue, int length)
{
  int result = wsapiJNI_ERROR_INTERNAL;

  if (wsapi_handle == 0)
  {
    fprintf (stderr, "wsapi not loaded\n");
  }
  else
  {
    if (ws_handle == NULL)
    {
      fprintf (stderr, "ws not opened\n");
    }
    else
    {
//fprintf( stderr, "\nWrite \"%s\":%d\n", outValue, length );
      int returnValue = wsapi_write(ws_handle, (char*)outValue, length+1);
      if (returnValue == 0)
      {
        result = wsapiJNI_ERROR_SUCCESS;
      }
      else
      {
        fprintf( stderr, "\nDevice Write Error[%d]\n", returnValue );
      }
    }
  }

  return result;
}

int read(char* inValue, int* length)
{
  int result = wsapiJNI_ERROR_INTERNAL;

  if (wsapi_handle == 0)
  {
    fprintf (stderr, "wsapi not loaded\n");
  }
  else
  {
    if (ws_handle == NULL)
    {
      fprintf (stderr, "ws not opened\n");
    }
    else
    {
      int returnValue = (int)wsapi_read(ws_handle, (char*)inValue, length);
      if (returnValue > 0)//== *length)
      {
        *length = returnValue;
//inValue[MAX_RESPONSE] = NULL;
//fprintf( stderr, "\nRead \"%s\":%d\n", inValue, *length );
        result = wsapiJNI_ERROR_SUCCESS;
      }
      else
      {
        fprintf( stderr, "\nDevice Read Error/Length[%d]\n", returnValue );
      }
    }
  }

  return result;
}

int writeToDevice(const char* outValue, int length)
{
  int result = wsapiJNI_ERROR_INTERNAL;

  if (outValue == NULL)
  {
    fprintf (stderr, "invalid parameter NULL pointer\n");
  }
  else
  if (length < 1)
  {
    fprintf (stderr, "invalid parameter\n");
  }
  else
  {
    result = write(outValue, length);
  }

  return result;
}

int readFromDevice(char* inValue, int* length)
{
  int  result = wsapiJNI_ERROR_INTERNAL;

  if (inValue == NULL)
  {
    fprintf (stderr, "invalid parameter NULL pointer\n");
  }
  else
  if (*length < 1)
  {
    fprintf (stderr, "invalid parameter\n");
  }
  else
  {
    result = read(inValue, length);
  }

  return result;
}

int readResponseStringValue(char *szValue, int *length, int valueOffset, int valueLengthMax, int valueLengthMin, const char* responseHeader)
{
  int result = wsapiJNI_ERROR_INTERNAL;

  if (valueLengthMax >= valueLengthMin)
  {
    int responseLengthMax = valueLengthMax + valueOffset + 1;
    int responseLengthMin = valueLengthMin + valueOffset + 1;

    if (*length >= responseLengthMax)
    {
      char  stringBuffer[MAX_RESPONSE+1];
      int   bufferLength = MAX_RESPONSE;

      result = readFromDevice(stringBuffer, &bufferLength);
      if ((result == wsapiJNI_ERROR_SUCCESS) && (bufferLength >= responseLengthMin) && (bufferLength <= responseLengthMax))
      {
        if (strncmp(stringBuffer, responseHeader, strlen(responseHeader) - 1) == 0)
        {
          memcpy(szValue, &stringBuffer[valueOffset], bufferLength - valueOffset);
          szValue[bufferLength - valueOffset - 1] = '\0';
          *length = bufferLength - valueOffset;
        }
        else
        {
          fprintf (stderr, "Response header not matched\n");
          result = wsapiJNI_ERROR_INTERNAL;
        }
      }
      else
      {
        fprintf (stderr, "Response header length not matched %d : %d\n", responseLengthMax, bufferLength);
        result = wsapiJNI_ERROR_INTERNAL;
      }
    }
    else
    {
      fprintf (stderr, "invalid parameter\n");
    }
  }
  else
  {
    fprintf (stderr, "invalid parameter\n");
  }

  return result;
}

int getResponseStringValue(char *szValue, int *length, int valueOffset, int valueLengthMax, int valueLengthMin, const char* responseHeader, const char* command)
{
  int result = wsapiJNI_ERROR_INTERNAL;

  if (valueLengthMax >= valueLengthMin)
  {
    int responseLengthMax = valueLengthMax + valueOffset + 1;
    //int responseLengthMin = valueLengthMin + valueOffset + 1;

    if (*length >= responseLengthMax)
    {
      result = writeToDevice(command, strlen(command));
      if (result == wsapiJNI_ERROR_SUCCESS)
      {
        result = readResponseStringValue(szValue, length, valueOffset, valueLengthMax, valueLengthMin, responseHeader);
      }
    }
    else
    {
      fprintf (stderr, "invalid parameter\n");
    }
  }
  else
  {
    fprintf (stderr, "invalid parameter\n");
  }

  return result;
}

jbyteArray readImageFromDevice(JNIEnv *env)
{
  jbyteArray result = NULL;

  char  szValue[MAX_RESPONSE+1];
  int   length = MAX_RESPONSE;

  if (readResponseStringValue(szValue, &length, 5, 8, 8, "#PGBX") == wsapiJNI_ERROR_SUCCESS)
  {
    int imageSize = 0;
    sscanf(szValue, "%08X", &imageSize);

    if (imageSize > 0)
    {
      result = env->NewByteArray(imageSize);
      if (result != NULL)
      {
        jboolean isCopy;
        jbyte* imageBuffer = (jbyte*)env->GetPrimitiveArrayCritical(result, &isCopy);
        if (imageBuffer != NULL)
        {
          int returnValue = readFromDevice((char*)imageBuffer, &imageSize);
          if (returnValue == wsapiJNI_ERROR_SUCCESS)
          {
            env->ReleasePrimitiveArrayCritical(result, imageBuffer, 0); //  free after copy
          }
          else
          {
fprintf (stderr, "GetByteArrayElements failed: [%d]\n", returnValue);
            env->ReleasePrimitiveArrayCritical(result, imageBuffer, JNI_ABORT); //  free and forget
            //  to make result freed
            imageBuffer = NULL;
          }
        }
        else
        {
          fprintf (stderr, "GetByteArrayElements failed\n");
        }

        if (imageBuffer == NULL)
        {
          env->DeleteGlobalRef(result);
          result = NULL;
        }
      }
      else
      {
        fprintf (stderr, "insufficient memory\n");
      }
    }
    else
    {
      fprintf (stderr, "imageSize = 0\n");
    }
  }
  else
  {
    fprintf (stderr, "getResponseStringValue failed\n");
  }

  return result;
}

jbyteArray readFrontImage(JNIEnv *env)
{
  jbyteArray result = NULL;

  if (writeToDevice("#GB0", 4) == 0)
  {
    result = readImageFromDevice(env);
  }

  return result;
}

jbyteArray readRearImage(JNIEnv *env)
{
  jbyteArray result = NULL;

  if (writeToDevice("#GB1", 4) == 0)
  {
    result = readImageFromDevice(env);
  }

  return result;
}

int getJavaIntegerFieldDef(JNIEnv *env, jobject thisObj, jobject obj, const char* className, const char* fieldName, int def)
{
  int result = def;

  try
  {
    // get the class
    jclass javaClass = env->FindClass(className);

    // get the field id
    jfieldID fdField = env->GetFieldID(javaClass, fieldName, "I");

    // get the data from the field
    result = env->GetIntField(obj, fdField);
  }
  catch (...)
  {
    result = def;
  }

  return result;
}

int applyScanOptions(JNIEnv *env, jobject thisObj, jobject obj)
{
  int result = wsapiJNI_ERROR_INTERNAL;

  int resolution  = getJavaIntegerFieldDef(env, thisObj, obj, "wsapiJNI$Option", "resolution", wsapiJNI_RES_DEF);
  int color       = getJavaIntegerFieldDef(env, thisObj, obj, "wsapiJNI$Option", "color"     , wsapiJNI_COL_DEF);
  int rotation    = getJavaIntegerFieldDef(env, thisObj, obj, "wsapiJNI$Option", "rotation"  , wsapiJNI_ROT_DEF);
  int dropout     = getJavaIntegerFieldDef(env, thisObj, obj, "wsapiJNI$Option", "dropout"   , wsapiJNI_DROP_DEF);
  int fdLevel     = getJavaIntegerFieldDef(env, thisObj, obj, "wsapiJNI$Option", "fdLevel"   , wsapiJNI_FD_LEVEL_DEF);

  char  szValue[MAX_RESPONSE+1];
  int   lenValue = MAX_RESPONSE;

  sprintf((char*)szValue, "#SCD%d,B%d,L%d,R%d", resolution, color, dropout, rotation);
  result = writeToDevice(szValue, strlen(szValue));
  readFromDevice(szValue, &lenValue);

  sprintf((char*)szValue, "#SP5,%d", fdLevel);
  result = writeToDevice(szValue, strlen(szValue));
  readFromDevice(szValue, &lenValue);

  return result;
}

int readFdValues(char *szValue, int *length)
{
  return  getResponseStringValue(szValue, length, 4, 8+1+8+1+8, 8+1+8+1+8, "#PAL", "#ALV");
}

int readControlNumber(char *szValue, int *length)
{
  return  getResponseStringValue(szValue, length, 6, 8, 8, "#PAL05", "#ALCG05");
}

int readSensorStatus(char *szValue, int *length)
{
  return  getResponseStringValue(szValue, length, 5, 4, 4, "#PGSX0", "#GSX");
}

int readFwVersAndSerialNum(char *fwVers, int *lengthVers, char* serialNum, int *lengthSN)
{
  int result = wsapiJNI_ERROR_INTERNAL;

  char  szValue[MAX_RESPONSE+1];
  int   length = MAX_RESPONSE;

  result = getResponseStringValue(szValue, &length, 4, 41, 39, "#PGV", "#GV");
  if (result == 0)
  {
    szValue[length] = '\0';

    int   nFwMajor, nFwMinor;
    int   nFpgaMajor, nFpgaMinor;
    char  szDummy[MAX_RESPONSE+1];
    char  szSeqNum[MAX_RESPONSE+1];

    sscanf(szValue, "IDS600 %d.%d.%s (%d:%d) %s", &nFwMajor, &nFwMinor, szDummy, &nFpgaMajor, &nFpgaMinor, szSeqNum);

    if ((szSeqNum[0] == '[') && (szSeqNum[5] == '-'))
    {
      memcpy(&serialNum[0], &szSeqNum[1], 4);
      if (szSeqNum[10] == ']')
      {
       memcpy(&serialNum[4], &szSeqNum[6], 4);
       *lengthSN = 8;
       serialNum[*lengthSN] = '\0';
      }
      else
      if (szSeqNum[11] == ']')
      {
       memcpy(&serialNum[4], &szSeqNum[6], 5);
       *lengthSN = 9;
       serialNum[*lengthSN] = '\0';
      }
      else
      {
       result = wsapiJNI_ERROR_INTERNAL;
      }

      sprintf(fwVers, "%d.%d", nFwMajor, nFwMinor);
      *lengthVers = strlen(fwVers);
    }
    else
    {
      result = wsapiJNI_ERROR_INTERNAL;
    }
  }
  else
  {
    fprintf (stderr, "invalid parameter\n");
  }

  return result;
}

int fillDeviceInfoFields(JNIEnv *env, jobject thisObj, jclass deviceInfoClass, jobject deviceInfo)
{
  jint  result = wsapiJNI_ERROR_INTERNAL;

  if (deviceInfoClass != NULL)
  {
    if (deviceInfo != NULL)
    {
      //  get firmwareVersion
      //  get serialNumber
      {
        char szFW[MAX_RESPONSE+1];
        char szSN[MAX_RESPONSE+1];
        int lenFW = MAX_RESPONSE;
        int lenSN = MAX_RESPONSE;
        if (readFwVersAndSerialNum(szFW, &lenFW, szSN, &lenSN) == wsapiJNI_ERROR_SUCCESS)
        {
          szFW[lenFW] = '\0';
          szSN[lenSN] = '\0';

          jstring jstrBuf;
          jfieldID valueField;

          jstrBuf = env->NewStringUTF(szFW);
          // add firmwareVersion into data
          valueField = env->GetFieldID(deviceInfoClass, "firmwareVersion", "Ljava/lang/String;");
          if (valueField != NULL)
          {
            env->SetObjectField(deviceInfo, valueField, jstrBuf);
          }
          else
          {
            fprintf (stderr, "valueField = NULL\n");
          }

          jstrBuf = env->NewStringUTF(szSN);
          // add serialNumber into data
          valueField = env->GetFieldID(deviceInfoClass, "serialNumber", "Ljava/lang/String;");
          if (valueField != NULL)
          {
            env->SetObjectField(deviceInfo, valueField, jstrBuf);
          }
          else
          {
            fprintf (stderr, "valueField = NULL\n");
          }
        }
        else
        {
        }
      }

      //  get controlNumber
      char  szValue[MAX_RESPONSE+1];
      int   length = MAX_RESPONSE;
      if (readControlNumber(szValue, &length) == wsapiJNI_ERROR_SUCCESS)
      {
        szValue[length] = '\0';
        jstring jstrBuf = env->NewStringUTF(szValue);

        // add controlNumber into data
        jfieldID controlNumberField = env->GetFieldID(deviceInfoClass, "controlNumber", "Ljava/lang/String;");
        if (controlNumberField != NULL)
        {
          env->SetObjectField(deviceInfo, controlNumberField, jstrBuf);
        }
        else
        {
          fprintf (stderr, "controlNumberField = NULL\n");
        }
      }

      //  get sensorStatus
      length = MAX_RESPONSE;
      if (readSensorStatus(szValue, &length) == wsapiJNI_ERROR_SUCCESS)
      {
        szValue[length] = '\0';
        jstring jstrBuf = env->NewStringUTF(szValue);

        // add sensorStatus into data
        jfieldID sensorStatusField = env->GetFieldID(deviceInfoClass, "sensorStatus", "Ljava/lang/String;");
        if (sensorStatusField != NULL)
        {
          env->SetObjectField(deviceInfo, sensorStatusField, jstrBuf);
        }
        else
        {
          fprintf (stderr, "sensorStatusField = NULL\n");
        }
      }

      result = wsapiJNI_ERROR_SUCCESS;
    } //  if (deviceInfo != NULL)
    else
    {
      fprintf (stderr, "deviceInfo = NULL\n");
    }
  } //  if (deviceInfoClass != NULL)
  else
  {
    fprintf (stderr, "deviceInfoClass = NULL\n");
  }

  return  result;
}

JNIEXPORT jint JNICALL Java_wsapiJNI_scan(JNIEnv *env, jobject thisObj, jobjectArray data, jobject options)
{
  jint  result = wsapiJNI_ERROR_INTERNAL;

  //  lock mutex
#ifdef _WINDOWS
  if (true)
#else
  if (exclusive_device.try_lock())
#endif
  {
    //  if open succeeded
    result = open();
    if (result == wsapiJNI_ERROR_SUCCESS)
    {
      result = wsapiJNI_ERROR_INTERNAL;

      //  check device info
      if (isOnline != 0)
      {
        //  set FDLevel and scan options (side, resolution, color, rotation, colorDropout);
        if (applyScanOptions(env, thisObj, options) == wsapiJNI_ERROR_SUCCESS)
        {
          //  scan with options (side, resolution, color, rotation, colorDropout);
          int timeout = getJavaIntegerFieldDef(env, thisObj, options, "wsapiJNI$Option", "timeout", wsapiJNI_TIMEOUT_DEF);
          int side    = getJavaIntegerFieldDef(env, thisObj, options, "wsapiJNI$Option", "side"   , wsapiJNI_SIDE_DEF);

          char szValue[MAX_RESPONSE+1];
          sprintf((char*)szValue, "#AS%d", timeout);
          //  if scan succeeded
          if (writeToDevice(szValue, strlen(szValue)) == wsapiJNI_ERROR_SUCCESS)
          {
            int length = MAX_RESPONSE;
            readFromDevice(szValue, &length);
            szValue[length] = '\0';

            int imageSizeFront;
            int imageSizeRear;
            int imageSizeDummy;
            //  "#PAS1950774,1950774,0"
            sscanf(szValue, "#PAS%d,%d,%d", &imageSizeFront, &imageSizeRear, &imageSizeDummy);

            jclass scanDataClass = env->FindClass("wsapiJNI$ScanData");
            if (scanDataClass != NULL)
            {
              jmethodID scanDataConstructor = env->GetMethodID(scanDataClass, "<init>", "()V"); //  empty argument and empty return
              if (scanDataConstructor != NULL)
              {
                jobject scanData = env->NewObject(scanDataClass, scanDataConstructor);
                if (scanData != NULL)
                {
                  if ((side & wsapiJNI_SIDE_FRONT) == wsapiJNI_SIDE_FRONT)
                  {
                    //    get front image
                    jbyteArray arrFrontImage = readFrontImage(env);
                    //    add front image into data
                    if (arrFrontImage != NULL)
                    {
                      jfieldID frontImageField = env->GetFieldID(scanDataClass, "frontImage", "[B"); //  java  byte[]
                      if (frontImageField != NULL)
                      {
                        env->SetObjectField(scanData, frontImageField, arrFrontImage);
                      }
                      else
                      {
                        fprintf (stderr, "frontImageField = NULL\n");
                      }
                    } //  if (arrFrontImage != NULL)
                    else
                    {
                      fprintf (stderr, "arrFrontImage = NULL\n");
                    }
                  } //  wsapiJNI_SIDE_FRONT

                  if ((side & wsapiJNI_SIDE_REAR) == wsapiJNI_SIDE_REAR)
                  {
                    //    get front image
                    jbyteArray arrRearImage = readRearImage(env);
                    //    add front image into data
                    if (arrRearImage != NULL)
                    {
                      jfieldID rearImageField = env->GetFieldID(scanDataClass, "rearImage", "[B");
                      if (rearImageField != NULL)
                      {
                        env->SetObjectField(scanData, rearImageField, arrRearImage);
                      }
                      else
                      {
                        fprintf (stderr, "rearImageField = NULL\n");
                      }
                    } //  if (arrRearImage != NULL)
                    else
                    {
                      fprintf (stderr, "arrRearImage = NULL\n");
                    }
                  } //  wsapiJNI_SIDE_REAR

                  // get FD value
                  length = MAX_RESPONSE;
                  if (readFdValues(szValue, &length) == wsapiJNI_ERROR_SUCCESS)
                  {
                    szValue[length] = '\0';
                    jstring jstrBuf = env->NewStringUTF(szValue);

                    // add FD value into data
                    jfieldID fdValuesField = env->GetFieldID(scanDataClass, "fdValues", "Ljava/lang/String;");
                    if (fdValuesField != NULL)
                    {
                      env->SetObjectField(scanData, fdValuesField, jstrBuf);
                    }
                    else
                    {
                      fprintf (stderr, "fdValuesField = NULL\n");
                    }
                  }

                  //  get Device Information
                  if (fillDeviceInfoFields(env, thisObj, scanDataClass, scanData) == wsapiJNI_ERROR_SUCCESS)
                  {
                  }
                  else
                  {
                    fprintf (stderr, "fillDeviceInfoFields = failed\n");
                  }

                  env->SetObjectArrayElement(data, 0, scanData);

                  result = wsapiJNI_ERROR_SUCCESS;
                } //  if (scanData != NULL)
                else
                {
                  fprintf (stderr, "scanData = NULL\n");
                }
              } //  if (scanDataConstructor != NULL)
              else
              {
                fprintf (stderr, "scanDataConstructor = NULL\n");
              }
            } //  if (scanDataClass != NULL)
            else
            {
              fprintf (stderr, "scanDataClass = NULL\n");
            }
          } //  if scan succeeded
          else
          {
            fprintf (stderr, "scan filed\n");
          }
        }//  if applyScanOptions
        else
        {
          fprintf (stderr, "applyScanOptions filed\n");
        }
      } //  if (isOnline != 0)
      else
      {
        result = wsapiJNI_ERROR_DISCONNECTED;
        fprintf (stderr, "wsapiJNI_ERROR_DISCONNECTED\n");
      }

      //  close
      close();
    } //  if open
    else
    {
      fprintf (stderr, "open failed: %d\n", result);
    }

    //  unlock Mutex
#ifndef _WINDOWS
    exclusive_device.unlock();
#endif
  } //  if not locked
  else
  {
    result = wsapiJNI_ERROR_BUSY;
    fprintf (stderr, "wsapiJNI_ERROR_BUSY\n");
  }

  return  result;
}

JNIEXPORT jint JNICALL Java_wsapiJNI_getDeviceInfo(JNIEnv *env, jobject thisObj, jobjectArray info)
{
  jint  result = wsapiJNI_ERROR_INTERNAL;

  //  lock mutex
#ifdef _WINDOWS
  if (true)
#else
  if (exclusive_device.try_lock())
#endif
  {
    //  if open succeeded
    result = open();
    if (result == wsapiJNI_ERROR_SUCCESS)
    {
      result = wsapiJNI_ERROR_INTERNAL;

      //  check device info
      if (isOnline != 0)
      {
        jclass deviceInfoClass = env->FindClass("wsapiJNI$DeviceInfo");
        if (deviceInfoClass != NULL)
        {
          jmethodID deviceInfoConstructor = env->GetMethodID(deviceInfoClass, "<init>", "()V"); //  empty argument and empty return
          if (deviceInfoConstructor != NULL)
          {
            jobject deviceInfo = env->NewObject(deviceInfoClass, deviceInfoConstructor);
            if (deviceInfo != NULL)
            {
              if (fillDeviceInfoFields(env, thisObj, deviceInfoClass, deviceInfo) == wsapiJNI_ERROR_SUCCESS)
              {
                env->SetObjectArrayElement(info, 0, deviceInfo);

                result = wsapiJNI_ERROR_SUCCESS;
              }
              else
              {
                fprintf (stderr, "fillDeviceInfoFields = failed\n");
              }
            } //  if (deviceInfo != NULL)
            else
            {
              fprintf (stderr, "deviceInfo = NULL\n");
            }
          } //  if (deviceInfoConstructor != NULL)
          else
          {
            fprintf (stderr, "deviceInfoConstructor = NULL\n");
          }
        } //  if (deviceInfoClass != NULL)
        else
        {
          fprintf (stderr, "deviceInfoClass = NULL\n");
        }
      } //  if (isOnline != 0)
      else
      {
        result = wsapiJNI_ERROR_DISCONNECTED;
        fprintf (stderr, "wsapiJNI_ERROR_DISCONNECTED\n");
      }

      //  close
      close();
    } //  if open
    else
    {
      fprintf (stderr, "open failed: %d\n", result);
    }

    //  unlock Mutex
#ifndef _WINDOWS
    exclusive_device.unlock();
#endif
  } //  if not locked
  else
  {
    result = wsapiJNI_ERROR_BUSY;
    fprintf (stderr, "wsapiJNI_ERROR_BUSY\n");
  }

  return  result;
}


#ifdef _WINDOWS
  // Entry point
  BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
  {
    switch(ul_reason_for_call)
    {
      case DLL_PROCESS_ATTACH:
        //OnLoadProc();
        return TRUE;

      case DLL_PROCESS_DETACH:
        //OnUnloadProc();
        return TRUE;
    }

      return TRUE;
  }
#endif
