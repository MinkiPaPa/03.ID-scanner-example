<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/WEB-INF/jsp/egovframework/include/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<!-- script -->
	<script type="text/javascript">
	<!--
		$(document).ready(function() {
			$("#changePw").click(function() {
				
				if ("" == $.trim($("#pw").val())) {
					alert("현재 비밀번호를 입력해주세요.");
					return false;
				}
				
				if ("" == $.trim($("#pw1").val())) {
					alert("새 비밀번호를 입력해주세요.");
					return false;
				}
				
				if ($.trim($("#pw1").val().length) < 8) {
					alert("새 비밀번호는 8자리 이상으로 입력해주세요.");
					return false;
				}
				
				if (!passwordcheck()) {
					alert("비밀번호는 영문 대문자, 소문자, 숫자, 특수문자를 혼용해야 합니다.");
					return false;
				}
				
				if ($.trim($("#pw1").val()) != $.trim($("#pw2").val())) {
					alert("새 비밀번호를 동일하게 입력해주세요.");
					return false;
				}
				
				$("#formPw").attr("action", "<c:url value='/wifi/changePw.do'/>").submit();
			});
			
			$("#init").click(function() {
				$("#pw").val("");
				$("#pw1").val("");
				$("#pw2").val("");
			});
		});
	
		function passwordcheck() {
			// 입력한 비밀번호의 값을 저장하는 변수
			var userPw = $("#pw1").val();

			// 영문 소문자가 사용되었는지 여부를 판단할 변수
			var alphaLowerCheck = false;
			// 영문 대문자가 사용되었는지 여부를 판단할 변수
			var alphaUpperCheck = false;
			// 숫자가 사용된 횟수를 저장하는 변수
			var numberCheck = false;
			// 특수문자가 사용된 횟수를 저장하는 변수
			var sCharCheck = false;

			// 비밀번호 유효성 검사
			for (var i = 0; i < userPw.length; i++) {
				// 영문 소문자 사용 여부
				if (userPw.charAt(i).search(/[a-z]/) != -1) {
					alphaLowerCheck = true;
				}

				// 영문 대문자 사용 여부
				if (userPw.charAt(i).search(/[A-Z]/) != -1) {
					alphaUpperCheck = true;
				}

				// 숫자 사용 여부
				if (userPw.charAt(i).search(/[0-9]/g) != -1) {
					numberCheck = true;
				}

				// 특수문자 사용 여부
				if (userPw.charAt(i).search(/[-_=+\|()*&^%$#@!~`?></;,.:]/g) != -1) {
					sCharCheck = true;
				}
			}

			// 숫자, 특수문자, 영문을 혼용하여 입력하지 않았을 경우 경고 메시지 발생
			if (alphaLowerCheck != true || alphaUpperCheck != true || numberCheck != true || sCharCheck != true) {
				return false;
			}
			else {
				return true;
			}
		}
		-->
	</script>
	<!-- //script -->
</head>
<body>
	<img src="<c:url value='/img/logo.png'/>">
	<form id="formPw" name="formPw" method="post">
		<div class="element">
			Password <br> <input type="password" name="pw" id="pw" value="" class="input-text" />
		</div>
		<div class="element">
			New Password <br> <input type="password" name="pw1" id="pw1" value="" class="input-text" />
		</div>
		<div class="element">
			Verification Password <br> <input type="password" name="pw2" id="pw2" value="" class="input-text" />
		</div>
		<br>
		<div class="button">
			<input type="button" name="changePw" id="changePw" value="Set" class="input-submit" /> <input type="button" name="init" id="init" value="Cancel" class="input-submit" />
		</div>
	</form>
</body>
</html>