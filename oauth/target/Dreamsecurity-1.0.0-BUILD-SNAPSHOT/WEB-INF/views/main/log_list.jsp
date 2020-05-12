<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>

<%@ include file="/WEB-INF/views/include/head.jsp"%>

<%@ include file="/WEB-INF/views/include/plugin_js.jsp"%>
<script src="<c:url value="/resources/vendor/chart/moment.min.js"/>"></script>
<script src="<c:url value="/resources/vendor/chart/chart.min.js"/>"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
<body>

<script type="text/javascript">

var CLIENTID = "";

function clientLog(clientId) {
	CLIENTID = clientId;
	$('#submitModal').modal();
}

$( document ).ready(function() {
		
	/*
	* 달력 스크립트
	*/
	$(function() {

		function startDatef(){
			var startd = '';
	    	if($('[name="startDate"]').val > 0){
	    		startd = $('[name="startDate"]').val();
	    		startd = [a.slice(0, 4), "-", a.slice(4, 6), "-", a.slice(6, 8)].join('');
	    		startd = new Date(startd);
	    	}
	    	console.log(startd);
	    	return startd;
		}
    	
		$('input[name="daterange"]').daterangepicker({
			autoApply: false,
			maxDate: moment().format('YYYY/MM/DD'),
			maxSpan: {
		    	days: '60'
		    },
		    locale: {
		        cancelLabel: '취소',
		        applyLabel: '확인',
		        format: 'YYYY/MM/DD',
		        daysOfWeek:['일','월','화','수','목','금','토'],
		        monthNames:['1월 ','2월 ','3월 ','4월 ','5월 ','6월 ','7월 ','8월 ','9월 ','10월 ','11월 ','12월 ']
		    }
		});
		
	});
	
	//확인 버튼
	$('[name="modalBtnView"]').click(function(){

		$('[name="clientId"]').val(CLIENTID);

		var tmpDate =  $('[name="daterange"]').val().replace('/','').split(' - ');
		var startDate = tmpDate[0];
		var endDate = tmpDate[1];

		var formData = {
				"startDate" : startDate,
				"endDate" : endDate
				}
		
		var viewf = $.ajax({
			cache : false,
			async: false,
			type : 'get',
			data : formData,
			url : "/main/clients/"+CLIENTID+"/logs",
			dataType : "json",
			contentType : "application/json; charset=UTF-8"
		});
		
		viewf.done(successf);

		function successf(rsdata){
			
			/*
			* 시작 ~ 끝 날짜간 사이의 날짜들 원하는 형태로 조립
			*/
			startDate = moment(startDate, 'YYYYMMDD');
			endDate = moment(endDate, 'YYYYMMDD');

		    var now = startDate.clone();
		    var dates = new Array();
		    var tmpDates = new Array();

		    while (now.isSameOrBefore(endDate)){
		        dates.push(now.format('YY-MM-DD'));
		        tmpDates.push(now.format('YYYYMMDD'));
		        now.add(1, 'days');
		    }

			// 통계 그래프에 사용될 배열값들 저장 (시작날짜, 종료날짜, 해당날짜)
			var logDate = rsdata.clientLogList;
			
			if(logDate.length < 1){
				alert('조회된 자료가 없습니다.');
				return;
			}
			
			/* 
			*	결과나온값의 API_ID 중복제거 후 리스트들 조립
			*/
			var tmpApiId = new Array();
			
			$.each(logDate, function(idx, row){
				tmpApiId[idx] = row.API_ID;
			})
			
			// 중복 제거
			var apiList = tmpApiId.filter(function(item, pos, self) {
                return self.indexOf(item) == pos;
			 });
			
			/*
			* Chart에 쓰일 Values 값들 다차원 배열로 조립
			*/
			const dataArray = Array(apiList.length).fill(null).map(() => Array());
			
			for(var i = 0; i < apiList.length; i++){
				for(var j = 0; j < tmpDates.length; j++){
					for(var k = 0; k < logDate.length; k++){
						if(apiList[i] == logDate[k].API_ID){
							if(tmpDates[j] == logDate[k].YM){
								dataArray[i][j] = logDate[k].CNT;
								break;
							}
							else{
								dataArray[i][j] = 0;
							}
						}
					}
				}
			}
						
			/*
			* 통계 그래프 스크립트
			*/
			
			// chart colors
			var colors = ['#007bff','#28a745','#333333','#c3e6cb','#dc3545','#6c757d'];
			 
			/* large line chart */
			var chLine = document.getElementById("chLine");
			var chartDatasets = new Array();
						
			for(var i = 0; i < apiList.length; i++){
				var newDataset = {
					label: apiList[i],
					data: dataArray[i],
					backgroundColor: 'transparent',
				    borderColor: colors[i % colors.length],
				    borderWidth: 2,
				    pointBackgroundColor: colors[i % colors.length]
				}
				chartDatasets.push(newDataset);
			}
			
			var config = {
				type: 'line',
				data:{
					labels: dates,
					datasets: chartDatasets
				},
				options: {
					//responsive: false,
					scales: {
						xAxes: [{
							display: true,
							scaleLabel: {
								display: true,
								labelString: '날짜'
							}
						}],
						yAxes: [{
							scaleLabel: {
								display: true,
								labelString: '사용횟수'
							},
							ticks: {
								beginAtZero: false,
								stepSize: 1
							}
						}]
					},
					legend: {
						display: true
					}
				}

			}
			if (chLine) {
			  new Chart(chLine, config);
			}
		}
		
	});
	
	
});

