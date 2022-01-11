//
//  ViewController.m
//  wifi-sample
//
//  Created by Jae Cheol Ko on 12/11/2017.
//  Copyright © 2017 ezdocu. All rights reserved.
//

#include <ifaddrs.h>
#include <arpa/inet.h>
#import "ViewController.h"
#import "wifi_sdk/wifi_sdk.h"
#import "Reachability.h"
#import "DeviceInfo.h"
#import "Network.h"

@interface ViewController ()

@property (weak, nonatomic) IBOutlet UIButton *btnInfo;
@property (weak, nonatomic) IBOutlet UIButton *btnScan;
@property (weak, nonatomic) IBOutlet UILabel *lblCmd;
@property (weak, nonatomic) IBOutlet UILabel *lblResult;
@property (weak, nonatomic) IBOutlet UIImageView *img1;
@property (weak, nonatomic) IBOutlet UIImageView *img2;
@property (weak, nonatomic) IBOutlet UIImageView *img3;
@property (weak, nonatomic) IBOutlet UIButton *btnOpen;
@property (weak, nonatomic) IBOutlet UIButton *btnClose;
@property (weak, nonatomic) IBOutlet UITextField *txtUrl;
@property (weak, nonatomic) IBOutlet UITextField *txtReadTimeout;
@property (weak, nonatomic) IBOutlet UITextField *txtSessionTimeout;
@property (weak, nonatomic) IBOutlet UIButton *btnSearch;
@property (weak, nonatomic) IBOutlet UIButton *btnAll;

@end

@implementation ViewController

Type_HTTPInfo httpInfo;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.

    memset(&httpInfo, 0x00, sizeof(httpInfo));
    [self.txtUrl setText:@"https://192.168.0.33:8443/test/handle.do"];
    [self.txtReadTimeout setText:@"30"];
    [self.txtSessionTimeout setText:@"30"];
    [self.lblCmd setText:@"2017.12.18.01"];
    httpInfo.nReadTimeout = 30;
    
    [self store];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)store {
    strcpy(httpInfo.szSvrAddr, [self.txtUrl text].UTF8String);
    httpInfo.nReadTimeout = [[self.txtReadTimeout text] intValue];
}

- (void)startCommand:(NSString *)command {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *tick = [formatter stringFromDate:[NSDate date]];
    [self.lblCmd setText:[NSString stringWithFormat:@"%@: %@", command, tick]];
    NSLog(@"\nstartCommand : %@", [self.lblCmd text]);
}

- (void)endCommand {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *tick = [formatter stringFromDate:[NSDate date]];
    [self.lblCmd setText:[NSString stringWithFormat:@"%@ ~ %@", [self.lblCmd text], tick]];
    NSLog(@"\nendCommand : %@", [self.lblCmd text]);
}

- (IBAction)btnOpenClick:(UIButton *)sender {
    [self startCommand:@"open"];
    NSLog(@"openSession");
    
    [self store];
    
    char szOutComCode[128];
    char szOutUserInfo[128];
    
    [self.img1 setImage:nil];
    [self.img2 setImage:nil];
    [self.img3 setImage:nil];
    
    memset(szOutComCode, 0x00, sizeof(szOutComCode));
    memset(szOutUserInfo, 0x00, sizeof(szOutUserInfo));
    httpInfo.nSessionTimeOut = [[self.txtSessionTimeout text] intValue];
    EzsLogYn(1);
    int result = EzSWifiOpenSession(&httpInfo, "VOIM", szOutComCode, "이기자", szOutUserInfo);

    NSLog(@"result : %d", result);
    NSLog(@"sessionId: %s", httpInfo.szSessionId);
    NSLog(@"szOutComCode: %s", szOutComCode);
    NSLog(@"szOutUserInfo: %s", szOutUserInfo);

    NSString *str = @"";
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"open.result: ", result]];
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"sessionId: ", httpInfo.szSessionId]];
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"szOutComCode: ", [NSString stringWithUTF8String:szOutComCode]]];
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"szOutUserInfo: ", [NSString stringWithUTF8String:szOutUserInfo]]];
    [self.lblResult setText:str];
    [self endCommand];
}

- (IBAction)btnCloseClick:(UIButton *)sender {
    [self startCommand:@"close"];
    NSLog(@"closeSession");
    
    [self store];

    [self.img1 setImage:nil];
    [self.img2 setImage:nil];
    [self.img3 setImage:nil];
    
    int result = EzSWifiCloseSession(&httpInfo);
    
    NSLog(@"result : %d", result);
    NSLog(@"sessionId: %s", httpInfo.szSessionId);
    
    NSString *str = @"";
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"close.result: ", result]];
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"sessionId: ", httpInfo.szSessionId]];
    [self.lblResult setText:str];
    [self endCommand];
}

