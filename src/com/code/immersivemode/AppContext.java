package com.code.immersivemode;

import com.androidquery.callback.BitmapAjaxCallback;


import android.app.Application;

public class AppContext extends Application {
	private static AppContext mInstance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		
		
	}
	
	public static AppContext getAppContext() {
        return mInstance;
    }
	
	@Override
    public void onLowMemory(){
        BitmapAjaxCallback.clearCache();
    }
}
