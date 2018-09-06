package com.truckapp.valueObjects;

import java.util.List;

public class UserDetail {

	private String nickName;
	private String homeTown;
	private String portrait;
	private String signature;
	private String myTruck;
	private String myTruckPicId;
	private String boughtTime;
	private String driverLicensePic;
	private String registrationPic;
	private List<String> goodTypeList;
	private List<String> frequentPlaceList;
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
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
	public List<String> getGoodTypeList() {
		return goodTypeList;
	}
	public void setGoodTypeList(List<String> goodTypeList) {
		this.goodTypeList = goodTypeList;
	}
	public List<String> getFrequentPlaceList() {
		return frequentPlaceList;
	}
	public void setFrequentPlaceList(List<String> frequentPlaceList) {
		this.frequentPlaceList = frequentPlaceList;
	}
}
