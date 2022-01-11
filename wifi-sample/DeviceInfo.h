//
//  DeviceInfo.h
//  wifi-sample
//
//  Created by Jae Cheol Ko on 08/12/2017.
//  Copyright © 2017 ezdocu. All rights reserved.
//

#ifndef DeviceInfo_h
#define DeviceInfo_h

@interface DeviceInfo : NSObject {

@private
    bool found;
    bool running;

@public
    NSString *firmwareVersion;
    NSString *serialNo;
    NSString *controlNo;
    NSString *host;
    NSString *nickname;
}

- (void)start;
- (void)stop;
- (bool)isRunning;
- (bool)isFound;

@end

#endif /* DeviceInfo_h */
