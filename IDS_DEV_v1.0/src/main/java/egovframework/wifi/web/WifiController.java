package egovframework.wifi.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;

import egovframework.cmmn.Aes128Util;
import egovframework.cmmn.EgovFileTool;
import egovframework.cmmn.PropertyUtil;
import egovframework.cmmn.service.CommonService;
import egovframework.cmmn.service.EgovUserDetailsService;
import egovframework.wifi.service.LoginVO;
import egovframework.wifi.service.WifiVO;

@Controller
@RequestMapping(value = "/wifi")
public class WifiController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WifiController.class);

	/** SampleService */
	@Resource(name = "commonService")
	private CommonService commonService;

	/** egovUserDetailsService */
	@Resource(name = "egovUserDetailsService")
	protected EgovUserDetailsService egovUserDetailsService;

	/**
	 * login 화면
	 * @param LoginVO
	 * @return loginView.jsp
	 * @exception Exception
	 */
	@RequestMapping(value = "/loginView.do")
	public String loginView(@ModelAttribute("loginVO") LoginVO loginVO, HttpServletRequest req, ModelMap model) throws Exception {
		
		model.addAttribute("url", req.getRequestURL().toString().substring(0, req.getRequestURL().toString().indexOf(req.getRequestURI())));
//		LOGGER.info("webId : {}, webPw : {}", webId, webPw);
		
		return "/wifi/loginView";
	}

	/**
	 * 로그인을 처리한다
	 * @param loginVO - 아이디, 비밀번호가 담긴 LoginVO
	 * @param req
	 * @return result
	 * @exception Exception
	 */
	@RequestMapping(value = "/login.do")
	public String login(@ModelAttribute("loginVO") LoginVO loginVO, @RequestParam Map<String, String> mapParam, HttpServletRequest req, ModelMap model) throws Exception {

		String sPropertyFile = req.getSession().getServletContext().getRealPath("scanner.conf");
		Properties properties = PropertyUtil.getProperties(sPropertyFile);
		
		String webId = properties.getProperty("web.id");
		String webPw = properties.getProperty("web.pw");
		
		// 암호화 키값 설정
		Aes128Util.key = loginVO.getUserId() + "idsw";
		
		loginVO.setPw(Aes128Util.encrypt(Aes128Util.key, loginVO.getPw()));
		
		if (webId.equals(loginVO.getUserId()) && webPw.equals(loginVO.getPw())) {
			req.getSession().setAttribute("loginVO", loginVO);
			
			// 초기 비밀번호 admin_idsw 일경우 비밀번호 변경으로 이동
			if ("admin_idsw".equals(Aes128Util.decrypt(Aes128Util.key, loginVO.getPw()))) {
				model.addAttribute("url", "/wifi/changePwView.do");
			}
			else {
				model.addAttribute("url", "/wifi/main.do");
			}
		}
		else {
			model.addAttribute("message", "ID, 비밀번호를 다시 확인하세요.");
			model.addAttribute("url", "/wifi/loginView.do");
		}

		model.addAttribute("mapParam", mapParam);
		
		return "/cmmn/cmmPageSubmit";
	}

	/**
	 * 비밀번호를 변경한다
	 * @param loginVO - 아이디, 비밀번호가 담긴 LoginVO
	 * @param req
	 * @return result
	 * @exception Exception
	 */
	@RequestMapping(value = "/changePwView.do")
	public String changePwView(@RequestParam Map<String, String> mapParam, HttpServletRequest req, ModelMap model) throws Exception {

		return "/wifi/changePwView";
	}

	/**
	 * 비밀번호를 변경한다
	 * @param loginVO - 아이디, 비밀번호가 담긴 LoginVO
	 * @param req
	 * @return result
	 * @exception Exception
	 */
	@RequestMapping(value = "/changePw.do")
	public String changePw(@RequestParam Map<String, String> mapParam, HttpServletRequest req, ModelMap model) throws Exception {

		LoginVO loginVO = (LoginVO) egovUserDetailsService.getAuthenticatedUser();
		Aes128Util.key = loginVO.getUserId() + "idsw";
		
		// 현재 비밀번호가 틀리면
		if (!Aes128Util.encrypt(Aes128Util.key, mapParam.get("pw")).equals(loginVO.getPw())) {
			model.addAttribute("message", "현재 비밀번호를 다시 확인하세요.");
			model.addAttribute("url", "/wifi/changePwView.do");
		}
		else {
			loginVO.setPw(Aes128Util.encrypt(Aes128Util.key, mapParam.get("pw1")));

			String sPropertyFile = req.getSession().getServletContext().getRealPath("scanner.conf");
			Properties properties = PropertyUtil.getProperties(sPropertyFile);

			properties.setProperty("web.pw", loginVO.getPw());
			properties.store(new FileOutputStream(sPropertyFile), "");

			model.addAttribute("message", "비밀번호가 수정 되었습니다.");
			model.addAttribute("url", "/wifi/main.do");
		}
		
		return "/cmmn/cmmPageSubmit";
	}

	/**
	 * main 화면
	 * @param loginVO - 아이디, 비밀번호가 담긴 LoginVO
	 * @param req
	 * @return result
	 * @exception Exception
	 */
	@RequestMapping(value = "/main.do")
	public String main(@RequestParam Map<String, String> mapParam, HttpServletRequest req, ModelMap model) throws Exception {

		return "/wifi/main";
	}

	/**
	 * Wifi 설정 화면
	 * @param wifiVO - Wifi 설정 정보 WifiVO
	 * @param req
	 * @return result
	 * @exception Exception
	 */
	@RequestMapping(value = "/setWifiView.do")
	public String setWifiView(@ModelAttribute("wifiVO") WifiVO wifiVO, HttpServletRequest req, ModelMap model) throws Exception {

		String sPropertyFile = req.getSession().getServletContext().getRealPath("scanner.conf");
		Properties properties = PropertyUtil.getProperties(sPropertyFile);
		
		wifiVO.setAuth(properties.getProperty("auth"));
		wifiVO.setDvcName(properties.getProperty("nickname"));

		return "/wifi/setWifiView";
	}

	/**
	 * Wifi 설정
	 * @param wifiVO - Wifi 설정 정보 WifiVO
	 * @param req
	 * @return result
	 * @exception Exception
	 */
	@RequestMapping(value = "/setWifi.do")
	public void setWifi(@ModelAttribute("wifiVO") WifiVO wifiVO, HttpServletRequest req, ModelMap model) throws Exception {

		String sPropertyFile = req.getSession().getServletContext().getRealPath("scanner.conf");
		Properties properties = PropertyUtil.getProperties(sPropertyFile);
		
		String sWpaConfDir = properties.getProperty("wpa.conf.dir");
		String sCertDir = properties.getProperty("cert.dir");
		
		properties.setProperty("ssid", wifiVO.getSsid());
		properties.setProperty("nickname", wifiVO.getDvcName());
		
		Map<String, String> mapFile = new HashMap<String, String>();
		List<String> listWap = new ArrayList<String>();
		
		List<String> listCmd = new ArrayList<String>();
		Process process;

		if ("2".equals(wifiVO.getAuth())) {
			properties.setProperty("ssid", wifiVO.getSsid());
			properties.setProperty("psk", wifiVO.getPsk());
			
			listCmd.add("/usr/bin/wpa_passphrase");
			listCmd.add(wifiVO.getSsid());
			listCmd.add(wifiVO.getPsk());
			listCmd.add(">");
			listCmd.add(sWpaConfDir + "/wpa_psk.conf");
			
			process = new ProcessBuilder(listCmd).start();
			
			// 외부 프로그램 출력 읽기
		    BufferedReader bufferedReaderOut   = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		    
			String sProcessMessage;

			// "표준 출력"과 "표준 에러 출력"을 출력
			while ((sProcessMessage = bufferedReaderOut.readLine()) != null) {
				listWap.add(sProcessMessage);
			}
			
//			while ((sProcessMessage = bufferedReaderError.readLine()) != null) {
//				LOGGER.info(sProcessMessage);
//			}
		}
		else if ("3".equals(wifiVO.getAuth())) {
			mapFile = commonService.insertAttachFile(req);
			
			properties.setProperty("identity", wifiVO.getIdentity());
			properties.setProperty("privateKeyPassword", wifiVO.getPrivateKeyPassword());
		}
		
		properties.store(new FileOutputStream(sPropertyFile), "");
		
		String sWpaConFile = sWpaConfDir + "/wpa_psk.conf";
		File fileWpa = new File(sWpaConFile);
		
		if(!fileWpa.exists()) {
			fileWpa.createNewFile();
		}
        
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sWpaConFile), "UTF8"));

		if ("1".equals(wifiVO.getAuth())) {
			StringBuilder stringBuilderWpa = new StringBuilder();
			stringBuilderWpa.append("network={\n");
			stringBuilderWpa.append("	ssid=\"" + wifiVO.getSsid() + "\"\n");
			stringBuilderWpa.append("	mode=0\n");
			stringBuilderWpa.append("	key_mgmt=NONE\n");
			stringBuilderWpa.append("}\n");

			output.write(stringBuilderWpa.toString());
		}
		else if ("2".equals(wifiVO.getAuth())) {
			for (int i = 0; i < listWap.size(); i ++) {
				if (listWap.get(i).indexOf("#psk") < 0) {
					output.write(listWap.get(i) + "\n");
				}
			}
			
		}
		else if ("3".equals(wifiVO.getAuth())) {
			StringBuilder stringBuilderWpa = new StringBuilder();
			stringBuilderWpa.append("network={\n");
			stringBuilderWpa.append("	ssid=\"" + wifiVO.getSsid() + "\"\n");
			stringBuilderWpa.append("	scan_ssid=1\n");
			stringBuilderWpa.append("	key_mgmt=WPA-EAP\n");
			stringBuilderWpa.append("	pairwise=CCMP TKIP\n");
			stringBuilderWpa.append("	group=CCMP TKIP\n");
			stringBuilderWpa.append("	eap=TLS\n");
			stringBuilderWpa.append("	identity=\"" + wifiVO.getIdentity() + "\"\n");
			stringBuilderWpa.append("	ca_cert=\"" + sCertDir + "/caCert/" + mapFile.get("caCert") + "\"\n");
			stringBuilderWpa.append("	client_cert=\"" + sCertDir + "/clentCert/" + mapFile.get("clentCert") + "\"\n");
			stringBuilderWpa.append("	private_key=\"" + sCertDir + "/privatrKey/" + mapFile.get("privatrKey") + "\"\n");
			stringBuilderWpa.append("	private_key_passwd=\"" + wifiVO.getPrivateKeyPassword() + "\"\n");
			stringBuilderWpa.append("}\n");

			output.write(stringBuilderWpa.toString());
		}
        
		output.close();
		
		listCmd = new ArrayList<String>();
		listCmd.add("reboot");
		
		process = new ProcessBuilder(listCmd).start();
	}

	/**
	 * 애드혹 모드 설정
	 * @param wifiVO - Wifi 설정 정보 WifiVO
	 * @param req
	 * @return result
	 * @exception Exception
	 */
	@RequestMapping(value = "/setAdhoc.do")
	public void setAdhoc(HttpServletRequest req, ModelMap model) throws Exception {

		String sPropertyFile = req.getSession().getServletContext().getRealPath("scanner.conf");
		Properties properties = PropertyUtil.getProperties(sPropertyFile);
		
		String sWpaConfDir = properties.getProperty("wpa.conf.dir");
		
		String sWpaConFile = sWpaConfDir + "/wpa_psk.conf";
		EgovFileTool.deleteFile(sWpaConFile);
		
		List<String> listCmd = new ArrayList<String>();
		Process process;
		
		listCmd.add("reboot");
		
		process = new ProcessBuilder(listCmd).start();
	}
}
