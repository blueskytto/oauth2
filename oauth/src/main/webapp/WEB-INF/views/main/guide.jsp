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

			<div class="jumbotron ml-2 mr-2">
				<h1 class="display-5">
					OAuth 2.0 사용 가이드
				</h1>
				<p class="lead">드림시큐리티 OAuth2를 이용하기 위해 어플리케이션 등록 및 사용에 대한 가이드를 제공합니다.</p>
				<hr class="my-4">
				<p> 드림시큐리티 및 사용처의 API 기능을 개발자가 OAuth를 이용하여 사용할 수 있도록 웹 방식 또는 공통가이드를 동해 기술합니다.
					<br/> 또한 해당 가이드는 애플리케이션을 개발할 때 기본적인 사항을 숙지하며 개념을 이해하도록 설명합니다.
				</p>
			</div>

			</main>

		</div>

	</div>

	<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>

</body>
	<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>