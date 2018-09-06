<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <title>登陆页面</title>
</head>
<body>

<h2>登陆进入TruckApp</h2>
<form:form method="POST" action="/AServer/submitLogin" modelAttribute="userObj">
   <table>

    <tr>
        <td><form:label path="userID">用户ID(手机号):</form:label></td>
        <td><form:input path="userID" /></td>
    </tr>
    <tr>
        <td><form:label path="pwdhash">密码:</form:label></td>
        <td><form:password path="pwdhash" /></td>
    </tr>

    <tr>
        <td colspan="2">
            <input type="submit" value="提交"/>
        </td>
    </tr>
</table>  
</form:form>

</body>
</html>