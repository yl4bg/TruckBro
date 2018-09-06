package com.truckapp.database;

import java.io.Serializable;

public class PicCompositeId implements Serializable{

	private String eventId;
	private String picId;
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getPicId() {
		return picId;
	}
	public void setPicId(String picId) {
		this.picId = picId;
	}
	
	public PicCompositeId(){}
	
	public PicCompositeId(String eventId, String picId){
		this.eventId = eventId;
		this.picId = picId;
	}
}
