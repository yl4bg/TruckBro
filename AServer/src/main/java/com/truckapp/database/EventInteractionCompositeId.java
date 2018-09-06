package com.truckapp.database;

import java.io.Serializable;

public class EventInteractionCompositeId implements Serializable{

	private String eventId;
	private String interaction;
	private String userId;
	
	public EventInteractionCompositeId(){}
	
	public EventInteractionCompositeId(String eventId, String interaction, String userId){
		this.eventId = eventId;
		this.interaction = interaction;
		this.userId = userId;
	}
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getInteraction() {
		return interaction;
	}
	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}

