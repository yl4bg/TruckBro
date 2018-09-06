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
    	<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=0b663f05066d916119d25cb3533c9cb6"></script>
		<script type="text/javascript">
		
		function geocoder(line, addrQueryStr) {
			console.log('querying address: ' + addrQueryStr);
		    var MGeocoder;
		    //加载地理编码插件
		    AMap.service(["AMap.Geocoder"], function() {        
		        MGeocoder = new AMap.Geocoder({
		            radius:1000000 //范围，默认：500
		        });
		        //返回地理编码结果  
		        //地理编码
		        MGeocoder.getLocation(addrQueryStr, function(status, result){
		        	if(status === 'complete' && result.info === 'OK'){
		        		geocoder_CallBack(line, result);
		        	}
		        });
		    });
		}
		
		function reversegeocoder(line, longitude, latitude){
			console.log('querying LngLat coors: ' + longitude + ' ' + latitude);
			var lnglatXY = new AMap.LngLat(parseFloat(longitude), parseFloat(latitude));
		    var MGeocoder;
		    //加载地理编码插件
		    AMap.service(["AMap.Geocoder"], function() {        
		        MGeocoder = new AMap.Geocoder({
		            radius:1000000 //范围，默认：500
		        });
		        //返回地理编码结果  
		        //地理编码
		        MGeocoder.getAddress(lnglatXY, function(status, result){
		        	if(status === 'complete' && result.info === 'OK'){
		        		geocoder_CallBackReverse(line, result);
		        	}
		        });
		    });
		}
		
		//地理编码返回结果展示   
		function geocoder_CallBack(line, data){
		    //地理编码结果数组
		    var geocode = new Array();
		    geocode = data.geocodes;  
		    for (var i = 0; i < geocode.length; i++) {
		        //拼接输出html
		        //resultStr += "<span style=\"font-size: 12px;padding:0px 0 4px 2px; border-bottom:1px solid #C1FFC1;\">"+"<b>地址</b>："+geocode[i].formattedAddress+""+ "<b>&nbsp;&nbsp;&nbsp;&nbsp;坐标</b>：" + geocode[i].location.getLng() +", "+ geocode[i].location.getLat() +""+ "<b>&nbsp;&nbsp;&nbsp;&nbsp;匹配级别</b>：" + geocode[i].level +"</span>";   
		        var lng = geocode[i].location.getLng();
		        var lat = geocode[i].location.getLat()
		        reversegeocoder(lng+"~~~"+lat+"~~~"+line, lng, lat);
		    }
		} 
		
		//地理编码返回结果展示   
		function geocoder_CallBackReverse(line, data){
		    //地理编码结果数组
		    var regeocode = data.regeocode;
		    //拼接输出html
		    var province = regeocode.addressComponent.province;
		    var city = regeocode.addressComponent.city;
		    var district = regeocode.addressComponent.district;
		    var roadNum = regeocode.addressComponent.street;
		    sendLineToServer(line+"~~~"+province+"~~~"+city+"~~~"+district+"~~~"+roadNum);
		}
	</script> 
<title>批量上传路况</title>
</head>
<body>
		<h2>批量上传路况</h2>
		<h4>复制多个路况至输入框。(每行格式为: 类型 地址 描述)</h4>
		<h4>注意：类型和地址里均不能有空格。</h4>
		<p>
			<textarea data-bind="value: $root.eventsText" cols="100" rows="25"></textarea>
			<br>
			<span data-bind="text: $root.errorMsg" style="color:red"></span>
			<br>
			<button data-bind="click: $root.uploadEvents">上传所有路况</button>
		</p>

		<script type="text/javascript" src="<c:url value="/resources/js/bulkNewEventsKnockOut.js" />"></script>
		<form:form method="GET" action="/AServer/login">
		<table>
			<tr>
				<td colspan="2"><input type="submit" value="返回主页" /></td>
			</tr>
		</table>
		</form:form>
</body>
</html>