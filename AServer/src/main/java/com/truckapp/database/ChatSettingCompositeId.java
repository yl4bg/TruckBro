package com.truckapp.database;

import java.io.Serializable;

public class ChatSettingCompositeId implements Serializable{

	private String userId;
	private String chatId;
	
	public ChatSettingCompositeId(){}
	
	public ChatSettingCompositeId(String userId2, String chatId2) {
		this.userId = userId2;
		this.chatId = chatId2;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	
}
