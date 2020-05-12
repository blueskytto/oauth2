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
			<h3>애플리케이션 개발 가이드</h3>
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">OAuth2.0 흐름</h3>
				<div class="card-body">
					<h5 class="card-title">드림시큐리티 서비스에서 제공하는 OAuth 대표적인 인증 과정입니다.</h5>
					<p class="card-text mt-3 mb-1">1. 사용자는 OAuth 로그인을 이용하기 위하여 로그인 링크 버튼을 이용하여 접근합니다.</p>
					<p class="card-text mb-1">2. 사용자 정보를 입력후 로그인하면 전달된 클라이언트, 사용자계정 자격정보를 통해 로그인처리 됩니다.</p>
					<p class="card-text mb-1">3. 정상적으로 수행되면 등록 및 요청된 Redirect URI를 통해 인증코드(Authorization Code)가 발급됩니다.</p>
					<p class="card-text mb-1">4. 인증코드(Authorization Code)를 이용하여 사용자토큰(Access Token)을 요청후 발급받습니다.</p>
					<p class="card-text mb-1">5. 발급받은 AccessToken을 API 서버에 제출 후 해당 API 시스템을 이용합니다.</p>
				</div>
			</div>
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">1. Authorization Code 방식</h3>
				<div class="card-body">
					<p class="card-text mb-1">OAuth 인증방법 중 대표적인 인증처리입니다. code방식 인증은 일반사용자의 로그인 처리를 위해 브라우저가 존재해야 합니다.</p>
					<h5 class="card-title mt-3">1) 인증 코드 받기</h5>
					<h6 class="card-subtitle text-muted">로그인 버튼의 요청 URI의 필수 값들을 정의합니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code>
 <b>GET</b> /authorize?response_type=code&client_id={app_key}&redirect_uri={redirect_uri}&state={csrf_value}&scope={scopes} HTTP/1.1
 Host: server.example.com
					</code></pre>
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
								<th scope="row">response_type</th>
								<td>'code' 값으로 고정</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_id</th>
								<td>애플리케이션 생성 시 발급된 애플리케이션 아이디 (clientId)</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">redirect_uri</th>
								<td>CODE를 리다이렉트 받을 URI (애플리케이션 설정된 URI만 허용)</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">state</th>
								<td>CSRF공격방지 값, 전달한 state값이 code 발급때 전달되어 돌아옵니다.</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">scope</th>
								<td>사용자의 정보를 어느정도 받을지 애플리케이션 설정에 등록된 허용값 범위내의 값을 입력 (예시 > 전달값이 2개이상일 경우 띄어쓰기로 구분: scope={스코프값1} {스코프값2))</td>
								<td>X</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">사용자가 로그인 완료 후 code 및 state를 쿼리스트링으로 담아 Redirect URI로 리다이렉트 합니다.</h6>
					<p class="card-text mt-1 mb-1">성공할 경우:</p>
					<pre><code class="http">
 <b>HTTP/1.1 302 Found</b>
 Location: {redirect_uri}?code={authorize_code}&state={csrf_value}
					</code></pre>
					<p class="card-text mt-1 mb-1">실패할 경우:</p>
					<pre><code class="http">
 <b>HTTP/1.1 302 Found</b>
 Location: {redirect_uri}?error_description={error_description}&error={error}
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
								<th scope="row">code</th>
								<td>AccessToken을 발급받기위한 인증코드</td>
							</tr>
							<tr>
								<th scope="row">state</th>
								<td>csrf 방지값, 이전에 전달한 랜덤값이 그대로 전달되서 돌아옵니다.</td>
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
				
				<div class="card-body">
					<h5 class="card-title">2) 사용자 토큰(AccessToken) 받기</h5>
					<h6 class="card-subtitle text-muted">받은 코드값으로 API사용하기 위한 사용자 토큰들(Access Token, Refresh Token)을 받아옵니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>POST /token HTTP/1.1</b>
 Host : server.example.com
 Content-Type: application/x-www-form-urlencoded
 					
 grant_type=authorization_code&code={code}&redirect_uri={redirect_uri}&client_id={app_key}
					</code></pre>
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
								<th scope="row">grant_type</th>
								<td>'authorization_code' 값으로 고정</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">code</th>
								<td>이전에 받은 인증코드 값</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">redirect_uri</th>
								<td>CODE를 리다이렉트 받을 URI (애플리케이션 설정된 URI만 허용)</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_id</th>
								<td>애플리케이션 생성 시 발급된 Client 아이디</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">Response Body 에서 JSON 객체로 다음과 같이 받습니다.</h6>
					<p class="card-text mt-1 mb-1">성공할 경우:</p>
					<pre><code class="json">
{
	"access_token": "xNKRrfPBmjTF5nM47YSYQa",
	"refresh_token": "ffBV27o4r5PbR5bhVowdja",
	"token_type": "bearer",
	"expires_in": 3600
}
					</code></pre>
					<p class="card-text mt-1 mb-1">실패할 경우:</p>
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
								<th scope="row">access_token</th>
								<td>사용자 인증 토큰</td>
							</tr>
							<tr>
								<th scope="row">refresh_token</th>
								<td>accessToekn이 만료 될 경우 재발급할 때 사용할 인증토큰</td>
							</tr>
							<tr>
								<th scope="row">token_type</th>
								<td>일반형일 경우 'bearer'</td>
							</tr>
							<tr>
								<th scope="row">expires_in</th>
								<td>토큰 만료 시간(초)</td>
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
				
				<div class="card-body">
					<h5 class="card-title">2-1) 사용자 토큰(AccessToken) 갱신하기</h5>
					<h6 class="card-subtitle text-muted">사용자토큰(AccessToken)이 만료가 되면 토큰발급 때 받았던 refresh_token으로 재갱신합니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>POST /token HTTP/1.1</b>
 Host : server.example.com
 Content-Type: application/x-www-form-urlencoded
 					
 grant_type=refresh_token&client_id={app_key}&refresh_token={refresh_token}
					</code></pre>
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
								<th scope="row">grant_type</th>
								<td>'refresh_token' 값으로 고정</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_id</th>
								<td>AccessToken을 발급한 클라이언트아이디</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">refresh_token</th>
								<td>토근 발급시 받은 refresh_token을 제출하여 AccessToken을 새로 받습니다.</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">Response Body 에서 JSON 객체로 다음과 같이 받습니다.</h6>
					<p class="card-text mt-1 mb-1">성공할 경우:</p>
					<pre><code class="json">
{
	"access_token": "qVjOWT3ibf4b3RiFh4GgKa",
	"token_type": "bearer",
	"expires_in": 3600,
	"refreshToken": "k7L2EEmGyaVIaWN7G2LKsa"
}
					</code></pre>
					<p class="card-text mt-1 mb-1">실패할 경우:</p>
					<pre><code class="json">
{
	"error_description": {error_description},
	"error": {error}
}
					</code></pre>
				</div>
				
				<div class="card-body">
					<h5 class="card-title">3) 사용자 토큰(AccessToken)을 이용하여 API서버 사용하기</h5>
					<h6 class="card-subtitle text-muted">사용자토큰(AccessToken)를 API서버에 제출하여 원하는 정보를 얻어오는 일반적인 방식입니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>GET/POST {api_url} HTTP/1.1</b>
 Host : apiserver.example.com
 Authorization: Bearer {access_token}
 Content-Type: application/x-www-form-urlencoded
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
								<td>'Bearer' 작성후 한칸 공백넣은 후 발급받은 AccessToken을 담아서 보냅니다.</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">API서버마다 개발요건에 따라 다르게 응답처리 하므로 일반적인 Response Body 에서 JSON 객체로 받는 처리를 예로 듭니다.</h6>
					<pre><code class="json">
{
	"userId" : "JKE"
	"role" : "ACL000001"
}
					</code></pre>
				</div>
			</div>
			
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">2. Implicit Grant 방식</h3>
				<div class="card-body">
					<p class="card-text mb-1">Authorization Code 방식에서 code부분을 제외한 로그인처리 후 바로 AccessToken이 발행되는 방식입니다.</p>
					<h5 class="card-title mt-3">1) 사용자 토큰(AccessToken) 받기</h5>
					<h6 class="card-subtitle text-muted">로그인 버튼의 요청 URI의 필수 값들을 정의합니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code>
 <b>GET</b> /authorize?response_type=token&client_id={app_key}&redirect_uri={redirect_uri}&state={csrf_value}&scope={scopes} HTTP/1.1
 Host: server.example.com
					</code></pre>
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
								<th scope="row">response_type</th>
								<td>'token' 값으로 고정</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_id</th>
								<td>애플리케이션 생성 시 발급된 애플리케이션 아이디 (clientId)</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">redirect_uri</th>
								<td>CODE를 리다이렉트 받을 URI (애플리케이션 설정된 URI만 허용)</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">state</th>
								<td>CSRF공격방지 값, 전달한 state값이 code 발급때 전달되어 돌아옵니다.</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">scope</th>
								<td>사용자의 정보를 어느정도 받을지 애플리케이션 설정에 등록된 허용값 범위내의 값을 입력 (예시 > 전달값이 2개이상일 경우 띄어쓰기로 구분: scope={스코프값1} {스코프값2))</td>
								<td>X</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">사용자가 로그인 완료 후 사용자토큰 및 토큰정보를 쿼리스트링으로 담아 Redirect URI로 리다이렉트 합니다.</h6>
					<p class="card-text mt-1 mb-1">성공할 경우:</p>
					<pre><code class="http">
 <b>HTTP/1.1 302 Found</b>
 Location: {redirect_uri}?access_token={access_token}&state={csrf_value}&token_type=bearer&expires_in={expires_in}
					</code></pre>
					<p class="card-text mt-1 mb-1">실패할 경우:</p>
					<pre><code class="http">
 <b>HTTP/1.1 302 Found</b>
 Location: {redirect_uri}?error_description={error_description}&error={error}
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
								<th scope="row">access_token</th>
								<td>사용자 인증 토큰</td>
							</tr>
							<tr>
								<th scope="row">token_type</th>
								<td>일반형일 경우 'bearer'</td>
							</tr>
							<tr>
								<th scope="row">expires_in</th>
								<td>토큰 만료 시간(초)</td>
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
												
				<div class="card-body">
					<h5 class="card-title">1-1) 사용자 토큰(AccessToken) 갱신하기</h5>
					<p class="card-text mt-2 mb-1"><b>Implicit Grant 방식은 RefreshToken을 제공하지 않습니다.</b></p>
				</div>
				
				<div class="card-body">
					<h5 class="card-title">2) 사용자 토큰(AccessToken)을 이용하여 API서버 사용하기</h5>
					<h6 class="card-subtitle text-muted">사용자토큰(AccessToken)를 API서버에 제출하여 원하는 정보를 얻어오는 일반적인 방식입니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>GET/POST {api_url} HTTP/1.1</b>
 Host : apiserver.example.com
 Authorization: Bearer {access_token}
 Content-Type: application/x-www-form-urlencoded
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
								<td>'Bearer' 작성후 한칸 공백넣은 후 발급받은 AccessToken을 담아서 보냅니다.</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">API서버마다 개발요건에 따라 다르게 응답처리 하므로 일반적인 Response Body 에서 JSON 객체로 받는 처리를 예로 듭니다.</h6>
					<pre><code class="json">
{
	"userId" : "JKE"
	"role" : "ACL000001"
}
					</code></pre>
				</div>
			</div>
			
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">3. Resource Owner Password Credentials Grant 방식</h3>
				<div class="card-body">
					<p class="card-text mb-1">이 인증방식은 사용자의 아이디, 패스워드, 클라이언트아이디를 REST방식으로만 넘겨주면 AccessToken이 발급되는 인증방식입니다.</p>
					<p class="card-text mb-1">인증플로우에서 브라우저는 사용되지 않으며 사용자 비밀번호로 애플리케이션에 접근할 수 있기때문에 이 애플리케이션에 대한 사용자의 강한 신뢰가 바탕이 되어야 합니다.</p>
					<h5 class="card-title mt-3">1) 사용자 토큰(AccessToken) 받기</h5>
					<h6 class="card-subtitle text-muted">사용자 토큰들(Access Token, Refresh Token)을 받아옵니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>POST /token HTTP/1.1</b>
 Host : server.example.com
 Content-Type: application/x-www-form-urlencoded
 					
 grant_type=password&username={userId}&password={password}&client_id={app_key}
					</code></pre>
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
								<th scope="row">grant_type</th>
								<td>'password' 값으로 고정</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">username</th>
								<td>OAuth 사용자 로그인 아이디 (userID)</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">password</th>
								<td>OAuth 사용자 비밀번호</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_id</th>
								<td>애플리케이션 생성 시 발급된 애플리케이션 아이디 (clientId)</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">사용자토큰 및 토큰정보를  Response Body 에서 JSON 객체로 처리하여 응답합니다.</h6>
					<p class="card-text mt-1 mb-1">성공할 경우:</p>
					<pre><code class="json">
{
	"access_token": "qVjOWT3ibf4b3RiFh4GgKa",
	"token_type": "bearer",
	"expires_in": 3600,
	"refreshToken": "ffBV27o4r5PbR5bhVowdja"
}
					</code></pre>
					<p class="card-text mt-1 mb-1">실패할 경우:</p>
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
								<th scope="row">access_token</th>
								<td>사용자 인증 토큰</td>
							</tr>
							<tr>
								<th scope="row">refresh_token</th>
								<td>accessToekn이 만료 될 경우 재발급할 때 사용할 인증토큰</td>
							</tr>
							<tr>
								<th scope="row">token_type</th>
								<td>일반형일 경우 'bearer'</td>
							</tr>
							<tr>
								<th scope="row">expires_in</th>
								<td>토큰 만료 시간(초)</td>
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
												
				<div class="card-body">
					<h5 class="card-title">1-1) 사용자 토큰(AccessToken) 갱신하기</h5>
					<h6 class="card-subtitle text-muted">사용자토큰(AccessToken)이 만료가 되면 토큰발급 때 받았던 refresh_token으로 재갱신합니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>POST /token HTTP/1.1</b>
 Host : server.example.com
 Content-Type: application/x-www-form-urlencoded
 					
 grant_type=refresh_token&client_id={app_key}&refresh_token={refresh_token}
					</code></pre>
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
								<th scope="row">grant_type</th>
								<td>'refresh_token' 값으로 고정</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_id</th>
								<td>AccessToken을 발급한 클라이언트아이디</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">refresh_token</th>
								<td>토근 발급시 받은 refresh_token을 제출하여 AccessToken을 새로 받습니다.</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">사용자토큰 및 토큰정보를  Response Body 에서 JSON 객체로 처리하여 응답합니다.</h6>
					<p class="card-text mt-1 mb-1">성공할 경우:</p>
					<pre><code class="json">
{
	"access_token": "qVjOWT3ibf4b3RiFh4GgKa",
	"token_type": "bearer",
	"expires_in": 3600,
	"refreshToken": "k7L2EEmGyaVIaWN7G2LKsa"
}
					</code></pre>
					<p class="card-text mt-1 mb-1">실패할 경우:</p>
					<pre><code class="json">
{
	"error_description": {error_description},
	"error": {error}
}
					</code></pre>
				</div>
				
				<div class="card-body">
					<h5 class="card-title">2) 사용자 토큰(AccessToken)을 이용하여 API서버 사용하기</h5>
					<h6 class="card-subtitle text-muted">사용자토큰(AccessToken)를 API서버에 제출하여 원하는 정보를 얻어오는 일반적인 방식입니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>GET/POST {api_url} HTTP/1.1</b>
 Host : apiserver.example.com
 Authorization: Bearer {access_token}
 Content-Type: application/x-www-form-urlencoded
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
								<td>'Bearer' 작성후 한칸 공백넣은 후 발급받은 AccessToken을 담아서 보냅니다.</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">API서버마다 개발요건에 따라 다르게 응답처리 하므로 일반적인 Response Body 에서 JSON 객체로 받는 처리를 예로 듭니다.</h6>
					<pre><code class="json">
{
	"userId" : "JKE"
	"role" : "ACL000001"
}
					</code></pre>
				</div>
			</div>
			
			<div class="card mb-3 mt-3" style="max-width: 50rem;">
				<h3 class="card-header">4. Client Credentials Grant 방식</h3>
				<div class="card-body">
					<p class="card-text mb-1">일반 사용자가 아닌 애플리케이션(클라이언트) 정보만으로 AccessToken을 발행합니다.</p>
					<p class="card-text mb-1">일반 사용자에게 접근을 위임받을 필요가 없고 단순 API의 자원을 사용하기 위할때 사용됩니다.</p>
					<h5 class="card-title mt-3">1) 인증 토큰(AccessToken) 받기</h5>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>POST /token HTTP/1.1</b>
 Host : server.example.com
 Content-Type: application/x-www-form-urlencoded
 					
 grant_type=client_credentials&client_id={app_key}&client_secret={client_secret}
					</code></pre>
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
								<th scope="row">grant_type</th>
								<td>'client_credentials' 값으로 고정</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_id</th>
								<td>애플리케이션 생성 시 발급된 애플리케이션 아이디 (clientId)</td>
								<td>O</td>
							</tr>
							<tr>
								<th scope="row">client_secret</th>
								<td>애플리케이션 생성 시 사용한 애플리케이션 비밀번호</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">Response Body 에서 JSON 객체로 다음과 같이 받습니다.</h6>
					<p class="card-text mt-1 mb-1">성공할 경우:</p>
					<pre><code class="json">
{
	"access_token": "qVjOWT3ibf4b3RiFh4GgKa",
	"token_type": "bearer",
	"expires_in": 3600,
}
					</code></pre>
					<p class="card-text mt-1 mb-1">실패할 경우:</p>
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
								<th scope="row">access_token</th>
								<td>사용자 인증 토큰</td>
							</tr>
							<tr>
								<th scope="row">token_type</th>
								<td>일반형일 경우 'bearer'</td>
							</tr>
							<tr>
								<th scope="row">expires_in</th>
								<td>토큰 만료 시간(초)</td>
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
												
				<div class="card-body">
					<h5 class="card-title">1-1) 사용자 토큰(AccessToken) 갱신하기</h5>
					<p class="card-text mt-2 mb-1"><b>client_credentials 방식은 RefreshToken을 제공하지 않습니다.</b></p>
				</div>
				
				<div class="card-body">
					<h5 class="card-title">2) 사용자 토큰(AccessToken)을 이용하여 API서버 사용하기</h5>
					<h6 class="card-subtitle text-muted">사용자토큰(AccessToken)를 API서버에 제출하여 원하는 정보를 얻어오는 일반적인 방식입니다.</h6>
					<p class="card-text mt-2 mb-1"><b>[Request]</b></p>
					<pre><code class="http">
 <b>GET/POST {api_url} HTTP/1.1</b>
 Host : apiserver.example.com
 Authorization: Bearer {access_token}
 Content-Type: application/x-www-form-urlencoded
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
								<td>'Bearer' 작성후 한칸 공백넣은 후 발급받은 AccessToken을 담아서 보냅니다.</td>
								<td>O</td>
							</tr>
						</tbody>
					</table>
					<p class="card-text mt-2 mb-1"><b>[Response]</b></p>
					<h6 class="card-subtitle text-muted">API서버마다 개발요건에 따라 다르게 응답처리 하므로 일반적인 Response Body 에서 JSON 객체로 받는 처리를 예로 듭니다.</h6>
					<pre><code class="json">
{
	"userId" : "JKE"
	"role" : "ACL000001"
}
					</code></pre>
				</div>
			</div>

			</main>

		</div>

	</div>

	<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>

</body>
<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>