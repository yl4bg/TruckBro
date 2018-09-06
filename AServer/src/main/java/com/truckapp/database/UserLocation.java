package com.truckapp.database;

import java.util.Date;

/**
 * Represents the user location information to be sent to the server.
 */
public class UserLocation {

    private String userID;
    private double longitude;
    private double latitude;
    private Date time;

    public Date getTime() {
        return time;
    }

    public String getUserID() {
        return userID;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    
    public void setUserID(String userID){
    	this.userID = userID;
    }
    
    public void setLongitude(double longitude){
    	this.longitude = longitude;
    }
    
    public void setLatitude(double latitude){
    	this.latitude = latitude;
    }
    
    public void setTime(Date time){
    	this.time = time;
    }
}
