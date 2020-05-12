<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header>
	<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
		<a class="navbar-brand" href="<c:url value='/'/>">Dreamsecurity Oauth2.0</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarColor01" aria-controls="navbarColor01"
			aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>

		<div class="collapse navbar-collapse" id="navbarColor01">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active">
					<a class="nav-link" href="<c:url value='/'/>">메인 <span class="sr-only">(current)</span></a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="/guide">개발가이드</a>
				</li>
				<c:if test="${USER_ROLE == 'ROLE_ADMIN'}">
				<li class="nav-item">
					<a class="nav-link" href="/admin/oauth-config">Oauth 관리자 설정</a>
				</li>
				</c:if>
				<li class="nav-item">
					<a class="nav-link" href="#">계정 정보</a>
				</li>
			</ul>
			
			<c:choose>
				<c:when test="${USER_ID != null}">
					<form class="form-inline my-2 my-lg-0" method="post" action="/logout">
						<p class="naver-text text-light mr-auto">${USER_ID} 님</p>
						<button class="btn btn-secondary ml-2 my-2 my-sm-0" type="submit">로그아웃</button>
					</form>	
				</c:when>
				<c:otherwise>
					<button class="btn btn-secondary ml-2 my-2 my-sm-0" onclick="location.href='/login'">로그인</button>
				</c:otherwise>
			</c:choose>

		</div>
	</nav>
</header>