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
			
			<div class="card border-primary mb-3" style="max-width: 100%;">
			  <div class="card-header">API 서버 관리</div>
			  <div class="card-body">
			    <h4 class="card-title">API 서버 목록</h4>
			    <p class="card-text">API 서버를 추가 및 삭제, 수정 할 수 있습니다.</p>
		    	<button type="button" class="btn btn-primary mt-3" onclick="location.href='/admin/api-server/regist'">신규 API 애플리케이션 등록</button>
			  </div>
			</div>
			
			<select class='custom-select col-sm-1 pull-right mb-1' id='listCount' name='listCount' onchange='listCnt();'>
				<option value='5'>5개</option>
				<option value='10'>10개</option>
				<option value='15'>15개</option>
				<option value='20'>20개</option>
			</select>
			<table class="table table-hover">
				<thead>
					<tr>
						<th>API 명칭</th>
						<th>API 아이디</th>
						<th>API 서비스 주소</th>
						<th>상태</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="row" items="${apiList}">
						<tr onclick="location.href='/admin/api-servers/${row.API_ID}'">
							<td>${row.API_NAME}</td>
							<td>${row.API_ID}</td>
							<td>${row.API_SERVICE_URL}</td>
							<c:choose>
								<c:when test="${row.API_USABLE == 'Y'}">
									<td>
										<button type="button" class="btn btn-success btn-sm" disabled="disabled">사용중</button>
									</td>	
								</c:when>
								<c:when test="${row.API_USABLE == 'N'}">
									<td>
										<button type="button" class="btn btn-danger btn-sm" disabled="disabled">미사용</button>
									</td>	
								</c:when>
							</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<div>
				<ul class="pagination justify-content-center">
					<c:if test="${p.pageStartNum ne 1}">
						<!--맨 첫페이지 이동 -->
						<li class="page-item">
							<a class="page-link" onclick='pagePre(${p.pageCnt+1},${p.pageCnt});'>«</a>
						</li>
						<!--이전 페이지 이동 -->
						<li class="page-item">
							<a class="page-link" onclick='pagePre(${p.pageStartNum},${p.pageCnt});'>‹</a>
						</li>
					</c:if>

					<!--페이지번호 -->
					<c:forEach var='i' begin="${p.pageStartNum}" end="${p.pageLastNum}" step="1">
						<li class='page-item pageIndex${i}'>
							<a class="page-link" onclick='pageIndex(${i});'>${i}</a>
						</li>
					</c:forEach>

					<c:if test="${p.lastChk}">
						<!--다음 페이지 이동 -->
						<li class="page-item">
							<a class="page-link" onclick='pageNext(${p.pageStartNum},${p.total},${p.listCnt},${p.pageCnt});'>›</a>
						</li>
						<!--마지막 페이지 이동 -->
						<li class="page-item">
							<a class="page-link" onclick='pageLast(${p.pageStartNum},${p.total},${p.listCnt},${p.pageCnt});'>»</a>
						</li>
					</c:if>
					
				</ul>

				<form action="./api-servers" method="post" id='frmPaging'>
					<!--출력할 페이지번호, 출력할 페이지 시작 번호, 출력할 리스트 갯수 -->
					<input type='hidden' name='index' id='index' value='${p.index}'>
					<input type='hidden' name='pageStartNum' id='pageStartNum' value='${p.pageStartNum}'>
					<input type='hidden' name='listCnt' id='listCnt' value='${p.listCnt}'>
				</form>

			</div>

			</main>
		</div>
	</div>

	<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>

</body>
	<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>