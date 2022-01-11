package com.voimtech.idswifi.wssvc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.scanner.service.impl.ScannerServiceImpl;

/**
 *
 *
 */
public class wssvcJNI {

	private static final Logger LOGGER = LoggerFactory.getLogger(wssvcJNI.class);
	
  //  timeout
  public static final int TIMEOUT_MIN = 1;
  public static final int TIMEOUT_MAX = 60;
  public static final int TIMEOUT_DEF = 5;
  //  side
  public static final int SIDE_FRONT  = 1;
  public static final int SIDE_REAR   = 2;
  public static final int SIDE_BOTH   = 3;
  public static final int SIDE_DEF    = SIDE_BOTH;
  //  resolution
  public static final int RES_100_DPI = 100;
  public static final int RES_200_DPI = 200;
  public static final int RES_300_DPI = 300;
  public static final int RES_600_DPI = 600;
  public static final int RES_DEF     = RES_600_DPI;
  //  bitDepth
  public static final int COL_BW      = 1;
  public static final int COL_GRAY    = 8;
  public static final int COL_RGB     = 24;
  public static final int COL_DEF     = COL_RGB;
  //  rotation
  public static final int ROT_0       = 0;
  public static final int ROT_90      = 90;
  public static final int ROT_180     = 180;
  public static final int ROT_270     = 270;
  public static final int ROT_DEF     = ROT_0;
  //  bitDepth
  public static final int FD_LEVEL_MIN  = 0;
  public static final int FD_LEVEL_MAX  = 100;
  public static final int FD_LEVEL_DEF  = FD_LEVEL_MAX;
  //  colorDropout
  public static final int DROP_NONE     = 4;
  public static final int DROP_RED      = 0;
  public static final int DROP_GREEN    = 1;
  public static final int DROP_BLUE     = 2;
  public static final int DROP_DEF      = DROP_NONE;
  //  compression
  public static final int COMP_NONE     = 0;
  public static final int COMP_DEF      = COMP_NONE;
  
  //  Errors in wsapiJNI
  public static final int ERROR_SUCCESS         = 0;
  public static final int ERROR_OFFLINE         = -1;    //E_ERR_NOCONNECT, E_ERR_NO_DEVICE
  public static final int ERROR_INTERNAL        = -2;
  public static final int ERROR_IO				      = -3;   //E_ERR_IO		    /** Input/output error */
  public static final int ERROR_MEMORY          = -4;   //E_ERR_NO_MEM    /** Insufficient memory */
  public static final int ERROR_SVC_NOT_RUNNING = -10;
  public static final int ERROR_IPC_CONNECTION  = -11;
  public static final int ERROR_API_LOAD_FAILED = -12;
  public static final int ERROR_API_OPEN_FAILED = -13;
  public static final int ERROR_BUSY            = 1;
  public static final int ERROR_JAM             = 2;    //E_ERR_JAM_FEED~E_ERR_JAM_STACK,E_ERR_JAM_CHK~E_ERR_SLIP
  public static final int ERROR_TIMEOUT         = 3;    //E_ERR_DOCTIMEOUT

  static public class DeviceInfo {
    public String   firmwareVersion;
    public String   serialNumber;
    public String   controlNumber;
    public String   sensorStatus;
    public boolean  oldCard; 
    public boolean  isJam() {
      boolean result;
      
      result = !sensorStatus.equals("0004") &&
               !sensorStatus.equals("000D") &&
               !sensorStatus.equals("0015");
      
      return result;
    }
  };
  
  static public class ImageData {
    public byte[] front;
    public byte[] rear;
  };

  static public class ScanData {
    public DeviceInfo deviceInfo;
    public ImageData  imageData;
    public String     fdValues;

	public byte[] frontImage;
	public byte[] rearImage;
  };

	static public class Option {
		public Option() {
			// Body of constructor
			timeout = TIMEOUT_DEF;
			side = SIDE_DEF;
			resolution = RES_DEF;
			color = COL_DEF;
			rotation = ROT_DEF;
			dropout = DROP_DEF;
			fdLevel = FD_LEVEL_DEF;
			compression = COMP_DEF;
		}
		
		public int setOption(Map<String, String> mapParameters) {
			int resVal = 0;
			
			try {
				LOGGER.info("----------------------- scanner.4");
				timeout = Integer.parseInt(mapParameters.get("timeout").toString());

				if ("front".equals(mapParameters.get("side"))) {
					side = 1;
				}
				else if ("rear".equals(mapParameters.get("side"))) {
					side = 2;
				}
				else if ("both".equals(mapParameters.get("side"))) {
					side = 3;
				}

				resolution = Integer.parseInt(mapParameters.get("resolution").toString());

				if ("bw".equals(mapParameters.get("color"))) {
					color = 1;
				}
				else if ("gray".equals(mapParameters.get("color"))) {
					color = 8;
				}
				else if ("rgb".equals(mapParameters.get("color"))) {
					color = 24;
				}

				rotation = Integer.parseInt(mapParameters.get("rotation").toString());

				if ("none".equals(mapParameters.get("dropout"))) {
					dropout = 0;
				}
				else if ("red".equals(mapParameters.get("dropout"))) {
					dropout = 1;
				}
				else if ("green".equals(mapParameters.get("dropout"))) {
					dropout = 2;
				}
				else if ("blue".equals(mapParameters.get("dropout"))) {
					dropout = 3;
				}

				fdLevel = Integer.parseInt(mapParameters.get("fdLevel").toString());
			} catch (Exception e) {
				resVal = -1;
				LOGGER.info("----------------------- scanner.5");
			}
			
			return resVal;
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
//		System.load(libUrl + "/" + "libwssvcJNI.so");
		File lib = new File(libUrl + "/" + System.mapLibraryName("wssvcJNI"));
		System.load(lib.getAbsolutePath());
	}

	public static void saveStreamToBmp(String filename, InputStream bitmapStream) {
		saveStreamToImage(filename + ".bmp", "BMP", bitmapStream);
	}
	
	public static void saveStreamToImage(String filename, String format, InputStream bitmapStream) {
		try {
			int length = bitmapStream.available();
			BufferedImage bitmap = ImageIO.read(bitmapStream);
			ImageIO.write(bitmap, format, new File(filename));
			bitmap.flush();
		} catch (Exception e) {
//			System.out.println("saveStreamToBitmap:" + e.getMessage());
		}
	}
	
	public native int scan(ScanData[] data, Option options);
	public native int getDeviceInfo(DeviceInfo[] deviceInfo); 
}
