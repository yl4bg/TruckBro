package com.truckapp.database;

public class UserDevice {
	
    private String userID;
    private String deviceID;
    private String cookie;

    public UserDevice(){}
    
    public UserDevice(String userID){
    	this.userID = userID;
    	this.deviceID = "palceHolder";
    }
    
    public String getUserID() {
        return userID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getCookie() {
        return cookie;
    }
    
    public void setUserID(String userID){
    	this.userID = userID;
    }
    
    public void setDeviceID(String deviceID){
    	this.deviceID = deviceID;
    }
    
    public void setCookie(String cookie){
    	this.cookie = cookie;
    }
}