<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
<title>创建新账号页面</title>
</head>
<body>

	<h2>创建新账号</h2>
	<form:form method="POST" action="/AServer/submitNewAccount" modelAttribute="userObj">
		<table>
			<tr>
				<td><form:label path="userID">用户ID(电话号):</form:label></td>
				<td><form:input path="userID" /></td>
			</tr>
			<tr>
				<td><form:label path="username">用户名:</form:label></td>
				<td><form:input path="username" /></td>
			</tr>
			<tr>
				<td><form:label path="pwdhash">密码:</form:label></td>
				<td><form:password path="pwdhash" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="提交" /></td>
			</tr>
		</table>
	</form:form>
</body>
</html>