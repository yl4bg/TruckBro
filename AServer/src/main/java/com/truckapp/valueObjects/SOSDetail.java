package com.truckapp.valueObjects;

public class SOSDetail {

    private String homeTown;
    private String myTruck;
    private String userName;
    private String phoneNum;
    private String licensePlate;

    public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public void setHomeTown (String homeTown) {
        this.homeTown = homeTown;
    }

    public void setMyTruck (String myTruck) {
        this.myTruck = myTruck;
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }

    public void setPhoneNum (String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public String getHomeTown() {
        return homeTown;
    }

    public String getMyTruck() {
        return myTruck;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }


}