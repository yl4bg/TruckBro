package com.truckapp.valueObjects;

import java.util.Date;

public class UserInfo {

	private String userID;
    private String username;
    private String salt;
	private String pwdhash;
    private String deviceID;
    private String cookie;
    private double longitude;
    private double latitude;
    private Date locationTime;
    
    public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getPwdhash() {
		return pwdhash;
	}
	public void setPwdhash(String pwdhash) {
		this.pwdhash = pwdhash;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public Date getLocationTime() {
		return locationTime;
	}
	public void setLocationTime(Date locationTime) {
		this.locationTime = locationTime;
	}
    
}
