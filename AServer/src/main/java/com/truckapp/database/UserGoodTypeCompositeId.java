package com.truckapp.database;

import java.io.Serializable;

public class UserGoodTypeCompositeId implements Serializable{
	
	private String userID;
	private String goodType;
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getGoodType() {
		return goodType;
	}
	public void setGoodType(String goodType) {
		this.goodType = goodType;
	}
	
}
