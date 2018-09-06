package com.truckapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.truckapp.database.DAO;
import com.truckapp.database.User;
import com.truckapp.database.UserPrivilege;
import com.truckapp.util.Constants;

@Controller
public class MapViewController {

	private DAO myDao;

	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}

	@RequestMapping(value = "/eventsMapView", method = RequestMethod.GET)
	public ModelAndView eventsMapView(
			Model model,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

		// check privilege
		User user = myDao.getUserFromCookie(cookieToken);
		if (user.getPrivilege() == null
				|| user.getPrivilege().compareTo(UserPrivilege.admin) < 0) {
			return null;
		}

		return new ModelAndView("eventsMapView");
	}

	@RequestMapping(value = "/usersMapView", method = RequestMethod.GET)
	public ModelAndView usersMapView(
			Model model,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {

		// check privilege
		User user = myDao.getUserFromCookie(cookieToken);
		if (user.getPrivilege() == null
				|| user.getPrivilege().compareTo(UserPrivilege.admin) < 0) {
			return null;
		}

		return new ModelAndView("usersMapView");
	}
}
