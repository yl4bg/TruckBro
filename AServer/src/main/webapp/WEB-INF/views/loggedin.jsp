<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
<title>登陆成功!</title>
</head>
<body>

	<h2>您成功地登陆了</h2>
	<form:form method="POST" action="/AServer/logout">
		<table>

			<tr>
				<td>根据我们的记录显示，您是:</td>
				<td>${userObj.username}</td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="登出" /></td>
			</tr>
		</table>
	</form:form>

	<form:form method="GET" action="/AServer/newEvent">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="提交新路况" /></td>
			</tr>
		</table>
	</form:form>
	
	<form:form method="GET" action="/AServer/bulkNewEvents">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="批量上传路况" /></td>
			</tr>
		</table>
	</form:form>
	
	<form:form method="GET" action="/AServer/manageEvents">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="管理路况" /></td>
			</tr>
		</table>
	</form:form>
	
	<form:form method="GET" action="/AServer/eventsMapView">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="路况地图模式" /></td>
			</tr>
		</table>
	</form:form>

	<form:form method="GET" action="/AServer/manageUsers">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="管理用户" /></td>
			</tr>
		</table>
	</form:form>
	
	<form:form method="GET" action="/AServer/usersMapView">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="用户地图模式" /></td>
			</tr>
		</table>
	</form:form>
	
	<form:form method="GET" action="http://lbs.amap.com/console/show/picker" target="_blank">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="去高德地图" /></td>
			</tr>
		</table>
	</form:form>
	
</body>
</html>