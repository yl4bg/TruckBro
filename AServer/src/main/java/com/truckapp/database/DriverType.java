package com.truckapp.database;

import com.truckapp.util.Constants;

public enum DriverType {
	driver, owner, driverAndOwner;
	
	public static String toString(DriverType type){
		switch(type){
			case driver: return Constants.TYPE_DRIVER;
			case owner: return Constants.TYPE_OWNER;
			case driverAndOwner: return Constants.TYPE_DRIVERANDOWNER;
			default: return "undefinedType";
		}
	}

    public static String toChinese (DriverType type) {
        switch(type){
            case driver: return Constants.TYPE_DRIVER_CN;
            case owner: return Constants.TYPE_OWNER_CN;
            case driverAndOwner: return Constants.TYPE_DRIVERANDOWNER_CN;
            default: return "未知类型";
        }
    }

    public static DriverType chineseToType (String type) {
        if(type.equals(Constants.TYPE_DRIVER_CN)){
            return driver;
        } else if(type.equals(Constants.TYPE_OWNER_CN)){
            return owner;
        } else if(type.equals(Constants.TYPE_DRIVERANDOWNER_CN)){
            return driverAndOwner;
        }
        return driver;
    }
}
