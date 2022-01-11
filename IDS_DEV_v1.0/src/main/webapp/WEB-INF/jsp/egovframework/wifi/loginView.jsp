<%@ page contentType="text/html; charset=utf-8"%>
<%@include file="/WEB-INF/jsp/egovframework/include/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<!-- script -->
	<script type="text/javascript" src="<c:url value='/cmmn/validator.do'/>"></script>
	<validator:javascript formName="loginVO" staticJavascript="false" xhtml="true" cdata="false" />
	<script type="text/javascript">
	<!--
		$(document).ready(function() {
			$("#sign_in").click(function() {
				if (validateLoginVO(document.getElementById("formLogin"))) {
					$("#formLogin").attr("action", "<c:url value='/wifi/login.do'/>").submit();
				}
			});
		});
	-->
	</script>
	<!-- //script -->
</head>
<body>
	<img src="<c:url value='/img/logo.png'/>">
	<div>
		Authenication Required<br>
		<c:out value='${url}' /> requires a username and password<br>
		Your connection to this site is net secure<br>
	</div>
	<br>
	<form:form commandName="loginVO" id="formLogin" name="formLogin">
		<div class="element">
			User ID<br>
			<input type="text" id="userId" name="userId" class="input-text">
		</div>
		<div class="element">
			Password<br>
			<input type="password" id="pw" name="pw" class="input-text">
		</div>
		<br>
		<div class="button">
			<input type="button" name="sign_in" id="sign_in" value="Login" class="input-submit" />
		</div>
	</form:form>
</body>
</html>