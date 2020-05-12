<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>

<%@ include file="/WEB-INF/views/include/head.jsp"%>

<%@ include file="/WEB-INF/views/include/plugin_js.jsp" %>

<body>

<script type="text/javascript">
	
	$( document ).ready(function() {
						
		//ClientId 중복 체크 버튼
		$('[name="btnApiIdCheck"]').click(function(){
			
			var apiId = $('[name="apiId"]').val();
			if(apiId.length < 1){
				var errorMsg = '<div id="errorMsg">API ID를 입력하세요.</div>';
				$('#errorMsg').remove();
				$('#errorAlert').show();
				$('#errorAlert').append(errorMsg);
				return false;
			}
			$('#feedback').remove();
			
			$.ajax({
				async : true,
				type : "POST",
				data : apiId,
				url : "/admin/api-server/check",
				dataType : "json",
				contentType : "application/json; charset=UTF-8",
				success : function(data){
					
					$('#errorMsg').remove();
					$('#errorAlert').hide();
					
					if(data.result > 0){
						$('#divApiId').attr('class','input-group mb-1 has-danger');
						$('#apiIdInput').attr('class','form-control is-invalid');
						$('#divApiId').append('<div class="invalid-feedback" id="feedback">중복된 API 아이디입니다.</div>');
						$('#checkResult').val('true');
					} else{
						$('#divApiId').attr('class','input-group mb-1 has-success');
						$('#apiIdInput').attr('class','form-control is-valid');
						$('#divApiId').append('<div class="valid-feedback" id="feedback">사용 가능한 API 아이디입니다.</div>');
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
						
			if($('#usableCheckBox0').prop("checked")){
				$('[name="usableApi"]').val('Y');
			} else{
				$('[name="usableApi"]').val('N');
			}
			
			var formObj = $('#formAddApi');
			formObj.attr('action','/admin/api-server/regist');
			formObj.submit();
		});
		
		// Modal로 가는 추가버튼
		$('[name="btnSubmitAddModal"]').click(function(){
			
			var errorAlert = false;
			
			if($('[name="apiName"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">API 이름을 입력하세요.</div>';
				errorAlert = true;
			}
			else if($('#checkResult').val() == 'true'){
				var errorMsg = '<div id="errorMsg">API ID의 중복체크 먼저 확인하세요.</div>';
				errorAlert = true;
			}
			else if($('[name="apiSecret"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">API 비밀번호를 입력하세요.</div>';
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
			</ul>
			
			<form id="formAddApi" action="" method="post">
			
			<div id="myTabContent" class="tab-content">
				<!-- 메인 Tab -->
				<div class="tab-pane fade active show" id="home">
				
					<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
					  <div class="card-header">API 서버 정보
					  	<span class="badge badge-danger"> 필수항목</span>
					  </div>
					  <div class="card-body">
					    <div class="card-text">
					    	<div class="form-group">
								<fieldset>
									<label class="control-label" for="readOnlyInput">API 이름 (API Name)</label>
			    					<input class="form-control" id="apiNameInput" name="apiName" type="text" maxlength="30" placeholder="API 명칭">
			    					<label class="control-label mt-2" for="readOnlyInput">API 아이디 (API ID)</label>
			    					<div class="input-group mb-1" id="divApiId">
										<input class="form-control" id="apiIdInput" name="apiId" type="text" placeholder="API 아이디">
										<div class="input-group-append">
								    		<button class="btn btn-primary" type="button" name="btnApiIdCheck">중복 확인</button> 
										</div>
										<input type="hidden" id="checkResult" value="true">
									</div>
			  						<label class="control-label mt-2" for="inputDefault">API 비밀번호 (API Secret)</label>
									<input type="password" class="form-control" id="apiSecretInput" name="apiSecret" placeholder="API 비밀번호" maxlength="30">
									<label class="control-label mt-2" for="inputDefault">API 서비스 주소 (API Service URL)</label>
									<input class="form-control" id="apiUrlInput" name="apiUrl" placeholder="https://">
								</fieldset>
							</div>
					    </div>
					  </div>
					</div>
					
					<div class="card border-light mb-3" style="max-width: 40rem;">
					  <div class="card-header">사용 여부</div>
					  <div class="card-body">
					    <div class="form-group">
								<div class="custom-control custom-switch">
									<input type="checkbox" class="custom-control-input" id="usableCheckBox0" value="" checked="checked">
						    		<label class="custom-control-label" for="usableCheckBox0">해당 API 사용</label>
						    		<input type="hidden" name="usableApi" value="">
								</div> 
							</div>
					  </div>
					</div>
				
				</div>
				
			</div>
			
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