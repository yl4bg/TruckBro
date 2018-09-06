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
		        content:formatEventDetails(e), 
		        autoMove:true, 
		        size:new AMap.Size(150,0),  
		        offset:{x:0,y:-30}
		    });  
		    windowsArr.push(infoWindow);  
		    
		    var aa = function(e){infoWindow.open(map,mar.getPosition());};  
		    AMap.event.addListener(mar,"mouseover",aa);  
		}


var staticData = {"events":[]}

function Event(dataItem) {
    var self = this;
    self.eventID = dataItem.eventID;
    self.eventType = dataItem.eventType;
    self.eventTime = formatDate(new Date(dataItem.reportTime));
    self.eventInfo = dataItem.eventInfo;
    self.longitude = dataItem.longitude;
    self.latitude = dataItem.latitude;
    self.radius = dataItem.radius;
    self.senderID = dataItem.senderID;
    self.roadNum = dataItem.roadNum;
    self.province = dataItem.province;
    self.city = dataItem.city;
    self.district = dataItem.district;
    self.upCnt = dataItem.upCnt;
    self.reportCnt = dataItem.reportCnt;
}

function EventsMapViewViewModel() {
	
	var self = this;

    self.events =  ko.observableArray($.map(staticData.events, function(item){ return new Event(item);}));    
    
    self.totalNumOfEvent = ko.computed(function() {
       return self.events().length;
    });
    
	$.getJSON("/AServer/getAllEventsForAllUsers", 
		function(allData) {
		var mappedEvents = $.map(allData.events, function(item){ return new Event(item); })
		self.events(mappedEvents);
		var eventsArray = self.events();
//		console.log(eventsArray);
		for (var i=0;i<eventsArray.length;i++)
		{
			if(eventsArray[i].longitude > 0){
				addmarker(0, eventsArray[i]);
			}
		}
	    map.setFitView();
	});
	
}

ko.applyBindings(new EventsMapViewViewModel());

function formatEventDetails(e){
	return "类型: "+ e.eventType + '<br>'
	 + "时间: "+ e.eventTime + '<br>'
	 + "描述: "+ e.eventInfo + '<br>'
	 + "维度: "+ e.longitude + '<br>'
	 + "经度: "+ e.latitude + '<br>'
	 + "半径: "+ e.radius + '<br>'
	 + "发布人: "+ e.senderID + '<br>'
	 + "公路: "+ e.roadNum + '<br>'
	 + "省: "+ e.province + '<br>'
	 + "市: "+ e.city + '<br>'
	 + "县: "+ e.district + '<br>'
	 + "赞: "+ e.upCnt + '<br>'
	 + "举报: "+ e.reportCnt;
}

function formatDate(d){
	return d.getFullYear() + "-" + (d.getMonth()+1) + "-" +
			d.getDate() + " " + d.getHours() + ":" + 
			d.getMinutes() + ":" + d.getSeconds();
}
