package com.immersive.service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.Step;
import com.immersive.activity.SplashActivity;
import com.immersive.sneaker.SneakerCore;
import com.immersive.utils.BitmapUtils;
import com.immersive.utils.GreenDaoUtils;

public class SneakerGuardService extends SneakerService implements SensorEventListener {

	private static final String TAG = "SneakerGuardService";
	private static final int MIN_STEP = 6; 
	private static final int TICK_RECORD = 200; // 计算周期 200ms
	private static final int TICK_GUARD = 8000; // 守护周期 8s
	
	private GreenDaoUtils mDBUtils = null;
	private AlarmReceiver mAlarmReceiver = null;
	private IntentFilter filter = null;
	
	private SensorManager sensorManager = null;
	private Sensor accelerometerSensor = null;
	private SneakerCore mSneakerCore = null;
	private Thread recordThread = null;
	private Thread guardThread = null;

	private float[] accelerometer = new float[3];
	private float[] geomagnetic = new float[3];
	
	private List<Double> turn_y = new ArrayList<Double>();
	private List<Double> turn_x = new ArrayList<Double>();
	private List<Double> turn_z = new ArrayList<Double>();

	private double nature_y = 0;
	private double nature_x = 0;
	private double nature_z = 0;
	private int step = 0;
	private int save_step = 0;
	private int sum_step = 0;
	private boolean isRecord = true;
	private boolean startAdd = true;
	private boolean overAdd = false;

	private Step dailyStep = null;
	private long step_id = -1;
	public static int mSneakerStep = 0;
	
