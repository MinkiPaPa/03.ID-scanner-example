package com.voimtech.idswifi.wssvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class Test {
	public static void main(String[] args) {
		
//		wssvcJNI scanner = new wssvcJNI();

		wssvcJNI.libUrl = "/var/lib/tomcat8/webapps/ROOT/WEB-INF/lib";
		wssvcJNI.imgDir = "/home/scan/img";
		wssvcJNI scanner = new wssvcJNI();
		wssvcJNI.ScanData[] scanData = new wssvcJNI.ScanData[1];
		wssvcJNI.DeviceInfo[] info = new wssvcJNI.DeviceInfo[1];
		wssvcJNI.Option options = new wssvcJNI.Option();
		wssvcJNI.loadLib();
		
//		options.timeout = 5;
//		options.side = 3;
//		options.resolution = 600;
//		options.color = 24;
//		options.rotation = 0;
//		options.dropout = 4;
//		options.fdLevel = 100;
//		options.compression = 0;
		
		int resVal = -1;

		resVal = scanner.getDeviceInfo(info);
		resVal = scanner.scan(scanData, options);
		System.out.println("================= " + scanData[0].imageData.front + ", ====" + scanData[0].imageData.front.length);

//		InputStream bitmapStream;
//		bitmapStream = new ByteArrayInputStream(scanData[0].imageData.front);
//		wssvcJNI.saveStreamToBmp(wssvcJNI.imgDir + "/testFront", bitmapStream); // BMP
	}
}
