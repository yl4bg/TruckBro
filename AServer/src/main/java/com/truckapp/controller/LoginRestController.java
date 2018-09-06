package com.truckapp.controller;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.truckapp.database.DAO;
import com.truckapp.database.User;
import com.truckapp.database.UserPrivilege;
import com.truckapp.util.Constants;
import com.truckapp.util.SHA_Hash;
import com.truckapp.valueObjects.BackEndUserList.BackEndUser;
import com.truckapp.valueObjects.BoolMsg;
import com.truckapp.valueObjects.ClientUser;
import com.truckapp.valueObjects.ErrorMsg;

@RestController
public class LoginRestController {

	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}
	
	private static Logger myLogger = Logger.getLogger(LoginRestController.class.getName());

	@RequestMapping(value = "/loginRequest", method = RequestMethod.GET)
	public ClientUser loginRequest(
			@RequestParam(value = "userID", defaultValue = "noId") String userID,
			@RequestParam(value = "pwdhash", defaultValue = "noPw") String password,
			@RequestParam(value = "pushClientID", defaultValue = "noPushId") String pushClientID,
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String token) {

		ClientUser toReturn = new ClientUser();
		toReturn.setUserID("defaultID");
		
		myLogger.info("\t Request cookie: " + token);
		
		// cookie param is only specified by client when trying to validate an existing cookie
		if(!token.equals(Constants.NO_TOKEN)){
			if(myDao.checkValidCookieExists(token)){
				toReturn.setCookie(token);
				toReturn.setUserID(myDao.getUserFromCookie(token).getUserID());
				toReturn.setUsername(myDao.getUserNickName(toReturn.getUserID()));
			} else {
				toReturn.setCookie("invalidCookie");
			}
			return toReturn;
		}
		
		myLogger.info("\tRequest params: userID = " + userID + " pwdhash = " + password + " clientID = " + pushClientID + " cookie = " + token);
		
		if (myDao.checkUserExists(userID)) {
			User user = myDao.getUser(userID);
			user.setPwdhash(password);
			if(myDao.checkPw(user)){
				String cookie = UUID.randomUUID().toString();
				myDao.updateUserInfo(userID, cookie, pushClientID);
				toReturn.setCookie(cookie);
				toReturn.setUserID(userID);
				toReturn.setUsername(myDao.getUserNickName(userID));
			}
		}

		return toReturn;

	}
	
	@RequestMapping(value = "/locationUpdate", method = RequestMethod.GET)
    public ErrorMsg locationUpdate(
                    @RequestParam(value = "longitude", defaultValue = "noLongitude") String longitude,
                    @RequestParam(value = "latitude", defaultValue = "noLatitude") String latitude,
                    @RequestParam(value = "cookie", defaultValue = "noCookie") String cookie,
                    @RequestParam(value = "pushID", defaultValue = "noPushID") String pushID){
		ErrorMsg msg = new ErrorMsg();
		
		if(!myDao.updateUserLocation(cookie, longitude, latitude, pushID)){
			msg.setText("坐标上传失败！请检查网络及GPS");
		}
		
		return msg;
	}
	
	@RequestMapping(value = "/pwResetRequest", method = RequestMethod.GET)
        public ErrorMsg pwResetRequest(
                        @RequestParam(value = "userID", defaultValue = "noId") String userID,
                        @RequestParam(value = "pwdhash", defaultValue = "noPw") String password){
            
	    ErrorMsg replyMsg = new ErrorMsg();

	    User user = myDao.getUser(userID);
	    try {
	        user.setSalt(SHA_Hash.getSalt());
	    } catch (NoSuchAlgorithmException e) {
	        myLogger.log(Level.INFO, e.getMessage(), e);
	        replyMsg.setText("服务器哈希方程出错。请稍后重试。");;
	    }
	    user.setPwdhash(SHA_Hash.getHashedPassword(password, user.getSalt()));
	    myDao.insertIntoDB(user, User.class);

            myLogger.info("\t Password Reset Request userID = " + userID + " new pwhash = " + user.getPwdhash() + " new salt = " + user.getSalt());
	    
	    replyMsg.setText("密码重置成功!");

	    return replyMsg;
        }
	
	@RequestMapping(value = "/modifySpecifiedUser", method = RequestMethod.POST)
        public @ResponseBody ErrorMsg modifySpecifiedUser(
                     @RequestBody BackEndUser modifiedUser,
                     @CookieValue(value = "token", defaultValue = "noToken") String token){
	    
	    ErrorMsg msg = new ErrorMsg();
	    
	    User user = myDao.getUserFromCookie(token);
	    if(user.getPrivilege().compareTo(UserPrivilege.admin) < 0){
	        msg.setText("您的权限不够进行此操作");
	        return msg;
	    }
	    
	    if(myDao.modifyUser(modifiedUser)){;
	        msg.setText("修改用户成功");
	    } else {
	        msg.setText("修改用户出错");
	    }
	    return msg;
	}
}
