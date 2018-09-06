package com.truckapp.controller;

import java.util.Date;
import java.util.List;
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
import com.truckapp.database.Event;
import com.truckapp.database.EventInteraction;
import com.truckapp.database.EventInteractionCompositeId;
import com.truckapp.database.Pic;
import com.truckapp.database.PicCompositeId;
import com.truckapp.database.User;
import com.truckapp.database.UserPrivilege;
import com.truckapp.util.Constants;
import com.truckapp.util.GeTuiUtil;
import com.truckapp.valueObjects.BackEndEventList;
import com.truckapp.valueObjects.ErrorMsg;
import com.truckapp.valueObjects.EventDetail;
import com.truckapp.valueObjects.ReceivedEventList;
import com.truckapp.valueObjects.BackEndUserList.BackEndUser;

@RestController
public class EventRestController {

	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}

	private static Logger logger = Logger
			.getLogger(EventRestController.class.getName());

	@RequestMapping(value = "/newEventRequest", method = RequestMethod.GET)
	public ErrorMsg newEventRequest(
			@RequestParam(value = "eventType", defaultValue = "noType") String eventtype,
			@RequestParam(value = "longitude", defaultValue = "119.1") String longitude,
			@RequestParam(value = "latitude", defaultValue = "39.1") String latitude,
			@RequestParam(value = "radius", defaultValue = Constants.EVENT_DEF_RADIUS_STR) String radius,
			@RequestParam(value = "eventInfo", defaultValue = "没有描述") String eventinfo,

			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie,

			@RequestParam(value = "roadNum", defaultValue = "未知道路") String roadNum,
			@RequestParam(value = "province", defaultValue = "未知省") String province,
			@RequestParam(value = "city", defaultValue = "未知市") String city,
			@RequestParam(value = "district", defaultValue = "未知县") String district,
			@RequestParam(value = "picIDs", defaultValue = "") String picIds) {

		logger.info("New Event Request params: " + " longitude = "
				+ longitude + " latitude = " + latitude + " cookie = " + cookie
				+ " eventInfo = " + eventinfo + " picIDs = " + picIds);

		ErrorMsg msg = new ErrorMsg();
		Event newEvent = new Event();
		newEvent.setEventID(UUID.randomUUID().toString());
		newEvent.setEventType(eventtype);
		
		try {

			String userID = myDao.getUserFromCookie(cookie).getUserID();
			newEvent.setLongitude(Double.parseDouble(longitude));
			newEvent.setLatitude(Double.parseDouble(latitude));
			newEvent.setRadius(Double.parseDouble(radius));
			newEvent.setOccurTime(new Date());
			newEvent.setEventInfo(eventinfo);
			newEvent.setSenderID(userID);

			newEvent.setRoadNum(roadNum.equals("") ? "未知道路" : roadNum);
			newEvent.setProvince(province);
			newEvent.setCity(city);
			newEvent.setDistrict(district);

			String[] pics = picIds.split(",");
			for (String pic : pics) {
				if (pic.equals(""))
					continue;
				Pic newPic = new Pic(new PicCompositeId(newEvent.getEventID(),
						pic));
				newEvent.getPics().add(newPic);
				myDao.insertIntoDB(newPic, Pic.class);
			}
			myDao.insertIntoDB(newEvent, Event.class);

			msg.setText("上传路况成功！");

			List<User> users = myDao.getUsersByLocation(
					Double.parseDouble(longitude),
					Double.parseDouble(latitude), Double.parseDouble(radius));
			
			int index = -1;
	        for(index=0;index<users.size();index++){
	        	if(users.get(index).getUserID().equals(userID)){
	        		break;
	        	}
	        }
	        if(index >=0 && index < users.size())
	        	users.remove(index);
	        
			GeTuiUtil.pushNewEventToClients(users);

		} catch (NumberFormatException e) {
			logger.log(Level.INFO, e.getMessage(), e);
			msg.setText("输入的数字无法被理解。");
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
			msg.setText("创建新路况出错。请稍后再试。");
		}
		return msg;
	}

	@RequestMapping(value = "/getEventsRequest", method = RequestMethod.GET)
	public ReceivedEventList getEventsRequest(
			@RequestParam(value = "longitude", defaultValue = "noLongitude") String longitude,
			@RequestParam(value = "latitude", defaultValue = "noLatitude") String latitude) {

		logger.info("\tGet Events Request : eventList longitude = "
				+ longitude + " latitude = " + latitude);

		ReceivedEventList toReturn = new ReceivedEventList();
		toReturn.setEvents(myDao.getEventsByLocation(
				Double.parseDouble(longitude), Double.parseDouble(latitude)));
		toReturn.setTravelTogetherCnt(Integer.toString(myDao.getAllUsersForBackEnd().size()));
		return toReturn;

	}

	@RequestMapping(value = "/getAllEventsForAllUsers", method = RequestMethod.GET)
	public BackEndEventList getAllEventsForAllUsers(
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

		BackEndEventList toReturn = new BackEndEventList();
		toReturn.setEvents(myDao.getAllEvents());
		return toReturn;

	}

	@RequestMapping(value = "/getEventDetail", method = RequestMethod.GET)
	public EventDetail getEventDetail(
			@RequestParam(value = "eid", defaultValue = "noEid") String eid,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

		User user = myDao.getUserFromCookie(cookieToken);
		if (user.getPrivilege() == null
				|| user.getPrivilege().compareTo(UserPrivilege.admin) < 0) {
			return null;
		}

		EventDetail toReturn = myDao.getEventDetail(eid);
		return toReturn;

	}

	@RequestMapping(value = "/deleteSpecifiedEvent", method = RequestMethod.GET)
	public void deleteSpecifiedEvent(
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken,
			@RequestParam(value = "eid", defaultValue = "noEid") String eid) {

		logger.info("Delete event request: Eid = " + eid);
		myDao.deleteEventIfExists(eid);
		return;

	}

	@RequestMapping(value = "/searchEventsRequest", method = RequestMethod.GET)
	public ReceivedEventList getEventsOnSearchRequest(
			@RequestParam(value = "longitude", defaultValue = "50.0") String longitude,
			@RequestParam(value = "latitude", defaultValue = "50.0") String latitude,
			@RequestParam(value = "roadNum", defaultValue = "") String roadNum,
			@RequestParam(value = "province", defaultValue = "") String province,
			@RequestParam(value = "city", defaultValue = "") String city,
			@RequestParam(value = "district", defaultValue = "") String district) {

		logger.info("\tSearch Events Request : eventList roadNo. = "
				+ roadNum + " province = " + province + " city = " + city
				+ " district = " + district);

		ReceivedEventList toReturn = new ReceivedEventList();
		toReturn.setEvents(myDao.getEventsByFieldEntry(roadNum, province, city,
				district, Double.parseDouble(longitude),
				Double.parseDouble(latitude)));
		return toReturn;

	}

	@RequestMapping(value = "/voteUpEvent", method = RequestMethod.GET)
	public ErrorMsg voteUpEvent(
			@RequestParam(value = "eventId", defaultValue = "noEId") String eventId,
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie) {
		ErrorMsg msg = new ErrorMsg();
		String userId = myDao.getUserFromCookie(cookie).getUserID();
		if (myDao.ownerOfEvent(eventId, userId)) {
			msg.setText("不能给自己点赞！");
		} else if (myDao.userHasNotVoted(eventId, Constants.VOTE_UP_EVENT,
				userId)) {
			EventInteraction ei = new EventInteraction();
			ei.setEventInteractionCompositeId(new EventInteractionCompositeId(
					eventId, Constants.VOTE_UP_EVENT, userId));
			myDao.insertIntoDB(ei, EventInteraction.class);
			msg.setText("点赞成功！");
		} else {
			msg.setText("您已经赞过这个路况了!");
		}
		return msg;
	}

	@RequestMapping(value = "/voteDownEvent", method = RequestMethod.GET)
	public ErrorMsg voteDownEvent(
			@RequestParam(value = "eventId", defaultValue = "noEId") String eventId,
			@RequestParam(value = "cookie", defaultValue = Constants.NO_TOKEN) String cookie) {
		ErrorMsg msg = new ErrorMsg();
		String userId = myDao.getUserFromCookie(cookie).getUserID();
		if (myDao.ownerOfEvent(eventId, userId)) {
			msg.setText("不能举报自己！");
		} else if (myDao.userHasNotVoted(eventId, Constants.VOTE_REPORT_EVENT,
				userId)) {
			EventInteraction ei = new EventInteraction();
			ei.setEventInteractionCompositeId(new EventInteractionCompositeId(
					eventId, Constants.VOTE_REPORT_EVENT, userId));
			myDao.insertIntoDB(ei, EventInteraction.class);
			msg.setText("举报已收到！我们将会对此路况进行人工检查。");
		} else {
			msg.setText("您已经举报过这个路况了!");
		}
		return msg;
	}

	@RequestMapping(value = "/uploadBulkNewEvents", method = RequestMethod.POST)
	public @ResponseBody ErrorMsg uploadBulkNewEvents(
			@RequestBody ErrorMsg eventsText,
			@CookieValue(value = "token", defaultValue = "noToken") String token) {
		
		logger.info(eventsText.getText());
		
		ErrorMsg msg = new ErrorMsg();

//		User user = myDao.getUserFromCookie(token);
//		if (user.getPrivilege().compareTo(UserPrivilege.admin) < 0) {
//			msg.setText("您的权限不够进行此操作");
//			return msg;
//		}
		
		String[] parts = eventsText.getText().split(Constants.EVENT_TEXT_DELIMITER);
		String longitude = parts[0];
		String latitude = parts[1];
		String eventtype = parts[2];
		String eventinfo = parts[3];
		String province = parts[4];
		String city = parts[5];
		String district = parts[6];
		String roadNum = parts[7];
		
		Event newEvent = new Event();
		newEvent.setEventID(UUID.randomUUID().toString());
		newEvent.setEventType(eventtype);
		try {
			newEvent.setLongitude(Double.parseDouble(longitude));
			newEvent.setLatitude(Double.parseDouble(latitude));
			newEvent.setRadius(Double.parseDouble(Constants.EVENT_DEF_RADIUS_STR));
			newEvent.setOccurTime(new Date());
			newEvent.setEventInfo(eventinfo);
			newEvent.setSenderID(myDao.getUserFromCookie(token).getUserID());

			newEvent.setRoadNum(roadNum);
			newEvent.setProvince(province);
			newEvent.setCity(city);
			newEvent.setDistrict(district);

			myDao.insertIntoDB(newEvent, Event.class);

			msg.setText("上传路况成功！");

			GeTuiUtil.pushNewEventToClients(myDao.getUsersByLocation(
					Double.parseDouble(longitude),
					Double.parseDouble(latitude), Double.parseDouble(Constants.EVENT_DEF_RADIUS_STR)));

		} catch (NumberFormatException e) {
			logger.log(Level.INFO, e.getMessage(), e);
			msg.setText("输入的数字无法被理解。");
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
			msg.setText("创建新路况出错。请稍后再试。");
		}
		return msg;
	}

}