- (IBAction)btnInfoClick:(UIButton *)sender {
    [self startCommand:@"info"];
    NSLog(@"getDeviceInfo");

    [self store];
    
    Type_DeviceInfo     DeviceInfo;
    
 
    [self.img1 setImage:nil];
    [self.img2 setImage:nil];
    [self.img3 setImage:nil];
  
    EzsLogYn(1);
    memset(&DeviceInfo, 0x00, sizeof(DeviceInfo));
    int result = EzSWifiScanInfo(&httpInfo, &DeviceInfo);

    NSLog(@"result : %d", result);
    NSLog(@"sessionId: %s", httpInfo.szSessionId);
    NSString *str = @"";
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"info.result: ", result]];
    str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"sessionId: ", httpInfo.szSessionId]];
    if (result == 0) {
        NSLog(@"NickName: %s", DeviceInfo.szNickName);
        NSLog(@"FirmwareVer: %s", DeviceInfo.szFirmwareVer);
        NSLog(@"SerialNo: %s", DeviceInfo.szSerialNo);
        NSLog(@"SensorStatus: %s", DeviceInfo.szSensorStatus);
        NSLog(@"oldCard: %d", (int)DeviceInfo.bOldCard);
        NSLog(@"isJam: %d", (int)DeviceInfo.bIsJam);
        
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"NickName: ", [NSString stringWithUTF8String:DeviceInfo.szNickName]]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"FirmwareVer: ", DeviceInfo.szFirmwareVer]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"SerialNo: ", DeviceInfo.szSerialNo]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"SensorStatus: ", DeviceInfo.szSensorStatus]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"oldCard: ", (int)DeviceInfo.bOldCard]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"IsJam: ", (int)DeviceInfo.bIsJam]];
    }
    [self.lblResult setText:str];
    [self endCommand];
}

