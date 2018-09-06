package com.truckapp.valueObjects;

import java.util.Date;
import java.util.List;

public class EventDetail {
    
    private String eventType;
    private Date reportTime;
    private String eventInfo;
    private Sender sender;
    private List<ShortUser> upUsers;
    private List<ShortUser> reportUsers;
    private List<String> picIds;
    
    public List<String> getPicIds(){
    	return picIds;
    }
    
    public void setPicIds(List<String> picIds){
    	this.picIds = picIds;
    }
    
    public String getEventType () {
        return eventType;
    }

    public void setEventType (String eventType) {
        this.eventType = eventType;
    }

    public Date getReportTime () {
        return reportTime;
    }

    public void setReportTime (Date reportTime) {
        this.reportTime = reportTime;
    }

    public String getEventInfo () {
        return eventInfo;
    }

    public void setEventInfo (String eventInfo) {
        this.eventInfo = eventInfo;
    }

    public Sender getSender () {
        return sender;
    }

    public void setSender (Sender sender) {
        this.sender = sender;
    }

    public List<ShortUser> getUpUsers () {
        return upUsers;
    }

    public void setUpUsers (List<ShortUser> upUsers) {
        this.upUsers = upUsers;
    }

    public List<ShortUser> getReportUsers () {
        return reportUsers;
    }

    public void setReportUsers (List<ShortUser> reportUsers) {
        this.reportUsers = reportUsers;
    }
    
    
    public static class Sender{

        private String userID;
        private String userName;
        private int points;
        private double longitude;
        private double latitude;
        private Date reportTime;
        
        public String getUserID () {
            return userID;
        }
        public void setUserID (String userID) {
            this.userID = userID;
        }
        public String getUserName () {
            return userName;
        }
        public void setUserName (String userName) {
            this.userName = userName;
        }
        public int getPoints () {
            return points;
        }
        public void setPoints (int points) {
            this.points = points;
        }
        public double getLongitude () {
            return longitude;
        }
        public void setLongitude (double longitude) {
            this.longitude = longitude;
        }
        public double getLatitude () {
            return latitude;
        }
        public void setLatitude (double latitude) {
            this.latitude = latitude;
        }
        public Date getReportTime () {
            return reportTime;
        }
        public void setReportTime (Date reportTime) {
            this.reportTime = reportTime;
        }
        
    }
    
    public static class ShortUser{
        
        private String userID;
        private String userName;
         
        public ShortUser(){}
        
        public ShortUser(String userID, String userName){
            this.userID = userID;
            this.userName = userName;
        }
        
        public String getUserID () {
            return userID;
        }
        public void setUserID (String userID) {
            this.userID = userID;
        }
        public String getUserName () {
            return userName;
        }
        public void setUserName (String userName) {
            this.userName = userName;
        }
    }
}
