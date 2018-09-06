package com.truckapp.controller;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.truckapp.database.DAO;
import com.truckapp.database.Event;
import com.truckapp.database.User;
import com.truckapp.database.UserPrivilege;
import com.truckapp.util.Constants;
import com.truckapp.util.GeTuiUtil;

@Controller
public class EventController {

	private DAO myDao;

	private static Logger logger = Logger.getLogger(EventController.class
			.getName());

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}

	@RequestMapping(value = "/newEvent", method = RequestMethod.GET)
	public ModelAndView newEvent(
			ModelMap model,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

		if (!myDao.checkValidCookieExists(cookieToken)) {
			model.addAttribute("userObj", new User());
			return new ModelAndView("login");
		}

		Event e = new Event();
		e.setOccurTime(new Date());
		e.setRadius(Constants.EVENT_DEF_RADIUS);
		model.addAttribute("eventObj", e);
		return new ModelAndView("newEvent");

	}

	@RequestMapping(value = "/submitEvent", method = RequestMethod.POST)
	public ModelAndView submitEvent(
			ModelMap model,
			@ModelAttribute("eventObj") Event event,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

		event.setEventID(UUID.randomUUID().toString());
		event.setSenderID(myDao.getUserFromCookie(cookieToken).getUserID());
		myDao.insertIntoDB(event, Event.class);

		try {
			GeTuiUtil.pushNewEventToClients(myDao.getUsersByLocation(
					event.getLongitude(), event.getLatitude(),
					event.getRadius()));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

		return newEvent(model, cookieToken);

	}

	@RequestMapping(value = "/manageEvents", method = RequestMethod.GET)
	public ModelAndView manageEvents(
			ModelMap model,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

		User user = myDao.getUserFromCookie(cookieToken);
		if (user.getPrivilege().compareTo(UserPrivilege.admin) < 0) {
			model.addAttribute("userObj", new User());
			return new ModelAndView("login");
		}

		return new ModelAndView("manageEvents");

	}

	@RequestMapping(value = "/bulkNewEvents", method = RequestMethod.GET)
	public ModelAndView bulkNewEvents(
			ModelMap model,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

//		User user = myDao.getUserFromCookie(cookieToken);
//		if (user.getPrivilege().compareTo(UserPrivilege.admin) < 0) {
//			model.addAttribute("userObj", new User());
//			return new ModelAndView("login");
//		}
		
		return new ModelAndView("bulkNewEvents");
	}
}
