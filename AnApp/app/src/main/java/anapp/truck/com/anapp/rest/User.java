package anapp.truck.com.anapp.rest;

/**
 * Represents a user
 */
public class User {

    private String userID;
    private String username;
    private String pwdhash;
    private String cookie;
    private String pushClientID;

    public String getPwdhash() {
        return pwdhash;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getCookie() {
        return cookie;
    }

    public String getPushClientID() {
        return pushClientID;
    }
}
