package egovframework.cmmn.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import egovframework.cmmn.PropertyUtil;
import egovframework.cmmn.service.CommonService;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : CommonServiceImpl.java
 * @
 * @   수정일      수정자              수정내용
 * @ ----------   --------   -------------------------------
 * @ 2017.06.21    서용기                 최초생성
 *
 *
 */
@Service("commonService")
public class CommonServiceImpl extends EgovAbstractServiceImpl implements CommonService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceImpl.class);

	/**
	 * 첨부파일을 등록한다.
	 * @param Map - 첨부파일 정보
	 * @return
	 * @exception Exception
	 */
	public Map<String, String> insertAttachFile(HttpServletRequest req) throws Exception {

		// 파일등록
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) req;

		Map<String, MultipartFile> files = multiRequest.getFileMap();

		String sPropertyFile = req.getSession().getServletContext().getRealPath("scanner.conf");
		Properties properties = PropertyUtil.getProperties(sPropertyFile);
		
		String uploadPath = properties.getProperty("cert.dir");
		
		File saveFolder;

		Iterator<Entry<String, MultipartFile>> itr = files.entrySet().iterator();
		MultipartFile file;
		String orginFilePath;
		Map<String, String> mapFile = new HashMap<String, String>();

		while (itr.hasNext()) {
			Entry<String, MultipartFile> entry = itr.next();

			file = entry.getValue();
			if (!"".equals(file.getOriginalFilename())) {
				String sName = file.getName();
				
				saveFolder = new File(uploadPath + "/" + sName);

				// 디렉토리 생성
				if (!saveFolder.exists() || saveFolder.isFile()) {
					saveFolder.mkdirs();
				}
				
				orginFilePath = file.getOriginalFilename();
				
				file.transferTo(new File(uploadPath + "/" + sName + "/" + orginFilePath));
				mapFile.put(sName, orginFilePath);
			}
		}
		
		return mapFile;
	}
}
