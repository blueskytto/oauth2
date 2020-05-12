<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>

<%@ include file="/WEB-INF/views/include/head.jsp"%>

<%@ include file="/WEB-INF/views/include/plugin_js.jsp" %>
<body>

<script type="text/javascript">
	
	$( document ).ready(function() {
		
		// FORM 데이터 JSON으로 변환처리 함수
		jQuery.fn.serializeObject = function() { 
			var obj = null; 
			try { 
				if(this[0].tagName && this[0].tagName.toUpperCase() == "FORM" ) { 
					var arr = this.serializeArray(); 
					if(arr){
						obj = {}; 
						jQuery.each(arr, function() { 
						obj[this.name] = this.value; }); 
					} 
				} 
			} catch(e) { 
				alert(e.message); 
			} finally {} 
			return obj; 
		}
		
		//TokenRadio 버튼 체크 처리
		var userPwdType = '${oauthConfig.userPwdType}';
		var clientPwdType = '${oauthConfig.clientPwdType}';
		var uobj = document.getElementsByName('userPwdRadio');
		var cobj = document.getElementsByName('clientPwdRadio');
		
		if(userPwdType == '0'){
			uobj[0].checked = true;
		}
		else if(userPwdType == '1'){
			uobj[1].checked = true;
		}
		
		if(clientPwdType == '0'){
			cobj[0].checked = true;
		}
		else if(clientPwdType == '1'){
			cobj[1].checked = true;
		}
		
		//취소 버튼
		$('[name="modalBtnCancel"]').click(function(){
			location.reload();
		});
		
		//확인 버튼
		$('[name="modalBtnSubmit"]').click(function(){
					
			const formData = $("#formModiConfig").serializeObject();
						
			$.ajax({
				cache : false,
				type : 'put',
				data : JSON.stringify(formData),
				url : "/admin/oauth-config",
				dataType : "json",
				contentType : "application/json; charset=UTF-8",
				success : function(data){
					console.log(data);
					if(data.result){
						$('#modal-header').hide();
						$('#modal-body').children('#modal-body-text').text('반영중..');
						$('#progress').show();
						$('#modal-footer').hide();
						
						var current_progress = 0;
						var interval = setInterval(function(){
							current_progress += 1;
							$('#progressbar').attr('aria-valuenow', current_progress)
							.css('width', current_progress + "%")
							.text(current_progress + '% Complete');
							if(current_progress >= 100){
								clearInterval(interval);
								location.reload();
							}
						}, 50);
						
					} else{
						alert("서버오류로 인한 저장 실패");
					}
				}
			});
		});
		
		// Modal로 가는 추가버튼
		$('[name="btnSubmitModal"]').click(function(){
			
			var errorAlert = false;
			
			if($('[name="authCode"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">Authorization Code 유효 기간을 입력하세요.</div>';
				errorAlert = true;
			}
			else if($('[name="accessTokenLength"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">Access Token 길이를 입력하세요.</div>';
				errorAlert = true;
			}
			else if($('[name="refreshTokenLength"]').val().length < 1){
				var errorMsg = '<div id="errorMsg">Refresh Token 길이를 입력하세요.</div>';
				errorAlert = true;
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
				<div class="card-header">관리자 Oauth 환경설정</div>
				<div class="card-body">
					<p class="card-text">토큰 길이 등 기본적인 설정을 수정 및 변경할 수 있습니다.</p>
				</div>
			</div>
			
			<form id="formModiConfig" action="" method="post">

			<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
				<div class="card-header">토큰 설정</div>
				<div class="card-body">
					<div class="card-text">
						<div class="form-group">
							<fieldset>
								<label class="control-label" for="readOnlyInput">Authorization Code 유효 기간 (min)
									<span class="badge badge-warning">권장 10분</span>
								</label>
								<input class="form-control" id="authCodeVaildTimeInput" name="authCode" type="text" maxlength="10" value="${oauthConfig.codeValidTime}">
								<label class="control-label mt-2" for="readOnlyInput">AccessToken 길이
									<span class="badge badge-warning">권장 22</span>
								</label>
								<input class="form-control" id="accessTokenLengthInput" name="accessTokenLength" type="text" value="${oauthConfig.accessTokenLength}">
								<label class="control-label mt-2" for="inputDefault">RefreshToken 길이
									<span class="badge badge-warning">권장 22</span>
								</label>
								<input class="form-control" id="refreshTokenLengthInput" name="refreshTokenLength" type="text" value="${oauthConfig.refreshTokenLength}">
							</fieldset>
						</div>
					</div>
				</div>
			</div>
			
			<div class="card border-light mb-3 mt-3" style="max-width: 40rem;">
				<div class="card-header">암호화 설정</div>
				<div class="card-body">
					<div class="card-text">
						<div class="form-group">
							<fieldset>
								<label class="control-label mb-3" for="readOnlyInput">사용자 비밀번호 암호화 여부	</label>
								<div class="form-group">
									<div class="custom-control custom-radio">
										<input type="radio" id="userPwdplainRadio" name="userPwdRadio" class="custom-control-input" value="0">
										<label class="custom-control-label" for="userPwdplainRadio">평문</label>
								    </div>
								    <div class="custom-control custom-radio">
										<input type="radio" id="userPwdsha256Radio" name="userPwdRadio" class="custom-control-input" value="1">
										<label class="custom-control-label" for="userPwdsha256Radio">SHA-256</label>
								    </div>
			  					</div>
								
								<%-- <input class="form-control" id="authCodeVaildTimeInput" name="authCode" type="text" maxlength="10" value="${oauthConfig.userPwdType}"> --%>
								
								<label class="control-label mb-3" for="readOnlyInput">클라이언트, API 비밀번호 암호화 여부</label>
								<div class="form-group">
									<div class="custom-control custom-radio">
										<input type="radio" id="clientPwdplainRadio" name="clientPwdRadio" class="custom-control-input" value="0">
										<label class="custom-control-label" for="clientPwdplainRadio">평문</label>
								    </div>
								    <div class="custom-control custom-radio">
										<input type="radio" id="clientPwdsha256Radio" name="clientPwdRadio" class="custom-control-input" value="1">
										<label class="custom-control-label" for="clientPwdsha256Radio">SHA-256</label>
								    </div>
			  					</div>
								
								<%-- <input class="form-control" id="authCodeVaildTimeInput" name="authCode" type="text" maxlength="10" value="${oauthConfig.clientPwdType}"> --%>
							</fieldset>
						</div>
					</div>
				</div>
			</div>

			<!-- ERROR Alert START -->
			<div id="errorAlert" class="alert alert-dismissible alert-danger" style="display: none; max-width: 40rem;"></div>
			<!-- ERROR Alert END -->

			<button class="btn btn-primary" type="button" data-toggle="modal" name="btnSubmitModal">수정</button>
			<button class="btn btn-secondary ml-1" type="button" data-toggle="modal" data-target="#cancelModal">취소</button>

			</form>

			<%@ include file="/WEB-INF/views/include/client_modal.jsp"%>
			</main>

		</div>
	</div>

	<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>

</body>
<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>