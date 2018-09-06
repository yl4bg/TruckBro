package com.truckapp.database;

import com.truckapp.util.Constants;

public enum UserType {
	driver, goodsOwner, her;
	
	public static String toString(UserType type){
		switch(type){
			case driver: return Constants.TYPE_DRIVER;
			case goodsOwner: return Constants.TYPE_GOODSOWNER;
			case her: return Constants.TYPE_HER;
			default: return "undefinedType";
		}
	}

    public static String toChinese (UserType type) {
        switch(type){
            case driver: return Constants.TYPE_DRIVER_CN;
            case goodsOwner: return Constants.TYPE_GOODSOWNER_CN;
            case her: return Constants.TYPE_HER_CN;
            default: return "未知类型";
    }
    }

    public static UserType chineseToType (String type) {
        if(type.equals(Constants.TYPE_DRIVER_CN)){
            return driver;
        } else if(type.equals(Constants.TYPE_GOODSOWNER_CN)){
            return goodsOwner;
        } else if(type.equals(Constants.TYPE_HER_CN)){
            return her;
        }
        return driver;
    }
}
