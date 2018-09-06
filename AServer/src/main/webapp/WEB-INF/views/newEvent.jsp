<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>路况页面</title>
	<style type="text/css">
		body{
			margin:0;
			height:100%;
			width:100%;
			position:absolute;
		}
		#mapContainer{
			position: absolute;
			top:50;
			left:280;
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
		
		#tip{
            background-color:#fff;
            border:1px solid #ccc;
            padding-left:10px;
            padding-right:2px;
            position:absolute;
            min-height:65px;
            top:10px;
            font-size:12px;
            right:10px;
            border-radius:3px;
            overflow:hidden;
            line-height:20px;
            min-width:400px;
        }
        #tip input[type="button"]{
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
        #tip input[type="text"]{
            height:25px;
            border:1px solid #ccc;
            padding-left:5px;
            border-radius:3px;
            outline:none;
        }
        #pos{
            height: 70px;
            background-color: #fff;
            padding-left: 10px;
            padding-right: 10px;
            position:absolute;
            font-size: 12px;
            right: 10px;
            bottom: 30px;
            border-radius: 3px;
            line-height: 30px;
            border:1px solid #ccc;
        }
        #pos input{
            border:1px solid #ddd;
            height:23px;
            border-radius:3px;
            outline:none;
        }
 
        #result1{
            max-height:300px;
        }
	</style>
</head>  
<body>
<h2>创建新路况</h2><div id="form">
<form:form method="POST" action="/AServer/submitEvent" modelAttribute="eventObj">
   <table>
    <tr>
        <td><label >完整地址:</label></td>
        <td><input id="addrInput" /></td>
    </tr>
    <tr>
        <td><form:label path="eventType">事件类型:</form:label></td>
        <td><form:select path="eventType">
        		<option>拥堵</option>
        		<option>事故</option>
        		<option>检查</option>
        		<option>限行</option>
        		<option>施工</option>
        		<option>其他</option>
        	</form:select>
        </td>
        <td id="mapContainer" rowspan="13"></td>
        <td id="tip">
	        <b>请输入关键字：</b>
	        <input type="text" id="keyword" name="keyword" value="" onkeydown='keydown(event)' style="width: 95%;"/>
	        <div id="result1" name="result1"></div>
	    </td>
	    <td id="pos">
	        <b>鼠标左键在地图上单击获取坐标</b>
	        <br><div>X：<input type="text" id="lngX" name="lngX" value=""/>&nbsp;Y：<input type="text" id="latY" name="latY" value=""/></div>
	    </td>
    </tr>
    <tr>
        <td><form:label path="longitude">经度:</form:label></td>
        <td><form:input path="longitude" id="longitudeInput"/></td>
    </tr>
    <tr>
        <td><form:label path="latitude">维度:</form:label></td>
        <td><form:input path="latitude" id="latitudeInput"/></td>
    </tr>
	<tr>
        <td><form:label path="radius">半径(公里):</form:label></td>
        <td><form:input path="radius" /></td>
    </tr>
    <tr>
        <td><form:label path="occurTime">发现时间:</form:label></td>
        <td><form:input path="occurTime" /></td>
    </tr>
    <tr>
        <td><form:label path="eventInfo">事件信息:</form:label></td>
        <td><form:input path="eventInfo" /></td>
    </tr>
    <tr>
        <td><form:label path="roadNum">道路:</form:label></td>
        <td><form:input path="roadNum" /></td>
    </tr>
    <tr>
        <td><form:label path="province">省:</form:label></td>
        <td><form:input path="province" id="provinceForm" /></td>
    </tr>
    <tr>
        <td><form:label path="city">市:</form:label></td>
        <td><form:input path="city" id="cityForm" /></td>
    </tr>
    <tr>
        <td><form:label path="district">县:</form:label></td>
        <td><form:input path="district" id="districtForm" /></td>
    </tr>
    <tr>
        <td colspan="1">
            <input type="button" value="地址到经纬" 
        	onclick="geocoder(document.getElementById('addrInput').value)"/>
        </td>
        <td colspan="1">
            <input type="button" value="经纬到地址" 
        	onclick="reversegeocoder(document.getElementById('longitudeInput').value, document.getElementById('latitudeInput').value)"/>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="提交"/>
        </td>
    </tr>
