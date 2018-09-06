package com.truckapp.controller;

import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.truckapp.database.DAO;
import com.truckapp.database.User;
import com.truckapp.database.UserDevice;
import com.truckapp.database.UserPrivilege;
import com.truckapp.util.Constants;

@Controller
public class LogInController {
	
	private DAO myDao;

	private static Logger logger = Logger.getLogger(LogInController.class.getName());
	
	@Value("#{DAO}")
	public void setMyDao(DAO myDao) {
		this.myDao = myDao;
	}
	
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public ModelAndView loginForm(Model model,
    		@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {
    	
    	if(cookieToken!=Constants.NO_TOKEN && myDao.checkValidCookieExists(cookieToken)){
    		model.addAttribute("userObj", myDao.getUserFromCookie(cookieToken));
    		return new ModelAndView("loggedin");
    	} else {
	        model.addAttribute("userObj", new User());
	        return new ModelAndView("login");
    	}
    }

    @RequestMapping(value="/submitLogin", method=RequestMethod.POST)
    public ModelAndView loginSubmit(@ModelAttribute("userObj") User user, Model model,
    		HttpServletResponse response,
			@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken) {
    	
    	if(myDao.checkPw(user)){
    		User u = myDao.getUser(user.getUserID());
    		if(u.getDevice()==null) u.setDevice(new UserDevice(u.getUserID()));
    		u.getDevice().setCookie(UUID.randomUUID().toString());
			response.addCookie(new Cookie("token", u.getDevice().getCookie()));
			myDao.insertIntoDB(u, User.class);
            model.addAttribute("userObj", u);
            
            return new ModelAndView("loggedin");
    	} else {
    		model.addAttribute("userObj", new User());
    		return new ModelAndView("login");
    	}
    	
    }
    
    @RequestMapping(value="/logout")
    public ModelAndView logout(Model model,
    		@CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken){
    	if(myDao.checkValidCookieExists(cookieToken)){
    		myDao.deleteCookie(cookieToken);
    	}
    	model.addAttribute("userObj", new User());
    	return new ModelAndView("login");
    }
    


    @RequestMapping(value="/manageUsers", method=RequestMethod.GET)
    public ModelAndView manageUsers(
           ModelMap model,
           @CookieValue(value = "token", defaultValue = Constants.NO_TOKEN) String cookieToken){
        
        User user = myDao.getUserFromCookie(cookieToken);
        if(user.getPrivilege().compareTo(UserPrivilege.admin) < 0){
            model.addAttribute("userObj", new User());
            return new ModelAndView("login");
        }
        
        return new ModelAndView("manageUsers");
        
    }

}