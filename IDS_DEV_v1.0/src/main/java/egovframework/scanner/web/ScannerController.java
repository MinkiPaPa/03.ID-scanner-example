package egovframework.scanner.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voimtech.idswifi.wssvc.wssvcJNI;

import egovframework.cmmn.EgovFileTool;
import egovframework.cmmn.PropertyUtil;
import egovframework.scanner.service.ScannerService;
import egovframework.scanner.service.ScannerVO;

@Controller
@RequestMapping(value = "/scanner")
public class ScannerController {

	/** ScannerService */
	@Resource(name = "scannerService")
	private ScannerService scannerService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScannerController.class);
	
//	@Value("#{scanner['nickname']}")
//	private String sNickname;
//	
//	@Value("#{scanner['img.dir']}")
//	private String sImgDir;

	/**
	 * 스캐너를 스캔 한다.
	 * @param Map<String, String> - 스캐너 옵션 정보
	 * @exception Exception
	 */
	@RequestMapping(value = "/scan.do")
	@ResponseBody
	public Map<String, Object> scan(@ModelAttribute("scannerVO") ScannerVO scannerVO, HttpServletRequest req, ModelMap model) throws Exception {

		String sPropertyFile = req.getSession().getServletContext().getRealPath("scanner.conf");
		Properties properties = PropertyUtil.getProperties(sPropertyFile);
		
		String sNickname = properties.getProperty("nickname");
		String sImgDir = properties.getProperty("img.dir");
		
		// wssvcJNI 경로가 없을때 처음 한번만 실행
		if ("".equals(wssvcJNI.libUrl)) {
			wssvcJNI.libUrl = req.getSession().getServletContext().getRealPath("/WEB-INF/lib");
			wssvcJNI.imgDir = sImgDir;
		}

		Map<String, Object> mapResult = new HashMap<String, Object>();
		
		try {
			if ("scan".equals(scannerVO.getCommand())) {
				mapResult = scannerService.scan(scannerVO);
			}
			else if ("getDeviceInfo".equals(scannerVO.getCommand())) {
				mapResult = scannerService.getDeviceInfo(scannerVO);
			}
			else if ("openSession".equals(scannerVO.getCommand())) {
				mapResult = scannerService.openSession(scannerVO);
			}
			else if ("closeSession".equals(scannerVO.getCommand())) {
				mapResult = scannerService.closeSession(scannerVO);
			}
			else {
				mapResult.put("result", "errorInvalidCommand");
			}
		} catch (Exception e) {
			mapResult.put("result", "-99");
			mapResult.put("message", e.getMessage());
		} finally {
			EgovFileTool.deleteFile(wssvcJNI.imgDir + "/frontimg.bmp");
			EgovFileTool.deleteFile(wssvcJNI.imgDir + "/rearimg.bmp");
		}

		mapResult.put("nickname", sNickname);
		
		return mapResult;
	}
}
