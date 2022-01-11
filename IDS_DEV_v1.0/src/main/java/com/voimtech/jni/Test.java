package com.voimtech.jni;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Test {
	public static void main(String[] args) {
		
//		wsapiJNI scanner = new wsapiJNI();

		wsapiJNI.libUrl = "/var/lib/tomcat8/webapps/ROOT/WEB-INF/lib/";
		wsapiJNI.imgDir = "/home/scan/img";
		wsapiJNI.ScanData[] data;
		wsapiJNI.DeviceInfo[] info;
		wsapiJNI.Option options;
		wsapiJNI.loadLib();

		InputStream bitmapStream;

		// while (true)
		{
			data = new wsapiJNI.ScanData[1];
			options = new wsapiJNI.Option();

			System.out.println("----------------------- scanner.scan");

			int returnValue = wsapiJNI.scan(data, options);
			System.out.println("================= " + data[0].frontImage + ", ====" + data[0].frontImage.length);
			
			if (returnValue == 0) {
				bitmapStream = new ByteArrayInputStream(data[0].frontImage);
				wsapiJNI.saveStreamToBmp(wsapiJNI.imgDir + "/testFront", bitmapStream); // BMP
				try {
					bitmapStream.close();
				} catch (Exception e) {
					System.out.println("testFront:" + e.getMessage());
				}

//				bitmapStream = new ByteArrayInputStream(data[0].rearImage);
//				wsapiJNI.saveStreamToBmp(wsapiJNI.imgDir + "/testRear", bitmapStream); // JPG
//				try {
//					bitmapStream.close();
//				} catch (Exception e) {
//					System.out.println("testRear:" + e.getMessage());
//				}

				System.out.println("fdValues: " + data[0].fdValues);
				System.out.println("firmwareVersion: " + data[0].firmwareVersion);
				System.out.println("serialNumber: " + data[0].serialNumber);
				System.out.println("cotrolNumber: " + data[0].controlNumber);
				System.out.println("sensorStatus: " + data[0].sensorStatus);
			} else {
				System.out.println("scanner.scan failed: " + returnValue);
			}

			System.out.println("----------------------- scanner.getDeviceInfo");

			info = new wsapiJNI.DeviceInfo[1];

			returnValue = wsapiJNI.getDeviceInfo(info);
			if (returnValue == 0) {
				System.out.println("firmwareVersion: " + info[0].firmwareVersion);
				System.out.println("serialNumber: " + info[0].serialNumber);
				System.out.println("cotrolNumber: " + info[0].controlNumber);
				System.out.println("sensorStatus: " + info[0].sensorStatus);
			} else {
				System.out.println("scanner.getDeviceInfo failed: " + returnValue);
			}

			System.out.println("");
		}
	}
}
