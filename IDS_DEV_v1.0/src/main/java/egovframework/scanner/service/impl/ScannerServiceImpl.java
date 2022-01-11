package egovframework.scanner.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.voimtech.idswifi.wssvc.wssvcJNI;

import egovframework.cmmn.Aes128Util;
import egovframework.cmmn.SessionManager;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.scanner.service.ScannerService;
import egovframework.scanner.service.ScannerVO;

@Service("scannerService")
public class ScannerServiceImpl extends EgovAbstractServiceImpl implements ScannerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScannerServiceImpl.class);

	/**
	 * 스캐너를 스캔 한다.
	 * @param Map<String, String> - 스캐너 옵션 정보
	 * @exception Exception
	 */
	public Map<String, Object> scan(ScannerVO scannerVO) throws Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("result", "0");
		
		wssvcJNI scanner = new wssvcJNI();
		wssvcJNI.ScanData[] scanData = new wssvcJNI.ScanData[1];
		wssvcJNI.DeviceInfo[] info = new wssvcJNI.DeviceInfo[1];
		wssvcJNI.Option options = new wssvcJNI.Option();
		wssvcJNI.loadLib();
		
		int resVal = -1;

		// Session이 없을때
		if ("".equals(scannerVO.getSessionId())) {
			mapResult.put("result", "errorInvalidSessionId");
		}
		else {
			// 세션이 옳바를 때
			if (SessionManager.sessionValidChk("scan", scannerVO.getSessionId(), new Date().getTime())) {
				Map<String, String> mapParameters = scannerVO.getParameters();

				// 옵션 설정
				resVal = options.setOption(mapParameters);
				
				if (0 == resVal) {
					LOGGER.info("----------------------- scanner.scan");
					LOGGER.info("timeout : {}", options.timeout);
					LOGGER.info("side : {}", options.side);
					LOGGER.info("resolution : {}", options.resolution);
					LOGGER.info("color : {}", options.color);
					LOGGER.info("rotation : {}", options.rotation);
					LOGGER.info("dropout : {}", options.dropout);
					LOGGER.info("fdLevel : {}", options.fdLevel);
					LOGGER.info("compression : {}", options.compression);
					
					// 스캔
					resVal = scanner.scan(scanData, options);
					LOGGER.info("resVal : {}", resVal);
					
					if (resVal == 0) {
						InputStream bitmapStream;

						File saveFolder = new File(wssvcJNI.imgDir);

						// 디렉토리 생성
						if (!saveFolder.exists() || saveFolder.isFile()) {
							saveFolder.mkdirs();
						}

						if (1 == options.side || 3 == options.side) {
							bitmapStream = new ByteArrayInputStream(scanData[0].imageData.front);
							wssvcJNI.saveStreamToBmp(wssvcJNI.imgDir + "/frontimg", bitmapStream);

							try {
								bitmapStream.close();
							} catch (Exception e) {
								throw new java.lang.Exception("testFront:" + e.getMessage());
							}
						}

						if (2 == options.side || 3 == options.side) {
							bitmapStream = new ByteArrayInputStream(scanData[0].imageData.rear);
							wssvcJNI.saveStreamToBmp(wssvcJNI.imgDir + "/rearimg", bitmapStream);

							try {
								bitmapStream.close();
							} catch (Exception e) {
								throw new java.lang.Exception("testRear:" + e.getMessage());
							}
						}

						LOGGER.info("fdValues: " + scanData[0].fdValues);

						Map<String, String> mapResultTemp = new HashMap<String, String>();
						mapResultTemp.put("fdValues", scanData[0].fdValues);

						byte[] imageInByte;
						BufferedImage originalImage;
						ByteArrayOutputStream baos;
						
						Aes128Util.key = SessionManager.seesionId;

						if (1 == options.side || 3 == options.side) {
							LOGGER.info("----------------------- frontImage read");

							originalImage = ImageIO.read(new File(wssvcJNI.imgDir + "/frontimg.bmp"));
							baos = new ByteArrayOutputStream();
							ImageIO.write(originalImage, "bmp", baos);
							baos.flush();
							baos.close();

							LOGGER.info("----------------------- frontImage AES ENCRYPT");
							LOGGER.info("AES key: {}", Aes128Util.toString16(Aes128Util.key));
							imageInByte = baos.toByteArray();
							// 암호화
							imageInByte = Aes128Util.encrypt(Aes128Util.key, imageInByte);

							LOGGER.info("----------------------- frontImage Base64");
							// Base64
							imageInByte = Base64.encodeBase64(imageInByte);

							mapResultTemp.put("frontImage", new String(imageInByte));
						}

						if (2 == options.side || 3 == options.side) {
							LOGGER.info("----------------------- rearImage read");
							originalImage = ImageIO.read(new File(wssvcJNI.imgDir + "/rearimg.bmp"));
							baos = new ByteArrayOutputStream();
							ImageIO.write(originalImage, "bmp", baos);
							baos.flush();
							baos.close();

							LOGGER.info("----------------------- rearImage AES ENCRYPT");
							LOGGER.info("AES key: {}", Aes128Util.toString16(Aes128Util.key));
							imageInByte = baos.toByteArray();
							// 암호화
							imageInByte = Aes128Util.encrypt(Aes128Util.key, imageInByte);

							LOGGER.info("----------------------- rearImage Base64");
							// Base64
							imageInByte = Base64.encodeBase64(imageInByte);

							mapResultTemp.put("rearImage", new String(imageInByte));
						}

						mapResult.put("result", resVal);
						mapResult.put("data", mapResultTemp);

						LOGGER.info("----------------------- json date send");
					}
					else {
						mapResult.put("result", "error" + resVal);
					}
				}
				else {
					mapResult.put("result", "errorParameterSet");
				}
			}
			else {
				mapResult.put("result", "errorInvalidSessionId");
			}
		}
		
		return mapResult;
	}

	/**
	 * 스캐너 정보를 가져온다.
	 * @exception Exception
	 */
	public Map<String, Object> getDeviceInfo(ScannerVO scannerVO) throws Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("result", "0");
		
		wssvcJNI scanner = new wssvcJNI();
		wssvcJNI.ScanData[] scanData = new wssvcJNI.ScanData[1];
		wssvcJNI.DeviceInfo[] info = new wssvcJNI.DeviceInfo[1];
		wssvcJNI.Option options = null;
		wssvcJNI.loadLib();
		
		int resVal = -1;

		// Session이 없을때
		if ("".equals(scannerVO.getSessionId())) {
			
			LOGGER.info("----------------------- scanner.getDeviceInfo");
			resVal = scanner.getDeviceInfo(info);
			
			if (resVal == 0) {
				Map<String, String> mapResultDeviceInfo = new HashMap<String, String>();
				mapResultDeviceInfo.put("firmwareVersion", info[0].firmwareVersion);
				mapResultDeviceInfo.put("serialNumber", info[0].serialNumber);
				mapResultDeviceInfo.put("controlNumber", info[0].controlNumber);
				
				LOGGER.info("firmwareVersion: " + info[0].firmwareVersion);
				LOGGER.info("serialNumber: " + info[0].serialNumber);
				LOGGER.info("cotrolNumber: " + info[0].controlNumber);
				
				mapResult.put("result", resVal);
				mapResult.put("deviceInfo", mapResultDeviceInfo);
			}
			else {
				mapResult.put("result", "error" + resVal);
			}
		}
		else {
			// 세션이 옳바를 때
			if (SessionManager.sessionValidChk("getDeviceInfo", scannerVO.getSessionId(), new Date().getTime())) {
				LOGGER.info("----------------------- scanner.getDeviceInfo");
				resVal = scanner.getDeviceInfo(info);
				
				if (resVal == 0) {
					Map<String, String> mapResultDeviceInfo = new HashMap<String, String>();
					mapResultDeviceInfo.put("firmwareVersion", info[0].firmwareVersion);
					mapResultDeviceInfo.put("serialNumber", info[0].serialNumber);
					mapResultDeviceInfo.put("controlNumber", info[0].controlNumber);
					
					LOGGER.info("firmwareVersion: " + info[0].firmwareVersion);
					LOGGER.info("serialNumber: " + info[0].serialNumber);
					LOGGER.info("cotrolNumber: " + info[0].controlNumber);
					
					mapResult.put("result", resVal);
					mapResult.put("deviceInfo", mapResultDeviceInfo);
				}
				else {
					mapResult.put("result", "error" + resVal);
				}
			}
			else {
				mapResult.put("result", "errorInvalidSessionId");
			}
		}
		
		return mapResult;
	}

	/**
	 * 스캐너 Session을 생성한다.
	 * @exception Exception
	 */
	public Map<String, Object> openSession(ScannerVO scannerVO) throws Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("result", "0");
		

		// Session이 없을때
		if ("".equals(SessionManager.seesionId)) {
			mapResult.put("sessionId", SessionManager.createSession(Long.valueOf(scannerVO.getTimeout())));
		}
		else {
			// 세션 타임아웃이 지난 경우
			if ((SessionManager.seesionMakeTime + SessionManager.seesionTimeOut) < new Date().getTime()) {
				mapResult.put("sessionId", SessionManager.createSession(Long.valueOf(scannerVO.getTimeout())));
			}
			else {
				mapResult.put("result", "errorBusy");
				mapResult.put("userInfo", scannerVO.getUserInfo());
			}
		}
		
		return mapResult;
	}

	/**
	 * 스캐너 Session을 삭제한다.
	 * @exception Exception
	 */
	public Map<String, Object> closeSession(ScannerVO scannerVO) throws Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("result", "0");
		
		// Session이 없을때
		if ("".equals(SessionManager.seesionId)) {
			mapResult.put("result", "errorInvalidSessionId");
		}
		else {
			if (!SessionManager.expireSession(scannerVO.getSessionId())) {
				mapResult.put("result", "errorInvalidSessionId");
			}
		}

		return mapResult;
	}
}
