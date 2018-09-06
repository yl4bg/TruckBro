package anapp.truck.com.anapp.utility.valueObjects;

import java.util.List;
import java.util.Date;

/**
 * An event with information to be displayed to user
 */
public class Event {

    public String eventID;
    public String type;
    public double distance;
    public String direction;
    public String description;
    public String uuid;
    public String time;
    public List<String> picIDs;
    public Date reportTime;
    public String longitude;
    public String latitude;
    public int upCnt;
    public int reportCnt;
    public String addressText;
    public String roadNumText;
    public boolean official;
    public String senderName;

    public Event(String eventID, String type, double distance,
                 String direction, String description, String uuid, String time, List<String> picIDs,
                 Date reportTime) {
        this.eventID = eventID;
        this.type = type;
        this.distance = distance;
        this.direction = direction;
        this.description = description;
        this.uuid = uuid;
        this.time = time;
        this.picIDs = picIDs;
        this.reportTime = reportTime;
    }

    public Event(String eventID, String type, double distance,
                 String direction, String description, String uuid, String time, List<String> picIDs,
                 Date reportTime, String longitude, String latitude,
                 int upCnt, int reportCnt, String roadNumText, String addressText,
                 boolean official, String senderName) {
        this(eventID, type, distance, direction, description, uuid, time, picIDs, reportTime);
        this.longitude = longitude;
        this.latitude = latitude;
        this.upCnt = upCnt;
        this.reportCnt = reportCnt;
        this.addressText = addressText;
        this.roadNumText = roadNumText;
        this.official = official;
        this.senderName = senderName;
    }

    public String getPicIDStr(){
        String picStr = picIDs.toString();
        return picStr.substring(1, picStr.length() - 1).replaceAll(" ","");
    }

}
