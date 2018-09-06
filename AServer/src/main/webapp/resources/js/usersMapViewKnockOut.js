function addmarker(i, e) {
		    var lngX = e.longitude;
		    var latY = e.latitude;
		    var markerOption = {
		        map:map,                 
//		        icon:"http://webapi.amap.com/images/"+(i+1)+".png",  
		        position:new AMap.LngLat(lngX, latY)
		    };            
		    var mar = new AMap.Marker(markerOption);  
		    marker.push(new AMap.LngLat(lngX, latY));
		
		    var infoWindow = new AMap.InfoWindow({  
		        content:formatUserDetails(e), 
		        autoMove:true, 
		        size:new AMap.Size(150,0),  
		        offset:{x:0,y:-30}
		    });  
		    windowsArr.push(infoWindow);  
		    
		    var aa = function(e){infoWindow.open(map,mar.getPosition());};  
		    AMap.event.addListener(mar,"mouseover",aa);  
		}


var staticData = {"users":[]}

function User(dataItem) {
    var self = this;
    self.userID = dataItem.userID;
    self.userName = dataItem.userName;
    self.points = dataItem.points;
    self.privilege = dataItem.privilege;
    self.longitude = dataItem.longitude;
    self.latitude = dataItem.latitude;
    self.reportTime = formatDate(new Date(dataItem.reportTime));
    self.deviceID = dataItem.deviceID;
    self.cookie = dataItem.cookie;
    self.pushMsg = "";
}

function UsersMapViewViewModel() {
	
	var self = this;

    self.users =  ko.observableArray($.map(staticData.users, function(item){ return new User(item);}));    
    
    self.totalNumOfUser = ko.computed(function() {
       return self.users().length;
    });
    
	$.getJSON("/AServer/getAllUsers", 
		function(allData) {
		var mappedUsers = $.map(allData.users, function(item){ return new User(item); })
		self.users(mappedUsers);
		var usersArray = self.users();

		for (var i=0;i<usersArray.length;i++)
		{
			if(usersArray[i].longitude > 0){
				addmarker(0, usersArray[i]);
			}
		}
	    map.setFitView();
	});
	
}

ko.applyBindings(new UsersMapViewViewModel());

function formatUserDetails(u){
	return "手机号: "+ u.userID + '<br>'
	 + "姓名: "+ u.userName + '<br>'
	 + "点数: "+ u.points + '<br>'
	 + "等级: "+ u.privilege + '<br>'
	 + "纬度: "+ u.longitude + '<br>'
	 + "经度: "+ u.latitude + '<br>'
	 + "位置时间: "+ u.reportTime + '<br>'
	 + "通知ID: "+ u.deviceID + '<br>'
	 + "饼干: "+ u.cookie;
}

function formatDate(d){
	return d.getFullYear() + "-" + (d.getMonth()+1) + "-" +
			d.getDate() + " " + d.getHours() + ":" + 
			d.getMinutes() + ":" + d.getSeconds();
}
