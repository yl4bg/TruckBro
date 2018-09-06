package com.truckapp.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.truckapp.util.Constants;

/**
 * Represents an event that user sends to the server.
 */
public class Event{

	private String eventID;
    private String eventType;
    private double longitude;
    private double latitude;
    private double radius;
    @DateTimeFormat(pattern="yyyy/MM/dd HH:mm")
    private Date occurTime;
    private String eventInfo;
    private String senderID;
    
    private String roadNum;
	private String province;
    private String city;
    private String district;

    private Set<Pic> pics = new HashSet<Pic>();
    private Set<EventInteraction> interactions = new HashSet<EventInteraction>();
    
    public Set<EventInteraction> getInteractions() {
		return interactions;
	}

	public void setInteractions(Set<EventInteraction> interactions) {
		this.interactions = interactions;
	}

	public String getRoadNum() {
		return roadNum;
	}

	public void setRoadNum(String roadNum) {
		this.roadNum = roadNum;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

    public String getEventID() {
        return eventID;
    }

    public Set<Pic> getPics() {
		return pics;
	}

	public void setPics(Set<Pic> pics) {
		this.pics = pics;
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

    public double getRadius() {
        return radius;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public String getSenderID() {
        return senderID;
    }
    
    public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}

	public void setEventInfo(String eventInfo) {
		this.eventInfo = eventInfo;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}
	public int calUpCnt(){
		int cnt = 0;
		for(EventInteraction ei : interactions){
			if(ei.getEventInteractionCompositeId().getInteraction().equals(Constants.VOTE_UP_EVENT)){
				cnt++;
			}
		}
		return cnt;
	}

	public List<String> getPicIds() {
		List<String> ret = new ArrayList<String>();
		for(Pic p : pics){
			ret.add(Constants.PIC_PREFIX + p.getPicCompositeId().getPicId());
		}
		return ret;
	}
}