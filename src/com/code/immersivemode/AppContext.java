package com.code.immersivemode;

import java.io.File;

import com.androidquery.callback.BitmapAjaxCallback;


import com.baidu.mapapi.SDKInitializer;
import com.code.immersivemode.DaoMaster.OpenHelper;
import com.immersive.utils.GreenDaoUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;


public class AppContext extends Application {
	private static AppContext mInstance;
	
	public static final String TAG = "AppContext";
	private static String DATABASE_NAME = "sneaker";
	public final static String PATH = Environment.getExternalStorageDirectory() + "/Sneaker";
	public final static String PATH_SCREENSHOT = PATH + File.separator + "/ScreenShot";
	public final static String PATH_MAPSHOT = PATH + File.separator + "/MapShot";
	
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
		initFolder();
		initImageLoader();
		
		if (debug) {
			testDBCase();
//			addTestData();
		}
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
		mFolder = new File(PATH_MAPSHOT);
		if (!mFolder.exists()) {
			Log.v(TAG, "MapShot mkdir");
			mFolder.mkdir();
		} else {
			Log.v(TAG, "MapShot already mkdir");
		}
		
	}
	
	public void testDBCase() {
		if(GreenDaoUtils.getInstance(getAppContext()).isUserSaved(1)) {
			Log.e(TAG,"user exist");
			user_id = 1;
			return;
		} else {
			Log.e(TAG, "user not find");
		}
		User user = new User((long) 1, "512225682@qq.com", "123456", "13672437864", "512225682");
		GreenDaoUtils.getInstance(getAppContext()).addToUserTable(user);
		Log.e(TAG, "user create");
		user_id = 1;
	}
	
	public void addTestData() {
		Step step1 = new Step(1l, user_id, "2015:07:06", 87, 23, 0, 0, 0, 0, 0, 0, 91, 197, 263, 136, 370, 189, 37, 0, 92, 12, 189, 271, 386, 14, 19, 87 );
		Step step2 = new Step(2l, user_id, "2015:07:07", 0, 0, 0, 0, 0, 0, 0, 0, 191, 19, 231, 436, 327, 419, 237, 410, 392, 129, 18, 0, 386, 14, 19, 87 );
		Step step3 = new Step(3l, user_id, "2015:07:08", 187, 39, 0, 0, 0, 0, 0, 16, 59, 19, 163, 436, 70, 19, 371, 0, 192, 12, 18, 271, 36, 140, 192, 187 );
		GreenDaoUtils.getInstance(getAppContext()).addToStepTable(step1);
		GreenDaoUtils.getInstance(getAppContext()).addToStepTable(step2);
		GreenDaoUtils.getInstance(getAppContext()).addToStepTable(step3);
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
	
	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration  
			    .Builder(getApplicationContext())  
			    .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽  
			    .threadPoolSize(3)//线程池内加载的数量  
			    .threadPriority(Thread.NORM_PRIORITY - 2)  
			    .denyCacheImageMultipleSizesInMemory()  
			    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
			    .memoryCacheSize(2 * 1024 * 1024)    
			    .discCacheSize(50 * 1024 * 1024)    
			    .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密  
			    .tasksProcessingOrder(QueueProcessingType.LIFO)  
			    .discCacheFileCount(100) //缓存的文件数量  
			    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())   
			    .writeDebugLogs() // Remove for release app  
			    .build();//开始构建  
			    // Initialize ImageLoader with configuration. 
		
		ImageLoader.getInstance().init(config);//全局初始化此配置  
	}
}
