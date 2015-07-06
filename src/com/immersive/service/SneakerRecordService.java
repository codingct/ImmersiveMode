package com.immersive.service;

import java.lang.ref.WeakReference;
import java.util.Date;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.code.immersivemode.AppContext;
import com.code.immersivemode.Location;
import com.code.immersivemode.R;
import com.code.immersivemode.Record;
import com.immersive.activity.SneakerActivity;
import com.immersive.utils.BitmapUtils;
import com.immersive.utils.GreenDaoUtils;
import com.immersive.utils.LocationUtils;


public class SneakerRecordService extends SneakerService{
	public static final String TAG = "SneakerRecordService";
	private static final int TIME_TICK = 1001;
	
	private LocationClient mLocationClient = null;
	private BDLocationListener mBLocationListener = null;
	private LocationClientOption mLocationOption = null;
	
	public static RecordHandler mRecordHandler = null;
	public int timer = -1;
	
	private GreenDaoUtils mDBUtils = null;
	private long currentRecordId = -1;
	
	private double mLastLatitude = -1;
	private double mLastLongitude = -1;
	/** 
     * 返回一个Binder对象 
     */  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
	
	@Override    
    public void onCreate() {    
        super.onCreate();  
        //initNotification();
        mRecordHandler = new RecordHandler(this);
        mDBUtils = GreenDaoUtils.getInstance(this);
        initLocation();
    }    
	
	private void initNotification() {
		Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, SneakerActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//设置启动模式
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);    
        builder.setContentIntent(contentIntent);  
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setLargeIcon(BitmapUtils.getInstance().drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher)));
        builder.setTicker("Sneaker Service Start");  
        builder.setContentTitle("Sneaker");  
        builder.setContentText("Running State");
        Notification notification = builder.build();  
          
        startForegroundCompat(NOTIFICATION_ID, notification);    
	}
	
	private void initLocation() {
		mBLocationListener = new MyLocationListener();
		mLocationClient = new LocationClient(getApplicationContext());     	//声明LocationClient类
	    mLocationClient.registerLocationListener(mBLocationListener);    	//注册监听函数
	   
	    mLocationOption = new LocationClientOption();
	    mLocationOption.setLocationMode(LocationMode.Hight_Accuracy);		//设置定位模式
	    mLocationOption.setCoorType("bd09ll");								//返回的定位结果是百度经纬度,默认值gcj02
	    mLocationOption.setScanSpan(5000);									//设置发起定位请求的间隔时间为5000ms
	    mLocationOption.setIsNeedAddress(true);								//返回的定位结果包含地址信息
	    mLocationOption.setNeedDeviceDirect(true);							//返回的定位结果包含手机机头的方向
	    mLocationClient.setLocOption(mLocationOption);
	    mLocationClient.start();
	    
	    if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
	    } else { 
			Log.d("LocSDK5", "locClient is null or not started");
	    }
	}
	
    
    public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			} 
			Log.d("Location", sb.toString());
			
			// 发送位置信息
			try {
				sendLocationBroadCast(location.getRadius(), location.getDirection(), 
						location.getLatitude(), location.getLongitude(), location.getSatelliteNumber());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (mLastLatitude == -1 || mLastLongitude == -1) {
				mLastLatitude = location.getLatitude();
				mLastLongitude = location.getLongitude();
				recordLocation(location);
			}
			
			if (AppContext.isRecordStart) {
				if (LocationUtils.isValid(mLastLatitude, mLastLongitude, location.getLatitude(), location.getLongitude())) {
					recordLocation(location);
					mLastLatitude = location.getLatitude();
					mLastLongitude = location.getLongitude();
				} else {
					Log.e(TAG, "InValid Location!");
				}
			}
 
		}
	}
    
    private void sendLocationBroadCast(float radius, float direction, double latitude, double lontitude, int num) 
    		throws InterruptedException {  
        //Log.d(TAG, "ServiceThread===>>startLocation() executed===>>线程ID:"+Thread.currentThread().getId());  
        Intent intent = new Intent();
        intent.setAction("com.immersive.broadcast.SneakerReceiver");
        intent.putExtra("value", 1);
        intent.putExtra("radius", radius);
        intent.putExtra("direction", direction);
        intent.putExtra("latitude", latitude);
        intent.putExtra("lontitude", lontitude);
        intent.putExtra("num", num);
        sendBroadcast(intent);
    }
    
    public void sendTimerBroadCast(int time) throws InterruptedException {  
        //Log.d(TAG, "ServiceThread===>>startLocation() executed===>>线程ID:"+Thread.currentThread().getId());  
        Intent intent = new Intent();
        intent.setAction("com.immersive.broadcast.SneakerReceiver");
        intent.putExtra("time", time);
        sendBroadcast(intent);
    }
    
    private void recordTime() {
    	Thread mTimer = new Thread(new Runnable() {
			@Override
			public void run() {
				timer = -1;	
				while (AppContext.isRecordStart) {	
					mRecordHandler.sendEmptyMessage(TIME_TICK);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
    		
    	});
    	mTimer.start();
    }
    
    private void recordLocation(BDLocation BDlocation) {
    	Location location = new Location(null, currentRecordId, BDlocation.getLatitude(), BDlocation.getLongitude());
    	mDBUtils.addToLocationTable(location);
    	Log.d(TAG, "Location Add");
    }
    
    public void initNewRecord() {
    	Date now = new Date();
    	Record record = new Record(null, AppContext.user_id, now);
    	mDBUtils.addToRecordTable(record);
    	currentRecordId = mDBUtils.getReocrdIdbyDate(now);
    	if (currentRecordId == -1) {
    		Log.e(TAG, "Record_wrong");
    		return;
    	} else if (mDBUtils.isRecordSaved(currentRecordId)) {
    		Log.d(TAG, "new Record Create!");
    	}
    	
    	
    }
    
    @Override    
    public void onDestroy() {    
        super.onDestroy();  
        mLocationClient.stop();
    }
    
    public static class RecordHandler extends Handler {
        private WeakReference<SneakerRecordService> mOuter;
 
        public RecordHandler(SneakerRecordService service) {
            mOuter = new WeakReference<SneakerRecordService>(service);
        }
 
		@Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        	case TIME_TICK:
        		mOuter.get().timer += 1;
        		try {
					mOuter.get().sendTimerBroadCast(mOuter.get().timer);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		break;
        	case AppContext.MSG_CHECK:
        		if (AppContext.isRecordStart) {
        			mOuter.get().recordTime();
        			mOuter.get().initNewRecord();
        			
        		}
        		break;
        	}
        }
	}
}