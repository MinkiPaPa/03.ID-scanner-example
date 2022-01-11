<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@include file="/WEB-INF/jsp/egovframework/include/head.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title></title>
	<script>
	<!--
		function fncInit() {
		<c:if test="${not empty message}">
			alert("${message}");
		</c:if>
		<c:if test="${not empty back}">
			history.back();
		</c:if>
		<c:if test="${not empty method}">
			opener.${method};
		</c:if>
		<c:if test="${not empty close}">
			window.close();
		</c:if>
		<c:if test="${not empty parent}">
			parent.${parent};
		</c:if>
		<c:if test="${not empty target}">
			$("#formPage").attr("target", "${target}");
		</c:if>
		<c:if test="${not empty url}">
			$("#formPage").attr("action", "<c:url value='${url}'/>").submit();
		</c:if>
		<c:if test="${not empty location}">
		$(location).attr("href", "<c:url value='${location}'/>");
		</c:if>
		}
	//-->
	</script>
</head>
<body onLoad="fncInit();">
	<form name="formPage" id="formPage" method="post" target="">
		<c:forEach var="result" items="${mapParam}">
			<c:if test="${!empty result.value && 'arr' != fn:substring(result.key, 0, 3)}">
				<input type="hidden" name="<c:out value='${result.key}' />" value="<c:out value='${result.value}'/>" />
			</c:if>
		</c:forEach>
	</form>
</body>
</html>