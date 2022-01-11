<%@ page contentType="text/html; charset=utf-8"%>
<%@include file="/WEB-INF/jsp/egovframework/include/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- script -->
<script type="text/javascript">
<!--
	$(document).ready(function() {

		$("#openSession").click(function() {
		    $.ajax({
	            url : "<c:url value='/scanner/scan.do'/>",
	            data : {
	            	"command" : "openSession",
	            	"timeout" : "30",
	            	"userInfo" : "점장님"
	            },
	            dataType : "json",
	            type : "post",
	            success : function(data) {
					console.log(data);
					
					alert(data.result);
					
					if ("0" == data.result) {
						$("#sessionId").val(data.sessionId);
					}
	            },
	            error : function() {
		                alert("에러!");
	            }
		    });
		});

		$("#closeSession").click(function() {
		    $.ajax({
	            url : "<c:url value='/scanner/scan.do'/>",
	            data : {
	            	"command" : "closeSession",
	            	"sessionId" : $("#sessionId").val()
	            },
	            dataType : "json",
	            type : "post",
	            success : function(data) {
					console.log(data);
					
					alert(data.result);
	            },
	            error : function() {
		                alert("에러!");
	            }
		    });
		});
		
		$("#getDeviceInfo").click(function() {
		    $.ajax({
	            url : "<c:url value='/scanner/scan.do'/>",
	            data : {
	            	"command" : "getDeviceInfo",
	            	"sessionId" : $("#sessionId").val()
	            },
	            dataType : "json",
	            type : "post",
	            success : function(data) {
					console.log(data);
					
					alert(data.result);
	            },
	            error : function() {
		                alert("에러!");
	            }
		    });
		});
		
		$("#scan").click(function() {
		    $.ajax({
	            url : "<c:url value='/scanner/scan.do'/>",
	            data : {
	            	"command" : "scan",
	            	"sessionId" : $("#sessionId").val(),
	            	"parameters" : {
		            	"timeout" : $("#timeout").val(),
		            	"resolution" : $("#resolution").val(),
		            	"side" : $("#side").val(),
		            	"color" : $("#color").val(),
		            	"rotation" : $("#rotation").val(),
		            	"dropout" : $("#dropout").val(),
		            	"fdLevel" : $("#fdLevel").val(),
		            	"compression" : $("#compression").val()
	            	}
	            },
	            dataType : "json",
	            type : "post",
	            success : function(data) {
					console.log(data);
					
					alert(data.result);
	            },
	            error : function() {
		                alert("에러!");
	            }
		    });
		});
	});

	-->
</script>
<!-- //script -->
</head>
<body>
<table border="1">
	<tr>
		<td>timeout</td>
		<td><input type="text" id="timeout" value="5"></td>
		<td>side</td>
		<td><input type="text" id="side" value="3"></td>
	</tr>
	<tr>
		<td>resolution</td>
		<td><input type="text" id="resolution" value="600"></td>
		<td>color</td>
		<td><input type="text" id="color" value="24"></td>
	</tr>
	<tr>
		<td>rotation</td>
		<td><input type="text" id="rotation" value="0"></td>
		<td>dropout</td>
		<td><input type="text" id="dropout" value="4"></td>
	</tr>
	<tr>
		<td>fdLevel</td>
		<td><input type="text" id="fdLevel" value="5"></td>
		<td>compression</td>
		<td><input type="text" id="compression" value="0"></td>
	</tr>
	<tr>
		<td>sessionId</td>
		<td><input type="text" id="sessionId" value=""></td>
		<td></td>
		<td></td>
	</tr>
</table>
<input type="button" id="openSession" title="openSession" value="openSession">
<input type="button" id="closeSession" title="closeSession" value="closeSession">
<input type="button" id="scan" title="scan" value="scan">
<input type="button" id="getDeviceInfo" title="getDeviceInfo" value="getDeviceInfo">
</body>
</html>