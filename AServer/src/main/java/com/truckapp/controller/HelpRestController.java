package com.truckapp.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.truckapp.database.DAO;
import com.truckapp.database.Event;
import com.truckapp.database.GroupChatId;
import com.truckapp.database.Pic;
import com.truckapp.database.PicCompositeId;
import com.truckapp.database.User;
import com.truckapp.database.UserPrivilege;
import com.truckapp.util.ChatGroupThread;
import com.truckapp.util.ChatUserThread;
import com.truckapp.util.Constants;
import com.truckapp.util.GeTuiUtil;
import com.truckapp.valueObjects.ErrorMsg;
import com.truckapp.valueObjects.SOSDetail;


@RestController
public class HelpRestController {

    private DAO myDao;

    @Value("#{DAO}")
    public void setMyDao (DAO myDao) {
        this.myDao = myDao;
    }

    private static Logger myLogger = Logger.getLogger(HelpRestController.class.getName());

    @RequestMapping(value = "/HelpRequest", method = RequestMethod.GET)
    public ErrorMsg helpRequest (
                                 @RequestParam(value = "longitude", defaultValue = "noLongitude") String longitude,
                                 @RequestParam(value = "latitude", defaultValue = "noLatitude") String latitude,
                                 @RequestParam(value = "radius", defaultValue = Constants.HELP_DEF_RADIUS) String radius,
                                 @RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,
                                 @RequestParam(value = "text", defaultValue = "") String text,
                                 @RequestParam(value = "picIDs", defaultValue = "") String picIDs,
                                 @RequestParam(value = "province", defaultValue = "未知省") String province,
                                 @RequestParam(value = "city", defaultValue = "未知市") String city,
                                 @RequestParam(value = "district", defaultValue = "未知县") String district,
                                 @RequestParam(value = "roadNum", defaultValue = "未知道路") String roadNum) {

        myLogger.info("Help Request params: " + "longitude = " + longitude
                      + " latitude = " + latitude + " radius = " + radius +
                      " cookie = " + cookie + " text = " + text +
                      " picIDs = " + picIDs);

        ErrorMsg msg = new ErrorMsg();

        User user = myDao.getUserFromCookie(cookie);
        String userID = user.getUserID();

        // 内测开放所有权限
        // if(user.getPrivilege()==null || user.getPrivilege().compareTo(UserPrivilege.pc60)<0){
        // msg.setText("您的权限不允许发送信号弹！");
        // return msg;
        // }

        double longi = 0.0;
        double lati = 0.0;
        double radi = 0.0;

        try {
            longi = Double.parseDouble(longitude);
            lati = Double.parseDouble(latitude);
            radi = Double.parseDouble(radius);
        }
        catch (NumberFormatException e) {
            myLogger.log(Level.INFO, e.getMessage(), e);
            msg.setText("输入的数字无效。请稍后重试。");
        }

        Event helpEvent = new Event();
        helpEvent.setEventID(UUID.randomUUID().toString());
        helpEvent.setOccurTime(new Date());
        helpEvent.setLongitude(longi);
        helpEvent.setLatitude(lati);
        helpEvent.setRadius(radi);
        helpEvent.setEventInfo("来自另一位兄弟的求助: " + text);
        helpEvent.setEventType(Constants.EVENT_TYPE_HELP);
        helpEvent.setSenderID(user.getUserID());
        helpEvent.setProvince(province);
        helpEvent.setCity(city);
        helpEvent.setDistrict(district);
        helpEvent.setRoadNum(roadNum);

        String[] pics = picIDs.split(",");
        for (String pic : pics) {
            if (pic.equals(""))
                continue;
            Pic newPic = new Pic(new PicCompositeId(helpEvent.getEventID(), pic));
            helpEvent.getPics().add(newPic);
            myDao.insertIntoDB(newPic, Pic.class);
        }
        myDao.insertIntoDB(helpEvent, Event.class);

        List<User> users = myDao.getUsersByLocation(longi, lati, radi);
        
        GroupChatId gcid = new GroupChatId();
        gcid.setAddress(user.getUserInformation().getNickName() + "的求助--SOS群");
        gcid.setChatId(helpEvent.getEventID());
        myDao.insertIntoDB(gcid, GroupChatId.class);
        myDao.createSOSChatGroup(gcid.getChatId(), users);

        int index = -1;
        for(index=0;index<users.size();index++){
        	if(users.get(index).getUserID().equals(userID)){
        		break;
        	}
        }
        if(index >=0 && index < users.size())
        	users.remove(index);
		// create chat group in openfire
		new Thread(new ChatGroupThread(gcid.getChatId())).start();
        	
        try {
            GeTuiUtil.pushNewSOSToClients(users);
            msg.setText(Integer.toString(users.size()));
        }
        catch (Exception e) {
            msg.setText("推送SOS出错。");
        }
        return msg;
    }

    @RequestMapping(value = "/getHelpNum", method = RequestMethod.GET)
    public ErrorMsg getHelpNum (
                                @RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie)
    {
        ErrorMsg msg = new ErrorMsg();
        Event e = myDao.getSOSEventForUser(myDao.getUserFromCookie(cookie).getUserID());
        if (e == null) {
            msg.setText("服务器未找到您的SOS");
        }
        else {
            List<User> users =
                    myDao.getUsersByLocation(e.getLongitude(), e.getLatitude(), e.getRadius());
            msg.setText(Integer.toString(users.size()-1));
        }
        return msg;
    }

    @RequestMapping(value = "/deleteSOS", method = RequestMethod.GET)
    public ErrorMsg deleteSOS (
                               @RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie)
    {
        ErrorMsg msg = new ErrorMsg();
        Event e = myDao.getSOSEventForUser(myDao.getUserFromCookie(cookie).getUserID());
        if (e == null) {
            msg.setText("服务器未找到您的SOS");
        }
        else {
            myDao.deleteEventIfExists(e.getEventID());
            myDao.deleteSOSChatGroup(e.getEventID());
            msg.setText("取消SOS成功");
        }
        return msg;
    }

    @RequestMapping(value = "/getSOSDetail", method = RequestMethod.GET)
    public SOSDetail getSOSDetail (
                                   @RequestParam(value = "eventID", defaultValue = "") String eventID)
    {
        if (eventID.equals(""))
            return null;
        return myDao.getSOSDetail(eventID);
    }

}
