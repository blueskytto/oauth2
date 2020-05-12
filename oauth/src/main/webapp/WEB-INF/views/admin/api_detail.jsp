<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>

<%@ include file="/WEB-INF/views/include/head.jsp"%>

<%@ include file="/WEB-INF/views/include/plugin_js.jsp" %>

<body>

<script type="text/javascript">
	
	$( document ).ready(function() {
		
		//Token 자동 연장 기능 CheckBox 버튼 체크 처리
		var apiUsable = '${apiInfo.API_USABLE}';
				
		if(apiUsable == 'Y'){
			$("input:checkbox[id='usableCheckBox0']").prop("checked", true);
		}
		
		//취소 버튼
		$('[name="modalBtnCancel"]').click(function(){
			location.reload();
		});
		
		//확인 버튼
		$('[name="modalBtnSubmit"]').click(function(){
			
			if($('#usableCheckBox0').prop("checked")){
				$('[name="usableApi"]').val('Y');
			}else{
				$('[name="usableApi"]').val('N');
			}
			
			var formObj = $('#formModiApiDetail');
			formObj.attr('action','/admin/api-servers/' + '${apiInfo.API_ID}');
			formObj.submit();
		});
		
		//삭제 버튼
		$('[name="modalDeleteBtnSubmit"]').click(function(){

			var formObj = $('#formDeleteApi');
			formObj.attr('action','/admin/api-servers/' + '${apiInfo.API_ID}');
			formObj.submit();
		});
		
		// Modal로 가는 추가버튼
		$('[name="btnSubmitModal"]').click(function(){
			
			var errorAlert = false;
			
			if($('[name="apiName"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">API 이름을 입력하세요.</div>';
				errorAlert = true;
			}
			else if($('[name="apiSecret"]').val().length < 1){
				$('[name="apiSecret"]').remove();
			}
			if(errorAlert){
				$('#errorMsg').remove();
				$('#errorAlert').show();
				$('#errorAlert').append(errorMsg);
				return false;
			}
			
			$('#submitModal').modal();
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
			
			<div class="card border-primary mb-3" style="max-width: 40rem;">
				<div class="card-header">API 서버 상세</div>
				<div class="card-body">
				<h4 class="card-title">' ${apiInfo.API_NAME} ' API Server</h4>
				<p class="card-text">API 서버의 정보를 수정 및 삭제 할수 있습니다.</p>
				<form id="formDeleteApi" action="" method="post">
					<input type="hidden" name="_method" value="DELETE" />
					<button class="btn btn-danger mt-3 pull-right" type="button" data-toggle="modal" data-target="#submitDeleteModal"> 삭제 </button>
				</form>
		    	
				</div>
			</div>
		
			<ul class="nav nav-tabs mt-3">
				<li class="nav-item">
					<a class="nav-link active" data-toggle="tab" href="#home">메인</a>
				</li>
			</ul>
			
			<form id="formModiApiDetail" action="" method="post">
				<input type="hidden" name="_method" value="PUT" />
			
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
				    					<input class="form-control" id="apiNameInput" name="apiName" type="text" maxlength="30" value="${apiInfo.API_NAME}">
				    					<label class="control-label mt-2" for="readOnlyInput">API ID (API ID)</label>
				    					<input class="form-control" id="apiIdInput" name="apiId" type="text" value="${apiInfo.API_ID}" readonly>
				  						<label class="control-label mt-2" for="inputDefault">API 비밀번호 (API Secret)</label>
										<input type="password" class="form-control" id="apiSecretInput" name="apiSecret" value="" placeholder="공백일 시 수정 안 함">
										<label class="control-label mt-2" for="inputDefault">API 서비스 주소 (API Service URL)</label>
										<input class="form-control" id="apiUrlInput" name="apiUrl" value="${apiInfo.API_SERVICE_URL}">
									</fieldset>
								</div>
						    </div>
						  </div>
						</div>
						
						<div class="card border-light mb-3" style="max-width: 40rem;">
						  <div class="card-header">사용 여부</div>
						  <div class="card-body">
						    <div class="form-group">
									<div class="custom-control custom-switch"">		
										<input type="checkbox" class="custom-control-input" id="usableCheckBox0" value="">
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
				
				<button class="btn btn-primary" type="button" data-toggle="modal" name="btnSubmitModal"> 수정 </button>
				<button class="btn btn-secondary ml-1" type="button" data-toggle="modal" data-target="#cancelModal"> 취소 </button>
			
			</form>
			
			<%@ include file="/WEB-INF/views/include/client_modal.jsp"%>
		
		</main>
	</div>
</div>
</body>
	<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>