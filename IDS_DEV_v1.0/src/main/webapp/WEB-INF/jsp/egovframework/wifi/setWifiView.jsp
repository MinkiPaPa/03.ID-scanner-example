<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/include/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<script type="text/javascript" src="<c:url value='/cmmn/validator.do'/>"></script>
	<validator:javascript formName="wifiVO" staticJavascript="false" xhtml="true" cdata="false" />
	<!-- script -->
	<script type="text/javascript">
	<!--
		$(document).ready(function() {
			
			$("#setWifi").click(function() {
				if (confirm("저장 하시겠습니까?")) {
					if (validateWifiVO(document.getElementById("formWifi"))) {
						$("#formWifi").attr("action", "<c:url value='/wifi/setWifi.do'/>").submit();
					}
					else {
						return false;
					}
				}
			});
			
			$("#setAdhoc").click(function() {
				if (confirm("저장 하시겠습니까?")) {
					$("#formWifi").attr("action", "<c:url value='/wifi/setAdhoc.do'/>").submit();
				}
			});

			$("#main").click(function() {
				$(location).attr("href", "<c:url value='/wifi/main.do'/>");
			});

			$("#auth").change(function() {
				if ("1" == $(this).val()) {
					$("#divSsid").show();
					$("#divPsk").hide();
					$("#divIdentity").hide();
					$("#divClentCert").hide();
					$("#divCaCert").hide();
					$("#divPrivatrKey").hide();
					$("#divPrivateKeyPassword").hide();
					$("#divDvcName").show();
					
					$("#psk").val("@");
					$("#identity").val("@");
				}
				else if ("2" == $(this).val()) {
					$("#divSsid").show();
					$("#divPsk").show();
					$("#divIdentity").hide();
					$("#divClentCert").hide();
					$("#divCaCert").hide();
					$("#divPrivatrKey").hide();
					$("#divPrivateKeyPassword").hide();
					$("#divDvcName").show();
					
					$("#psk").val("");
					$("#identity").val("@");
				}
				else {
					$("#divSsid").show();
					$("#divPsk").hide();
					$("#divIdentity").show();
					$("#divClentCert").show();
					$("#divCaCert").show();
					$("#divPrivatrKey").show();
					$("#divPrivateKeyPassword").show();
					$("#divDvcName").show();
					
					$("#psk").val("@");
					$("#identity").val("");
				}
			});
		});

		$(window).load(function() {
			$("#auth").change();
		});
		-->
	</script>
	<!-- //script -->
</head>
<body>
	<img src="<c:url value='/img/logo.png'/>">
	<br> 보안
	<br>
	<form:form commandName="wifiVO" id="formWifi" name="formWifi" enctype="multipart/form-data">
		<form:select path="auth">
			<form:option value="1">없음</form:option>
			<form:option value="2">개인용 WPA또는 WPA2</form:option>
			<form:option value="3">기업용 WPA또는 WPA2</form:option>
		</form:select>
		<div id="div1" width="300" height="200">
			<div class="element" id="divSsid" style="display: none;">
				SSID<br>
				<form:input path="ssid" cssClass="input-text"/>
			</div>
			<div class="element" id="divPsk" style="display: none;">
				암호<br>
				<form:input path="psk" cssClass="input-text"/>
			</div>
			<div class="element" id="divIdentity" style="display: none;">
				인증정보<br>
				<form:input path="identity" cssClass="input-text"/>
			</div>
			<div class="element" id="divClentCert" style="display: none;">
				사용자 인증서<br>
				<input type="file" name="clentCert" id="clentCert" class="input-text" />
			</div>
			<div class="element" id="divCaCert" style="display: none;">
				CA 인증서<br>
				<input type="file" name="caCert" id="caCert" class="input-text" />
			</div>
			<div class="element" id="divPrivatrKey" style="display: none;">
				비밀키<br>
				<input type="file" name="privatrKey" id="privatrKey" class="input-text" />
			</div>
			<div class="element" id="divPrivateKeyPassword" style="display: none;">
				비밀키 암호<br>
				<form:input path="privateKeyPassword" cssClass="input-text"/>
			</div>
			<div class="element" id="divDvcName" style="display: none;">
				장치 이름<br>
				<form:input path="dvcName" cssClass="input-text"/>
			</div>
		</div>
	</form:form>
	<br> <input type="button" name="setWifi" id="setWifi" value="설정저장 및 연결" class="input-submit" />
	<input type="button" name="setAdhoc" id="setAdhoc" value="ap모드로 재시동" class="input-submit" />
	<input type="button" name="main" id="main" value="취소" class="input-submit" />
</body>
</html>