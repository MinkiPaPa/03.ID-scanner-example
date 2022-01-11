//
//  sdk.h
//  wifi-sample
//
//  Created by Kimis_EzS_MacMini on 2017. 11. 30..
//  Copyright © 2017년 ezdocu. All rights reserved.
//

#ifndef sdk_h
#define sdk_h

#import "EzSWifi.h"


#define EZS_WIFI_ERROR_HTTP_UNKNOWN                 -1000
#define EZS_WIFI_ERROR_HTTP400                      -1001
#define EZS_WIFI_ERROR_HTTP401                      -1002
#define EZS_WIFI_ERROR_HTTP500                      -1003
#define EZS_WIFI_ERROR_READ_TIMEOUT                 -2001
#define EZS_WIFI_ERROR_INVALID_DATA                 -2003
#define EZS_WIFI_ERROR_INVALID_IMAGE_FRONT_GZIP     -2004
#define EZS_WIFI_ERROR_INVALID_IMAGE_FRONT_BMP      -2005
#define EZS_WIFI_ERROR_INVALID_IMAGE_REAR_GZIP      -2006
#define EZS_WIFI_ERROR_INVALID_IMAGE_REAR_BMP       -2007
#define EZS_WIFI_ERROR_INVALID_BUNDLE               -2008
#define EZS_WIFI_ERROR_CORE                         -3000

typedef struct{
    int    nSide;                           // 1(앞면), 2(뒷면), 3(양면)
    int    nRes;                            // 100, 200, 300, 600(무조건 300)
    int    nBis;                            // 1, 8, 24(무조건 24)
    int    nRotate;                         // 0, 90, 180, 270(무조건 270)
    int    nFdLevel;                        // 0 ~ 100(현재 판매점은 100 을 사용)
    int    nColorDrop;                      // 0(NONE), 1(Red), 2(Green), 3(Blue) (무조건 0)
    int    nFormat;                         // Reserved
    int    nCompress;                       // 0(NONE), Reserved
    int    nScanTimeOut;                    // 스캐너에 카드를 삽입할수 있는 시간까지 기다리는 TimeOut 설정
} Type_ScanCfg;                             // 신분증 스캐너에서 스캔을 하기위한 설정값

typedef struct{
    int nRes;
    int nFrontSize, nRearSize;          // 스캔결과, 이미지 정보, 앞면 이미지 크기, 뒷면 이미지 크기
    
    char szRawFDValue[64];              // 위변조 원시값
    char szOcrFDValue[16];              // 신분증에 따른 위변조값
    BYTE *vImgFront;                    // 앞면이미지
    BYTE *vImgRear;                     // 뒷면 이미지
} Type_ScanRes;                         // 신분증 스캐너에서 스캔후 스캐너에서 넘겨준 값

typedef struct {
    char szNickName[256];              // 신분증 스캐너 별칭
    char szFirmwareVer[64];            // 펌웨어 버전
    char szSerialNo[64];               // 시리얼번호
    char szSensorStatus[64];           // 스캐너 상태코드
    Boolean bOldCard;                  // 신분증 스캐너에 삽입된 카드가 이전에 스캔된적이 있는지 여부
    Boolean bIsJam;                    // 신분증 스캐너에 카드 걸려있는지 여부
} Type_DeviceInfo;                     // EzSWifiScanInfo, EzSWifiProc 에서 반환되는 장비정보와 상태값

typedef struct {
    char szSvrAddr[256];                // 신분증 스캐너를 처리하는 주소
    char szSvrCmd[64];                  // 신분증 스캐너를 처리하는 명령
    int nConnTimeout;                   // 신분증스캐너에 연결시 TimeOut 설정
    int nReadTimeout;                   // 신분증스캐너에 명령 처리시 TimeOut 설정
    
    int nSessionTimeOut;                // 신분증스캐너에서 새롭게 생성될수있는 MaxTimeOut 설정 - EzSWifiOpenSession 함수에서 동작함
    char szSessionId[128];              // EzSWifiOpenSession 에서 성공시 넘겨준 Session Id
} Type_HTTPInfo;                        // EzSWifiOpenSession 에서 신분증 스캐너 접속정보 설정및 Session 정보 반환


