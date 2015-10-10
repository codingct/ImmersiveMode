package com.immersive.net;

import android.util.Log;

public class NetContext {
	private static final String TAG = "NetInterface";
	
	private static final String SERVER_ADDRESS = "http://sunriser.gotoip55.com/Monitor/";
	private static final String PHP = ".php";
	
	public static final String URL_USER_LOGIN = "login";
	
	public static final String URL_USER_REGISTER = "register";
	
	public static final String URL_USER_INFO_MODIFY = "modifyUserInfo";
	public static final String URL_USER_INFO_GET = "getMyInfo";
	
	public static final String URL_USER_AVATAR_UPLOAD = "upLoadUserAvatar";
	public static final String URL_USER_AVATAR_GET = "getAvatar";
	
	
	public static final String URL_RECORD_DAILY_ADD = "addDayRecord";
	public static final String URL_RECORD_DAILY_GET = "getDayRecords";
	
	public static final String URL_RECORD_SNEAKER_ADD = "addRunRecord";
	public static final String URL_RECORD_SNEAKER_GET = "getRunRecord";
	
	
	public static String suffix(String URL) {
		String url = SERVER_ADDRESS + URL + PHP;
		Log.d(TAG, "URL:" + url);
		return url;
	}
	
	
	
}
