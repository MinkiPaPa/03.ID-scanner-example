package egovframework.cmmn;

import java.util.Date;
import java.util.UUID;

public class SessionManager {
	
	public static String seesionId = "";
	public static String userInfo = "";
	public static long 	 seesionMakeTime = 0;	
	public static long 	 seesionTimeOut = 0;	
	
	
	/**
	 * 
	 * <p>Copyright: Copyright(c) 2017 Voim technologies Inc. All rights reserved.</p>
	 * 
	 * @author voim
	 * <p>입력을 sec로 받기 때문에 milie sec * 1000</p>
	 * */
	public static String createSession(long timeOut) {

		seesionId = UUID.randomUUID().toString().replace("-","").trim();
		userInfo  = "";
		seesionMakeTime  = new Date().getTime();
		seesionTimeOut   = (timeOut * 1000);
		
		return seesionId;
	}
	
	/**
	 * 
	 * <p>Copyright: Copyright(c) 2017 Voim technologies Inc. All rights reserved.</p>
	 * 
	 * @author voim
	 * <p>세션 시간을 새로고침</p>
	 * */
	public static void refreshSessionTime(){

		seesionMakeTime  = new Date().getTime();
	}
	
	
	/**
	 * 
	 * <p>Copyright: Copyright(c) 2017 Voim technologies Inc. All rights reserved.</p>
	 * 
	 * @author voim
	 * <p>기존 세션과 넘어온 세션 아이디를 비교하여 세션 초기화</p>
	 * <p>1. 일치 할 경우 </p>
	 *    <p>- 기존 세션 초기화</p>
	 * <p>2. 불일치 </p>
	 *    <p>- false</p>
	 * */
	public static boolean expireSession(String currentSessionID) {
		boolean res = false;
		if(currentSessionID.equals(seesionId)){ 
			seesionId = "";
			res = true;
		}else{
			res = false;
		}
		return res;
	}
	
	
	
	/**
	 * 
	 * <p>Copyright: Copyright(c) 2017 Voim technologies Inc. All rights reserved.</p>
	 * 
	 * @author voim
	 * <p>기존 세션과 넘어온 세션 아이디를 비교</p>
	 * <p>1. 일치 할 경우 </p>
	 *    <p>- 기존 세션의 세션 타임을 넘어온 시간으로 재 지정함으로 시간을 늘림</p>
	 * <p>2. 불일치 하지만 세션시간이 타임아웃 시간 보다 작을 때 (타임아웃이 안지남)</p>
	 *    <p>- false</p>
	 * <p>3. 불일치 하고 세션 타임아웃이 지난 경우 </p>
	 *    <p>- 새롭게 세션 발급 및 ture   </p>
	 * */
	public static boolean sessionValidChk(String command, String currentSessionID, long currentSessionTime){
		boolean res = false;

		if ((seesionMakeTime + seesionTimeOut) < currentSessionTime) {
			if (!"scan".equals(command)) {
				createSession(seesionTimeOut);
				res = true;
			}
			else {
				res = false;
			}
		}
		else {
			if (currentSessionID.equals(seesionId)) {
				refreshSessionTime();
				res = true;

			}
			else {
				res = false;
			}
		}
		
		return res;
	}
}
