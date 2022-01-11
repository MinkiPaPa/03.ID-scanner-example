<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/include/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<!-- script -->
	<script type="text/javascript">
	<!--
		$(document).ready(function() {
			$("#changePwView").click(function() {
				$(location).attr("href", "<c:url value='/wifi/changePwView.do'/>");
			});

			$("#setWifiView").click(function() {
				$(location).attr("href", "<c:url value='/wifi/setWifiView.do'/>");
			});
		});
		-->
	</script>
	<!-- //script -->
</head>
<body>
	<img src="<c:url value='/img/logo.png'/>">
	<div></div>
	<input type="button" name="changePwView" id="changePwView" value="Change Passowrd" class="input-submit" />
	<input type="button" name="setWifiView" id="setWifiView" value="Wireless settings" class="input-submit" />
</body>
</html>