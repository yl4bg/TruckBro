package com.truckapp.util;

import java.util.Date;
import java.util.HashMap;

public class Utility {

	public static String trimLocationText(String text){
		if(text.endsWith("省") || text.endsWith("市") 
				|| text.endsWith("县") || text.endsWith("区") || text.length()>2){
			text = text.substring(0, text.length()-1);
		}
		return text;
	}
	
	public static final HashMap<Integer, String> DIRECTION;
	static{
		DIRECTION = new HashMap<Integer, String>();
		DIRECTION.put(1, "北");
		DIRECTION.put(2, "东北");
		DIRECTION.put(3, "东");
		DIRECTION.put(4, "东南");
		DIRECTION.put(5, "南");
		DIRECTION.put(6, "西南");
		DIRECTION.put(7, "西");
		DIRECTION.put(8, "西北");
	}
	/**
	 * Calculates the distance (in kilometer) between two points of given
	 * longitude and latitude
	 */
	public static double distFrom(double lng1, double lat1, double lng2, double lat2) {
		double earthRadius = 6371; //kilometers
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		return dist;
	}
	
	/**
	 * Gets the direction between two points of given longitude and latitude
	 * @return 1: N 2: NE 3: E 4: SE 5: S 6: SW 7: W 8: NW
	 */
	public static int directionBtwPoints(double long1, double lat1, double long2, double lat2) {
        double dRotateAngle = Math.atan2(Math.abs(long1 - long2), Math.abs(lat1 - lat2));
        if (long2 >= long1) {
            if (lat2 < lat1) dRotateAngle = Math.PI - dRotateAngle;
        }
        else {
            if (lat2 >= lat1) dRotateAngle = 2 * Math.PI - dRotateAngle;
            else dRotateAngle = Math.PI + dRotateAngle;
        }
        dRotateAngle = dRotateAngle * 180 / Math.PI;
        
        if (dRotateAngle <= 22.5 || dRotateAngle >=337.5) return 1;	//N
        else if (dRotateAngle > 22.5 && dRotateAngle <=67.5) return 2;	//NE
        else if (dRotateAngle > 67.5 && dRotateAngle <=112.5) return 3;	//E
        else if (dRotateAngle > 112.5 && dRotateAngle <=157.5) return 4; //SE
        else if (dRotateAngle > 157.5 && dRotateAngle <=202.5) return 5;	//S
        else if (dRotateAngle > 202.5 && dRotateAngle <=247.5) return 6;	//SW
        else if (dRotateAngle > 247.5 && dRotateAngle <=292.5) return 7;	//W
        else return 8;	//NW
    }
	
	/**
	 * Computes time passed between two days.
	 * @param dateOld
	 * @param dateNew
	 * @return
	 */
	public static String timePassed(Date dateOld, Date dateNew) {
		long minPassed = (long) (dateNew.getTime() - dateOld.getTime())/60000;
		if (minPassed >= 518400) {
			return (Long.toString(minPassed / 518400) + "年前");
		} else if (minPassed >= 43200) {
			return (Long.toString(minPassed / 43200) + "个月前");
		} else if (minPassed >= 1440) {
			return (Long.toString(minPassed / 1440) + "天前");
		} else if (minPassed >= 60) {
			return (Long.toString(minPassed / 60) + "小时前");
		} else if (minPassed >= 1) {
			return (Long.toString(minPassed) + "分钟前");
		} else return ("刚刚");
	}
	
}
