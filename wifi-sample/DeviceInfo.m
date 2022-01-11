//
//  DeviceInfo.m
//  wifi-sample
//
//  Created by Jae Cheol Ko on 08/12/2017.
//  Copyright © 2017 ezdocu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <netinet/in.h>
#import <arpa/inet.h>
#import "DeviceInfo.h"
#import "Reachability.h"
#import "wifi_sdk/wifi_sdk.h"

@implementation DeviceInfo {
    
}

- (id)init {
    self = [super init];
    if (self) {
        found = false;
        running = false;
        firmwareVersion = @"";
        serialNo = @"";
        controlNo = @"";
    }
    return self;
}

- (void)start {
    found = false;
    running = true;
    

    Type_HTTPInfo       httpInfo;
    Type_DeviceInfo     deviceInfo;
    
    
     memset(&httpInfo, 0x00, sizeof(httpInfo));
     memset(&deviceInfo, 0x00, sizeof(deviceInfo));
    
    NSString *url = [NSString stringWithFormat:@"https://%@:%d/test/handle.do", host, 8443];
    strcpy(httpInfo.szSvrAddr, url.UTF8String);
    httpInfo.nReadTimeout = 5;
    
    int result = EzSWifiScanInfo(&httpInfo, &deviceInfo);
    
    NSLog(@"result : %d", result);
    NSLog(@"sessionId: %s", httpInfo.szSessionId);
    
    
    if (result == 0) {
        self->nickname = [NSString stringWithUTF8String:deviceInfo.szNickName];
        
        found = true;
    }
    running = false;
}

- (void)stop {
    if (running) {
        
    }
    running = false;
}

- (bool)isRunning {
    return running;
}

- (bool)isFound {
    return found;
}

@end
