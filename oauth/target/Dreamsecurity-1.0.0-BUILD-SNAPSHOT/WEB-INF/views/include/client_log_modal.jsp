<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- 클라이언트 클릭시 -->
<div class="modal fade" id="submitModal">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">

			<!-- Modal Header -->
			<div class="modal-header" id="modal-header">
				<h4 class="modal-title">클라이언트 통계</h4>
				<button type="button" class="close" data-dismiss="modal">&times;</button>
			</div>

			<!-- Modal body -->
			<div class="modal-body" id="modal-body">
				<div id="modal-body-text">

					<!-- 달력 폼 -->
					<div class="card border-primary mb-3" style="max-width: 100%;">
						<div class="card-header">
							조회 기간
							<h6 class="text-warning">(한번에 최대 60일간 조회 가능)</h6>
						</div>
						<div class="card-body">

							<div class="input-daterange input-group date justify-content-center">
								<div class="input-group-prepend">
									<span class="fa fa-calendar mr-2 align-self-center" aria-hidden="true"></span>
								</div>
								<input type="text" name="daterange" readonly="readonly" size="20" placeholder="조회기간 선택">
								<button class="btn btn-primary pull-right ml-3" type="button" name="modalBtnView">조회</button>
							</div>

						</div>
					</div>
				</div>

				<div id="divGraph">
					<div class="card">
						<div class="card-body">
							<canvas id="chLine"></canvas>
						</div>
					</div>
				</div>

			</div>
			<!-- Modal footer -->
			<div class="modal-footer" id="modal-footer">
				<button type="button" class="btn btn-danger" data-dismiss="modal">닫기</button>
			</div>
		</div>
	</div>
</div>
