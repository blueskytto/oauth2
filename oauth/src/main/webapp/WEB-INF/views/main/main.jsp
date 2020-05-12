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
			<%@ include file="/WEB-INF/views/include/left_column.jsp"%>

			<main class="col-md-8 col-xl-9 p-t-2 mt-3">

			<div class="jumbotron ml-2 mr-2">
				<h1 class="display-5">
					Magic OAUTH 2.0
					<img src="resources/images/dreamsecurity_logo.png" width="200"/>
				</h1>
				<p class="lead">드림시큐리티 Oauth2 인증 프레임워크는 표준요건에 맞추어 제공합니다.</p>
				<hr class="my-4">
				<p>Oauth2를 이용하기 위해 어플리케이션 등록 과정의 사용자 가이드를 제공합니다.</p>
				<p class="lead">
					<a class="btn btn-primary btn-lg mt-3" href="guide" role="button">정보 더 보기</a>
				</p>
			</div>

			</main>

		</div>

	</div>

	<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>

</body>
	<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>