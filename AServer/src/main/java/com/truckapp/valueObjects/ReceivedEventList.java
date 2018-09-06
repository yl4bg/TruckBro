package com.truckapp.valueObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import com.truckapp.database.Event;
import com.truckapp.database.Pic;


/**
 * Represents an event received from server
 */
public class ReceivedEventList {

    private List<ReceivedEvent> events;
    private String travelTogetherCnt;

    public String getTravelTogetherCnt() {
		return travelTogetherCnt;
	}

	public void setTravelTogetherCnt(String travelTogetherCnt) {
		this.travelTogetherCnt = travelTogetherCnt;
	}

	public List<ReceivedEvent> getEvents () {
        return events;
    }

    public void setEvents (List<ReceivedEvent> events) {
        this.events = events;
    }

    public static class ReceivedEvent implements Comparable<ReceivedEvent> {

        private String eventID;
        private String eventType;
        private double longitude;
        private double latitude;
        private String eventInfo;
        private Date reportTime;
        private List<String> picID;
        
        private double distance;
		private String direction;
        private String timePassed;
        
        private String addressText;
		private String roadNumText;
		
		private String senderName;

		private int upCnt;
		private int reportCnt;
		
		private boolean official;
        
        public String getSenderName() {
			return senderName;
		}

		public void setSenderName(String senderName) {
			this.senderName = senderName;
		}
		
		public boolean getOfficial(){
			return official;
		}
		
		public void setOfficial(boolean official){
			this.official = official;
		}

        public String getAddressText() {
			return addressText;
		}

		public void setAddressText(String addressText) {
			this.addressText = addressText;
		}

		public String getRoadNumText() {
			return roadNumText;
		}

		public void setRoadNumText(String roadNumText) {
			this.roadNumText = roadNumText;
		}
		
        public int getUpCnt() {
			return upCnt;
		}

		public void setUpCnt(int upCnt) {
			this.upCnt = upCnt;
		}

		public int getReportCnt() {
			return reportCnt;
		}

		public void setReportCnt(int reportCnt) {
			this.reportCnt = reportCnt;
		}
        
        public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		public String getDirection() {
			return direction;
		}

		public void setDirection(String direction) {
			this.direction = direction;
		}

		public String getTimePassed() {
			return timePassed;
		}

		public void setTimePassed(String timePassed) {
			this.timePassed = timePassed;
		}


        public ReceivedEvent(){}
        
        public ReceivedEvent(Event event){
            this.setEventID(event.getEventID());
            this.setEventType(event.getEventType());
            this.setLongitude(event.getLongitude());
            this.setLatitude(event.getLatitude());
            this.setEventInfo(event.getEventInfo());
            this.setReportTime(event.getOccurTime());
            Set<Pic> pics = event.getPics();
            List<String> picsID = new ArrayList<String>();
            for (Pic p: pics) picsID.add(p.getPicCompositeId().getPicId());
            this.setPicID(picsID);
            this.setUpCnt(event.calUpCnt());
            this.setReportCnt(event.getInteractions().size()-this.upCnt);
            this.setRoadNumText(event.getRoadNum());
            this.setAddressText(event.getProvince() + " " + event.getCity() + " " + event.getDistrict());
        }
        
        public void setEventID (String eventID) {
            this.eventID = eventID;
        }

        public void setEventType (String eventType) {
            this.eventType = eventType;
        }

        public void setLongitude (double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude (double latitude) {
            this.latitude = latitude;
        }

        public void setEventInfo (String eventInfo) {
            this.eventInfo = eventInfo;
        }

        public void setReportTime (Date reportTime) {
            this.reportTime = reportTime;
        }

        public void setPicID (List<String> picID) {
            this.picID = picID;
        }

        public String getEventID () {
            return eventID;
        }

        public String getEventType () {
            return eventType;
        }

        public double getLongitude () {
            return longitude;
        }

        public double getLatitude () {
            return latitude;
        }

        public String getEventInfo () {
            return eventInfo;
        }

        public List<String> getPicID () {
            return picID;
        }

        public Date getReportTime () {
            return reportTime;
        }

        @Override
        public int compareTo (ReceivedEvent o) {
            return o.getReportTime().compareTo(reportTime);
        }

    }
}
