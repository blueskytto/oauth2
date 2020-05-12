<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>

<%@ include file="/WEB-INF/views/include/head.jsp"%>

<body>

<script src="<c:url value="/resources/vendor/highlight/highlight.min.js"/>"></script>
<script>hljs.initHighlightingOnLoad();</script>

	<div class="wrapper">

		<!-- Main Header -->
		<%@ include file="/WEB-INF/views/include/main_header.jsp"%>

		<div class="row">
			<!-- Main Left -->
			<%@ include file="/WEB-INF/views/include/left_guide.jsp"%>

			<main class="col-md-8 col-xl-9 p-t-2 mt-3">
			<h3>애플리케이션 API 등록 및 인증 가이드</h3>
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">API 인증 과정</h3>
				<div class="card-body">
					<h5 class="card-title">관리자가 API 서버를 등록하여 OAuth 인증토큰의 유효성을 검사하고 결과를 받습니다.</h5>
					<p class="card-text mt-3 mb-1">1. API 서버를 등록합니다.</p>
					<p class="card-text mb-1">2. 등록된 API서버 정보를 통해 사용자토큰의 유효성 검사를 요청합니다.</p>
					<p class="card-text mb-1">3. 토큰의 유효성의 따라 결과값을 리턴받습니다.</p>
					<p class="card-text mb-1">4. 정상적일 경우 API에 요청한 값을 처리 해줍니다.</p>
				</div>
			</div>
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">1. API 서버 등록</h3>
				<img src="/resources/images/guide5.png" width="80%" alt="Card image" />
				
				<div class="card-body">
					<p class="card-text mt-2 mb-1"><b>API 이름 : </b>API 명칭을 지정합니다.</p>
					<p class="card-text mt-2 mb-1"><b>API ID : </b>API의 고유 아이디를 입력합니다. </p>
					<p class="card-text mt-2 mb-1"><b>API Secret : </b>API 서버의 비밀번호를 입력합니다.</p>
					<p class="card-text mt-2 mb-1"><b>API 서비스 주소 : </b>API 서버의 대표 서비스 주소를 입력합니다.(단순 표기용)</p>
					<p class="card-text mt-2 mb-1"><b>사용 여부 : </b>API 서버에서 토큰 유효성 검사를 할때 허용여부를 선택합니다.</p>
				</div>
			</div>
			
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">2. 사용자토큰 유효성 검사 요청</h3>
				<div class="card-body">
						<h5 class="card-title">AccessToken을 OAtuh서버에 제출하여 토큰의 유효성 상태를 체크합니다.</h5>
						<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
						<pre><code class="http">
	 <b>POST /introspect HTTP/1.1</b>
	 Host : server.example.com
	 Content-Type : application/x-www-form-urlencoded
	 Authorization : Basic dGVzdDoxMjM0
	 					
	 token={Access_Token}
						</code></pre>
						<table class="table table-hover mt-3">
							<thead>
								<tr class="table-primary">
									<th scope="col">헤더 키</th>
									<th scope="col">설명</th>
									<th style="width: 10%" scope="col">필수</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row">Authorization</th>
									<td>'Basic' 이후 한칸을 띄운후 Base64한 {API_ID}:{API_SECRET} 합친 값을 입력합니다.
									<br/>예 ) APP_ID = test, APP_SECRET = 1234 일 경우 -> Base64_Encode("test:1234") -> dGVzdDoxMjM0
									</td>
									<td>O</td>
								</tr>
							</tbody>
						</table>
						<table class="table table-hover mt-3">
							<thead>
								<tr class="table-primary">
									<th scope="col">키</th>
									<th scope="col">설명</th>
									<th style="width: 10%" scope="col">필수</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row">token</th>
									<td>인증 하고자 할 AccessToken 값</td>
									<td>O</td>
								</tr>
							</tbody>
						</table>
						<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
						<h6 class="card-subtitle text-muted">Response Body 에서 JSON 객체로 다음과 같이 받습니다.</h6>
						<p class="card-text mt-1 mb-1">1) 유효한 AccessToken 일 경우:</p>
						<pre><code class="json">
{
	"scope": "address,name",
	"iss": "{URL}",
	"active": "true",
	"exp": 1567150367,
	"iat": 1567146767,
	"client_id": "{app_key}",
	"username": "{user_id}"
}
						</code></pre>
						<p class="card-text mt-1 mb-1">2-1) 만료된 AccessToken 일 경우:</p>
						<pre><code class="json">
{
	"active": "false"
}
						</code></pre>
						<p class="card-text mt-1 mb-1">2-2) 잘못된 정보입력으로 인해 실패할 경우:</p>
						<pre><code class="json">
{
	"error_description": {error_description},
	"error": {error}
}
						</code></pre>
						
						<table class="table table-hover mt-3">
							<thead>
								<tr class="table-primary">
									<th scope="col">키</th>
									<th scope="col">설명</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row">scope</th>
									<td>토큰 발급시 부여받은 scope 값</td>
								</tr>
								<tr>
									<th scope="row">iss</th>
									<td>토큰 발급했던 애플리케이션(클라이언트)의 URI</td>
								</tr>
								<tr>
									<th scope="row">active</th>
									<td>토큰의 유효성 상태값. 정상 : 'true' , 비정상 : 'false'</td>
								</tr>
								<tr>
									<th scope="row">exp</th>
									<td>토큰 만료 시간(Epoch Time, 1970.01.01 이후 지난 누적초, UTC 시간형태)</td>
								</tr>
								<tr>
									<th scope="row">iat</th>
									<td>토큰이 발급된 시간(Epoch Time, 1970.01.01 이후 지난 누적초, UTC 시간형태)</td>
								</tr>
								<tr>
									<th scope="row">client_id</th>
									<td>토큰을 발급한 애플리케이션 아이디</td>
								</tr>
								<tr>
									<th scope="row">username</th>
									<td>토큰을 발급한 사용자의 아이디 (Client Credentials Grant 방식의 경우 해당 내용 미포함)</td>
								</tr>
								<tr>
									<th scope="row">error_description</th>
									<td>Human-readable ASCII [USASCII] text providing additional information</td>
								</tr>
								<tr>
									<th scope="row">error</th>
									<td>A single ASCII [USASCII] error code</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>

			</main>

		</div>

	</div>

	<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>

</body>
<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>