- (IBAction)btnScanClick:(UIButton *)sender {
    [self startCommand:@"scan"];
    NSLog(@"scan");
    
    [self store];
 
    Type_ScanCfg        scanConfig;
    Type_DeviceInfo     deviceInfo;
    Type_ScanRes        scanResult;
    Type_WifiIdInfo     ocrResult;
    
    [self.img1 setImage:nil];
    [self.img2 setImage:nil];
    [self.img3 setImage:nil];
    
    memset(&scanConfig, 0x00, sizeof(scanConfig));
    memset(&deviceInfo, 0x00, sizeof(deviceInfo));
    memset(&scanResult, 0x00, sizeof(scanResult));
    memset(&ocrResult, 0x00, sizeof(ocrResult));
    
    scanConfig.nSide = 1;
    scanConfig.nRes = 300;
    scanConfig.nBis = 24;
    scanConfig.nRotate = 270;
    scanConfig.nFdLevel = 100;
    scanConfig.nColorDrop = 0;
    scanConfig.nFormat = 0;
    scanConfig.nCompress = 0;
    
    
    int result = EzSWifiProc(&httpInfo, 902, 0, scanConfig, &deviceInfo, &scanResult, &ocrResult);
    @try {
        NSLog(@"result : %d", result);
        NSLog(@"sessionId: %s", httpInfo.szSessionId);

        NSString *str = @"";
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"result: ", result]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"sessionId: ", httpInfo.szSessionId]];
        
        NSLog(@"NickName: %s", deviceInfo.szNickName);
        NSLog(@"FirmwareVer: %s", deviceInfo.szFirmwareVer);
        NSLog(@"SerialNo: %s", deviceInfo.szSerialNo);
        NSLog(@"SensorStatus: %s", deviceInfo.szSensorStatus);
        NSLog(@"oldCard: %d", (int)deviceInfo.bOldCard);
        NSLog(@"isJam: %d", (int)deviceInfo.bIsJam);
        
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"NickName: ", [NSString stringWithUTF8String:deviceInfo.szNickName]]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\t", @"FirmwareVer: ", deviceInfo.szFirmwareVer]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\t", @"SerialNo: ", deviceInfo.szSerialNo]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\t", @"SensorStatus: ", deviceInfo.szSensorStatus]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\t", @"oldCard: ", (int)deviceInfo.bOldCard]];
        str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"IsJam: ", (int)deviceInfo.bIsJam]];
        
        if (result == 0) {
            UIImage *image;
            str = [str stringByAppendingString:[NSString stringWithFormat:@"nType: %d\n", ocrResult.nType]];

            CFStringEncoding encoding = (CFStringEncoding)CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingDOSKorean);

            NSString *no = [NSString stringWithCString:(const char *)ocrResult.szIdNo encoding:encoding];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"주민번호: ", no]];

            NSString *name = [NSString stringWithCString:(const char *)ocrResult.szIdName encoding:encoding];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"이름: ", name]];

            NSString *day = [NSString stringWithCString:(const char *)ocrResult.szIdDay encoding:encoding];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"발급일자: ", day]];

            NSString *authorNo = [NSString stringWithCString:(const char *)ocrResult.szAuthorNo encoding:encoding];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"면허번호: ", authorNo]];
            
            NSString *RawFDValue = [NSString stringWithCString:(const char *)scanResult.szRawFDValue encoding:encoding];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"전체 TR-UV-IR: ", RawFDValue]];
            
            NSString *OcrFDValue = [NSString stringWithCString:(const char *)scanResult.szOcrFDValue encoding:encoding];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"TR-UV-IR: ", OcrFDValue]];
            
            NSLog(@"ImgPos: %d,%d,%d,%d", ocrResult.rIdRearPos.left, ocrResult.rIdRearPos.top, ocrResult.rIdRearPos.right, ocrResult.rIdRearPos.bottom);
            NSLog(@"ocrResult.rotate : %d", ocrResult.nRotate);
            
            CGColorRef color = [UIColor colorWithRed:230.0/255.0 green:20.0/255.0 blue:20.0/255.0 alpha:0.75].CGColor;

            switch (scanConfig.nSide) {
                case 1: // front
                    image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgFront length:scanResult.nFrontSize]];
                    [self.img1 setImage:image];
                    image = EzSWifiImageProc(image, ocrResult.rIdRearPos, color, ocrResult.nRotate);
                    [self.img2 setImage:image];
                    break;

                case 2: // rear
                    image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgRear length:scanResult.nRearSize]];
                    [self.img1 setImage:image];
                    image = EzSWifiImageProc(image, ocrResult.rIdRearPos, [UIColor blackColor].CGColor, ocrResult.nRotate);
                    [self.img2 setImage:image];
                    break;

                default: // both
                    if (ocrResult.nSwapYn == 0) {
                        image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgFront length:scanResult.nFrontSize]];
                        [self.img1 setImage:image];
                        image = EzSWifiImageProc(image, ocrResult.rIdRearPos, [UIColor blackColor].CGColor, ocrResult.nRotate);
                        [self.img2 setImage:image];
                    } else {
                        image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgRear length:scanResult.nRearSize]];
                        [self.img1 setImage:image];
                        image = EzSWifiImageProc(image, ocrResult.rIdRearPos, [UIColor blackColor].CGColor, ocrResult.nRotate);
                        [self.img2 setImage:image];
                    }
                    break;
            }
            UIImage *timestamp = EzSWifiTimestamp(image, "SKT", NULL);
            [self.img3 setImage:timestamp];
        }
        [self.lblResult setText:str];
    }
    @finally {
        if (scanResult.vImgFront != nil && scanResult.nFrontSize > 0) {
            free(scanResult.vImgFront);
        }
        if (scanResult.vImgRear != nil && scanResult.nRearSize > 0) {
            free(scanResult.vImgRear);
        }
    }
//        if (scan.nFrontSize > 0 && scan.vImgSkew != nil) {
//            free(scan.vImgSkew);
//        }
//
//        if (scan.nRearSize > 0 && scan.vImgMsk != nil) {
//            free(scan.vImgMsk);
//        }

//        UIImage to JPEG File - 80 > compress
//        [UIImageJPEGRepresentation(image, 80) writeToFile:@"filepath" atomically:YES]
    [self endCommand];
}

