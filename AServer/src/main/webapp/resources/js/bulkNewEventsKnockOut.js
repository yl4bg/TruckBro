var staticData = {"text": "上传错误信息提示：暂无。"}

if (typeof String.prototype.contains != 'function') {
	  String.prototype.contains = function (str){
	    return this.indexOf(str) > -1;
	  };
	}

function MsgObj(textData){
	var self = this;
	self.text = textData;
}

function sendLineToServer(line){
	
	console.log(line);
	
	$.ajax("/AServer/uploadBulkNewEvents", {
	    data: ko.toJSON(new MsgObj(line)),
		type:"POST",
		contentType:"application/json",
		dataType: "json",
		success: function(result){}
	});
	
}
function BulkNewEventsViewModel() {
    var self = this;

    self.errorMsg = ko.observable(staticData.text);
    self.eventsText = "";

	self.uploadEvents = function(){    
		
		var lines = self.eventsText.split("\n");

		for(var i in lines){
			var splits = lines[i].split(/\s+/);
			var desc = "";
			for(var j in splits){
				if(j==0 || j==1){ continue; }
				desc += splits[j];
			}
			geocoder(splits[0] + "~~~" + desc, splits[1]);
		}
		
		self.errorMsg("批量路况上传成功！");
		
	}
}

ko.applyBindings(new BulkNewEventsViewModel());
