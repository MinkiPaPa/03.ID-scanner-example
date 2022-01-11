package egovframework.cmmn.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @Class Name : CommonService.java
 * @
 * @   수정일      수정자              수정내용
 * @ ----------   --------   -------------------------------
 * @ 2017.06.21    서용기                 최초생성
 *
 *
 */
public interface CommonService {

	/**
	 * 첨부파일을 등록한다.
	 * @param Map - 첨부파일 정보
	 * @return
	 * @exception Exception
	 */
	Map<String, String> insertAttachFile(HttpServletRequest req) throws Exception;
}
