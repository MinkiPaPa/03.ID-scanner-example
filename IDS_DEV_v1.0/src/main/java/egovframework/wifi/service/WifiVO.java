package egovframework.wifi.service;

public class WifiVO {

	/** 보안 */
	private String auth;

	/** SSID */
	private String ssid;

	/** 암호 */
	private String psk;

	/** 인증정보 */
	private String identity;

	/** 비밀키 암호 */
	private String privateKeyPassword;

	/** 장치 이름 */
	private String dvcName;

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPsk() {
		return psk;
	}

	public void setPsk(String psk) {
		this.psk = psk;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

	public String getDvcName() {
		return dvcName;
	}

	public void setDvcName(String dvcName) {
		this.dvcName = dvcName;
	}

}
