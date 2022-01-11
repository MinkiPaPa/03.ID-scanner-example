import java.nio.ByteBuffer;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics;

public class Test {
  public static void saveStreamToImage(String filename, String format, InputStream bitmapStream){
		try {
      int length = bitmapStream.available();
      System.out.println("bitmapStream = " + length);
      BufferedImage bitmap = ImageIO.read(bitmapStream);
      ImageIO.write(bitmap, format, new File(filename));
      bitmap.flush();
		} catch (Exception e) {
			System.out.println("saveStreamToBitmap:" + e.getMessage());
		}
  }
  public static void saveStreamToBmp(String filename, InputStream bitmapStream){
    saveStreamToImage(filename + ".bmp", "BMP", bitmapStream);
  }

  public static void saveStreamToJpeg(String filename, InputStream bitmapStream){
    saveStreamToImage(filename + ".jpg", "JPG", bitmapStream);
  }
	public static void main(String [] args) {
		wsapiJNI scanner = new wsapiJNI();

  	wsapiJNI.ScanData[] data;
  	wsapiJNI.DeviceInfo[] info;
  	wsapiJNI.Option  options;

    InputStream bitmapStream;
		
    //while (true) 
    {
    	data = new wsapiJNI.ScanData[1];
    	options = new wsapiJNI.Option();
    	
    	System.out.println("----------------------- scanner.scan");

      int returnValue = scanner.scan(data, options);
      if (returnValue == 0)
      {
        bitmapStream = new ByteArrayInputStream(data[0].frontImage);
        saveStreamToBmp("./testFront", bitmapStream); //  BMP
        try {
          bitmapStream.close();
        } catch (Exception e) {
			    System.out.println("testFront:" + e.getMessage());
        }
        
        bitmapStream = new ByteArrayInputStream(data[0].rearImage);
        saveStreamToJpeg("./testRear", bitmapStream); //  JPG
        try {
          bitmapStream.close();
        } catch (Exception e) {
			    System.out.println("testRear:" + e.getMessage());
        }
        
        System.out.println("fdValues: " + data[0].fdValues);
        System.out.println("firmwareVersion: " + data[0].firmwareVersion);
        System.out.println("serialNumber: " + data[0].serialNumber);
        System.out.println("cotrolNumber: " + data[0].controlNumber);
        System.out.println("sensorStatus: " + data[0].sensorStatus);
      }
      else
      {
        System.out.println("scanner.scan failed: " + returnValue);
      }

    	System.out.println("----------------------- scanner.getDeviceInfo");

      info = new wsapiJNI.DeviceInfo[1];
      
      returnValue = scanner.getDeviceInfo(info);
      if (returnValue == 0)
      {
        System.out.println("firmwareVersion: " + info[0].firmwareVersion);
        System.out.println("serialNumber: " + info[0].serialNumber);
        System.out.println("cotrolNumber: " + info[0].controlNumber);
        System.out.println("sensorStatus: " + info[0].sensorStatus);
      }
      else
      {
        System.out.println("scanner.getDeviceInfo failed: " + returnValue);
      }

    	System.out.println("");
    }
	}
}


