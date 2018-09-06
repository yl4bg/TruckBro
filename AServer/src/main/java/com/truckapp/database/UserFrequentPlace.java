package com.truckapp.database;

public class UserFrequentPlace {

	private UserFrequentPlaceCompositeId userFrequentPlaceCompositeId;

	public UserFrequentPlace() {}
	
	public UserFrequentPlace(String userId, String frequentPlace){
		UserFrequentPlaceCompositeId newId = new UserFrequentPlaceCompositeId();
		newId.setUserID(userId);
		newId.setFrequentPlace(frequentPlace);
		this.userFrequentPlaceCompositeId = newId;
	}
	
	public UserFrequentPlaceCompositeId getUserFrequentPlaceCompositeId() {
		return userFrequentPlaceCompositeId;
	}

	public void setUserFrequentPlaceCompositeId(
			UserFrequentPlaceCompositeId userFrequentPlaceCompositeId) {
		this.userFrequentPlaceCompositeId = userFrequentPlaceCompositeId;
	}

	@Override
	public int hashCode(){
		return userFrequentPlaceCompositeId.getUserID().hashCode() + userFrequentPlaceCompositeId.getFrequentPlace().hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		return this.hashCode() == o.hashCode();
	}
	
}
