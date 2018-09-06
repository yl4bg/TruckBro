package anapp.truck.com.anapp.rest;

import java.util.List;

/**
 * Created by angli on 4/5/15.
 */
public class PersonalInfo {

    private String nickName;
    private String userName;
    private String privilege;
    private String homeTown;
    private List<String> frequentPlaces;
    private List<String> goodTypes;
    private String portrait;
    private String signature;
    private String myTruck;
    private String myTruckPicId;
    private String driverLicensePic;
    private String registrationPic;
    private String boughtTime;
    private String points;
    private String licensePlate;

    public String getLicensePlate(){
        return licensePlate;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public List<String> getFrequentPlaces() {
        return frequentPlaces;
    }

    public void setFrequentPlaces(List<String> frequentPlaces) {
        this.frequentPlaces = frequentPlaces;
    }

    public List<String> getGoodTypes() {
        return goodTypes;
    }

    public void setGoodTypes(List<String> goodTypes) {
        this.goodTypes = goodTypes;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMyTruck() {
        return myTruck;
    }

    public void setMyTruck(String myTruck) {
        this.myTruck = myTruck;
    }

    public String getMyTruckPicId() {
        return myTruckPicId;
    }

    public void setMyTruckPicId(String myTruckPicId) {
        this.myTruckPicId = myTruckPicId;
    }

    public String getDriverLicensePic() {
        return driverLicensePic;
    }

    public void setLicensePlate(String licensePlate){
        this.licensePlate = licensePlate;
    }

    public void setDriverLicensePic(String driverLicensePic) {
        this.driverLicensePic = driverLicensePic;
    }

    public String getRegistrationPic() {
        return registrationPic;
    }

    public void setRegistrationPic(String registrationPic) {
        this.registrationPic = registrationPic;
    }

    public String getBoughtTime() {
        return boughtTime;
    }

    public void setBoughtTime(String boughtTime) {
        this.boughtTime = boughtTime;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}
