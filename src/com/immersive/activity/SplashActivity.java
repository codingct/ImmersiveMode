package com.immersive.activity;


import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.immersive.net.NetStatus;
import com.immersive.net.SneakerApi;
import com.immersive.utils.ServiceUtils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;


public class SplashActivity extends BaseActivity {
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 setContentView(R.layout.page_start);
		 
		 if	(AppContext.user != null) {
			SneakerApi.userLogin(AppContext.user); 
		 }
		 
		 MyThread t = new MyThread();
		 t.start();
		 
	}
	
	
	
	private class MyThread extends Thread {
		@Override
		public void run() {
			try {
				sleep(2000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (AppContext.user_id == -1) {
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				
			} else {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				if (ServiceUtils.isWorked(getApplicationContext(),
						"com.immersive.service.SneakerRecordService")) {
					AppContext.isRecordStart = true;
					intent = new Intent(SplashActivity.this,
							SneakerActivity.class);
					startActivity(intent);
				}
			}
			
			finish();
		}
	}
	
	@Override
	public void handler(Message msg) {
		switch (msg.what) {
		case NetStatus.LOGIN_SUC:
			Log.d("SplashActivity", "login success");
			break;
			
		case NetStatus.LOGIN_SUC>>2:
			break;
			
		case NetStatus.USER_NOT_EXIST:
		case NetStatus.PASSWORD_INVALID:
			Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			break;
		}
	}

}
