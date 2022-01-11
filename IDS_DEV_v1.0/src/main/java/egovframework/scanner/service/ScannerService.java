package egovframework.scanner.service;

import java.util.Map;

public interface ScannerService {

	/**
	 * 스캐너를 스캔 한다.
	 * @param Map<String, String> - 스캐너 옵션 정보
	 * @exception Exception
	 */
	Map<String, Object> scan(ScannerVO scannerVO) throws Exception;

	/**
	 * 스캐너 정보를 가져온다.
	 * @exception Exception
	 */
	Map<String, Object> getDeviceInfo(ScannerVO scannerVO) throws Exception;

	/**
	 * 스캐너 Session을 생성한다.
	 * @exception Exception
	 */
	Map<String, Object> openSession(ScannerVO scannerVO) throws Exception;

	/**
	 * 스캐너 Session을 삭제한다.
	 * @exception Exception
	 */
	Map<String, Object> closeSession(ScannerVO scannerVO) throws Exception;
}
