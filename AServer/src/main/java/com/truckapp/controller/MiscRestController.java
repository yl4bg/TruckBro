package com.truckapp.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.truckapp.database.DAO;
import com.truckapp.util.Constants;
import com.truckapp.util.GeTuiUtil;
import com.truckapp.util.SmackTrialUtil;
import com.truckapp.util.Utility;
import com.truckapp.valueObjects.ErrorMsg;
import com.truckapp.valueObjects.ReceivedChatList;
import com.truckapp.valueObjects.ReceivedChatList.ReceivedChat;

@RestController
public class MiscRestController{
	
	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}
	
	@RequestMapping(value = "/createAllChatGroups", method = RequestMethod.GET)
    public ErrorMsg createAllChatGroups(
                    @CookieValue(value = "token", defaultValue = "noToken") final String token
                    ){
		ErrorMsg ret = new ErrorMsg();
		String[] provinces = Constants.provinces;
		String[][] cities = Constants.cities;
		String[][][] districts = Constants.districts;
		
		Set<String> allChatGroupNames = new HashSet<String>();
		for(int i=1;i<provinces.length;i++){

			allChatGroupNames.add(Utility.trimLocationText(provinces[i]) + Constants.HOMETOWN_GROUP);
			allChatGroupNames.add(Utility.trimLocationText(provinces[i]) + Constants.LOCAL_GROUP);
			
			for(int j=1;j<cities[i-1].length;j++){
				
				allChatGroupNames.add(Utility.trimLocationText(provinces[i]) + 
						Utility.trimLocationText(cities[i-1][j]) + Constants.HOMETOWN_GROUP);
				allChatGroupNames.add(Utility.trimLocationText(provinces[i]) + 
						Utility.trimLocationText(cities[i-1][j]) + Constants.LOCAL_GROUP);
				
				for(int k=1;k<districts[i-1][j-1].length;k++){
					String province = Utility.trimLocationText(provinces[i]);
					String city = Utility.trimLocationText(cities[i-1][j]);
					String district = Utility.trimLocationText(districts[i-1][j-1][k]);
					
					StringBuilder sb = new StringBuilder();
					sb.append(province);
					sb.append(city);
					sb.append(district);
					
					allChatGroupNames.add(sb.toString() + Constants.HOMETOWN_GROUP);
					allChatGroupNames.add(sb.toString() + Constants.LOCAL_GROUP);
				}
			}
		}

		for(String s : Constants.goodTypes){
			allChatGroupNames.add(s + Constants.GOOD_TYPE_GROUP);
		}
		
		ReceivedChatList chatList = new ReceivedChatList();
		chatList.setChats(new ArrayList<ReceivedChat>());
		
		myDao.fillInChatList(chatList, new ArrayList<String>(allChatGroupNames), new ArrayList<String>(), 
				new ArrayList<String>(), new ArrayList<String>(), myDao.getUserFromCookie(token).getUserID());
		
		AbstractXMPPConnection conn = null;
		try {
			conn = SmackTrialUtil.connectToOF();
			for(ReceivedChat rc : chatList.getChats()){
				SmackTrialUtil.createGroupChatRoom(conn, rc.getChatRoomId());
			}
		} 
		catch (Exception e){
			// log if needed
		}
		finally {
			if(conn != null)
				conn.disconnect();
		}
		return ret;
	}
	
	@RequestMapping(value = "/pushMsgToUser", method = RequestMethod.GET)
    public ErrorMsg pushMsgToUser(
                    @RequestParam(value = "deviceID", defaultValue = "") final String deviceID,
                    @RequestParam(value = "msg", defaultValue = "无消息内容") final String msg,
                    @CookieValue(value = "token", defaultValue = "noToken") final String token
                    ){
		
		ErrorMsg ret = new ErrorMsg();
		if(GeTuiUtil.pushMsgToUser(myDao.getUserFromCookie(token).getUserID(), msg, 
				new ArrayList<String>(){{add(deviceID);}})){
			ret.setText("推送消息成功!");
		} else {
			ret.setText("推送消息失败!");
		}
		return ret;
		
	}
	
	@RequestMapping(value = "/pushMsgToAllUsers", method = RequestMethod.GET)
    public ErrorMsg pushMsgToAllUsers(
                    @RequestParam(value = "msg", defaultValue = "无消息内容") final String msg,
                    @CookieValue(value = "token", defaultValue = "noToken") final String token
                    ){
		
		ErrorMsg ret = new ErrorMsg();
		if(GeTuiUtil.pushMsgToUser(myDao.getUserFromCookie(token).getUserID(), msg, 
				myDao.getAllUserDevices())){
			ret.setText("推送消息成功!");
		} else {
			ret.setText("推送消息失败!");
		}
		return ret;
		
	}
	
	@RequestMapping(value = "/getCurrentAppVersion", method = RequestMethod.GET)
    public ErrorMsg getCurrentAppVersion(){
		
		ErrorMsg ret = new ErrorMsg();
		ret.setText(Constants.APP_VERSION);
		return ret;
		
	}
}
