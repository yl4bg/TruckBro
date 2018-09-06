package anapp.truck.com.anapp.rest;

import java.util.List;

/**
 * Created by angli on 6/27/15.
 */
public class ChatUserProfile {

    private String nickName;
    private String userId;
    private String hometown;
    private List<String> frequentPlaceList;
    private String myTruck;

    public List<String> getFrequentPlaceList() {
        return frequentPlaceList;
    }

    public void setFrequentPlaceList(List<String> frequentPlaceList) {
        this.frequentPlaceList = frequentPlaceList;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getMyTruck() {
        return myTruck;
    }

    public void setMyTruck(String myTruck) {
        this.myTruck = myTruck;
    }

}
