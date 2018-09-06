var staticData = {"events":[]}

if (typeof String.prototype.contains != 'function') {
	  String.prototype.contains = function (str){
	    return this.indexOf(str) > -1;
	  };
	}

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

function EventDetail(data) {
    var self = this;
    self.eventType = data.eventType;
    self.eventTime = formatDate(new Date(data.reportTime));
    self.eventInfo = data.eventInfo;
    self.sender = new Sender(data.sender);
    self.upUsers = $.map(data.upUsers, function(item) { return new ShortUser(item); });
    self.reportUsers = $.map(data.reportUsers, function(item) { return new ShortUser(item); });
    self.picIds = data.picIds; 
}

function Sender(sender){
	var self = this;
    self.userID = "用户ID: " + sender.userID;
    self.userName = "用户名: " + sender.userName;
    self.points = "积分: " + sender.points;
    self.longitude = "位置经度: " + sender.longitude;
    self.latitude = "位置纬度: " + sender.latitude;
    self.reportTime = "位置上传时间: " + formatDate(new Date(sender.reportTime));
}

function ShortUser(shortUser){
	var self = this;
    self.userIdentity = "用户ID: " + shortUser.userID + "。 用户名: " + shortUser.userName;
}

function EventsViewModel() {
    var self = this;

    self.availableTypes = ["拥堵", "限行", "检查", "事故", "其他", "施工", "求助", "全部"];
    self.availableReportTypes = ["全部", "被举报的", "未被举报的"];
    
    self.selectedType = ko.observable("全部");
    self.selectedReportType = ko.observable("全部");
    self.selectedProvince = ko.observable("");
    self.selectedCity = ko.observable("");
    self.selectedDistrict = ko.observable("");
    self.selectedRoad = ko.observable("");
    
    self.eventVisible =
    		function(event){

				var eventObj = ko.toJS(event);

    			return (self.selectedType()=="全部" || self.selectedType()==eventObj.eventType)
    			&& (eventObj.province.contains(self.selectedProvince()))
    			&& (eventObj.city.contains(self.selectedCity()))
    			&& (eventObj.district.contains(self.selectedDistrict()))
    			&& (eventObj.roadNum.contains(self.selectedRoad()))
    			&& ((self.selectedReportType()=="被举报的" && eventObj.reportCnt>0)
    					|| (self.selectedReportType()=="未被举报的" && eventObj.reportCnt == 0)
    					|| (self.selectedReportType()=="全部")
    			);
    		};
    
    self.events =  ko.observableArray($.map(staticData.events, function(item){ return new Event(item);}));    
    
    self.showingAllEvents = ko.observable("dummy");
    self.chosenEvent = ko.observable();
    
    self.goToEvent = function(event){
    
    	self.showingAllEvents(null);
    	
    	$.getJSON("/AServer/getEventDetail?eid=" + event.eventID, 
		function(allData) {
			self.chosenEvent(new EventDetail(allData));
		});
    };
    
    self.goBackToEvents = function(){
    	self.showingAllEvents("dummy");
    	self.chosenEvent(null);
    };
    
    self.removeEvent = function(event) { 
    	self.events.remove(event);
    	var completeLink = "/AServer/deleteSpecifiedEvent?eid=" + event.eventID;
    	$.ajax(completeLink, {
            type: "get", contentType: "application/json"
        });
    }
    
    self.totalNumOfEvent = ko.computed(function() {
       return self.events().length;
    });
    
	$.getJSON("/AServer/getAllEventsForAllUsers", 
		function(allData) {
		var mappedEvents = $.map(allData.events, function(item){ return new Event(item); })
		self.events(mappedEvents)
	});
}

ko.applyBindings(new EventsViewModel());

function formatDate(d){
	return d.getFullYear() + "-" + (d.getMonth()+1) + "-" +
			d.getDate() + " " + d.getHours() + ":" + 
			d.getMinutes() + ":" + d.getSeconds();
}