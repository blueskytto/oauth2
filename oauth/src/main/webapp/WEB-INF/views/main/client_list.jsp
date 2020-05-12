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
			  <div class="card-header">애플리케이션 관리</div>
			  <div class="card-body">
			    <h4 class="card-title"> ${USER_ID} 님의 Application 목록</h4>
			    <p class="card-text">등록한 어플리케이션을 추가 및 삭제, 수정 할 수 있습니다.</p>
		    	<button type="button" class="btn btn-primary mt-3" onclick="location.href='/main/client/regist'">신규 애플리케이션 등록</button>
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
						<th>애플리케이션 명칭</th>
						<th>애플리케이션 아이디</th>
						<th>애플리케이션 생성자</th>
						<th>애플리케이션 생성 날짜</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="row" items="${clientList}">
						<tr onclick="location.href='/main/clients/${row.CLIENT_ID}'">
							<td>${row.CLIENT_NAME}</td>
							<td>${row.CLIENT_ID}</td>
							<td>${row.CLIENT_OWNER}</td>
							<td>${row.CLIENT_CREATION_DATE}</td>
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

				<form action="./clients" method="post" id='frmPaging'>
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