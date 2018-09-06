package com.truckapp.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.truckapp.database.DAO;
import com.truckapp.database.User;
import com.truckapp.database.UserPrivilege;
import com.truckapp.util.Constants;
import com.truckapp.valueObjects.ErrorMsg;
import com.truckapp.valueObjects.PersonalInfo;
import com.truckapp.valueObjects.UserDetail;

@RestController
public class PersonalCenterRestController {
	
	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}
	
	private static Logger logger = Logger.getLogger(PersonalCenterRestController.class.getName());

	@RequestMapping(value = "/changeInfoField", method = RequestMethod.GET)
	public ErrorMsg changeInfoField(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "fieldname", defaultValue = "noField") String field,
			@RequestParam(value = "value", defaultValue = "noValue") String value){
		
		ErrorMsg msg = new ErrorMsg();
		User user = myDao.getUserFromCookie(cookie);
		if(myDao.updatePersonalInformation(user.getUserID(),
				field, value)){
			msg.setText("设置保存成功！");
		} else {
			msg.setText("设置保存失败。服务器出错。");
		}
		return msg;
	}
	
	@RequestMapping(value = "/detailedHometown", method = RequestMethod.GET)
	public ErrorMsg detailedHometown(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "province", defaultValue = "noProvince") String province,
			@RequestParam(value = "city", defaultValue = "noCity") String city,
			@RequestParam(value = "district", defaultValue = "noDistrict") String district){
		
		ErrorMsg msg = new ErrorMsg();
		if(myDao.addDetailedHometownForUser(myDao.getUserFromCookie(cookie).getUserID(),
				province, city, district)){
			msg.setText("设置保存成功！");
		} else {
			msg.setText("设置保存失败。服务器出错。");
		}
		return msg;
	}
	
	@RequestMapping(value = "/updateGoodType", method = RequestMethod.GET)
	public ErrorMsg addGoodType(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "goodTypeList", defaultValue = "") String goodTypeList){
		
		ErrorMsg msg = new ErrorMsg();
		String[] goodTypes = goodTypeList.split(",");
		if(myDao.updateGoodTypes(myDao.getUserFromCookie(cookie).getUserID(),
				goodTypes)){
			msg.setText("设置保存成功！");
		} else {
			msg.setText("设置保存失败。服务器出错。");
		}
		return msg;
	}
	
	@RequestMapping(value = "/addFrequentPlace", method = RequestMethod.GET)
	public ErrorMsg addFrequentPlace(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "frequentPlaceList", defaultValue = "") String frequentPlaceList){
		
		ErrorMsg msg = new ErrorMsg();
		String[] frequentPlaces = frequentPlaceList.split(",");
		if(myDao.addFrequentPlaces(myDao.getUserFromCookie(cookie).getUserID(),
				frequentPlaces)){
			msg.setText("设置保存成功！");
		} else {
			msg.setText("设置保存失败。服务器出错。");
		}
		return msg;
	}
	
	@RequestMapping(value = "/updateFrequentPlaces", method = RequestMethod.GET)
	public ErrorMsg updateFrequentPlaces(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "frequentPlaceList", defaultValue = "") String frequentPlaceList){
		
		ErrorMsg msg = new ErrorMsg();
		String[] frequentPlaces = frequentPlaceList.split(",");
		if(myDao.updateFrequentPlaces(myDao.getUserFromCookie(cookie).getUserID(),
				frequentPlaces)){
			msg.setText("设置保存成功！");
		} else {
			msg.setText("设置保存失败。服务器出错。");
		}
		return msg;
	}
	
	@RequestMapping(value = "/getUserDetail", method = RequestMethod.GET)
	public UserDetail getUserDetail(
			@RequestParam(value = "userid", defaultValue = "noUserId") String userid,
            @CookieValue(value = "token", defaultValue = "noToken") final String token){
		
		// check privilege
		User user = myDao.getUserFromCookie(token);
		if(user.getPrivilege()==null || user.getPrivilege().compareTo(UserPrivilege.admin)<0){
			return null;
		}
		
		UserDetail ret;
		ret = myDao.getUserDetail(userid);
		return ret;
	}
	
	@RequestMapping(value = "/getPersonalInfoRequest", method = RequestMethod.GET)
	public PersonalInfo getPersonalInfo(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie){
		
		PersonalInfo ret;
		ret = myDao.getPersonalInfo(myDao.getUserFromCookie(cookie).getUserID());
		return ret;
	}
	
	@RequestMapping(value = "/updateShowPhoneInChat", method = RequestMethod.GET)
	public ErrorMsg updateShowPhoneInChat(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "showToDriver", defaultValue = "false") String showToDriver,
			@RequestParam(value = "showToOwner", defaultValue = "false") String showToOwner,
			@RequestParam(value = "showToHer", defaultValue = "false") String showToHer){
		
		ErrorMsg ret = new ErrorMsg();
		if(myDao.updateShowPhoneInChat(myDao.getUserFromCookie(cookie).getUserID(),
				Boolean.parseBoolean(showToDriver),
				Boolean.parseBoolean(showToOwner),
				Boolean.parseBoolean(showToHer))){
			ret.setText("设置保存成功");
		} else {
			ret.setText("设置保存失败");
		}
		return ret;
	}
	
	@RequestMapping(value = "/updateShowFPInChat", method = RequestMethod.GET)
	public ErrorMsg updateShowFPInChat(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "showToDriver", defaultValue = "false") String showToDriver,
			@RequestParam(value = "showToOwner", defaultValue = "false") String showToOwner,
			@RequestParam(value = "showToHer", defaultValue = "false") String showToHer){
		
		ErrorMsg ret = new ErrorMsg();
		if(myDao.updateShowFPInChat(myDao.getUserFromCookie(cookie).getUserID(),
				Boolean.parseBoolean(showToDriver),
				Boolean.parseBoolean(showToOwner),
				Boolean.parseBoolean(showToHer))){
			ret.setText("设置保存成功");
		} else {
			ret.setText("设置保存失败");
		}
		return ret;
	}

}