- (IBAction)btnSearchClick:(UIButton *)sender {
    [self startCommand:@"search"];
    [self.lblResult setText:@"start"];
    
    // Wifi check - apple sample code
    bool reachable = [[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] == ReachableViaWiFi;

    NSLog(@"%d", (int)reachable);
    
    if (reachable) {
        // ip, subnet mask, ip range - open source
        Network *network = [[Network alloc] init];
        
        // 병렬 처리를 위한 큐 생성
        NSOperationQueue *queue = [[NSOperationQueue alloc] init];
        [queue setMaxConcurrentOperationCount:10];
        
        // 완료시점 체크를 위한 카운트 변수
        __block int finished = 0;
        // 화면 표시를 위한 문자열 저장 변수
        __block NSString *found = @"";
        
        // 실제로 체크하는 아이피 목록(디바이스 아이피 제외)
        int ipCount = (int)[network.ipsInRange count] - 1; // exclude device ip
        
        // 전체 아이피 목록 만큼 반복
        for (int i = 0; i < [network.ipsInRange count]; i ++) {
            NSString *ip = network.ipsInRange[i];
        
            /*
            if (i == 0 || i == [network.ipsInRange count] - 1) {
                continue;
                finished++;
            }
            */
            
            // 디바이스 아이피는 검사에서 제외
            if ([ip isEqualToString:network.deviceIP]) {
                continue;
            }
        
            // 큐에 작업 코드 블럭 추가
            [queue addOperationWithBlock:^{
                @try {
                    // 아이피 응답 테스트를 위한 객체 생성
                    DeviceInfo *deviceInfo = [[DeviceInfo alloc] init];
                    // 아이피 할당
                    deviceInfo->host = ip;

                    NSLog(@"start %@", deviceInfo->host);
                    
                    @try {
                        // 아이피 응답 체크 시작
                        [deviceInfo start];
                    } @catch (NSException *e) {
                        NSLog(@"\n\n%@ error", deviceInfo->host);
                    }
                    
                    // UI에 아이피 체크한 결과 반영하기 위해 UI 쓰레드에 동기화 코드 블럭 추가
                    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                        if ([deviceInfo isFound]) {
                            if ([found isEqualToString:@""]) {
                                found = [NSString stringWithFormat:@"%@ %@", deviceInfo->host, deviceInfo->nickname];
                            } else {
                                found = [found stringByAppendingFormat:@"\n%@ %@", deviceInfo->host, deviceInfo->nickname];
                            }
                        };
                        
                        [self.lblResult setText:[NSString stringWithFormat:@"%@\n Searching... %@", found, deviceInfo->host]];
                        
                        // NSLog(@"XXXX %d finished %@ - %d", finished, deviceInfo->host, (int)[deviceInfo isFound]);
                    }];
                } @finally {
                    finished++;
                }
            }];
        }
        
        // 완료 대기를 위한 비동기 코드 실행
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            while (finished < ipCount) {
                sleep(100);
            }
            
            // 화면에 데이터를 뿌려주기 위해 메인큐에 코드 블럭 추가
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.lblResult setText:[NSString stringWithFormat:@"%@\n%@", [self.lblResult text], @"end"]];
                [self endCommand];
                NSLog(@"search end");
            });
        });
    }
 //   [self.lblResult setText:[NSString stringWithFormat:@"%@\nClose", [self.lblResult text]]];
}

