<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
 	<link rel="stylesheet" href="<c:url value="/resources/css/pure-min.css" />">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>管理路况</title>
		<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.11.2.min.js" />"></script>
		<script type="text/javascript" src="<c:url value="/resources/js/knockout-3.3.0.js" />"></script>
		
</head>
	<body>
		<h3>共有<span data-bind="text: $root.totalNumOfEvent"></span>个路况。点击单个路况显示详情。</h3>
		<form:form method="GET" action="/AServer/login">
		<table>
			<tr></tr>
			<tr>
				<td colspan="2"><input type="submit" value="返回主页" /></td>
			</tr>
		</table>
		</form:form>
		<p>
		</p>
		  <table id="table1" class="pure-table pure-table-horizontal" data-bind="with: $root.showingAllEvents">
					<thead>
					<tr>
					<th>
						<label>搜索路况</label>
					</th>
					<th>
						<label>类型:</label>
					</th>
					<th>
						<label> 省:</label>
					</th>
					<th>
						<label> 市:</label>
					</th>
					<th>
						<label> 县:</label>
					</th>
					<th>
						<label> 公路:</label>
					</th>
					<th>	
						<label> 被举报</label>
					</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					</tr>
					<tr>
					<th>
					</th>
					<th>
						<select data-bind="options: $root.availableTypes, value: $root.selectedType"/>
					</th>
					<th>
						<input data-bind="value: $root.selectedProvince" size="6"/>
					</th>
					<th>
						<input data-bind="value: $root.selectedCity" size="6"/>
					</th>
					<th>
						<input data-bind="value: $root.selectedDistrict" size="6"/>
					</th>
					<th>
						<input data-bind="value: $root.selectedRoad" size="8"/>
					</th>
					<th>
						<select data-bind="options: $root.availableReportTypes, value: $root.selectedReportType"/>
					</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					</tr>
					<tr>
						<th>
							路况类型
						</th>
						<th>
							发布时间
						</th>
						<th>
							路况描述
						</th>
						<th>
							半径
						</th>
						<th>
							发布者ID
						</th>
						<th>
							公路编号
						</th>
						<th>
							省
						</th>
						<th>
							市
						</th>
						<th>
							县
						</th>
						<th>
							点赞
						</th>
						<th>
							举报
						</th>
						<th>
						</th>
					</tr></thead>
					<tbody data-bind="foreach: $root.events">
						<tr data-bind="click: $root.goToEvent, visible: $root.eventVisible( $data )">
							<td data-bind="text: eventType"></td>
							<td data-bind="text: eventTime"></td>
							<td data-bind="text: eventInfo"></td>
							<td data-bind="text: radius"></td>
							<td data-bind="text: senderID"></td>
							<td data-bind="text: roadNum"></td>
							<td data-bind="text: province"></td>
							<td data-bind="text: city"></td>
							<td data-bind="text: district"></td>
							<td data-bind="text: upCnt"></td>
							<td data-bind="text: reportCnt"></td>
							<td><a href="#" data-bind="click: $root.removeEvent">删除</a></td>
						</tr>
					</tbody>
			</table>
			
		<div data-bind="with: chosenEvent">
			<h2>路况详情</h2>
	        <p><label>类型</label>: <span data-bind="text: eventType"></span></p>
	        <p><label>上传时间</label>: <span data-bind="text: eventTime"></span></p>
	        <p><label>描述</label>: <span data-bind="text: eventInfo"></span></p>
	        <p><label>发布者信息</label>: 
	        	<ul data-bind="sender">
    				<li data-bind="text: sender.userID"></li>
    				<li data-bind="text: sender.userName"></li>
    				<li data-bind="text: sender.points"></li>
    				<li data-bind="text: sender.longitude"></li>
    				<li data-bind="text: sender.latitude"></li>
    				<li data-bind="text: sender.reportTime"></li>
				</ul>
	        </p>
	        <p><label>点赞人</label>:
	        	<ul data-bind="foreach : upUsers">
    				<li data-bind="text: userIdentity"></li>
				</ul>
	        </p>
	        <p><label>举报人</label>:
	        	<ul data-bind="foreach : reportUsers">
    				<li data-bind="text: userIdentity"></li>
				</ul>
	        </p>
	        <p><label>路况图片</label>:
	        	<ul data-bind="foreach : picIds">
    				<li>
    					<img data-bind="attr:{src: $data}" style="width:204px;height:128px"/>
    				</li>
				</ul>
	        </p>
	        <button data-bind="click: $root.goBackToEvents">返回路况列表</button>
		</div>

		<script type="text/javascript" src="<c:url value="/resources/js/manageEventsKnockOut.js" />"></script>
		<form:form method="GET" action="/AServer/login">
		<table>
			<tr></tr>
			<tr>
				<td colspan="2"><input type="submit" value="返回主页" /></td>
			</tr>
		</table>
		</form:form>
	</body>
</html>