package com.truckapp.valueObjects;

public class ClientUser {

	private String userID;
	private String username;
	private String pwdhash;
	private String cookie;
	private String pushClientID;
	
	public String getPushClientID() {
		return pushClientID;
	}
	public void setPushClientID(String pushClientID) {
		this.pushClientID = pushClientID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getPwdhash() {
		return pwdhash;
	}
	public void setPwdhash(String pwdhash) {
		this.pwdhash = pwdhash;
	}

}
