package com.truckapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.truckapp.database.DAO;
import com.truckapp.util.ChatRequestParams;
import com.truckapp.util.Constants;
import com.truckapp.util.Utility;
import com.truckapp.valueObjects.ChatUserProfile;
import com.truckapp.valueObjects.ErrorMsg;
import com.truckapp.valueObjects.ReceivedChatList;
import com.truckapp.valueObjects.ReceivedChatList.ReceivedChat;

@RestController
public class ChatRestController {

	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}
	
	private static Logger logger = Logger.getLogger(AccountRestController.class.getName());

	@RequestMapping(value = "/fetchChatList", method = RequestMethod.GET)
	public ReceivedChatList fetchChatList(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "province", defaultValue = "") String province,
			@RequestParam(value = "city", defaultValue = "") String city,
			@RequestParam(value = "district", defaultValue = "") String district){
		
		ReceivedChatList ret = new ReceivedChatList();
		ret.setChats(new ArrayList<ReceivedChat>());
		
		String userId = myDao.getUserFromCookie(cookie).getUserID();
		List<String> chatNames1 = myDao.generateChatNamesByHometown(userId);
		List<String> chatNames2 = myDao.generateChatNamesByLocation(
				Utility.trimLocationText(province), 
				Utility.trimLocationText(city), 
				Utility.trimLocationText(district));
		List<String> chatNames3 = myDao.generateChatNamesByGoodTypes(userId);
		List<String> chatNames4 = myDao.generateChatNamesByLocks(userId);
		myDao.fillInChatList(ret, chatNames1, chatNames2, chatNames3, chatNames4, userId);
		return ret;
	}
	
	@RequestMapping(value = "/fetchChatUserProfile", method = RequestMethod.GET)
	public ChatUserProfile fetchChatUserProfile(
			@RequestParam(value = "userId", defaultValue = Constants.NO_TOKEN) String userId){
		
		ChatUserProfile ret = myDao.fillInChatUserProfile(userId);
		return ret;
	}
	
	@RequestMapping(value = "/updateChatSetting", method = RequestMethod.GET)
	public ErrorMsg updateChatSetting(
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
			@RequestParam(value = "chatId", defaultValue = "noChatId") String chatId,
			@RequestParam(value = "muted", defaultValue = "false") String muted,
			@RequestParam(value = "locked", defaultValue = "false") String locked,
			@RequestParam(value = "top", defaultValue = "false") String top){
		
		ErrorMsg ret = new ErrorMsg();
		if(myDao.saveChatSetting(
				myDao.getUserFromCookie(cookie).getUserID(), 
				chatId, 
				Boolean.parseBoolean(muted), 
				Boolean.parseBoolean(locked),
				Boolean.parseBoolean(top))){
			ret.setText("Success saving chat setting.");
		} else {
			ret.setText("Chat setting not saved...");
		}
		return ret;
	}
}
