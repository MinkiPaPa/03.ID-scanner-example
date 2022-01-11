package com.voimtech.jni;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class wsapiJNI {
	
	// timeout
	public static final int TIMEOUT_MIN = 1;
	public static final int TIMEOUT_MAX = 60;
	public static final int TIMEOUT_DEF = 5;
	// side
	public static final int SIDE_FRONT = 1;
	public static final int SIDE_REAR = 2;
	public static final int SIDE_BOTH = 3;
	public static final int SIDE_DEF = SIDE_FRONT;
	// resolution
	public static final int RES_100_DPI = 100;
	public static final int RES_200_DPI = 200;
	public static final int RES_300_DPI = 300;
	public static final int RES_600_DPI = 600;
	public static final int RES_DEF = RES_600_DPI;
	// bitDepth
	public static final int COL_BW = 1;
	public static final int COL_GRAY = 8;
	public static final int COL_RGB = 24;
	public static final int COL_DEF = COL_RGB;
	// rotation
	public static final int ROT_0 = 0;
	public static final int ROT_90 = 90;
	public static final int ROT_180 = 180;
	public static final int ROT_270 = 270;
	public static final int ROT_DEF = ROT_0;
	// bitDepth
	public static final int FD_LEVEL_MIN = 0;
	public static final int FD_LEVEL_MAX = 100;
	public static final int FD_LEVEL_DEF = FD_LEVEL_MAX;
	// colorDropout
	public static final int DROP_NONE = 4;
	public static final int DROP_RED = 0;
	public static final int DROP_GREEN = 1;
	public static final int DROP_BLUE = 2;
	public static final int DROP_DEF = DROP_NONE;
	// compression
	public static final int COMP_NONE = 0;
	public static final int COMP_DEF = COMP_NONE;

	
	static public class DeviceInfo {
		public String firmwareVersion;
		public String serialNumber;
		public String controlNumber;
		public String sensorStatus;
	};
	

	static public class ScanData {
		public ScanData() {}
		
		public String firmwareVersion;
		public String serialNumber;
		public String controlNumber;
		public String sensorStatus;

		public byte[] frontImage;
		public byte[] rearImage;
		public String fdValues;
	};

	
	static public class Option {
//		public Option(int timeout, int side, int resolution, int color, int rotation, int dropout, int fdLevel,
//				int compression) {
//			super();
//			this.timeout = timeout;
//			this.side = side;
//			this.resolution = resolution;
//			this.color = color;
//			this.rotation = rotation;
//			this.dropout = dropout;
//			this.fdLevel = fdLevel;
//			this.compression = compression;
//		}

		public Option() {
			timeout 	= TIMEOUT_DEF;
			side 		= SIDE_DEF;
			resolution 	= RES_DEF;
			color 		= COL_DEF;
			rotation 	= ROT_DEF;
			dropout 	= DROP_DEF;
			fdLevel 	= FD_LEVEL_DEF;
			compression = COMP_DEF;
		}
		
		public int timeout;
		
		public int side;
		
		public int resolution;
		
		public int color;
		
		public int rotation;
		
		public int dropout;
		
		public int fdLevel;
		
		public int compression;
	};
	
	public static String libUrl = "";
	public static String imgDir = "";
	
	
	public static void loadLib() {
		// Load the dll that exports functions callable from java
//		static {
//			System.out.println("===========> " + libUrl + "/" + System.mapLibraryName("wsapi"));
			File lib = new File(libUrl + "/" + System.mapLibraryName("wsapi"));
			System.load(lib.getAbsolutePath());
//		}
		
		// Load the dll that exports functions callable from java
//		static {
			lib = new File(libUrl + "/" + System.mapLibraryName("wsapiJNI"));
			System.load(lib.getAbsolutePath());
//		}
	}
	
	public static void saveStreamToImage(String filename, String format, InputStream bitmapStream) {
		try {
			int length = bitmapStream.available();
//			System.out.println("bitmapStream = " + length);
			BufferedImage bitmap = ImageIO.read(bitmapStream);
			ImageIO.write(bitmap, format, new File(filename));
			bitmap.flush();
		} catch (Exception e) {
//			System.out.println("saveStreamToBitmap:" + e.getMessage());
		}
	}

	public static void saveStreamToBmp(String filename, InputStream bitmapStream) {
		saveStreamToImage(filename + ".bmp", "BMP", bitmapStream);
	}

	public static void saveStreamToJpeg(String filename, InputStream bitmapStream) {
		saveStreamToImage(filename + ".jpg", "JPG", bitmapStream);
	}
	
	// Imported function declarations
	public static native int scan(ScanData[] data, Option options);
	public static native int getDeviceInfo(DeviceInfo[] deviceInfo);
}
