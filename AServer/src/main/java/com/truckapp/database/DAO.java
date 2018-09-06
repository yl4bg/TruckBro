package com.truckapp.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.truckapp.util.Constants;
import com.truckapp.util.SHA_Hash;
import com.truckapp.util.Utility;
import com.truckapp.valueObjects.BackEndEventList.BackEndEvent;
import com.truckapp.valueObjects.BackEndUserList.BackEndUser;
import com.truckapp.valueObjects.ChatUserProfile;
import com.truckapp.valueObjects.EventDetail;
import com.truckapp.valueObjects.EventDetail.Sender;
import com.truckapp.valueObjects.EventDetail.ShortUser;
import com.truckapp.valueObjects.PersonalInfo;
import com.truckapp.valueObjects.PointsHistoryList.PointsHistory;
import com.truckapp.valueObjects.ReceivedChatList;
import com.truckapp.valueObjects.ReceivedChatList.ReceivedChat;
import com.truckapp.valueObjects.ReceivedEventList.ReceivedEvent;
import com.truckapp.valueObjects.SOSDetail;
import com.truckapp.valueObjects.UserDetail;


public class DAO {

    // these constants are in longitude and latitude DEGREES
    private static final int LONGITUDE_RANGE_CAP = 10;
    private static final int LATITUDE_RANGE_CAP = 10;

    private static Logger logger = Logger.getLogger(DAO.class.getName());

    private static User dummyUser;
    static {
        dummyUser = new User();
        dummyUser.setUserID("1234567890");
        dummyUser.setUsername("DummyUser");
        dummyUser.setLocation(new UserLocation());
        dummyUser.setDevice(new UserDevice());
        dummyUser.setUserInformation(new UserInformation());
        dummyUser.setPrivilege(UserPrivilege.basic);
        dummyUser.setPoints(0);
        dummyUser.setReferPerson("123456");
    }

