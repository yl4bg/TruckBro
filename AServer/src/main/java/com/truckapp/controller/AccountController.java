package com.truckapp.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.truckapp.database.DAO;
import com.truckapp.database.DriverType;
import com.truckapp.database.User;
import com.truckapp.database.UserDevice;
import com.truckapp.database.UserInformation;
import com.truckapp.database.UserLocation;
import com.truckapp.database.UserPrivilege;
import com.truckapp.database.UserType;
import com.truckapp.util.Constants;
import com.truckapp.util.SHA_Hash;

//does not allow web-site user registration for now
@Controller
public class AccountController {

	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}
	
    @RequestMapping(value="/newAccount", method=RequestMethod.GET)
    public ModelAndView newAccountForm(Model model) {
        model.addAttribute("userObj", new User());
        return new ModelAndView("newAccount");
    }
    
    @RequestMapping(value="/submitNewAccount", method=RequestMethod.POST)
    public ModelAndView submitNewAccount(@ModelAttribute("userObj") User user, Model model){
    	
    	try {
			user.setSalt(SHA_Hash.getSalt());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
    	user.setPwdhash(SHA_Hash.getHashedPassword(user.getPwdhash(), user.getSalt()));
    	user.setUsername("管理员"+user.getUserID());
    	user.setPoints(0);
    	user.setPrivilege(UserPrivilege.admin);
    	user.setReferPerson("123456");
		user.setShowUserIdToDriver(false);
		user.setShowUserIdToOwner(false);
		user.setShowUserIdToHer(false);
		user.setUserType(UserType.driver);
		user.setDriverType(DriverType.driverAndOwner);
        
    	user.setDevice(new UserDevice());
    	user.getDevice().setUserID(user.getUserID());
    	user.getDevice().setDeviceID("noPushId");
    	user.getDevice().setCookie(Constants.NO_TOKEN);
        
    	user.setLocation(new UserLocation());
    	user.getLocation().setUserID(user.getUserID());
    	user.getLocation().setLongitude(-1);
        user.getLocation().setLatitude(-1);
        user.getLocation().setTime(new Date());
        
        user.setUserInformation(new UserInformation());
        user.getUserInformation().setUserID(user.getUserID());
        user.getUserInformation().setBoughtTime("未设置");
        user.getUserInformation().setDriverLicensePic(null);
        user.getUserInformation().setHomeTown("未设置");
        user.getUserInformation().setMyTruck("未设置");
        user.getUserInformation().setMyTruckPicId(null);
        user.getUserInformation().setNickName("未设置");
        user.getUserInformation().setPortrait("未设置");
        user.getUserInformation().setRegistrationPic(null);
        user.getUserInformation().setSignature("未设置");
		user.getUserInformation().setLicensePlate("未设置");
		user.getUserInformation().setShowFPToDriver(false);
		user.getUserInformation().setShowFPToOwner(false);
		user.getUserInformation().setShowFPToHer(false);
    	
    	myDao.insertIntoDB(user, User.class);
    	
    	model.addAttribute("userObj", new User());
    	return new ModelAndView("login");
    }
    
}