- (IBAction)btnAllClick:(UIButton *)sender {
    [self startCommand:@"All"];
    [self store];
    
    char                szOutComCode[128];
    char                szOutUserInfo[128];
    
    Type_ScanCfg        scanConfig;
    Type_DeviceInfo     deviceInfo;
    Type_ScanRes        scanResult;
    Type_WifiIdInfo     ocrResult;
    
    [self.img1 setImage:nil];
    [self.img2 setImage:nil];
    [self.img3 setImage:nil];
    
    memset(szOutComCode, 0x00, sizeof(szOutComCode));
    memset(szOutUserInfo, 0x00, sizeof(szOutUserInfo));
    httpInfo.nSessionTimeOut = [[self.txtSessionTimeout text] intValue];
    int result = EzSWifiOpenSession(&httpInfo, "VOIM", szOutComCode, "이기자", szOutUserInfo);
    if(result == 0){
        memset(&scanConfig, 0x00, sizeof(scanConfig));
        memset(&deviceInfo, 0x00, sizeof(deviceInfo));
        memset(&scanResult, 0x00, sizeof(scanResult));
        memset(&ocrResult, 0x00, sizeof(ocrResult));
        
        scanConfig.nSide = 1;
        scanConfig.nRes = 300;
        scanConfig.nBis = 24;
        scanConfig.nRotate = 270;
        scanConfig.nFdLevel = 100;
        scanConfig.nColorDrop = 0;
        scanConfig.nFormat = 0;
        scanConfig.nCompress = 0;
        
        int result = EzSWifiProc(&httpInfo, 902, 0, scanConfig, &deviceInfo, &scanResult, &ocrResult);
        @try {
            NSLog(@"result : %d", result);
            NSLog(@"sessionId: %s", httpInfo.szSessionId);
            
            NSString *str = @"";
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"result: ", result]];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\n", @"sessionId: ", httpInfo.szSessionId]];
            
            NSLog(@"NickName: %s", deviceInfo.szNickName);
            NSLog(@"FirmwareVer: %s", deviceInfo.szFirmwareVer);
            NSLog(@"SerialNo: %s", deviceInfo.szSerialNo);
            NSLog(@"SensorStatus: %s", deviceInfo.szSensorStatus);
            NSLog(@"oldCard: %d", (int)deviceInfo.bOldCard);
            NSLog(@"isJam: %d", (int)deviceInfo.bIsJam);
            
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"NickName: ", [NSString stringWithUTF8String:deviceInfo.szNickName]]];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\t", @"FirmwareVer: ", deviceInfo.szFirmwareVer]];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\t", @"SerialNo: ", deviceInfo.szSerialNo]];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%s\t", @"SensorStatus: ", deviceInfo.szSensorStatus]];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\t", @"oldCard: ", (int)deviceInfo.bOldCard]];
            str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%d\n", @"IsJam: ", (int)deviceInfo.bIsJam]];
            
            if (result == 0) {
                UIImage *image;
                str = [str stringByAppendingString:[NSString stringWithFormat:@"nType: %d\n", ocrResult.nType]];
                
                CFStringEncoding encoding = (CFStringEncoding)CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingDOSKorean);
                
                NSString *no = [NSString stringWithCString:(const char *)ocrResult.szIdNo encoding:encoding];
                str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"주민번호: ", no]];
                
                NSString *name = [NSString stringWithCString:(const char *)ocrResult.szIdName encoding:encoding];
                str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"이름: ", name]];
                
                NSString *day = [NSString stringWithCString:(const char *)ocrResult.szIdDay encoding:encoding];
                str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"발급일자: ", day]];
                
                NSString *authorNo = [NSString stringWithCString:(const char *)ocrResult.szAuthorNo encoding:encoding];
                str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"면허번호: ", authorNo]];
                
                NSString *RawFDValue = [NSString stringWithCString:(const char *)scanResult.szRawFDValue encoding:encoding];
                str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"전체 TR-UV-IR: ", RawFDValue]];
                
                NSString *OcrFDValue = [NSString stringWithCString:(const char *)scanResult.szOcrFDValue encoding:encoding];
                str = [str stringByAppendingString:[NSString stringWithFormat:@"%@%@\n", @"TR-UV-IR: ", OcrFDValue]];
                
                NSLog(@"ImgPos: %d,%d,%d,%d", ocrResult.rIdRearPos.left, ocrResult.rIdRearPos.top, ocrResult.rIdRearPos.right, ocrResult.rIdRearPos.bottom);
                NSLog(@"ocrResult.rotate : %d", ocrResult.nRotate);

                CGColorRef color = [UIColor colorWithRed:230.0/255.0 green:20.0/255.0 blue:20.0/255.0 alpha:0.75].CGColor;
                
                switch (scanConfig.nSide) {
                    case 1: // front
                        image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgFront length:scanResult.nFrontSize]];
                        [self.img1 setImage:image];
                        image = EzSWifiImageProc(image, ocrResult.rIdRearPos, color, ocrResult.nRotate);
                        [self.img2 setImage:image];
                   
                        break;
                        
                    case 2: // rear
                        image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgRear length:scanResult.nRearSize]];
                        [self.img1 setImage:image];
                        image = EzSWifiImageProc(image, ocrResult.rIdRearPos, [UIColor blackColor].CGColor, ocrResult.nRotate);
                        [self.img2 setImage:image];
                        break;
                        
                    default: // both
                        if (ocrResult.nSwapYn == 0) {
                            image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgFront length:scanResult.nFrontSize]];
                            [self.img1 setImage:image];
                            image = EzSWifiImageProc(image, ocrResult.rIdRearPos, [UIColor blackColor].CGColor, ocrResult.nRotate);
                            [self.img2 setImage:image];
                        } else {
                            image = [UIImage imageWithData:[NSData dataWithBytes:scanResult.vImgRear length:scanResult.nRearSize]];
                            [self.img1 setImage:image];
                            image = EzSWifiImageProc(image, ocrResult.rIdRearPos, [UIColor blackColor].CGColor, ocrResult.nRotate);
                            [self.img2 setImage:image];
                        }
                        break;
                }
                UIImage *timestamp = EzSWifiTimestamp(image, "SKT", NULL);
                [self.img3 setImage:timestamp];
            }
            
            [self.lblResult setText:str];
        } @finally {
            if (scanResult.vImgFront != nil && scanResult.nFrontSize > 0) {
                free(scanResult.vImgFront);
            }
            if (scanResult.vImgRear != nil && scanResult.nRearSize > 0) {
                free(scanResult.vImgRear);
            }
        }
    }
    EzSWifiCloseSession(&httpInfo);
  
    [self endCommand];
}

@end
