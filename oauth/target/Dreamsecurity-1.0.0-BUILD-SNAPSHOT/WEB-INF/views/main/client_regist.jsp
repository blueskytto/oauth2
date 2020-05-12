<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>

<%@ include file="/WEB-INF/views/include/head.jsp"%>

<%@ include file="/WEB-INF/views/include/plugin_js.jsp" %>

<body>

<script type="text/javascript">
	
	$( document ).ready(function() {
				
		//URI 버튼 추가 처리
		$('[name="btnUriAdd"]').click(function() {
			
			console.log($('#inputUriPlus').val().length);
			
			if($('#inputUriPlus').val().length === 0){
				alert('Redirect Uri를 입력하세요.');
				return false;
			}
			
			//var uriLength = $('#uri-group').children().length - 1; 
			$('#uri-group').append(
			'<div class="input-group mb-1">'+
				'<input type="text" class="form-control" name="uriList" value="'+$('#inputUriPlus').val()+'" readonly="readonly">'+
				'<div class="input-group-append">'+
					'<button class="btn btn-danger" type="button" name="btnUriDel">삭제</button>'+
				'</div>'+
			'</div>'
			);
			
			$('#inputUriPlus').val('');
			
	    });
		
		//URI 버튼 삭제 처리
		$('#uri-group').on('click','[name="btnUriDel"]',function(){
			var divTag = $( this ).closest('div').parent().closest('div');
			divTag.remove();
	    });
		
		//ClientId 중복 체크 버튼
		$('[name="btnClientIdCheck"]').click(function(){
			
			var clientId = $('[name="clientId"]').val();
			if(clientId.length < 1){
				var errorMsg = '<div id="errorMsg">애플리케이션 ID를 입력하세요.</div>';
				$('#errorMsg').remove();
				$('#errorAlert').show();
				$('#errorAlert').append(errorMsg);
				return false;
			}
			$('#feedback').remove();
			
			$.ajax({
				async : true,
				type : "POST",
				data : clientId,
				url : "/main/client/check",
				dataType : "json",
				contentType : "application/json; charset=UTF-8",
				success : function(data){
					
					$('#errorMsg').remove();
					$('#errorAlert').hide();
					
					if(data.result > 0){
						$('#divClientId').attr('class','input-group mb-1 has-danger');
						$('#clientIdInput').attr('class','form-control is-invalid');
						$('#divClientId').append('<div class="invalid-feedback" id="feedback">중복된 애플리케이션 아이디입니다.</div>');
						$('#checkResult').val('true');
					} else{
						$('#divClientId').attr('class','input-group mb-1 has-success');
						$('#clientIdInput').attr('class','form-control is-valid');
						$('#divClientId').append('<div class="valid-feedback" id="feedback">사용 가능한 애플리케이션 아이디입니다.</div>');
						$('#checkResult').val('false');
					}
				}
			});
			
		});
		
		
		//취소 버튼
		$('[name="modalBtnCancel"]').click(function(){
			location.reload();
		});
		
		//확인 버튼
		$('[name="modalAddBtnSubmit"]').click(function(){
			
			var checkedScopes = $.map($(':checkbox[name=scopes]:checked'), function(n, i){
				return n.value;
			}).join(',');
			
			var checkedGrantTypes = $.map($(':checkbox[name=grantTypes]:checked'), function(n, i){
				return n.value;
			}).join(',');
			
			$('[name="scopes"]').val(checkedScopes);
			$('[name="grantTypes"]').val(checkedGrantTypes);
			
			if($('#tokenCheckBox0').prop("checked")){
				$('[name="autoExToken"]').val('Y');
			} else{
				$('[name="autoExToken"]').val('N');
			}
			
			var formObj = $('#formAddClient');
			formObj.attr('action','/main/client/regist');
			formObj.submit();
		});
		
		// Modal로 가는 추가버튼
		$('[name="btnSubmitAddModal"]').click(function(){
			
			var errorAlert = false;
			
			if($('[name="clientName"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">애플리케이션 이름을 입력하세요.</div>';
				errorAlert = true;
			}
			else if($('#checkResult').val() == 'true'){
				var errorMsg = '<div id="errorMsg">애플리케이션 ID의 중복체크 먼저 확인하세요.</div>';
				errorAlert = true;
			}
			else if($('[name="clientSecret"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">애플리케이션 비밀번호를 입력하세요.</div>';
				errorAlert = true;
			}
			else if($(':checkbox[name=grantTypes]:checked').length < 1){
				var errorMsg = '<div id="errorMsg">최소한 한개 이상의 Grant Type을 지정해야합니다.</div>';
				errorAlert = true;
			}
			
			if(errorAlert){
				$('#errorMsg').remove();
				$('#errorAlert').show();
				$('#errorAlert').append(errorMsg);
				return false;
			}
			
			$('#submitAddModal').modal();
		});

	});

</script>

<div class="wrapper">
	<!-- Main Header -->
	<%@ include file="/WEB-INF/views/include/main_header.jsp"%>
	
	<div class="row">
		<!-- Main Left -->
		<%@ include file="/WEB-INF/views/include/left_column.jsp"%>

		<main class="col-md-8 col-xl-9 p-t-2 mt-3">
		
			<ul class="nav nav-tabs mt-3">
			  <li class="nav-item">
			    <a class="nav-link active" data-toggle="tab" href="#home">메인</a>
			  </li>
			  <li class="nav-item">
			    <a class="nav-link" data-toggle="tab" href="#api">API 설정</a>
			  </li>
			  <li class="nav-item">
			    <a class="nav-link" data-toggle="tab" href="#token">토큰 설정</a>
			  </li>
			</ul>
			
			<form id="formAddClient" action="" method="post">
			
			<div id="myTabContent" class="tab-content">
				<!-- 메인 Tab -->
				<div class="tab-pane fade active show" id="home">
				
					<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
					  <div class="card-header">애플리케이션 정보
					  	<span class="badge badge-danger"> 필수항목</span>
					  </div>
					  <div class="card-body">
					    <div class="card-text">
					    	<div class="form-group">
								<fieldset>
									<label class="control-label" for="readOnlyInput">애플리케이션 이름 (Client Name)</label>
			    					<input class="form-control" id="clientNameInput" name="clientName" type="text" maxlength="30" placeholder="Client Name">
			    					<label class="control-label mt-2" for="readOnlyInput">애플리케이션 ID (Client ID)</label>
			    					<div class="input-group mb-1" id="divClientId">
										<input class="form-control" id="clientIdInput" name="clientId" type="text" placeholder="Client ID">
										<div class="input-group-append">
								    		<button class="btn btn-primary" type="button" name="btnClientIdCheck">중복 확인</button> 
										</div>
										<input type="hidden" id="checkResult" value="true">
									</div>
			  						<label class="control-label mt-2" for="inputDefault">애플리케이션 비밀번호 (Client Secret)</label>
									<input type="password" class="form-control" id="clientSecretInput" name="clientSecret" placeholder="Client Secret" maxlength="30">
								</fieldset>
							</div>
					    </div>
					  </div>
					</div>
					
					<div class="card border-light mb-3" style="max-width: 40rem;">
					  <div class="card-header">Oauth 토큰 방식</div>
					  <div class="card-body">
					    <div class="card-text">
					    	<div class="form-group">
								<div class="custom-control custom-radio">
									<input type="radio" id="basicRadio" name="tokenRadio" class="custom-control-input" value="bearer" checked="checked">
									<label class="custom-control-label" for="basicRadio">기본형</label>
							    </div>
							    <div class="custom-control custom-radio">
									<input type="radio" id="jwtRadio" name="tokenRadio" class="custom-control-input" value="bearer-jwt">
									<label class="custom-control-label" for="jwtRadio">JWT(Json Web Token)</label>
							    </div>
							    <div class="custom-control custom-radio">
									<input type="radio" id="samlRadio" name="tokenRadio" class="custom-control-input" value="2" disabled>
									<label class="custom-control-label" for="samlRadio">SAML</label>
							    </div>
		  					</div>
					    </div>
					  </div>
					</div>
				
				</div>
			
				<!-- API 설정 Tab -->
				<div class="tab-pane fade" id="api">
				
					<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
						<div class="card-header">사용 API (SCOPE)</div>
						<div class="card-body">
							<div class="form-group">
								<c:forEach var="row" items="${scopesList}" varStatus="status">
									<div class="custom-control custom-checkbox">		
										<input type="checkbox" class="custom-control-input" id="scopesCheck${status.index}" name="scopes" value="${row.SCOPE}">
							    		<label class="custom-control-label" for="scopesCheck${status.index}">${row.SCOPE}</label>
									</div>    	
								</c:forEach>
							</div>
						</div>
					</div>
					
					<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
						<div class="card-header" id="grantTypes">인증 유형 (Grant Types)
							<span class="badge badge-danger">반드시 한개는 추가되야함</span>
						</div>
						<div class="card-body">
							<div class="form-group">
								<c:forEach var="row" items="${grantTypesList}" varStatus="status">
									<div class="custom-control custom-checkbox">		
										<input type="checkbox" class="custom-control-input" id="grantTypesCheck${status.index}" name="grantTypes" value="${row.GRANT_TYPE}">
							    		<label class="custom-control-label" for="grantTypesCheck${status.index}">${row.GRANT_TYPE}</label>
									</div>    	
								</c:forEach>
							</div>
						</div>
					</div>
					
					<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
						<div class="card-header">서비스 Uri (Redirect URI)</div>
						<div class="card-body">
							<div class="form-group" id="uri-group">
								<div class="input-group mb-1">	
									<input type="text" class="form-control" id="inputUriPlus" placeholder="https://">
									<div class="input-group-append">
								    	<button class="btn btn-success" type="button" name="btnUriAdd">추가</button> 
									</div>
								</div>
							
								<c:forEach var="row" items="${clientUriList}" varStatus="status">							
									<div class="input-group mb-1">	
										<input type="text" class="form-control" name="uriList" value="${row.REDIRECT_URI}" readonly="readonly">
										<div class="input-group-append">
									    	<button class="btn btn-danger" type="button" name="btnUriDel">삭제</button> 
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
						
				</div>
				
				<!-- 토큰 설정 Tab -->
				<div class="tab-pane fade" id="token">
				
					<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
						<div class="card-header">토큰 유효시간 (Access Token Timeout)</div>
						<div class="card-body">
							<h6 class="card-subtitle text-muted">토큰 발급시점부터의 유효한 시간을 지정합니다.</h6>
							<div class="form-group">
								<fieldset>
									<label class="control-label mt-4" for="readOnlyInput">Access Token 유효시간 (초)</label>
			    					<input class="form-control" id="clientTokenExInput" name="clientTokenEx" type="text" value="3600">
			    					<label class="control-label mt-2" for="readOnlyInput">Refresh Token 유효시간 (초)</label>
			    					<input class="form-control" id="clientReTokenExInput" name="clientReTokenEx" type="text" value="86400">
								</fieldset>
							</div>
						</div>
					</div>
					
					<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
						<div class="card-header">토큰 유효시간 옵션 </div>
						<div class="card-body">
							<div class="form-group">
								<div class="custom-control custom-switch">
									<input type="checkbox" class="custom-control-input" id="tokenCheckBox0" value="">
						    		<label class="custom-control-label" for="tokenCheckBox0">Access Token 유효시간을 발급한 날짜가 아닌 제출된 날짜부터로 자동연장합니다.</label>
						    		<input type="hidden" name="autoExToken" value="">
								</div> 
							</div>
						</div>
					</div>
					
				</div>
			</div>
			
			<input type="hidden" name="clientOwner" value="${USER_ID}">
			
			<!-- ERROR Alert START -->
			<div id="errorAlert" class="alert alert-dismissible alert-danger" style="display:none; max-width:40rem;">
			</div>
			<!-- ERROR Alert END -->
			
			<button class="btn btn-primary" type="button" data-toggle="modal" name="btnSubmitAddModal"> 추가 </button>
			<button class="btn btn-secondary ml-1" type="button" data-toggle="modal" data-target="#cancelModal"> 취소 </button>
			
			</form>
			
			<%@ include file="/WEB-INF/views/include/client_modal.jsp"%>
		
		</main>
	</div>
</div>
</body>
	<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>