	/** 
	 *  
	 * @ClassName: AlarmReceiver   
	 * @Description: 整点会收到广播
	 * @author tong.chen
	 * @date 2013-11-25 下午4:44:30   
	 * 
	 */  
	public class AlarmReceiver extends BroadcastReceiver {  
	      
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	    	Log.d(TAG, "AlarmReceiver onReceive");
	    	Bundle extras = intent.getExtras();
	        if (extras != null) {
	            if(extras.containsKey("time")){
	            	int time = (Integer) extras.get("time");
	            	Log.d(TAG, "time ====> " + time);
	            	reflectMethods(time);
	            	

	            }
	        }
	    }  
	  
	}  
	
	@SuppressLint("SimpleDateFormat")
	private void reflectMethods(int time) {
		try {
			if (time == 0) {
				dailyStep = null;
				step_id = -1;
			}
			
			if (dailyStep == null) {
				Date today = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd");
				String date = format.format(today);
				Log.e(TAG, "Date:" + date);
				
				step_id = mDBUtils.getStepIdByDateAndId(date, AppContext.user_id);
				if (step_id != -1) {
					Log.e(TAG, "Toaday Step exist: id==>" + step_id);
					dailyStep = mDBUtils.getStepById(step_id);
					
				} else {
					dailyStep = new Step(null, AppContext.user_id, date, 
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
					mDBUtils.addToStepTable(dailyStep);
					step_id = mDBUtils.getStepIdByDateAndId(date, AppContext.user_id);
					dailyStep.setId(step_id);
					Log.e(TAG, "new Step Add: id==>" + step_id);
				}
			}
			
			Field stepField = Step.class.getDeclaredField("step_"+time);
			stepField.setAccessible(true);
			if((Integer)stepField.get(dailyStep) == 0) {
				stepField.set(dailyStep, sum_step);
			}
			Log.e(TAG, "dailyStep_"+time+":" + stepField.get(dailyStep));
			sum_step = 0;
			mDBUtils.updateStep(dailyStep);
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mDBUtils = GreenDaoUtils.getInstance(SneakerGuardService.this);
		mSneakerCore = new SneakerCore();
		initBroadcastReceiver();
		initRecordClock();
		initNotification();
		initSensor();
		initListener();
		initThread();
	}
	
	private void initBroadcastReceiver() {
		mAlarmReceiver = new AlarmReceiver();
        filter = new IntentFilter();
        filter.addAction("com.immersive.broadcast.AlarmReceiver");
        registerReceiver(mAlarmReceiver, filter);
	}
	
	private void initRecordClock() {
		AlarmManager  alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);   
        Calendar calendar =Calendar.getInstance(Locale.getDefault());  
        for (int i = 0; i < 24 ; i++) {
        	calendar.setTimeInMillis(System.currentTimeMillis());  
            calendar.set(Calendar.HOUR_OF_DAY, i);  
            calendar.set(Calendar.MINUTE, 0);  
            calendar.set(Calendar.SECOND, 0);  
            calendar.set(Calendar.MILLISECOND, 0);  
            Intent intent = new Intent();  
            intent.putExtra("time", i);
            intent.setAction("com.immersive.broadcast.AlarmReceiver");  
            PendingIntent pendingIntent=PendingIntent.getBroadcast(this, i, intent, 0);  
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 
            		AlarmManager.INTERVAL_DAY, pendingIntent);  
        }
        Log.d(TAG, "initRecordClock");

	}

	private void initNotification() {
		Notification.Builder builder = new Notification.Builder(this);
		Intent notificationIntent = new Intent(this, SplashActivity.class);
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 设置启动模式
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		builder.setContentIntent(contentIntent);
		builder.setSmallIcon(R.drawable.ic_notification);
		builder.setLargeIcon(BitmapUtils.getInstance().drawable2Bitmap(getResources().getDrawable(R.drawable.ic_launcher)));
		builder.setTicker("Sneaker Service Start");
		builder.setContentTitle("Sneaker");
		builder.setContentText("Make this service run in the foreground.");
		Notification notification = builder.build();

		startForegroundCompat(NOTIFICATION_ID, notification);
	}
	
	private void initSensor() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

		if (sensorList.size() > 0) {
			accelerometerSensor = sensorList.get(0);

			String strSensor = "Name: " + accelerometerSensor.getName()
					+ "\nVersion: "
					+ String.valueOf(accelerometerSensor.getVersion())
					+ "\nVendor: " + accelerometerSensor.getVendor()
					+ "\nType: "
					+ String.valueOf(accelerometerSensor.getType()) + "\nMax: "
					+ String.valueOf(accelerometerSensor.getMaximumRange())
					+ "\nResolution: "
					+ String.valueOf(accelerometerSensor.getResolution())
					+ "\nPower: "
					+ String.valueOf(accelerometerSensor.getPower())
					+ "\nClass: " + accelerometerSensor.getClass().toString();
			Log.d(TAG, strSensor);
		} else {
			Log.e("SensorManager", "Accelerometer Error");
		}
		
	}
	
	private void initListener() {
		// 注册加速度传感器监听
		sensorManager.registerListener(this, accelerometerSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		// 注册陀螺仪监听
		sensorManager.registerListener(this,  
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),  
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void initThread() {
		// 记录线程
		recordThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (isRecord) {
					// start Record
					turn_y.add(nature_y);
					turn_x.add(nature_x);
					turn_z.add(nature_z);
					if (turn_y.size() > 20) {
//						Log.e(TAG, turn_y.toString());
						step += mSneakerCore.calStep2(turn_y);
						turn_y.clear();
						turn_x.clear();
						turn_z.clear();
					}

					try {
						Thread.sleep(TICK_RECORD); // 200ms计算一次
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		// 守护线程
		guardThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (isRecord) {
//					Log.d(TAG, "Step Turn Start");
					if (step < MIN_STEP) {
						Log.e(TAG, "Step Invalid:" + step);
						save_step = step;
						startAdd = true;
						step = 0;
						// 结束补余纠偏
						if (overAdd) {
							sum_step += save_step;
							mSneakerStep += save_step;
							overAdd = false;
							Log.d(TAG, "Over Add:" + save_step);
						}
					} else {
//						Log.e(TAG, "sum_step add:" + step);
						sum_step += step;
						mSneakerStep += step;
						overAdd = true;
						step = 0;
						// 开始补余纠偏
						if (startAdd) {
							sum_step += save_step;
							mSneakerStep += save_step;
							startAdd = false;
							Log.d(TAG, "Start Add:" + save_step);
							
						}
					}
//					Log.d(TAG, "Step Turn Over");
					try {
						Thread.sleep(TICK_GUARD); // 8s进行一次检测
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
		recordThread.start();
		guardThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mAlarmReceiver);
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 加速度传感器
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			
//			String content = String.valueOf(event.values[0]) 
//				+ " " + String.valueOf(event.values[1]) 
//				+ " " + String.valueOf(event.values[2]) 
//				+ "\n";
//			Log.d("SensorManager", "accelerometer:" + content);
			accelerometer = event.values;
		}
		
		// 陀螺仪
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			geomagnetic= event.values;
//		    Log.d("SensorManager", "geomagnetic:" + geomagnetic[0] + "," + geomagnetic[1] + "," + geomagnetic[2]); 
		}
		
		exchangeCoordinate ();
		
	}
	
	/**
	 * 转换自然坐标系
	 */
	private void exchangeCoordinate () {
		float[] R = new float[9];
		float[] orientation = new float[3];

//		Log.d("SensorManager", "accelerometer:" + accelerometer[0] + "," + accelerometer[1] + "," + accelerometer[2]);
//		Log.d("SensorManager", "geomagnetic:" + geomagnetic[0] + "," + geomagnetic[1] + "," + geomagnetic[2]);

		boolean RM = SensorManager.getRotationMatrix(R, null, accelerometer, geomagnetic);
		if (RM) {
			SensorManager.getOrientation(R, orientation);
//			Log.d("SensorManager","getOrientation");
			
			double y0 = (-Math.sin(orientation[1]));
	    	double y1 = Math.cos(orientation[1])*Math.cos(orientation[0]);
	    	double y2 = Math.cos(orientation[1])*Math.sin(orientation[0]);
	    	double temp;
	    	// 防止NAN
	    	if(-(Math.tan(orientation[1])*Math.tan(orientation[2])) > 1) {
	    		temp = Math.acos(1);
	    	} else if (-(Math.tan(orientation[1])*Math.tan(orientation[2])) < -1) {
	    		temp = Math.acos(-1);
	    	} else {
	    		temp = Math.acos(-(Math.tan(orientation[1])*Math.tan(orientation[2])));
	    	}
	    	double x0 = (-Math.sin(orientation[2]));
	    	double x1 = Math.cos(orientation[2])*Math.cos(orientation[0]+temp);
	    	double x2 = Math.cos(orientation[2])*Math.sin(orientation[0]+temp);
	    	
	    	double z0 = x2*y1-x1*y2;
	    	double z1 = x0*y2-x2*y0;
	    	double z2 = x1*y0-x0*y1;
	    	
	    	nature_y = accelerometer[0]*x0+accelerometer[1]*y0+accelerometer[2]*z0;
	    	nature_x = accelerometer[0]*x1+accelerometer[1]*y1+accelerometer[2]*z1;
	    	nature_z = accelerometer[0]*x2+accelerometer[1]*y2+accelerometer[2]*z2;
	    	
//	    	Log.e("SensorManager","NAN:" + (-(Math.tan(orientation[1])*Math.tan(orientation[2]))));
//	    	Log.e("SensorManager","NAN-o1:" + (-(Math.tan(orientation[1]))));
//	    	Log.e("SensorManager","NAN-o2:" + (Math.tan(orientation[2])));
	    	
//	    	content = String.valueOf(ay) 
//					+ " " + String.valueOf(ax) 
//					+ " " + String.valueOf(az) 
//					+ "\n";
//	    	textX.setText("X: " + String.format("%.2f", ax));
//			textY.setText("Y: " + String.format("%.2f", ay));
//			textZ.setText("Z: " + String.format("%.2f", az));
	    	
			
		} else {
			Log.d("SensorManager","getRotationMatrix Error");
		}
	}
	
	

}
