<%@ page language="java" contentType="text/html; charset=UTF-8"

pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>下载首页</title>
<script type="text/javascript">
var is_weixin = (function(){return navigator.userAgent.toLowerCase().indexOf('MicroMessenger') !== -1})();
window.onload = function() {
	var winHeight = typeof window.innerHeight != 'undefined' ? window.innerHeight : document.documentElement.clientHeight; //兼容IOS，不需要的可以去掉
	var btn = document.getElementById('J_weixin');
	var tip = document.getElementById('weixin-tip');
	var close = document.getElementById('close');
	if (is_weixin) {
		btn.onclick = function(e) {
			tip.style.height = winHeight + 'px';
			tip.style.display = 'block';
			return false;
		}
		close.onclick = function() {
			tip.style.display = 'none';
		}
	}
}
function init(){
	var is_weixin = (function(){return navigator.userAgent.toLowerCase().indexOf('MicroMessenger') !== -1})();
	var winHeight = typeof window.innerHeight != 'undefined' ? window.innerHeight : document.documentElement.clientHeight; //兼容IOS，不需要的可以去掉
	var btn = document.getElementById('J_weixin');
	var tip = document.getElementById('weixin-tip');
	var close = document.getElementById('close');
	if (is_weixin) {
		btn.onclick = function(e) {
			tip.style.height = winHeight + 'px';
			tip.style.display = 'block';
			return false;
		}
		close.onclick = function() {
			tip.style.display = 'none';
		}
	}
}
</script>
</head>
<body onload="init();">

	<center>
		<h2>欢迎使用卡车俱乐部APP</h2>
		<h2>
			<a href="/AServer/download.do">点击下载</a>
		</h2>
	</center>
</body>
</html>