package com.immersive.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.code.immersivemode.R;
import com.immersive.activity.MainActivity;
import com.immersive.utils.BitmapUtils;

public class SneakerGuardService extends SneakerService {

	@Override
	public void onCreate() {
		super.onCreate();
		initNotification();
	}

	private void initNotification() {
		Notification.Builder builder = new Notification.Builder(this);
		Intent notificationIntent = new Intent(this, MainActivity.class);
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
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
}