function clientLog(clientId) {
	CLIENTID = clientId;
	$('#submitModal').modal();
}

</script>

	<div class="wrapper">
		<!-- Main Header -->
		<%@ include file="/WEB-INF/views/include/main_header.jsp"%>
		<div class="row">
			<!-- Main Left -->
			<%@ include file="/WEB-INF/views/include/left_column.jsp"%>

			<main class="col-md-8 col-xl-9 p-t-2 mt-3">

			<div class="card border-primary mb-3" style="max-width: 100%;">
				<div class="card-header">애플리케이션 통계</div>
				<div class="card-body">
					<h4 class="card-title">${USER_ID} 님의 Application 목록</h4>
					<p class="card-text">등록한 어플리케이션의 API 사용 통계 등을 확인합니다.</p>
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
						<tr data-toggle="modal" onclick="clientLog('${row.CLIENT_ID}');">
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
						<li class="page-item"><a class="page-link" onclick='pagePre(${p.pageCnt+1},${p.pageCnt});'>«</a></li>
						<!--이전 페이지 이동 -->
						<li class="page-item"><a class="page-link" onclick='pagePre(${p.pageStartNum},${p.pageCnt});'>‹</a></li>
					</c:if>

					<!--페이지번호 -->
					<c:forEach var='i' begin="${p.pageStartNum}" end="${p.pageLastNum}" step="1">
						<li class='page-item pageIndex${i}'><a class="page-link" onclick='pageIndex(${i});'>${i}</a></li>
					</c:forEach>

					<c:if test="${p.lastChk}">
						<!--다음 페이지 이동 -->
						<li class="page-item"><a class="page-link" onclick='pageNext(${p.pageStartNum},${p.total},${p.listCnt},${p.pageCnt});'>›</a></li>
						<!--마지막 페이지 이동 -->
						<li class="page-item"><a class="page-link" onclick='pageLast(${p.pageStartNum},${p.total},${p.listCnt},${p.pageCnt});'>»</a></li>
					</c:if>

				</ul>

				<form action="./logs" method="post" id='frmPaging'>
					<!--출력할 페이지번호, 출력할 페이지 시작 번호, 출력할 리스트 갯수 -->
					<input type='hidden' name='index' id='index' value='${p.index}'> <input type='hidden' name='pageStartNum' id='pageStartNum'
						value='${p.pageStartNum}'> <input type='hidden' name='listCnt' id='listCnt' value='${p.listCnt}'>
				</form>

			</div>

			<%@ include file="/WEB-INF/views/include/client_log_modal.jsp"%> </main>
		</div>
	</div>

</body>
<%@ include file="/WEB-INF/views/include/main_footer.jsp"%>
</html>