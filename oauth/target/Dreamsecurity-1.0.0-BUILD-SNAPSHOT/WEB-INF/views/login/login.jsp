<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="/WEB-INF/views/include/head.jsp" %>

<body>

	<div class="limiter">
		<div class="container-login100">
			<div class="wrap-login100 p-t-50 p-b-90">
				<form class="login100-form validate-form flex-sb flex-w" method="post" action="/login">
					<span class="login100-form-title p-b-51"> Magic Oauth2 Login </span>

					<div class="wrap-input100 validate-input m-b-16" data-validate="사용자 아이디를 입력하세요">
						<input class="input100" type="text" name="userId" id="userId" placeholder="사용자 아이디">
						<span class="focus-input100"></span>
					</div>

					<div class="wrap-input100 validate-input m-b-16" data-validate="패스워드를 입력하세요">
						<input class="input100" type="password" name="userPwd" id="userPwd" placeholder="패스워드">
						<span class="focus-input100"></span>
					</div>

					<div class="flex-sb-m w-full p-t-3 p-b-24">
						<div class="contact100-form-checkbox">
							<input class="input-checkbox100" id="ckb1" type="checkbox" name="remember-me">
							<label class="label-checkbox100" for="ckb1"> 아이디 기억 </label>
						</div>

						<div>
							<a href="#" class="txt1"> 아이디/패스워드 찾기 </a>
						</div>
					</div>
					
					<c:if test="${error != null}">
						<div class="alert alert-danger"> ${error}</div>
					</c:if>

					<div class="container-login100-form-btn m-t-17">
						<button type="submit" class="login100-form-btn" >Login</button>
					</div>

				</form>
			</div>
		</div>
	</div>

	<div id="dropDownSelect1"></div>

<%@ include file="/WEB-INF/views/include/plugin_js.jsp" %>

</body>
</html>