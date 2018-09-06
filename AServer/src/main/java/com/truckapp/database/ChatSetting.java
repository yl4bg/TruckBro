package com.truckapp.database;

public class ChatSetting {

	private ChatSettingCompositeId chatSettingCompositeId;
	private boolean muted;
	private boolean top;
	private boolean locked;
	
	public ChatSetting(){}
	
	public ChatSetting(String userId, String chatId, 
			boolean muted, boolean top, boolean locked){
		this.chatSettingCompositeId = new ChatSettingCompositeId(userId, chatId);
		this.muted = muted;
		this.top = top;
		this.locked = locked;
	}
	
	public ChatSettingCompositeId getChatSettingCompositeId() {
		return chatSettingCompositeId;
	}
	public void setChatSettingCompositeId(
			ChatSettingCompositeId chatSettingCompositeId) {
		this.chatSettingCompositeId = chatSettingCompositeId;
	}
	public boolean isMuted() {
		return muted;
	}
	public void setMuted(boolean muted) {
		this.muted = muted;
	}
	public boolean isTop() {
		return top;
	}
	public void setTop(boolean top) {
		this.top = top;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean getMuted() {
		return muted;
	}
	public boolean getTop() {
		return top;
	}
	public boolean getLocked() {
		return locked;
	}
}
