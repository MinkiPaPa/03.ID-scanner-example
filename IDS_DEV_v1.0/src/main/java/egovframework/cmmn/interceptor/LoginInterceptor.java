package egovframework.cmmn.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import egovframework.cmmn.Property;
import egovframework.cmmn.service.EgovUserDetailsService;
import egovframework.wifi.service.LoginVO;

public class LoginInterceptor extends HandlerInterceptorAdapter {

	/** egovUserDetailsService */
	@Resource(name = "egovUserDetailsService")
	protected EgovUserDetailsService egovUserDetailsService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
		
		LOGGER.info("====================================== START ======================================");
		
		String[] args = new String[6];
		args[0] = req.getRequestURI();
		args[1] = req.getContextPath();
		args[2] = req.getLocalAddr();
		args[3] = req.getRemoteAddr();
		args[4] = req.getRequestURL().toString();
		args[5] = req.getRequestURL().toString().substring(0, req.getRequestURL().toString().indexOf(req.getRequestURI()));
		
		LOGGER.info("Request URI \t: {}, getContextPath : {}, getLocalAddr : {}, getRemoteAddr : {}, getRequestURL : {}, {}", args);

		if (egovUserDetailsService.isAuthenticated()) {
			LoginVO loginVO = (LoginVO) egovUserDetailsService.getAuthenticatedUser();
			
			LOGGER.info("webId \t: {}, webPw : {}", loginVO.getUserId(), loginVO.getPw());
		}
		else {
			res.sendRedirect(req.getContextPath() + "/wifi/loginView.do");
			return false;
		}
		
		return super.preHandle(req, res, handler);
	}

	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) throws Exception {
		LOGGER.info("====================================== END ======================================\n");
	}
}
