<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<aside class="col-12 col-md-3 col-xl-2">
		<nav class="navbar navbar-expand navbar-dark bg-dark flex-md-column flex-row align-items-start py-2">
		    <div class="collapse navbar-collapse">
		        <ul class="flex-md-column flex-row navbar-nav w-100 justify-content-between">
		            <li class="nav-item">
		                <a class="nav-link pl-0 text-nowrap" href="/main/clients">
		                <i class="fa fa-sitemap"></i>
		                <span class="font-weight-bolde">애플리케이션 관리</span></a>
		            </li>
		            <li class="nav-item">
		                <a class="nav-link pl-0" href="/main/logs">
		                <i class="fa fa-bar-chart-o"></i>
		                <span class="d-md-inline">이력관리</span></a>
		            </li>
		            <c:if test="${USER_ROLE == 'ROLE_ADMIN'}">
						<li class="nav-item">
			                <a class="nav-link pl-0" href="/admin/api-servers">
			                <i class="fa fa-server"></i>
			                <span class="d-md-inline">API 서버 관리</span></a>
						</li>
						<li class="nav-item">
			                <a class="nav-link pl-0" href="#">
			                <i class="fa fa-user"></i>
			                <span class="d-md-inline">사용자 관리</span></a>
						</li>
					</c:if>
		            
		            
		        </ul>
		    </div>
		</nav>
	</aside>