</table>  
</form:form>
</div>
	<form:form method="GET" action="/AServer/login">
			<table>
				<tr></tr>
				<tr>
					<td colspan="2"><input type="submit" value="返回主页" /></td>
				</tr>
			</table>
	</form:form>
		
	<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=0b663f05066d916119d25cb3533c9cb6"></script>
	<script type="text/javascript">
		var marker = new Array();
		var windowsArr = new Array(); 
		var mapObj = new AMap.Map("mapContainer", {
			resizeEnable: true,
			view: new AMap.View2D({
                resizeEnable: true
            }),
            keyboardEnable:false
		});
		var clickEventListener=AMap.event.addListener(mapObj,'click',function(e){
            document.getElementById("longitudeInput").value=e.lnglat.getLng();
            document.getElementById("latitudeInput").value=e.lnglat.getLat();
            document.getElementById("lngX").value=e.lnglat.getLng();
            document.getElementById("latY").value=e.lnglat.getLat();
        });
		document.getElementById("keyword").onkeyup = keydown;
		
		function geocoder(addrQueryStr) {
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
		        		geocoder_CallBack(result);
		        		var geocodeLocal = new Array();
		    		    geocodeLocal = result.geocodes;
		    		    reversegeocoder(geocodeLocal[0].location.getLng(),
		    		    		geocodeLocal[0].location.getLat());
		        		
		        	}
		        });
		    });
		}
		function reversegeocoder(longitude, latitude){
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
			        	/* console.log('callback function entered'); */
		        		geocoder_CallBackReverse(result);
		        	}
		        });
		    });
		}
		
		//地理编码返回结果展示   
		function geocoder_CallBack(data){
		    //var resultStr="";
		    //地理编码结果数组
		    var geocode = new Array();
		    geocode = data.geocodes;  
		    for (var i = 0; i < geocode.length; i++) {
		        //拼接输出html
		        //resultStr += "<span style=\"font-size: 12px;padding:0px 0 4px 2px; border-bottom:1px solid #C1FFC1;\">"+"<b>地址</b>："+geocode[i].formattedAddress+""+ "<b>&nbsp;&nbsp;&nbsp;&nbsp;坐标</b>：" + geocode[i].location.getLng() +", "+ geocode[i].location.getLat() +""+ "<b>&nbsp;&nbsp;&nbsp;&nbsp;匹配级别</b>：" + geocode[i].level +"</span>";   
		        addmarker(i, geocode[i]);
			    document.getElementById("longitudeInput").value = geocode[i].location.getLng();  
			    document.getElementById("latitudeInput").value =geocode[i].location.getLat();
		    }
		    map.setFitView();
		} 
		//地理编码返回结果展示   
		function geocoder_CallBackReverse(data){
		    //var resultStr="";
		    //地理编码结果数组
		    var regeocode = data.regeocode;
		        //拼接输出html
		    document.getElementById("provinceForm").value = regeocode.addressComponent.province;
		    document.getElementById("cityForm").value = regeocode.addressComponent.city;
		    document.getElementById("districtForm").value = regeocode.addressComponent.district;
		    //addmarker(0, regeocode);
		    //map.setFitView();   
		    //document.getElementById("geoLocationResult").innerHTML = resultStr;  
		}
		
		//输入提示
        function autoSearch() {
            var keywords = document.getElementById("keyword").value;
            var auto;
            //加载输入提示插件
                AMap.service(["AMap.Autocomplete"], function() {
                var autoOptions = {
                    city: "" //城市，默认全国
                };
                auto = new AMap.Autocomplete(autoOptions);
                //查询成功时返回查询结果
                if ( keywords.length > 0) {
                    auto.search(keywords, function(status, result){
                        autocomplete_CallBack(result);
                    });
                }
                else {
                    document.getElementById("result1").style.display = "none";
                }
            });
        }
 
        //输出输入提示结果的回调函数
        function autocomplete_CallBack(data) {
            var resultStr = "";
            var tipArr = data.tips;
            if (tipArr&&tipArr.length>0) {
                for (var i = 0; i < tipArr.length; i++) {
                    resultStr += "<div id='divid" + (i + 1) + "' onmouseover='openMarkerTipById(" + (i + 1)
                                + ",this)' onclick='selectResult(" + i + ")' onmouseout='onmouseout_MarkerStyle(" + (i + 1)
                                + ",this)' style=\"font-size: 13px;cursor:pointer;padding:5px 5px 5px 5px;\"" + "data=" + tipArr[i].adcode + ">" + tipArr[i].name + "<span style='color:#C1C1C1;'>"+ tipArr[i].district + "</span></div>";
                }
            }
            else  {
                resultStr = " π__π 亲,人家找不到结果!<br />要不试试：<br />1.请确保所有字词拼写正确<br />2.尝试不同的关键字<br />3.尝试更宽泛的关键字";
            }
            document.getElementById("result1").curSelect = -1;
            document.getElementById("result1").tipArr = tipArr;
            document.getElementById("result1").innerHTML = resultStr;
            document.getElementById("result1").style.display = "block";
        }
 
        //输入提示框鼠标滑过时的样式
        function openMarkerTipById(pointid, thiss) {  //根据id打开搜索结果点tip
            thiss.style.background = '#CAE1FF';
        }
 
        //输入提示框鼠标移出时的样式
        function onmouseout_MarkerStyle(pointid, thiss) {  //鼠标移开后点样式恢复
            thiss.style.background = "";
        }
 
        //从输入提示框中选择关键字并查询
        function selectResult(index) {
            if(index<0){
                return;
            }
            if (navigator.userAgent.indexOf("MSIE") > 0) {
                document.getElementById("keyword").onpropertychange = null;
                document.getElementById("keyword").onfocus = focus_callback;
            }
            //截取输入提示的关键字部分
            var text = document.getElementById("divid" + (index + 1)).innerHTML.replace(/<[^>].*?>.*<\/[^>].*?>/g,"");
            var cityCode = document.getElementById("divid" + (index + 1)).getAttribute('data');
            document.getElementById("keyword").value = text;
            document.getElementById("result1").style.display = "none";
            //根据选择的输入提示关键字查询
            mapObj.plugin(["AMap.PlaceSearch"], function() {
                var msearch = new AMap.PlaceSearch();  //构造地点查询类
                AMap.event.addListener(msearch, "complete", placeSearch_CallBack); //查询成功时的回调函数
                msearch.setCity(cityCode);
                msearch.search(text);  //关键字查询查询
            });
        }
 
        //定位选择输入提示关键字
        function focus_callback() {
            if (navigator.userAgent.indexOf("MSIE") > 0) {
                document.getElementById("keyword").onpropertychange = autoSearch;
           }
        }
 
        //输出关键字查询结果的回调函数
        function placeSearch_CallBack(data) {
            //清空地图上的InfoWindow和Marker
            windowsArr = [];
            marker     = [];
            mapObj.clearMap();
            var resultStr1 = "";
            var poiArr = data.poiList.pois;
            var resultCount = poiArr.length;
            for (var i = 0; i < resultCount; i++) {
                resultStr1 += "<div id='divid" + (i + 1) + "' onmouseover='openMarkerTipById1(" + i + ",this)' onmouseout='onmouseout_MarkerStyle(" + (i + 1) + ",this)' style=\"font-size: 12px;cursor:pointer;padding:0px 0 4px 2px; border-bottom:1px solid #C1FFC1;\"><table><tr><td><img src=\"http://webapi.amap.com/images/" + (i + 1) + ".png\"></td>" + "<td><h3><font color=\"#00a6ac\">名称: " + poiArr[i].name + "</font></h3>";
                    resultStr1 += TipContents(poiArr[i].type, poiArr[i].address, poiArr[i].tel) + "</td></tr></table></div>";
                    addmarker(i, poiArr[i]);
            }
            mapObj.setFitView();
        }
 
        //鼠标滑过查询结果改变背景样式，根据id打开信息窗体
        function openMarkerTipById1(pointid, thiss) {
            thiss.style.background = '#CAE1FF';
            windowsArr[pointid].open(mapObj, marker[pointid]);
        }
 
        //添加查询结果的marker&infowindow
        function addmarker(i, d) {
            var lngX = d.location.getLng();
            var latY = d.location.getLat();
            var markerOption = {
                map:mapObj,
                icon:"http://webapi.amap.com/images/" + (i + 1) + ".png",
                position:new AMap.LngLat(lngX, latY)
            };
            var mar = new AMap.Marker(markerOption);
            marker.push(new AMap.LngLat(lngX, latY));
 
            var infoWindow = new AMap.InfoWindow({
                content:"<h3><font color=\"#00a6ac\">  " + (i + 1) + ". " + d.name + "</font></h3>" + TipContents(d.type, d.address, d.tel),
                size:new AMap.Size(300, 0),
                autoMove:true,
                offset:new AMap.Pixel(0,-30)
            });
            windowsArr.push(infoWindow);
            var aa = function (e) {infoWindow.open(mapObj, mar.getPosition());};
            AMap.event.addListener(mar, "mouseover", aa);
        }
 
        //infowindow显示内容
        function TipContents(type, address, tel) {  //窗体内容
            if (type == "" || type == "undefined" || type == null || type == " undefined" || typeof type == "undefined") {
                type = "暂无";
            }
            if (address == "" || address == "undefined" || address == null || address == " undefined" || typeof address == "undefined") {
                address = "暂无";
            }
            if (tel == "" || tel == "undefined" || tel == null || tel == " undefined" || typeof address == "tel") {
                tel = "暂无";
            }
            var str = "  地址：" + address + "<br />  电话：" + tel + " <br />  类型：" + type;
            return str;
        }
        function keydown(event){
            var key = (event||window.event).keyCode;
            var result = document.getElementById("result1")
            var cur = result.curSelect;
            if(key===40){//down
                if(cur + 1 < result.childNodes.length){
                    if(result.childNodes[cur]){
                        result.childNodes[cur].style.background='';
                    }
                    result.curSelect=cur+1;
                    result.childNodes[cur+1].style.background='#CAE1FF';
                    document.getElementById("keyword").value = result.tipArr[cur+1].name;
                }
            }else if(key===38){//up
                if(cur-1>=0){
                    if(result.childNodes[cur]){
                        result.childNodes[cur].style.background='';
                    }
                    result.curSelect=cur-1;
                    result.childNodes[cur-1].style.background='#CAE1FF';
                    document.getElementById("keyword").value = result.tipArr[cur-1].name;
                }
            }else if(key === 13){
                var res = document.getElementById("result1");
                if(res && res['curSelect'] !== -1){
                    selectResult(document.getElementById("result1").curSelect);
                }
            }else{
                autoSearch();
            }
        }
	</script> 
</body>
</html>