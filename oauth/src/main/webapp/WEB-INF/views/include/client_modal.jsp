<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- 취소 버튼시 -->
<div class="modal fade" id="cancelModal">
	<div class="modal-dialog">
		<div class="modal-content">

		<!-- Modal Header -->
		<div class="modal-header">
			<h4 class="modal-title">취소 확인</h4>
			<button type="button" class="close" data-dismiss="modal">&times;</button>
		</div>

		<!-- Modal body -->
		<div class="modal-body">
			수정한 내용을 취소 하시겠습니까?
		</div>

		<!-- Modal footer -->
		<div class="modal-footer">
			<button type="button" class="btn btn-primary" name="modalBtnCancel">확인</button>
			<button type="button" class="btn btn-danger" data-dismiss="modal" >취소</button>
		</div>

    </div>
  </div>
</div>

<!-- 확인 버튼시 -->
<div class="modal fade" id="submitModal">
	<div class="modal-dialog">
		<div class="modal-content">

		<!-- Modal Header -->
		<div class="modal-header" id="modal-header">
			<h4 class="modal-title">수정 확인</h4>
			<button type="button" class="close" data-dismiss="modal">&times;</button>
		</div>

		<!-- Modal body -->
		<div class="modal-body" id="modal-body">
			<div id="modal-body-text">내용을 수정하시겠습니까?</div>
			
			<!-- Progress Bar -->
			<div class="progress" id="progress" style="display:none; height:20px;">
				<div class="progress-bar progress-bar-striped progress-bar-animated" id="progressbar" role="progressbar" aria-valuenow="" aria-valuemin="0" aria-valuemax="100" style=""></div>
			</div>
			
		</div>

		<!-- Modal footer -->
		<div class="modal-footer" id="modal-footer">
			<button type="button" class="btn btn-primary" name="modalBtnSubmit">확인</button>
			<button type="button" class="btn btn-danger" data-dismiss="modal" >취소</button>
		</div>
    </div>
  </div>
</div>

<!-- 확인 버튼시 -->
<div class="modal fade" id="submitDeleteModal">
	<div class="modal-dialog">
		<div class="modal-content">

		<!-- Modal Header -->
		<div class="modal-header">
			<h4 class="modal-title">삭제 확인</h4>
			<button type="button" class="close" data-dismiss="modal">&times;</button>
		</div>

		<!-- Modal body -->
		<div class="modal-body">
			해당 클라이언트를 삭제하시겠습니까?
		</div>

		<!-- Modal footer -->
		<div class="modal-footer">
			<button type="button" class="btn btn-primary" name="modalDeleteBtnSubmit">확인</button>
			<button type="button" class="btn btn-danger" data-dismiss="modal" >취소</button>
		</div>

    </div>
  </div>
</div>

<!-- 클라이언트 추가 버튼시 -->
<div class="modal fade" id="submitAddModal">
	<div class="modal-dialog">
		<div class="modal-content">

		<!-- Modal Header -->
		<div class="modal-header">
			<h4 class="modal-title">추가 확인</h4>
			<button type="button" class="close" data-dismiss="modal">&times;</button>
		</div>

		<!-- Modal body -->
		<div class="modal-body">
			해당 클라이언트를 추가 하시겠습니까?
		</div>

		<!-- Modal footer -->
		<div class="modal-footer">
			<button type="button" class="btn btn-primary" name="modalAddBtnSubmit">확인</button>
			<button type="button" class="btn btn-danger" data-dismiss="modal" >취소</button>
		</div>

    </div>
  </div>
</div>
