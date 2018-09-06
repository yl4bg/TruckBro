package com.truckapp.database;

public class UserGoodType {

	private UserGoodTypeCompositeId userGoodTypeCompositeId;
	
	public UserGoodType() {}
	
	public UserGoodType(String userId, String goodType){
		UserGoodTypeCompositeId newId = new UserGoodTypeCompositeId();
		newId.setUserID(userId);
		newId.setGoodType(goodType);
		this.userGoodTypeCompositeId = newId;
	}
	
	public UserGoodTypeCompositeId getUserGoodTypeCompositeId() {
		return userGoodTypeCompositeId;
	}

	public void setUserGoodTypeCompositeId(
			UserGoodTypeCompositeId userGoodTypeCompositeId) {
		this.userGoodTypeCompositeId = userGoodTypeCompositeId;
	}

	@Override
	public int hashCode(){
		return userGoodTypeCompositeId.getUserID().hashCode() + userGoodTypeCompositeId.getGoodType().hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		return this.hashCode() == o.hashCode();
	}
	
}
