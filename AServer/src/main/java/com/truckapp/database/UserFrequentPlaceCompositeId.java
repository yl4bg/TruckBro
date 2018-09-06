package com.truckapp.database;

import java.io.Serializable;

public class UserFrequentPlaceCompositeId implements Serializable{

	private String userID;
	private String frequentPlace;
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getFrequentPlace() {
		return frequentPlace;
	}
	public void setFrequentPlace(String frequentPlace) {
		this.frequentPlace = frequentPlace;
	}
}
