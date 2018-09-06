package com.truckapp.database;

public class Pic {

	private PicCompositeId picCompositeId;

	public PicCompositeId getPicCompositeId() {
		return picCompositeId;
	}

	public void setPicCompositeId(PicCompositeId picCompositeId) {
		this.picCompositeId = picCompositeId;
	}
	
	public Pic(){}
	
	public Pic(PicCompositeId picCompositeId){
		this.picCompositeId = picCompositeId;
	}
	
}
