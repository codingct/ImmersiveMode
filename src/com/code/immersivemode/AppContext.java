package com.code.immersivemode;

import com.androidquery.callback.BitmapAjaxCallback;


import com.baidu.mapapi.SDKInitializer;
import com.code.immersivemode.DaoMaster.OpenHelper;

import android.app.Application;
import android.content.Context;


public class AppContext extends Application {
	private static AppContext mInstance;
	private static String DATABASE_NAME = "sneaker";
	public static boolean isRecordStart = false;
	
	public static AppContext getInstance() {
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		SDKInitializer.initialize(getApplicationContext());
		
	}
	
	public static AppContext getAppContext() {
        return mInstance;
    }
	
	@Override
    public void onLowMemory(){
        BitmapAjaxCallback.clearCache();
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
