package com.truckapp.valueObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.truckapp.database.User;
import com.truckapp.database.UserPrivilege;

public class BackEndUserList {

    private List<BackEndUser> users = new ArrayList<BackEndUser>();

    public List<BackEndUser> getUsers () {
        return users;
    }

    public void setUsers (List<BackEndUser> users) {
        this.users = users;
    }

    public static class BackEndUser implements Comparable<BackEndUser> {

        private String userID;
        private String userName;
        private int points;
        private String privilege;
        private double longitude;
        private double latitude;
        private Date reportTime;
        private String deviceID;
        private String cookie;
        private String pushMsg;
        
        public String getPushMsg () {
            return pushMsg;
        }

        public void setPushMsg (String pushMsg) {
            this.pushMsg = pushMsg;
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

        public int getPoints () {
            return points;
        }

        public void setPoints (int points) {
            this.points = points;
        }

        public String getPrivilege () {
            return privilege;
        }

        public void setPrivilege (String privilege) {
            this.privilege = privilege;
        }

        public String getDeviceID () {
            return deviceID;
        }

        public void setDeviceID (String deviceID) {
            this.deviceID = deviceID;
        }

        public String getCookie () {
            return cookie;
        }

        public void setCookie (String cookie) {
            this.cookie = cookie;
        }

        public BackEndUser(){}
        
        public BackEndUser(User user){
            this.setUserID(user.getUserID());
            this.setUserName(user.getUsername());
            this.setPoints(user.getPoints());
            this.setPrivilege(UserPrivilege.toChinese(user.getPrivilege()));
            if(user.getLocation()!=null){
                this.setLongitude(user.getLocation().getLongitude());
                this.setLatitude(user.getLocation().getLatitude());
                this.setReportTime(user.getLocation().getTime());
            }
            if(user.getDevice()!=null){
                this.setDeviceID(user.getDevice().getDeviceID());
                this.setCookie(user.getDevice().getCookie());
            }
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

        @Override
        public int compareTo (BackEndUser o) {
            return -this.privilege.compareTo(o.privilege);
        }

    }
}
