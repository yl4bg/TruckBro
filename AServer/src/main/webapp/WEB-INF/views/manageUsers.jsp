<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
 	<link rel="stylesheet" href="<c:url value="/resources/css/pure-min.css" />">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>管理用户</title>
		<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.11.2.min.js" />"></script>
		<script type="text/javascript" src="<c:url value="/resources/js/knockout-3.3.0.js" />"></script>
		
</head>
	<body>
		<h3>共有<span data-bind="text: $root.totalNumOfUser"></span>名用户。上帝账号不可被改动。提升用户权限最多提升至完全。</h3>
		<div data-bind="visible: showingAllUsers!=null">
			<input data-bind="value: pushMsg" placeholder="输入消息" />
			<button data-bind="click: pushToAllUsers" >推送给所有用户</button>
		</div>
		<br>
		  <table id="table1" class="pure-table pure-table-horizontal" data-bind="with: $root.showingAllUsers">
					<thead>
					<tr>
						<th>
							用户ID（手机号）
						</th>
						<th>
							用户名
						</th>
						<th>
							积分
						</th>
						<th>
							权限
						</th>
						<th>
							位置时间
						</th>
						<th>
						</th>
						<th>
						</th>
						<th>
						</th>
					</tr></thead>
					<tbody data-bind="foreach: $root.users">
						<tr>
							<td data-bind="text: userID"></td>
							<td><input data-bind="value: userName, enable: privilege!='上帝'"/></td>
							<td><input data-bind="value: points, enable: privilege!='上帝'"/></td>
							<td><select data-bind="options: $root.availablePrivileges, value: privilege, optionsText: $data", disabled: privilege=='上帝'/></td>
							<td data-bind="text: reportTime"></td>
							<td><input data-bind="value: pushMsg" placeholder="输入消息"/><br><a href="#" data-bind="click: $root.pushToUser">推送给用户</a></td>
							<td><a href="#" data-bind="click: $root.modifyUser, visible: privilege!='上帝'">保存修改</a></td>
							<td><a href="#" data-bind="click: $root.goToUser">详细资料</a></td>
						</tr>
					</tbody>
			</table>
			
			<div data-bind="with: chosenUser">
				<h2>用户详情</h2>
		        <p><label>昵称</label>: <span data-bind="text: nickName"></span></p>
		        <p><label>家乡</label>: <span data-bind="text: homeTown"></span></p>
		        <p><label>头像</label>: <img data-bind="attr:{src: portrait}" style="width:102px;height:64px"/></p>
		        <p><label>签名</label>: <span data-bind="text: signature"></span></p>
		        <p><label>我的卡车</label>: <span data-bind="text: myTruck"></span></p>
		        <p><label>我的卡车照片</label>: <img data-bind="attr:{src: myTruckPicId}" style="width:204px;height:128px"/></p>
		        <p><label>购买时间</label>: <span data-bind="text: boughtTime"></span></p>
		        <p><label>驾照照片</label>: <img data-bind="attr:{src: driverLicensePic1}" style="width:204px;height:128px"/>
		        <img data-bind="attr:{src: driverLicensePic2}" style="width:204px;height:128px"/></p>
		        <p><label>行驶证照片</label>: <img data-bind="attr:{src: registrationPic1}" style="width:204px;height:128px"/>
		        <img data-bind="attr:{src: registrationPic2}" style="width:204px;height:128px"/></p>
		        <p><label>货物类型</label>: 
					<ul data-bind="foreach : goodTypeList">
    					<li data-bind="text: $data"></li>
					</ul>
				</p>
				<p><label>常跑地</label>: 
					<ul data-bind="foreach : frequentPlaceList">
    					<li data-bind="text: $data"></li>
					</ul>
				</p>
		        <button data-bind="click: $root.goBackToUsers">返回用户列表</button>
			</div>
			
		<script type="text/javascript" src="<c:url value="/resources/js/manageUsersKnockOut.js" />"></script>
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