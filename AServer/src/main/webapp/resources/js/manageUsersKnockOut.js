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

function UserDetail(dataItem) {
	var self = this;
	self.nickName = dataItem.nickName;
	self.homeTown = dataItem.homeTown;
	self.portrait = dataItem.portrait;
	self.signature = dataItem.signature;
	self.myTruck = dataItem.myTruck;
	self.myTruckPicId = dataItem.myTruckPicId;
	self.boughtTime = dataItem.boughtTime;
	if(dataItem.driverLicensePic != null){
		self.driverLicensePic1 = dataItem.driverLicensePic.split(",")[0];
		self.driverLicensePic2 = dataItem.driverLicensePic.split(",")[1];
	} else {
		self.driverLicensePic1 = "未设置";
		self.driverLicensePic2 = "未设置";
	}
	if(dataItem.registrationPic != null){
		self.registrationPic1 = dataItem.registrationPic.split(",")[0];
		self.registrationPic2 = dataItem.registrationPic.split(",")[1];
	} else {
		self.registrationPic1 = "未设置";
		self.registrationPic2 = "未设置";
	}
	self.goodTypeList = dataItem.goodTypeList;
	self.frequentPlaceList = dataItem.frequentPlaceList;
}

function UsersViewModel() {
    var self = this;

    self.users =  ko.observableArray($.map(staticData.users, function(item){ return new User(item);}));    
    
    self.availablePrivileges = [
    	"上帝",
    	"管理员",
    	"完全",
    	"百分之60",
    	"普通"
    ];
    
    self.modifyUser = function(user) { 
    	user.reportTime = new Date().getTime();
    	$.ajax("/AServer/modifySpecifiedUser", {
    	    data: ko.toJSON(user),
			type:"POST",
			contentType:"application/json",
			dataType: "json",
			success: function(result){alert(result.text);}
        });
    }
    
    self.totalNumOfUser = ko.computed(function() {
       return self.users().length;
    });
    
    self.pushToUser = function(user) {
    	
    	var paramStr = "?deviceID=" + user.deviceID + "&msg=" + user.pushMsg;
    	
    	console.log(paramStr);
    	
    	$.ajax("/AServer/pushMsgToUser" + paramStr, {
			type:"GET",
			contentType:"application/json",
			success: function(result){alert(result.text);}
        });
    }
    
    self.pushMsg = ko.observable("");
    self.pushToAllUsers = function(){
    	
    	var paramStr = "?msg=" + self.pushMsg();
    	
    	console.log(paramStr);
    	
    	$.ajax("/AServer/pushMsgToAllUsers" + paramStr, {
			type:"GET",
			contentType:"application/json",
			success: function(result){alert(result.text);}
        });
    }
    
    self.showingAllUsers = ko.observable("dummy");
    self.chosenUser = ko.observable();
    
    self.goToUser = function(user){
    
    	self.showingAllUsers(null);
    	
    	$.getJSON("/AServer/getUserDetail?userid=" + user.userID, 
		function(allData) {
			self.chosenUser(new UserDetail(allData));
		});
    };
    
    self.goBackToUsers = function(){
    	self.showingAllUsers("dummy");
    	self.chosenUser(null);
    };
    
	$.getJSON("/AServer/getAllUsers", 
		function(allData) {
		var mappedUsers = $.map(allData.users, function(item){ return new User(item); })
		self.users(mappedUsers)
	});
}

ko.applyBindings(new UsersViewModel());

function formatDate(d){
	return d.getFullYear() + "-" + (d.getMonth()+1) + "-" +
			d.getDate() + " " + d.getHours() + ":" + 
			d.getMinutes() + ":" + d.getSeconds();
}