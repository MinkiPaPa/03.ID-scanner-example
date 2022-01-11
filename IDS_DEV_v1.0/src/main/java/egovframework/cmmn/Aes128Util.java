package egovframework.cmmn;

/*
 * 작성된 날짜: 2010. 5. 20.
 *
 * TODO 생성된 파일에 대한 템플리트를 변경하려면 다음으로 이동하십시오.
 * 창 - 환경 설정 - Java - 코드 스타일 - 코드 템플리트
 */

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Aes128Util {

	public static String key = "0123456789012345";

    /**
     * hex to byte[] : 16진수 문자열을 바이트 배열로 변환한다.
     *
     * @param hex    hex string
     * @return
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    /**
     * byte[] to hex : unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
     *
     * @param ba        byte[]
     * @return
     */
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

    /**
     * Key를 16자리로 만들어서 Return
     *	
     * @param sKey
     * @return 16자리 sKey
     * @throws Exception
     */
    public static String toString16(String sKey) throws Exception {
    	
    	if(16 > sKey.length()) {
    		int iAdd = 16 - sKey.length();

            for (int x = 0; x < iAdd; x++) {
            	sKey = sKey + "0";
            }
    		
    	}
    	else {
    		sKey = sKey.substring(0, 16);
    	}
    	
        return sKey;
    }

    /**
     * AES 방식의 암호화
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String encrypt(String message) throws Exception {

        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(message.getBytes());
        return byteArrayToHex(encrypted);
    }

    /**
     * AES 방식의 암호화
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String encrypt(String sKey, String message) throws Exception {

    	sKey = toString16(sKey);
    	
        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(), "AES");

        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(message.getBytes());
        return byteArrayToHex(encrypted);
    }

    /**
     * AES 방식의 암호화
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(String sKey, byte[] message) throws Exception {

    	sKey = toString16(sKey);
    	
        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(), "AES");

        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(message);
        return encrypted;
    }

    /**
     * AES 방식의 복호화
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String decrypt(String encrypted) throws Exception {

        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(hexToByteArray(encrypted));
        String originalString = new String(original);
        return originalString;
    }

    /**
     * AES 방식의 복호화
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String decrypt(String sKey, String encrypted) throws Exception {

    	sKey = toString16(sKey);
    	
        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(hexToByteArray(encrypted));
        String originalString = new String(original);
        return originalString;
    }

/*
    public static void main(String[] args)
    {
        try {
        	String key1 = "AKCSPass";
        	String message1 = "first";
        	String key2 = "AKCSPass00000000";
        	String message2 = "second";
            String encrypt = encrypt(key1, message1);
            System.out.println("origin str = "+key1);
            System.out.println("encrypt str = "+encrypt);
           
            String decrypt = decrypt(key1, encrypt);
            System.out.println("decrypt str = "+decrypt);
            
            System.out.println("=================================");

            String encrypt2 = encrypt(key2, message2);
            System.out.println("origin2 str = "+key2);
            System.out.println("encrypt2 str = "+encrypt2);
           
            String decrypt2 = decrypt(key2, encrypt2);
            System.out.println("decrypt2 str = "+decrypt2);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }
*/
}