void EzsLogYn(int nLogYn);
// 라이브러리에서 로그 출력 여부를 설정하는 함수
// int nLogYn : 라이브러리에서 로그 출력 여부(배포시 0, 디버깅시 1)

int EzSWifiScanInfo(Type_HTTPInfo *httpInfo, Type_DeviceInfo *deviceInfo);
// 신분증 스캐너의 Type_DeviceInfo를 반환하는 함수
// 함수처리 전후 별도의 함수 호출 필요없음
// Type_HTTPInfo *httpInfo : 신분증스캐너에 접속하기위한 정보 설정
// Type_DeviceInfo *deviceInfo : 반환되는 신분증 스캐너정보

int EzSWifiOpenSession(Type_HTTPInfo *httpInfo, char *szInComCode, char *szOutComCode, char *szInUserInfo, char *szOutUserInfo);
// 신분증을 스캔하기 위하여 신분증 스캐너의 스캐너를 점유하는 함수
// Type_HTTPInfo *httpInfo - 신분증 스캐너 접속정보및 성공시 szSessionId에 Session정보 반환
// char *szInComCode : 통신사 명칭을 설정(에러발생시 이전에 접속한 통신사 명칭를 szOutComCode 에 반환
// char *szOutComCode : EzSWifiOpenSession에서 마지막에 성공한 통신사 명칭을 반환(반환값이 성공시 szInComCode 와 szOutComCode 값이 동일)
// char *szInUserInfo : 통신사 명칭을 설정(에러발생시 이전에 접속한 통신사 명칭를 szOutComCode 에 반환
// char *szOutUserInfo : EzSWifiOpenSession에서 마지막에 성공한 통신사 명칭을 반환(반환값이 성공시 szInComCode 와 szOutComCode 값이 동일)

int EzSWifiProc(Type_HTTPInfo *httpInfo, int nCompany, int nOcrOpt, Type_ScanCfg scanCfg, Type_DeviceInfo *deviceInfo, Type_ScanRes *scanRes, Type_WifiIdInfo *wifiIdInfo);
// 신분증을 스캔하는 함수
// 반드시 이전에 EzSWifiOpenSession 이 성공후 호츌
// Type_HTTPInfo *httpInfo - 신분증 스캐너 접속정보
// int nCompany : Ocr을 처리하기위한 통신사 코드(902: SKT, 903: KT, 904: LGU)
// int nOcrOpt : 0: Ocr 처리, 그외값: Ocr 안함
// Type_ScanCfg scanCfg : 스캔을 하기위한 설정값
// Type_DeviceInfo *deviceInfo : 반환되는 신분증 스캐너정보
// Type_ScanRes *scanRes : 신분증 스캔후 신분증 스캐너에서 반환하는값 (스캔후 반환되는 이미지(앞/뒤)의 값을 확인하여 반드시 free 필요)
// Type_WifiIdInfo *wifiIdInfo : Ocr 처리시 반환되는 Ocr 정보

int EzSWifiCloseSession(Type_HTTPInfo *httpInfo);
// EzSWifiOpenSession의 szSessionId으로 점유돤 신분증 스캐너의 스캐너를 Free 하는 함수
// EzSWifiOpenSession 성공시 스캔을 완료하면 반드시 호출
// Type_HTTPInfo *httpInfo - 신분증 스캐너 접속정보

UIImage* EzSWifiImageProc(UIImage *VwImage, RECT rMask, CGColorRef ClColor, int nRotate);
// 회전및 마스킹 하는 함수
// UIImage *VwImage - 생성되어지는 원본 이미지
// RECT rMask - 마스킹 좌표
// CGColorRef ClColor - 마스킹 색깔
// int nRotate - 마스킹 하기전에 이미지 회전정보(0, 90, 180, 270)

UIImage* EzSWifiTimestamp(UIImage *VwImage, char *szComCode, char *szPwd);
// SK텔레콤에서만 사용하는 함수
// UIImage *VwImage - 생성되어지는 원본 이미지
// char *szComCode - 별도문의
// char *szPwd - 별도문의

#endif /* sdk_h */
