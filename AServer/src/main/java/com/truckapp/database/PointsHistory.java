package com.truckapp.database;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class PointsHistory {

	private String userID;
	private String pDesc;
	private String pDiff;
    @DateTimeFormat(pattern="yyyy/MM/dd HH:mm")
	private Date pDate;
    
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getpDesc() {
		return pDesc;
	}
	public void setpDesc(String pDesc) {
		this.pDesc = pDesc;
	}
	public String getpDiff() {
		return pDiff;
	}
	public void setpDiff(String pDiff) {
		this.pDiff = pDiff;
	}
	public Date getpDate() {
		return pDate;
	}
	public void setpDate(Date pDate) {
		this.pDate = pDate;
	}
    
}