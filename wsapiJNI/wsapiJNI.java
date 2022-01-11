import java.io.File;
import java.nio.ByteBuffer;

public class wsapiJNI {
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
  public static final int FD_LEVEL_DEF  = 5;
  //  colorDropout
  public static final int DROP_NONE     = 4;
  public static final int DROP_RED      = 0;
  public static final int DROP_GREEN    = 1;
  public static final int DROP_BLUE     = 2;
  public static final int DROP_DEF      = DROP_NONE;
  //  compression
  public static final int COMP_NONE     = 0;
  public static final int COMP_DEF      = COMP_NONE;
  
  static public class DeviceInfo {
    String firmwareVersion;
    String serialNumber;
    String controlNumber;
    String sensorStatus;
  };
  
  static public class ScanData {
    public ScanData() { }
    String firmwareVersion;
    String serialNumber;
    String controlNumber;
    String sensorStatus;

    byte[] frontImage;
    byte[] rearImage;
    String fdValues;
  };

  static public class Option {
    public Option() {
      // Body of constructor
      timeout     = TIMEOUT_DEF;
      side        = SIDE_DEF;
      resolution  = RES_DEF;
      color       = COL_DEF;
      rotation    = ROT_DEF;
      dropout     = DROP_DEF;
      fdLevel     = FD_LEVEL_DEF;
      compression = COMP_DEF;
    }
    int timeout;
    int side;
    int resolution;
    int color;
    int rotation;
    int dropout;
    int fdLevel;
    int compression;
  };
  
  // Load the dll that exports functions callable from java
  static {
    File lib = new File("./" + System.mapLibraryName("wsapi"));
    System.load(lib.getAbsolutePath());    
  }

  // Load the dll that exports functions callable from java
  static {
    File lib = new File("./" + System.mapLibraryName("wsapiJNI"));
    System.load(lib.getAbsolutePath());    
  }

  //static {
  //  String currentDir = System.getProperty("user.dir");
  //  System.setProperty("java.library.path",currentDir);
  //  System.loadLibrary("wsapiJNI");
  //}

  //static {
  //  String currentDir = System.getProperty("user.dir");
  //  String wsapiLibraryPathName = currentDir + File.separator + "wsapiJNI";
  //  System.load(wsapiLibraryPathName);
  //}

  //static {System.loadLibrary("wsapiJNI");}
  
  // Imported function declarations
	public native int scan(ScanData[] data, Option options);
	public native int getDeviceInfo(DeviceInfo[] deviceInfo);
}
