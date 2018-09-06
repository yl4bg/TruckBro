package com.truckapp.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Represents a user
 */
public class User {

    private String userID;
    private String username;
    private String salt;
    private String pwdhash;
    private int points;
    private UserPrivilege privilege;
    private String referPerson;
    private boolean showUserIdToDriver;
	private boolean showUserIdToOwner;
    private boolean showUserIdToHer;
    private UserType userType;
    private DriverType driverType;

	private UserLocation location;
    private UserDevice device;
    private DetailedHometown detailedHometown;
	private UserInformation userInformation;
	
	private Set<UserGoodType> userGoodTypes;
    private Set<UserFrequentPlace> userFrequentPlaces;
    private Set<ChatSetting> chatSettings;
    private Set<PointsHistory> pointsHistories;

    
    public Set<PointsHistory> getPointsHistories() {
		return pointsHistories;
	}

	public void setPointsHistories(Set<PointsHistory> pointsHistories) {
		this.pointsHistories = pointsHistories;
	}

	public boolean getShowUserIdToDriver(){
    	return showUserIdToDriver;
    }
    
    public boolean isShowUserIdToDriver() {
		return showUserIdToDriver;
	}

	public void setShowUserIdToDriver(boolean showUserIdToDriver) {
		this.showUserIdToDriver = showUserIdToDriver;
	}

	public boolean getShowUserIdToOwner() {
		return showUserIdToOwner;
	}
	
	public boolean isShowUserIdToOwner() {
		return showUserIdToOwner;
	}

	public void setShowUserIdToOwner(boolean showUserIdToOwner) {
		this.showUserIdToOwner = showUserIdToOwner;
	}

	public boolean getShowUserIdToHer() {
		return showUserIdToHer;
	}
	
	public boolean isShowUserIdToHer() {
		return showUserIdToHer;
	}

	public void setShowUserIdToHer(boolean showUserIdToHer) {
		this.showUserIdToHer = showUserIdToHer;
	}
	
	public DriverType getDriverType() {
		return driverType;
	}

	public void setDriverType(DriverType driverType) {
		this.driverType = driverType;
	}
	
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
    public Set<ChatSetting> getChatSettings() {
		return chatSettings;
	}

	public void setChatSettings(Set<ChatSetting> chatSettings) {
		this.chatSettings = chatSettings;
	}

	public DetailedHometown getDetailedHometown() {
		return detailedHometown;
	}

	public void setDetailedHometown(DetailedHometown detailedHometown) {
		this.detailedHometown = detailedHometown;
	}
	
    public UserInformation getUserInformation() {
		return userInformation;
	}

	public void setUserInformation(UserInformation userInformation) {
		this.userInformation = userInformation;
	}

	public Set<UserGoodType> getUserGoodTypes() {
		return userGoodTypes;
	}

	public void setUserGoodTypes(Set<UserGoodType> userGoodTypes) {
		this.userGoodTypes = userGoodTypes;
	}

	public Set<UserFrequentPlace> getUserFrequentPlaces() {
		return userFrequentPlaces;
	}

	public void setUserFrequentPlaces(Set<UserFrequentPlace> userFrequentPlaces) {
		this.userFrequentPlaces = userFrequentPlaces;
	}

	public String getReferPerson() {
		return referPerson;
	}

	public void setReferPerson(String referPerson) {
		this.referPerson = referPerson;
	}

	public UserPrivilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(UserPrivilege privilege) {
		this.privilege = privilege;
	}
	
    public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

    public User () {
    }

    public UserLocation getLocation () {
        return location;
    }

    public void setLocation (UserLocation location) {
        this.location = location;
    }

    public UserDevice getDevice () {
        return device;
    }

    public void setDevice (UserDevice device) {
        this.device = device;
    }

    public User (String username, String pwdhash) {
        this.username = username;
        this.pwdhash = pwdhash;
    }

    public User (String username, String pwdhash, String userID) {
        this(username, pwdhash);
        this.userID = userID;
    }

    public String getSalt () {
        return salt;
    }

    public void setSalt (String salt) {
        this.salt = salt;
    }

    public String getPwdhash () {
        return pwdhash;
    }

    public String getUserID () {
        return userID;
    }

    public String getUsername () {
        return username;
    }

    public void setUserID (String userID) {
        this.userID = userID;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setPwdhash (String pwdhash) {
        this.pwdhash = pwdhash;
    }

	public List<String> frequentPlaces() {
		List<String> ret = new ArrayList<String>();
		for(UserFrequentPlace fp : this.userFrequentPlaces){
			ret.add(fp.getUserFrequentPlaceCompositeId().getFrequentPlace());
		}
		return ret;
	}
	
	public List<String> goodTypes() {
		List<String> ret = new ArrayList<String>();
		for(UserGoodType gt : this.userGoodTypes){
			ret.add(gt.getUserGoodTypeCompositeId().getGoodType());
		}
		return ret;
	}
}
