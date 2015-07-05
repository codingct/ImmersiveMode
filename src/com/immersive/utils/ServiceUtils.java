package com.immersive.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class ServiceUtils {
	private static final String TAG = "ServiceUtils";
	
	/**
	 * 判断服务是否已经运行
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isWorked(Context mContext, String className) {  
        ActivityManager myManager = (ActivityManager)mContext.getApplicationContext().getSystemService(  
                        Context.ACTIVITY_SERVICE);  
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager  
                .getRunningServices(100);  
        for (int i = 0; i < runningService.size(); i++) {  
        	//Log.d(TAG, runningService.get(i).service.getClassName().toString());
            if (runningService.get(i).service.getClassName().toString()  
                    .equals(className)) {  
                return true;  
            }  
        }  
        return false;  
    }  
	
	/**
	 * 判断当前应用程序处于前台还是后台
	 * @param context
	 * @return
	 */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
            	System.out.println(topActivity.getPackageName()+" vs " + context.getPackageName());
                return true;
            }
        }
        return false;
    }
}
