package egovframework.test.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.voimtech.idswifi.wssvc.wssvcJNI;

@Controller
@RequestMapping(value = "/test")
public class TestScannerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestScannerController.class);

	/**
	 * 스캐너를 테스트 한다.
	 * @param Map<String, String> - 스캐너 옵션 정보
	 * @exception Exception
	 */
	@RequestMapping(value = "/testScanner.do")
	public String testScanner(@RequestParam Map<String, String> mapOption, HttpServletRequest req, ModelMap model) throws Exception {

//		String sPropertyFile = req.getSession().getServletContext().getRealPath("/WEB-INF/classes/egovframework/properties/scanner.properties");
//		FileInputStream in = new FileInputStream(sPropertyFile);
//		Properties properties = new Properties();
//		properties.load(in);
//		
//		LOGGER.info("========================== {}", properties.getProperty("aes.key"));
//		
//		properties.setProperty("aes.key", "aaaaaaaaaaaaaaaaaaaaaa");
//		properties.store(new FileOutputStream(sPropertyFile), "");
		
		wssvcJNI.Option options = new wssvcJNI.Option();
		
		model.addAttribute("options", options);
		
		return "/test/testScanner";
	}

}
