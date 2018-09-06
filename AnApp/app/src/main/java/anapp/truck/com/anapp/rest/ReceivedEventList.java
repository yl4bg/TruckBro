package anapp.truck.com.anapp.rest;

import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import anapp.truck.com.anapp.utility.valueObjects.Event;

/**
 * Represents an event received from server
 */
public class ReceivedEventList {

    private List<ReceivedEvent> events;

    public String getTravelTogetherCnt() {
        return travelTogetherCnt;
    }

    public void setTravelTogetherCnt(String travelTogetherCnt) {
        this.travelTogetherCnt = travelTogetherCnt;
    }

    private String travelTogetherCnt;

    public List<ReceivedEvent> getEvents() {
        return events;
    }

    public int getListSize() {
        return events.size();
    }

    public static class ReceivedEvent {
        private String eventID;
        private String eventType;
        private double longitude;
        private double latitude;
        private String eventInfo;
        private List<String> picID;
        private Date reportTime;

        private double distance;
        private String direction;
        private String timePassed;

        private String addressText;
        private String roadNumText;
        private String senderName;

        private int upCnt;
        private int reportCnt;
        private boolean official;

        public int getUpCnt() {
            return upCnt;
        }
        public int getReportCnt() {
            return reportCnt;
        }
        public double getDistance() {
            return distance;
        }

        public String getDirection() {
            return direction;
        }

        public String getTimePassed() {
            return timePassed;
        }

        public String getEventID() {
            return eventID;
        }

        public String getEventType() {
            return eventType;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getEventInfo() {
            return eventInfo;
        }

        public List<String> getPicID() {
            return picID;
        }

        public Date getReportTime() { return reportTime; }

        private boolean hasImage() {
            return !picID.isEmpty();
        }

        public String getAddressText(){
            return addressText;
        }

        public String getRoadNumText(){
            return roadNumText;
        }

        public boolean getOfficial() {return official; }

        public String getSenderName() {return senderName; }
        /**
         * Convert to the Event object used for UI display
         * @return a list of Event contained in the received event list
         */
        public Event toUiEvent() {
            return new Event(eventID, eventType, distance, direction,
                    eventInfo, eventID, timePassed, picID, reportTime,
                    Double.toString(longitude), Double.toString(latitude),
                    upCnt, reportCnt, roadNumText, addressText, official, senderName);
        }
    }

    public List<Event> toUiEventList() {
        List<Event> list = new ArrayList<>();
        for (ReceivedEvent e : events) {
            list.add(e.toUiEvent());
        }
        return list;
    }

    public List<LatLng> getAllLatLngs(){
        List<LatLng> list = new ArrayList<>();
        for (ReceivedEvent e : events) {
            list.add(new LatLng(e.latitude, e.longitude));
        }
        return list;
    }

}
