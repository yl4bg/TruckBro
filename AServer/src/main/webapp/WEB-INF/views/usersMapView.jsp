<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.11.2.min.js" />"></script>
		<script type="text/javascript" src="<c:url value="/resources/js/knockout-3.3.0.js" />"></script>
    	
<title>路况地图模式</title>
<style type="text/css">
		body{
			margin:0;
			height:100%;
			width:100%;
			position:absolute;
		}
		#mapContainer{
			position: absolute;
			height:95vh;
			width:100vw;
			top:50;
			left:50;
			right:50;
			bottom:50;
		}
		#form input[type="button"]{
			margin-top:10px;
			background-color: #0D9BF2;
			height:25px;
			text-align:center;
			line-height:25px;
			color:#fff;
			font-size:12px;
			border-radius:3px;
			outline: none;
			border:0;
			cursor:pointer;
		}
	</style>
</head>
<body>
		<h3>共有<span data-bind="text: $root.totalNumOfUser"></span>个用户。鼠标移动至单个用户显示详情。</h3>
		<form:form method="GET" action="/AServer/login">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="返回主页" /></td>
			</tr>
		</table>
		</form:form>
		
		<div id="mapContainer"></div>
		
		<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=0b663f05066d916119d25cb3533c9cb6"></script>
		<script type="text/javascript">
			var marker = new Array();
			var windowsArr = new Array(); 
			var map = new AMap.Map("mapContainer", {
				resizeEnable: true
			});
		</script> 
		<script type="text/javascript" src="<c:url value="/resources/js/usersMapViewKnockOut.js" />"></script>
		
</body>
</html>