package com.truckapp.valueObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.truckapp.database.Event;

public class BackEndEventList {
    
    private List<BackEndEvent> events = new ArrayList<BackEndEvent>();

    public List<BackEndEvent> getEvents () {
        return events;
    }

    public void setEvents (List<BackEndEvent> events) {
        this.events = events;
    }

    public static class BackEndEvent implements Comparable<BackEndEvent> {

        private String eventID;
        private String eventType;
        private double longitude;
        private double latitude;
        private String eventInfo;
        private Date reportTime;
        private double radius;
        private String senderID;
        private String roadNum;
        private String province;
        private String city;
        private String district;
        private int upCnt;
        private int reportCnt;

        public BackEndEvent(){}
        
        public BackEndEvent(Event event){
            this.setEventID(event.getEventID());
            this.setEventType(event.getEventType());
            this.setLongitude(event.getLongitude());
            this.setLatitude(event.getLatitude());
            this.setEventInfo(event.getEventInfo());
            this.setReportTime(event.getOccurTime());
            this.setRadius(event.getRadius());
            this.setSenderID(event.getSenderID());
            this.setRoadNum(event.getRoadNum());
            this.setProvince(event.getProvince());
            this.setCity(event.getCity());
            this.setDistrict(event.getDistrict());
            this.setUpCnt(event.calUpCnt());
            this.setReportCnt(event.getInteractions().size()-this.upCnt);
        }
        
        public String getEventID () {
            return eventID;
        }

        public void setEventID (String eventID) {
            this.eventID = eventID;
        }

        public String getEventType () {
            return eventType;
        }

        public void setEventType (String eventType) {
            this.eventType = eventType;
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

        public String getEventInfo () {
            return eventInfo;
        }

        public void setEventInfo (String eventInfo) {
            this.eventInfo = eventInfo;
        }

        public Date getReportTime () {
            return reportTime;
        }

        public void setReportTime (Date reportTime) {
            this.reportTime = reportTime;
        }

        public double getRadius () {
            return radius;
        }

        public void setRadius (double radius) {
            this.radius = radius;
        }

        public String getSenderID () {
            return senderID;
        }

        public void setSenderID (String senderID) {
            this.senderID = senderID;
        }

        public String getRoadNum () {
            return roadNum;
        }

        public void setRoadNum (String roadNum) {
            this.roadNum = roadNum;
        }

        public String getProvince () {
            return province;
        }

        public void setProvince (String province) {
            this.province = province;
        }

        public String getCity () {
            return city;
        }

        public void setCity (String city) {
            this.city = city;
        }

        public String getDistrict () {
            return district;
        }

        public void setDistrict (String district) {
            this.district = district;
        }

        public int getUpCnt () {
            return upCnt;
        }

        public void setUpCnt (int upCnt) {
            this.upCnt = upCnt;
        }

        public int getReportCnt () {
            return reportCnt;
        }

        public void setReportCnt (int reportCnt) {
            this.reportCnt = reportCnt;
        }

        @Override
        public int compareTo (BackEndEvent o) {
            return o.reportTime.compareTo(reportTime);
        }

    }
}
