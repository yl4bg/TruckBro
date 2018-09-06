package com.truckapp.valueObjects;

import java.util.List;

import com.truckapp.database.User;
import com.truckapp.database.UserInformation;
import com.truckapp.database.UserPrivilege;

/**
 * Created by angli on 4/5/15.
 */
public class PersonalInfo {

    private String nickName;
    private String userName;
    private String privilege;
    private String homeTown;
    private List<String> frequentPlaces;
    private List<String> goodTypes;
    private String portrait;
    private String signature;
    private String myTruck;
    private String myTruckPicId;
    private String driverLicensePic;
    private String registrationPic;
    private String boughtTime;
    private String points;
    private String licensePlate;

    public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public PersonalInfo(){}
    
    public PersonalInfo(UserInformation info){
    	this.nickName = info.getNickName();
    	this.homeTown = info.getHomeTown();
    	this.portrait = info.getPortrait();
    	this.signature = info.getSignature();
    	this.myTruck = info.getMyTruck();
    	this.myTruckPicId = info.getMyTruckPicId();
    	this.driverLicensePic = info.getDriverLicensePic();
    	this.registrationPic = info.getRegistrationPic();
    	this.boughtTime = info.getBoughtTime();
    	this.licensePlate = info.getLicensePlate();
    }
    
    public void updateWithUser(User user){
    	this.userName = user.getUsername();
    	this.points = Integer.toString(user.getPoints());
    	this.privilege = UserPrivilege.toString(user.getPrivilege());
    	this.frequentPlaces = user.frequentPlaces();
    	this.goodTypes = user.goodTypes();
    }
    
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public List<String> getFrequentPlaces() {
        return frequentPlaces;
    }

    public void setFrequentPlaces(List<String> frequentPlaces) {
        this.frequentPlaces = frequentPlaces;
    }

    public List<String> getGoodTypes() {
        return goodTypes;
    }

    public void setGoodTypes(List<String> goodTypes) {
        this.goodTypes = goodTypes;
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

    public String getBoughtTime() {
        return boughtTime;
    }

    public void setBoughtTime(String boughtTime) {
        this.boughtTime = boughtTime;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}
