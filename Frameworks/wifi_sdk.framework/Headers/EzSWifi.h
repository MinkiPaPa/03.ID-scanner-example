//
//  EzSWifi.h
//  EzSWifiLib
//
//  Created by Kimis_EzS_MacMini on 2016. 12. 5..
//  Copyright © 2016년 Kimis_EzS_MacMini. All rights reserved.
//

#ifndef EzSWifi_h
#define EzSWifi_h

#include <stdio.h>

#pragma pack(1)

typedef unsigned long       DWORD;
typedef unsigned char       BYTE;

typedef struct tagRECT{
    int    left;
    int    top;
    int    right;
    int    bottom;
} RECT;

typedef struct
{
    int		nType, nChkNo, nRotate, nSwapYn;									// 신분증 종류, 주민번호 ChkBit, 회전정보, 앞뒷면 바뀜
    BYTE	szIdNo[32];                                                         // 주민번호
    BYTE	szIdName[64];                                                       // 성명
    BYTE	szIdDay[32];                                                        // 발급일자
    BYTE	szDrvNo[32];                                                        // Reserved(사용안함)
    BYTE	szAddr[256];                                                        // 주소
    BYTE	szAuthorNo[32];                                                     // 면허번호, 보훈번호
    BYTE	szAgency[256];                                                      // 발급기관
    BYTE	szEtc01[32];                                                        // Reserved(사용안함)
    BYTE	szEtc02[8];                                                         // Reserved(사용안함)
    
    RECT    rIdPos, rIdRearPos;                                                 // 주민번호 위치, 주민번호 성별위치
    int     nColInfo, nBwThink, nReserved;                                      // Reserved(사용안함)
} Type_WifiIdInfo;


#endif /* EzSId_h */
