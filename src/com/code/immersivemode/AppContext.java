package com.code.immersivemode;

import com.androidquery.callback.BitmapAjaxCallback;


import com.baidu.mapapi.SDKInitializer;
import android.app.Application;

public class AppContext extends Application {
	private static AppContext mInstance;
	
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
}