    public void insertIntoDB (Object obj, Class typeClass) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(typeClass.cast(obj));
        session.getTransaction().commit();
    }

    public User getUser (String userID) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            User user = (User) session.load(User.class, userID);
            Hibernate.initialize(user);
            Hibernate.initialize(user.getLocation());
            Hibernate.initialize(user.getDevice());
            return user;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public List<User> getUsersByLocation (double longitude, double latitude, double radius) {
        List<User> result = new ArrayList<User>();

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(User.class);
            criteria.createAlias("location", "l");
            criteria.add(Restrictions.between("l.longitude", longitude - LONGITUDE_RANGE_CAP,
                                              longitude + LONGITUDE_RANGE_CAP));
            criteria.add(Restrictions.between("l.latitude", latitude - LATITUDE_RANGE_CAP,
                                              latitude + LATITUDE_RANGE_CAP));
            List<User> users = (List<User>) criteria.list();

            for (User user : users) {

                if (radius >= Utility.distFrom(longitude, latitude, user.getLocation()
                        .getLongitude(), user.getLocation().getLatitude())) {
                    result.add(user);
                }

                Hibernate.initialize(user.getDevice());
            }
            return result;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    /**
     * retrieves the list of matched events with the given
     * longitude and latitude.
     * 
     * @param longitude
     * @param latitude
     * @return
     */
    public List<ReceivedEvent> getEventsByLocation (double longitude, double latitude) {
        List<ReceivedEvent> result = new ArrayList<ReceivedEvent>();

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql =
                    "SELECT * FROM events WHERE (longitude BETWEEN :longiFloor AND :longiCeil) AND (latitude BETWEEN :latiFloor AND :latiCeil)";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(Event.class);
            query.setParameter("longiFloor", longitude - LONGITUDE_RANGE_CAP);
            query.setParameter("longiCeil", longitude + LONGITUDE_RANGE_CAP);
            query.setParameter("latiFloor", latitude - LATITUDE_RANGE_CAP);
            query.setParameter("latiCeil", latitude + LATITUDE_RANGE_CAP);
            List<Event> matchedEvents = (List<Event>) query.list();

            for (Event event : matchedEvents) {

                if (event.getRadius() >= Utility
                        .distFrom(longitude, latitude, event.getLongitude(), event.getLatitude())) {
                    ReceivedEvent toBeAdded = new ReceivedEvent(event);
                    calculateForEvents(toBeAdded, event, longitude, latitude);
                    User sender = (User) session.load(User.class, event.getSenderID());
                    toBeAdded.setOfficial(sender.getPrivilege().compareTo(UserPrivilege.admin) >= 0);
                    toBeAdded.setSenderName(sender.getUserInformation().getNickName());
                    result.add(toBeAdded);
                }
            }
            Collections.sort(result);

            return result;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    private void calculateForEvents (ReceivedEvent toBeAdded, Event event,
                                     double userLong, double userLat) {

        toBeAdded.setDirection(Utility.DIRECTION.get(
                Utility.directionBtwPoints(userLong,
                                           userLat,
                                           toBeAdded.getLongitude(),
                                           toBeAdded.getLatitude()
                        )));

        toBeAdded.setDistance(
                Utility.distFrom(userLong,
                                 userLat,
                                 toBeAdded.getLongitude(),
                                 toBeAdded.getLatitude()
                        ));

        toBeAdded.setTimePassed(Utility.timePassed(
                                                   toBeAdded.getReportTime(),
                                                   new Date()
                ));
    }

    /**
     * retrieves the list of matched events for the given roadNum, province, city, or district
     * 
     * @param roadNum
     * @param province
     * @param city
     * @param district
     * @return
     */
    public List<ReceivedEvent> getEventsByFieldEntry (String roadNum,
                                                      String province,
                                                      String city,
                                                      String district,
                                                      double longtidue,
                                                      double latitude) {
        List<ReceivedEvent> result = new ArrayList<ReceivedEvent>();

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(Event.class);
            if (!roadNum.equals(""))
                criteria.add(Restrictions.ilike("roadNum", "%" + roadNum + "%"));
            if (!province.equals(""))
                criteria.add(Restrictions.ilike("province", "%" + province + "%"));
            if (!city.equals(""))
                criteria.add(Restrictions.ilike("city", "%" + city + "%"));
            if (!district.equals(""))
                criteria.add(Restrictions.ilike("district", "%" + district + "%"));
            List<Event> matchedEvents = (List<Event>) criteria.list();

            for (Event event : matchedEvents) {
                ReceivedEvent toBeAdded = new ReceivedEvent(event);
                calculateForEvents(toBeAdded, event, longtidue, latitude);
                User sender = (User) session.load(User.class, event.getSenderID());
                toBeAdded.setOfficial(sender.getPrivilege().compareTo(UserPrivilege.admin) >= 0);
                toBeAdded.setSenderName(sender.getUserInformation().getNickName());
                result.add(toBeAdded);
            }
            Collections.sort(result);

            return result;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    /**
     * checks whether this cookie has a valid user associated with it
     * 
     * @param cookieToken
     * @return
     */
    public boolean checkValidCookieExists (String cookieToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM userdevices WHERE cookie = :cookie";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(UserDevice.class);
            query.setParameter("cookie", cookieToken);
            List results = query.list();

            return results.size() > 0;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    /**
     * check valid cookie exists before calling this method
     * 
     * @param cookieToken
     * @return
     */
    public User getUserFromCookie (String cookieToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM userdevices WHERE cookie = :cookie";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(UserDevice.class);
            query.setParameter("cookie", cookieToken);
            List results = query.list();
            String userID = ((UserDevice) results.get(0)).getUserID();
            User user = (User) session.load(User.class, userID);
            if (user == null) return dummyUser;
            Hibernate.initialize(user);
            Hibernate.initialize(user.getLocation());
            Hibernate.initialize(user.getDevice());
            Hibernate.initialize(user.getUserInformation());
            return user;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    /**
     * this method actually creates a new random cookie for the user, effectively disabling the
     * previous one
     * 
     * @param cookieToken
     */
    public void deleteCookie (String cookieToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM userdevices WHERE cookie = :cookie";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(UserDevice.class);
            query.setParameter("cookie", cookieToken);
            List results = query.list();

            UserDevice userDevice = (UserDevice) results.get(0);
            userDevice.setCookie(UUID.randomUUID().toString());
            session.save(userDevice);

        }
        finally {
            session.getTransaction().commit();
        }
    }

    /**
     * checks whether a userID exists in the database
     * 
     * @param userID
     * @return
     */
    public boolean checkUserExists (String userID) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria cr = session.createCriteria(User.class);
            cr.add(Restrictions.eq("userID", userID));
            List results = cr.list();
            return results.size() > 0;

        }
        finally {
            session.getTransaction().commit();
        }
    }

    /**
     * 
     * @param user entity, the pwdhash in this User class is in plain text, not in hash
     * @return true if pw is correct and there is a user match
     */
    public boolean checkPw (User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria cr = session.createCriteria(User.class);
            cr.add(Restrictions.eq("userID", user.getUserID()));
            List results = cr.list();
            if (results.size() == 0) return false;

            User fetched = (User) results.get(0);
            return fetched.getPwdhash().equals(SHA_Hash.getHashedPassword(user.getPwdhash(),
                                                                          fetched.getSalt()));

        }
        finally {
            session.getTransaction().commit();
        }
    }

    public void updateUserInfo (String userID, String cookie, String pushClientID) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            if (user.getDevice() == null) {
                user.setDevice(new UserDevice());
                user.getDevice().setUserID(user.getUserID());
            }
            user.getDevice().setCookie(cookie);
            if (!pushClientID.equals("noPushId"))
                user.getDevice().setDeviceID(pushClientID);
            session.saveOrUpdate(user);
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public boolean userHasNotVoted (
                                    String eventId,
                                    String interaction,
                                    String userId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Event event = (Event) session.load(Event.class, eventId);
            for (EventInteraction ei : event.getInteractions()) {
                if (ei.getEventInteractionCompositeId().getUserId().equals(userId)
                    && ei.getEventInteractionCompositeId().getInteraction().equals(interaction)) { return false; }
            }
            return true;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public boolean ownerOfEvent (String eventId, String userId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Event event = (Event) session.load(Event.class, eventId);
            return event.getSenderID().equals(userId);
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public boolean updateUserLocation (String cookie, String longitude,
                                       String latitude, String pushID) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM userdevices WHERE cookie = :cookie";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(UserDevice.class);
            query.setParameter("cookie", cookie);
            List results = query.list();
            if (results.size() == 0) return false;
            try {
                UserDevice device = (UserDevice) results.get(0);
                User user = (User) session.load(User.class, device.getUserID());
                if (user.getLocation() == null) {
                    user.setLocation(new UserLocation());
                    user.getLocation().setUserID(user.getUserID());
                }
                user.getLocation().setLongitude(Double.parseDouble(longitude));
                user.getLocation().setLatitude(Double.parseDouble(latitude));
                user.getLocation().setTime(new Date());
                session.save(user.getLocation());
                if (!pushID.equals("noPushID")) {
                    user.getDevice().setDeviceID(pushID);
                    session.save(user.getDevice());
                }
                session.save(user);
                return true;
            }
            catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public List<BackEndEvent> getAllEvents () {

        List<BackEndEvent> result = new ArrayList<BackEndEvent>();

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM events";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(Event.class);
            List<Event> allEvents = (List<Event>) query.list();

            for (Event event : allEvents) {
                BackEndEvent toBeAdded = new BackEndEvent(event);
                result.add(toBeAdded);
            }
            Collections.sort(result);

            return result;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public void deleteEventIfExists (String eid) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Event toDel = (Event) session.load(Event.class, eid);
            if (toDel != null) {
                for (EventInteraction interaction : toDel.getInteractions()) {
                    session.delete(interaction);
                }
                for (Pic pic : toDel.getPics()) {
                    session.delete(pic);
                }
                session.delete(toDel);
            }
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public List<BackEndUser> getAllUsersForBackEnd () {

        List<BackEndUser> result = new ArrayList<BackEndUser>();

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM users";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(User.class);
            List<User> allUsers = (List<User>) query.list();

            for (User user : allUsers) {
                BackEndUser toBeAdded = new BackEndUser(user);
                result.add(toBeAdded);
            }

            Collections.sort(result);

            return result;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public List<String> getAllUserDevices () {

        List<String> result = new ArrayList<String>();

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM userdevices";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(UserDevice.class);
            List<UserDevice> allUsersDevices = (List<UserDevice>) query.list();

            for (UserDevice userDevice : allUsersDevices) {
                result.add(userDevice.getDeviceID());
            }

            return result;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public boolean modifyUser (BackEndUser modifiedUser) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, modifiedUser.getUserID());
            if (user == null) { return false; }
            user.setUsername(modifiedUser.getUserName());
            user.setPoints(modifiedUser.getPoints());
            if (UserPrivilege.chineseToType(modifiedUser.getPrivilege())
                    .compareTo(UserPrivilege.admin) < 0) {
                user.setPrivilege(UserPrivilege.chineseToType(modifiedUser.getPrivilege()));
            }
            session.save(user);
            return true;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public EventDetail getEventDetail (String eid) {

        EventDetail ret = new EventDetail();
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Event event = (Event) session.load(Event.class, eid);
            if (event == null) return ret;
            ret.setEventType(event.getEventType());
            ret.setEventInfo(event.getEventInfo());
            ret.setReportTime(event.getOccurTime());
            User sender = (User) session.load(User.class, event.getSenderID());
            if (sender == null) return ret;
            ret.setSender(new Sender());
            ret.getSender().setUserID(sender.getUserID());
            ret.getSender().setUserName(sender.getUsername());
            ret.getSender().setPoints(sender.getPoints());
            if (sender.getLocation() != null) {
                ret.getSender().setReportTime(sender.getLocation().getTime());
                ret.getSender().setLongitude(sender.getLocation().getLongitude());
                ret.getSender().setLatitude(sender.getLocation().getLatitude());
            }
            ret.setUpUsers(new ArrayList<ShortUser>());
            ret.setReportUsers(new ArrayList<ShortUser>());
            for (EventInteraction interaction : event.getInteractions()) {
                User u =
                        (User) session.load(User.class, interaction
                                .getEventInteractionCompositeId().getUserId());
                if (u != null) {
                    if (interaction.getEventInteractionCompositeId().getInteraction()
                            .equals(Constants.VOTE_UP_EVENT)) {
                        ret.getUpUsers().add(new ShortUser(u.getUserID(), u.getUsername()));
                    }
                    else {
                        ret.getReportUsers().add(new ShortUser(u.getUserID(), u.getUsername()));
                    }
                }
            }
            ret.setPicIds(event.getPicIds());

            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public boolean updatePersonalInformation (String userID, String field,
                                              String value) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            if (field.equals("userName")) {
                user.setUsername(value);
                session.saveOrUpdate(user);
                return true;
            }
            else {
                if (user.getUserInformation() == null) { return false; }
                user.getUserInformation().getClass().getField(field)
                        .set(user.getUserInformation(), value);
                session.saveOrUpdate(user);
                return true;
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public boolean updateGoodTypes (String userID, String[] goodTypes) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Set<UserGoodType> userGoodTypes =
                    (Set<UserGoodType>) ((User) session.load(User.class, userID))
                            .getUserGoodTypes();
            if (userGoodTypes == null) { return false; }
            for (UserGoodType old : userGoodTypes) {
                session.delete(old);
            }
            for (String goodType : goodTypes) {
                if (goodType.equals("")) continue;
                UserGoodType newGoodType = new UserGoodType(userID, goodType);
                session.save(newGoodType);
            }
            return true;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public boolean addFrequentPlaces (String userID, String[] frequentPlaces) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Set<UserFrequentPlace> userFrequentPlaces =
                    (Set<UserFrequentPlace>) ((User) session.load(User.class, userID))
                            .getUserFrequentPlaces();
            if (userFrequentPlaces == null) { return false; }
            // for(UserFrequentPlace old : userFrequentPlaces){
            // session.delete(old);
            // }
            for (String freqPlace : frequentPlaces) {
                if (freqPlace.equals("")) continue;
                UserFrequentPlace newFreqPlace = new UserFrequentPlace(userID, freqPlace);
                session.save(newFreqPlace);
            }
            return true;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
    }


	public boolean updateFrequentPlaces(String userID, String[] frequentPlaces) {
		Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Set<UserFrequentPlace> userFrequentPlaces =
                    (Set<UserFrequentPlace>) ((User) session.load(User.class, userID))
                            .getUserFrequentPlaces();
            if (userFrequentPlaces == null) { return false; }
             for(UserFrequentPlace old : userFrequentPlaces){
            	 session.delete(old);
             }
            for (String freqPlace : frequentPlaces) {
                if (freqPlace.equals("")) continue;
                UserFrequentPlace newFreqPlace = new UserFrequentPlace(userID, freqPlace);
                session.save(newFreqPlace);
            }
            return true;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
	}

    public UserDetail getUserDetail (String userid) {
        UserDetail ret = new UserDetail();
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userid);
            if (user == null) return ret;
            ret.setNickName(user.getUserInformation().getNickName());
            ret.setHomeTown(user.getUserInformation().getHomeTown());
            ret.setPortrait(Constants.PIC_PREFIX + user.getUserInformation().getPortrait());
            ret.setSignature(user.getUserInformation().getSignature());
            ret.setMyTruck(user.getUserInformation().getMyTruck());
            ret.setMyTruckPicId(Constants.PIC_PREFIX + user.getUserInformation().getMyTruckPicId());
            ret.setBoughtTime(user.getUserInformation().getBoughtTime());

            String dlp = user.getUserInformation().getDriverLicensePic();
            if (dlp != null && !dlp.equals("")) {
                String[] dpics = dlp.split(",");
                ret.setDriverLicensePic(Constants.PIC_PREFIX + dpics[0] + "," +
                                        Constants.PIC_PREFIX + dpics[1]);
            }
            String rp = user.getUserInformation().getRegistrationPic();
            if (rp != null && !rp.equals("")) {
                String[] rpics = rp.split(",");
                ret.setRegistrationPic(Constants.PIC_PREFIX + rpics[0] + "," +
                                       Constants.PIC_PREFIX + rpics[1]);
            }

            List<String> goodTypeList = new ArrayList<String>();
            List<String> frequentPlaceList = new ArrayList<String>();
            for (UserGoodType gt : user.getUserGoodTypes()) {
                goodTypeList.add(gt.getUserGoodTypeCompositeId().getGoodType());
            }
            for (UserFrequentPlace fp : user.getUserFrequentPlaces()) {
                frequentPlaceList.add(fp.getUserFrequentPlaceCompositeId().getFrequentPlace());
            }
            ret.setGoodTypeList(goodTypeList);
            ret.setFrequentPlaceList(frequentPlaceList);
            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public PersonalInfo getPersonalInfo (String userID) {

        PersonalInfo result = null;

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            result = new PersonalInfo(user.getUserInformation());
            result.updateWithUser(user);
            return result;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public Event getSOSEventForUser (String userID) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM events WHERE senderid = :senderid AND eventtype = :eventtype";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(Event.class);
            query.setParameter("senderid", userID);
            query.setParameter("eventtype", Constants.EVENT_TYPE_HELP);
            List results = query.list();
            return (Event) results.get(0);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        finally {
            session.getTransaction().commit();
        }
    }

    public SOSDetail getSOSDetail (String eventID) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            SOSDetail ret = new SOSDetail();
            String senderID = ((Event) session.load(Event.class, eventID)).getSenderID();
            User u = (User) session.load(User.class, senderID);
            ret.setPhoneNum(u.getUserID());
            ret.setUserName(u.getUsername());
            if(u.getUserInformation()!=null){
                ret.setMyTruck(u.getUserInformation().getMyTruck());
                ret.setHomeTown(u.getUserInformation().getHomeTown());
                ret.setLicensePlate(u.getUserInformation().getLicensePlate());
            }
            return ret;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
        finally {
            session.getTransaction().commit();
        }
    }

	public String bulkNewEvents(String eventsText) {
		return eventsText;
	}

	public boolean addDetailedHometownForUser(String userId, String province,
			String city, String district) {
		Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userId);
            DetailedHometown ret = new DetailedHometown();
            if(user.getDetailedHometown()!=null){
            	ret = user.getDetailedHometown();
            }
            user.setDetailedHometown(ret);
            user.getDetailedHometown().setUserId(user.getUserID());
            if(!province.equals("noProvince"))
            	user.getDetailedHometown().setProvince(province);
            if(!city.equals("noCity"))
            	user.getDetailedHometown().setCity(city);
            if(!district.equals("noDistrict"))
            	user.getDetailedHometown().setDistrict(district);
            session.save(user.getDetailedHometown());
            session.save(user);
            return true;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public List<String> generateChatNamesByHometown(String userID) {
		Session session = null;
        List<String> ret = new ArrayList<String>();
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            if(user.getDetailedHometown()==null) return ret;
            
            String province = Utility.trimLocationText(user.getDetailedHometown().getProvince());
            String city = Utility.trimLocationText(user.getDetailedHometown().getCity());
            String district = Utility.trimLocationText(user.getDetailedHometown().getDistrict());
            if(!province.equals(Constants.NO_LOCATION)){
	            ret.add(province + Constants.HOMETOWN_GROUP);
	            if(!city.equals(Constants.NO_LOCATION)){
	            	ret.add(province + city + Constants.HOMETOWN_GROUP);
	            	if(!district.equals(Constants.NO_LOCATION))
	            		ret.add(province + city + district + Constants.HOMETOWN_GROUP);
	            }
            }
            return ret;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public List<String> generateChatNamesByLocation(String province,
			String city, String district) {
		
		Session session = null;
        List<String> ret = new ArrayList<String>();
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            if(!province.equals("")){
	            ret.add(province + Constants.LOCAL_GROUP);
	            if(!city.equals("")){
	            	ret.add(province + city + Constants.LOCAL_GROUP);
	            	if(!district.equals(""))
	            		ret.add(province + city + district + Constants.LOCAL_GROUP);
	            }
            }
            return ret;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
	}
	
	public List<String> generateChatNamesByGoodTypes(String userId) {
		
		Session session = null;
        List<String> ret = new ArrayList<String>();
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            User user = (User) session.load(User.class, userId);
            for(UserGoodType ugt : user.getUserGoodTypes()){
            	ret.add(ugt.getUserGoodTypeCompositeId().getGoodType() + Constants.GOOD_TYPE_GROUP);
            }
            return ret;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public void fillInChatList(ReceivedChatList chatList, List<String> chatNames1,
			List<String> chatNames2, List<String> chatNames3,
			List<String> chatNames4, String userId) {
		Session session = null;
		List<ReceivedChat> ret = chatList.getChats();
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            chatNames1.addAll(chatNames2);
            chatNames1.addAll(chatNames3);
            for(String cn4 : chatNames4){
            	if(!chatNames1.contains(cn4)){
            		chatNames1.add(cn4);
            	}
            }

            for(String chatName : chatNames1){
            	ReceivedChat rc = new ReceivedChat();
            	rc.setChatName(chatName);
            	rc.setChatPicId("profPlaceHolder");
            	
            	// take care of roomIds
            	Criteria cr = session.createCriteria(GroupChatId.class);
                cr.add(Restrictions.eq("address", chatName));
                List<GroupChatId> results = cr.list();
                boolean roomExists = results.size() > 0;

            	String roomId;
            	if(roomExists){
            		roomId = results.get(0).getChatId();
            		rc.setNeedToCreate(false);
            		
            		// take care of chat settings
            		String sql = "SELECT * FROM chatsettings WHERE userid = :userid AND chatid = :chatid";
                    SQLQuery query = session.createSQLQuery(sql);
                    query.addEntity(ChatSetting.class);
                    query.setParameter("userid", userId);
                    query.setParameter("chatid", roomId);
                    List<ChatSetting> settings = (List<ChatSetting>) query.list();
            		if(settings.size()>0){
            			rc.updateSettings(settings.get(0));
            		} else {
            			rc.defaultSettings();
            		}
            		
            	} else {
            		GroupChatId newId = new GroupChatId();
            		newId.setAddress(chatName);
            		newId.setChatId(UUID.randomUUID().toString());
            		session.save(newId);
            		roomId = newId.getChatId();
            		rc.setNeedToCreate(true);
            		rc.defaultSettings();
            	}
            	rc.setChatRoomId(roomId);
            	ret.add(rc);
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        finally {
            session.getTransaction().commit();
        }
		
	}

	public boolean saveChatSetting(String userId, String chatId, boolean muted,
			boolean locked, boolean top) {

		Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM chatsettings WHERE userid = :userid AND chatid = :chatid";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(ChatSetting.class);
            query.setParameter("userid", userId);
            query.setParameter("chatid", chatId);
            List<ChatSetting> results = (List<ChatSetting>) query.list();
            boolean settingExists = results.size() > 0;
            
            if(settingExists){
            	ChatSetting cs = results.get(0);
            	cs.setMuted(muted);
            	cs.setLocked(locked);
            	cs.setTop(top);
            	session.save(cs);
            } else {
            	ChatSetting newCs = new ChatSetting(userId, chatId, muted, top, locked);
            	session.save(newCs);
            	User user = (User) session.load(User.class, userId);
            	user.getChatSettings().add(newCs);
            	session.save(user);
            }
            
            return true;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public ChatUserProfile fillInChatUserProfile(String userID) {
		
		ChatUserProfile ret = new ChatUserProfile();
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            if (user == null) return ret;
            
            if((user.getUserType().equals(UserType.driver) && user.getShowUserIdToDriver())
            		|| (user.getUserType().equals(UserType.goodsOwner) && user.getShowUserIdToOwner())
            		|| (user.getUserType().equals(UserType.her) && user.getShowUserIdToHer())){
            	ret.setUserId(userID);
            } else {
            	ret.setUserId(Constants.NOT_SHOWING_IN_CHAT);
            }
            ret.setNickName(user.getUserInformation().getNickName());
            ret.setHometown(user.getUserInformation().getHomeTown());
            ret.setMyTruck(user.getUserInformation().getMyTruck());
            
            List<String> frequentPlaceList = new ArrayList<String>();
            
            if((user.getUserType().equals(UserType.driver) && user.getUserInformation().getShowFPToDriver())
            		|| (user.getUserType().equals(UserType.goodsOwner) && user.getUserInformation().getShowFPToOwner())
            		|| (user.getUserType().equals(UserType.her) && user.getUserInformation().getShowFPToHer())){
	            for (UserFrequentPlace fp : user.getUserFrequentPlaces()) {
	                frequentPlaceList.add(fp.getUserFrequentPlaceCompositeId().getFrequentPlace());
	            }
            } else {
            	frequentPlaceList.add(Constants.NOT_SHOWING_IN_CHAT);
            }
            ret.setFrequentPlaceList(frequentPlaceList);
            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public String getUserNickName(String userID) {
		Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            return user.getUserInformation().getNickName();
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public List<String> generateChatNamesByLocks(String userId) {
		Session session = null;
        List<String> ret = new ArrayList<String>();
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM chatsettings WHERE userid = :userid AND locked = :locked";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(ChatSetting.class);
            query.setParameter("userid", userId);
            query.setParameter("locked", 1);
            List<ChatSetting> results = (List<ChatSetting>) query.list();
            boolean settingExists = results.size() > 0;
            
            if(settingExists){
            	for(ChatSetting cs : results){
            		
            		String sql2 = "SELECT * FROM groupchatid WHERE chatid = :chatid";
                    SQLQuery query2 = session.createSQLQuery(sql2);
                    query2.addEntity(GroupChatId.class);
                    query2.setParameter("chatid", cs.getChatSettingCompositeId().getChatId());
                    List<GroupChatId> results2 = (List<GroupChatId>) query2.list();
                    if(results2.size() > 0)
                    	ret.add(results2.get(0).getAddress());
                    
            	}
            }
            
            return ret;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public void createSOSChatGroup(String chatId, List<User> users) {
		Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            for(User u : users){
            	ChatSetting cs = new ChatSetting(u.getUserID(), chatId, false, false, true);
            	session.save(cs);
            }
        }
        finally {
            session.getTransaction().commit();
        }
	}
	
	public void deleteSOSChatGroup(String chatId){
		Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            String sql = "SELECT * FROM chatsettings WHERE chatid = :chatid";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(ChatSetting.class);
            query.setParameter("chatid", chatId);
            List<ChatSetting> results = (List<ChatSetting>) query.list();
            for(ChatSetting cs : results){
            	session.delete(cs);
            }
            
            String sql2 = "SELECT * FROM groupchatid WHERE chatid = :chatid";
            SQLQuery query2 = session.createSQLQuery(sql2);
            query2.addEntity(GroupChatId.class);
            query2.setParameter("chatid", chatId);
            List<GroupChatId> results2 = (List<GroupChatId>) query2.list();
            for(GroupChatId gcid : results2){
            	session.delete(gcid);
            }
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public List<PointsHistory> getPointsHistory(String userID) {
		Session session = null;
        List<PointsHistory> ret = new ArrayList<PointsHistory>();
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            for(com.truckapp.database.PointsHistory ph : user.getPointsHistories()){
            	ret.add(new PointsHistory(ph));
            }
            return ret;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ret;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public boolean updateShowPhoneInChat(String userID, boolean showToDriver,
			boolean showToOwner, boolean showToHer) {
		Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            user.setShowUserIdToDriver(showToDriver);
            user.setShowUserIdToOwner(showToOwner);
            user.setShowUserIdToHer(showToHer);
            session.save(user);
            return true;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
	}

	public boolean updateShowFPInChat(String userID, boolean showToDriver,
			boolean showToOwner, boolean showToHer) {
		Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            User user = (User) session.load(User.class, userID);
            user.getUserInformation().setShowFPToDriver(showToDriver);
            user.getUserInformation().setShowFPToOwner(showToOwner);
            user.getUserInformation().setShowFPToHer(showToHer);
            session.save(user.getUserInformation());
            return true;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        finally {
            session.getTransaction().commit();
        }
	}

}
