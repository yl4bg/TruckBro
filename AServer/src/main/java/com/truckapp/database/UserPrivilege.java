package com.truckapp.database;

import com.truckapp.util.Constants;

public enum UserPrivilege {
	basic, pc60, full, admin, god;
	
	public static String toString(UserPrivilege privilege){
		switch(privilege){
			case basic: return Constants.PRIVILEGE_BASIC;
			case pc60: return Constants.PRIVILEGE_PC60;
			case full: return Constants.PRIVILEGE_FULL;
			case admin: return Constants.PRIVILEGE_ADMIN;
			case god: return Constants.PRIVILEGE_GOD;
			default: return "undefined";
		}
	}

    public static String toChinese (UserPrivilege privilege) {
        switch(privilege){
            case basic: return "普通";
            case pc60: return "百分之60";
            case full: return "完全";
            case admin: return "管理员";
            case god: return "上帝";
            default: return "未知权限";
    }
    }

    public static UserPrivilege chineseToType (String privilege) {
        if(privilege.equals("上帝")){
            return god;
        } else if(privilege.equals("管理员")){
            return admin;
        } else if(privilege.equals("完全")){
            return full;
        } else if(privilege.equals("百分之60")){
            return pc60;
        } else if(privilege.equals("普通")){
            return basic;
        }
        return basic;
    }
}
