package com.truckapp.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.truckapp.database.DAO;
import com.truckapp.database.DriverType;
import com.truckapp.database.User;
import com.truckapp.database.UserDevice;
import com.truckapp.database.UserInformation;
import com.truckapp.database.UserLocation;
import com.truckapp.database.UserPrivilege;
import com.truckapp.database.UserType;
import com.truckapp.util.ChatUserThread;
import com.truckapp.util.Constants;
import com.truckapp.util.SHA_Hash;
import com.truckapp.valueObjects.BackEndUserList;
import com.truckapp.valueObjects.ErrorMsg;
import com.truckapp.valueObjects.PointsHistoryList;

@RestController
public class AccountRestController {

	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}
	
	private static Logger myLogger = Logger.getLogger(AccountRestController.class.getName());

	@RequestMapping(value = "/signupRequest", method = RequestMethod.GET)
	public ErrorMsg signupRequest(
			@RequestParam(value = "userID", defaultValue = "noId") String userID,
			@RequestParam(value = "username", defaultValue = "noName") String username,
			@RequestParam(value = "pwdhash", defaultValue = "noPw") String password,
			@RequestParam(value = "referPerson", defaultValue = "noReferPerson") String referPerson,
			@RequestParam(value = "userType", defaultValue = "noUserType") String userType,
			@RequestParam(value = "driverType", defaultValue = "noDriverType") String driverType){
		
		ErrorMsg msg = new ErrorMsg();
		if(!myDao.checkUserExists(userID)){
			User newUser = new User();
			newUser.setUserID(userID);
			try {
				newUser.setSalt(SHA_Hash.getSalt());
			} catch (NoSuchAlgorithmException e) {
				myLogger.log(Level.INFO, e.getMessage(), e);
				msg.setText("服务器哈希方程出错。请稍后重试。");;
			}
			newUser.setPwdhash(SHA_Hash.getHashedPassword(password, newUser.getSalt()));
			newUser.setUsername(username);
			newUser.setPoints(Constants.DEFAULT_POINTS);
			newUser.setPrivilege(UserPrivilege.basic);
			newUser.setReferPerson(referPerson);
			newUser.setShowUserIdToDriver(false);
			newUser.setShowUserIdToOwner(false);
			newUser.setShowUserIdToHer(false);
			newUser.setUserType(UserType.chineseToType(userType));
			newUser.setDriverType(DriverType.chineseToType(driverType));
			
			newUser.setDevice(new UserDevice());
			newUser.getDevice().setUserID(userID);
			newUser.getDevice().setDeviceID("noPushId");
			newUser.getDevice().setCookie(Constants.NO_TOKEN);
			
			newUser.setLocation(new UserLocation());
			newUser.getLocation().setUserID(userID);
			newUser.getLocation().setLongitude(-1);
			newUser.getLocation().setLatitude(-1);
			newUser.getLocation().setTime(new Date());
			
			newUser.setUserInformation(new UserInformation());
			newUser.getUserInformation().setUserID(userID);
			newUser.getUserInformation().setBoughtTime("未设置");
			newUser.getUserInformation().setDriverLicensePic(null);
			newUser.getUserInformation().setHomeTown("未设置");
			newUser.getUserInformation().setMyTruck("未设置");
			newUser.getUserInformation().setMyTruckPicId(null);
			newUser.getUserInformation().setNickName("未设置");
			newUser.getUserInformation().setPortrait("未设置");
			newUser.getUserInformation().setRegistrationPic(null);
			newUser.getUserInformation().setSignature("未设置");
			newUser.getUserInformation().setLicensePlate("未设置");
			newUser.getUserInformation().setShowFPToDriver(false);
			newUser.getUserInformation().setShowFPToOwner(false);
			newUser.getUserInformation().setShowFPToHer(false);
			
			myDao.insertIntoDB(newUser, User.class);
			
			// create chat user in openfire
			new Thread(new ChatUserThread(userID)).start();
			
		} else {
			msg.setText("此手机号已被注册！");
		}
		return msg;
	}
	
	@RequestMapping(value = "/getPointsRequest", method = RequestMethod.GET)
	public ErrorMsg getPointsRequest(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie){
		ErrorMsg msg = new ErrorMsg();
		msg.setText(Integer.toString(myDao.getUserFromCookie(cookie).getPoints()));
		return msg;
	}
	
	@RequestMapping(value = "/getPointsHistory", method = RequestMethod.GET)
	public PointsHistoryList getPointsHistory(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie){
		PointsHistoryList ret = new PointsHistoryList();
		ret.setPoinstHistories(myDao.getPointsHistory(myDao.getUserFromCookie(cookie).getUserID()));
		return ret;
	}
	
	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
        public BackEndUserList getAllUsers(
                @CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken){
	    
	    User user = myDao.getUserFromCookie(cookieToken);
	    if(user.getPrivilege().compareTo(UserPrivilege.admin) < 0){
	        return new BackEndUserList();
	    }
	    
	    BackEndUserList toReturn = new BackEndUserList();
	    toReturn.setUsers(myDao.getAllUsersForBackEnd());
	    return toReturn;
	}
}
