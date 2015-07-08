package com.code.immersivemode;

import java.io.File;

import com.androidquery.callback.BitmapAjaxCallback;


import com.baidu.mapapi.SDKInitializer;
import com.code.immersivemode.DaoMaster.OpenHelper;
import com.immersive.utils.GreenDaoUtils;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class AppContext extends Application {
	private static AppContext mInstance;
	
	public static final String TAG = "AppContext";
	private static String DATABASE_NAME = "sneaker";
	private final static String PATH = Environment.getExternalStorageDirectory() + "/Sneaker";
	private final static String PATH_SCREENSHOT = PATH + File.separator + "/ScreenShot";
	
	public static boolean debug = true; 
	
	
	
	public static boolean isRecordStart = false;
	public static int user_id;
	
	public static AppContext getInstance() {
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		SDKInitializer.initialize(getApplicationContext());
		testDBCase();
		initFolder();
	}
	
	public static AppContext getAppContext() {
        return mInstance;
    }
	
	@Override
    public void onLowMemory(){
        BitmapAjaxCallback.clearCache();
    }
	
	public void initFolder() {
		File mFolder = new File(PATH);
		if (!mFolder.exists()) {
			Log.v(TAG, "Sneaker mkdir");
			mFolder.mkdir();
		} else {
			Log.v(TAG, "Sneaker already mkdir");
		}
		mFolder = new File(PATH_SCREENSHOT);
		if (!mFolder.exists()) {
			Log.v(TAG, "ScreenShot mkdir");
			mFolder.mkdir();
		} else {
			Log.v(TAG, "ScreenShot already mkdir");
		}
		
	}
	
	public void testDBCase() {
		if(GreenDaoUtils.getInstance(getAppContext()).isUserSaved(1)) {
			Log.e(TAG,"user exist");
			return;
		} else {
			Log.e(TAG, "user not find");
		}
		User user = new User((long) 1, "512225682@qq.com", "123456", "13672437864", "512225682");
		GreenDaoUtils.getInstance(getAppContext()).addToUserTable(user);
		Log.e(TAG, "user create");
		user_id = 1;
	}
	
	// GreenDao数据库操作
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
	/**
	 * 取得DaoMaster
	 *
	 * @param context
	 * @return
	 */
	public static DaoMaster getDaoMaster(Context context)
	{
	    if (daoMaster == null)
	    {
	        OpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
	        daoMaster = new DaoMaster(helper.getWritableDatabase());
	    }
	    return daoMaster;
	}
	/**
	 * 取得DaoSession
	 *
	 * @param context
	 * @return
	 */
	public static DaoSession getDaoSession(Context context)
	{
	    if (daoSession == null)
	    {
	        if (daoMaster == null)
	        {
	            daoMaster = getDaoMaster(context);
	        }
	        daoSession = daoMaster.newSession();
	    }
	    return daoSession;
	}
}
