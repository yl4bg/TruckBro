package com.truckapp.database;

public class UserInformation {

	public String userID;
	public String nickName;
	public String homeTown;
	public String portrait;
	public String signature;
	public String myTruck;
	public String myTruckPicId;
	public String boughtTime;
	public String driverLicensePic;
	public String registrationPic;
	public String licensePlate;
	public boolean showFPToDriver;
	public boolean showFPToOwner;
	public boolean showFPToHer;

	public boolean getShowFPToDriver() {
		return showFPToDriver;
	}
	public boolean isShowFPToDriver() {
		return showFPToDriver;
	}
	public void setShowFPToDriver(boolean showFPToDriver) {
		this.showFPToDriver = showFPToDriver;
	}
	public boolean getShowFPToOwner() {
		return showFPToOwner;
	}
	public boolean isShowFPToOwner() {
		return showFPToOwner;
	}
	public void setShowFPToOwner(boolean showFPToOwner) {
		this.showFPToOwner = showFPToOwner;
	}
	public boolean getShowFPToHer() {
		return showFPToHer;
	}
	public boolean isShowFPToHer() {
		return showFPToHer;
	}
	public void setShowFPToHer(boolean showFPToHer) {
		this.showFPToHer = showFPToHer;
	}
	
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getHomeTown() {
		return homeTown;
	}
	public void setHomeTown(String homeTown) {
		this.homeTown = homeTown;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getMyTruck() {
		return myTruck;
	}
	public void setMyTruck(String myTruck) {
		this.myTruck = myTruck;
	}
	public String getMyTruckPicId() {
		return myTruckPicId;
	}
	public void setMyTruckPicId(String myTruckPicId) {
		this.myTruckPicId = myTruckPicId;
	}
	public String getBoughtTime() {
		return boughtTime;
	}
	public void setBoughtTime(String boughtTime) {
		this.boughtTime = boughtTime;
	}
	public String getDriverLicensePic() {
		return driverLicensePic;
	}
	public void setDriverLicensePic(String driverLicensePic) {
		this.driverLicensePic = driverLicensePic;
	}
	public String getRegistrationPic() {
		return registrationPic;
	}
	public void setRegistrationPic(String registrationPic) {
		this.registrationPic = registrationPic;
	}
	
}
