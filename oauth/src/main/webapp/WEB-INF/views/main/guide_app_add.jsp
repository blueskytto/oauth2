<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>

<%@ include file="/WEB-INF/views/include/head.jsp"%>

<body>

	<div class="wrapper">

		<!-- Main Header -->
		<%@ include file="/WEB-INF/views/include/main_header.jsp"%>

		<div class="row">
			<!-- Main Left -->
			<%@ include file="/WEB-INF/views/include/left_guide.jsp"%>

			<main class="col-md-8 col-xl-9 p-t-2 mt-3">
			<div class="card mb-3" style="max-width: 50rem;">
				<h3 class="card-header">애플리케이션 등록 가이드</h3>
				<div class="card-body">
					<h5 class="card-title">1. 애플리케이션 등록</h5>
					<h6 class="card-subtitle text-muted">애플리케이션 관리 > 신규 애플리케이션 등록</h6>
				</div>
				<img src="/resources/images/guide1.png" width="100%" alt="Card image" />
				<div class="card-body">
					<h5 class="card-title">2. 애플리케이션 설정</h5>
					<h6 class="card-subtitle text-muted">애플리케이션에 대한 정보를 설정 합니다.</h6>
				</div>
				<img src="/resources/images/guide2.png" width="80%" alt="Card image" />
				<img src="/resources/images/guide3.png" width="80%" alt="Card image" />
				<img src="/resources/images/guide4.png" width="80%" alt="Card image" />
				<div class="card-body">
					<h5 class="mb-3">1) 애플리케이션 정보</h5> 
					<p class="card-text mb-1">애플리케이션 이름 : 클라이언트가 표시될 이름을 입력 (한글가능)</p>
					<p class="card-text mb-1">애플리케이션 아이디 : 클라이언트의 고유 아이디를 입력 (중복 확인 필요)</p>
					<p class="card-text mb-1">애플리케이션 비밀번호 : 클라이언트의 비밀번호를 입력 </p>
					
					<h5 class="mb-3 mt-5">2) OAuth 토큰 방식</h5> 
					<p class="card-text mb-1">기본형 : AccessToken이 관리자가 설정한 길이의 기본 형태로 발급 됩니다.</p>
					<p class="card-text mb-1">JWT : AccessToken이 Json Web Token 방식으로 발급 됩니다.</p>
					<p class="card-text mb-1">SAML : SAML 형태의 토큰으로 발행 됩니다. </p>
					
					<h5 class="mb-3 mt-5">3) SCOPE</h5> 
					<p class="card-text mb-1">일반 사용자가 OAuth 사용할 때 제공될 정보들을 정합니다.</p>
					
					<h5 class="mb-3 mt-5">4) 인증 유형</h5> 
					<p class="card-text mb-1">OAuth 토큰 발급 인증방식 중 허용할 범위를 체크합니다.</p>
					<p class="card-text mb-1">해당 방식별 OAuth 사용 용도와 토큰인증 발행에 필요한 전달값은 다릅니다.</p>
					<p class="card-text mb-1">일반적인 방식은 authorization Code 방식입니다. 자세한 설명은 <a class="text-primary" href="#">다음</a>을 참고</p>
					
					<h5 class="mb-3 mt-5">5) 서비스 URI</h5> 
					<p class="card-text mb-1">Code방식일 때 Code를 받거나 AccessToken을 발급받을때 리턴될 페이지를 지정합니다.</p>
					<p class="card-text mb-1">리턴페이지가 저장 되어있는곳 외에서는 토큰이 발급이 되지 않습니다.</p>
					
					<h5 class="mb-3 mt-5">6) 토큰 유효시간</h5> 
					<p class="card-text mb-1">AccessToken 및 RefreshToken의 유효시간을 정합니다.</p>
					<p class="card-text mb-1">기본정책은 AccessToken은 60 분, RefreshToken은 24시간입니다. </p>
					
					<h5 class="mb-3 mt-5">7) 토큰 유효시간 옵션</h5> 
					<p class="card-text mb-1">API 서버에서 AccessToken의 유효성 상태를 요청 할 때 토큰이 유효하다면 유효시간을 해당 기준으로 다시 연장합니다.</p>
				</div>
				
			</div>

			</main>

		</div>

	</div>

	<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>

</body>
<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>