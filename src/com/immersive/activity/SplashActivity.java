package com.immersive.activity;


import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.immersive.utils.ServiceUtils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class SplashActivity extends BaseActivity {
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 setContentView(R.layout.page_start);
		 
		 MyThread t = new MyThread();
		 t.start();
		 
	
	}
	
	
	private class MyThread extends Thread {
		@Override
		public void run() {
			try {
				sleep(1500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			if (ServiceUtils.isWorked(getApplicationContext(), "com.immersive.service.SneakerRecordService")) {
				AppContext.isRecordStart = true;
				intent = new Intent(SplashActivity.this, SneakerActivity.class);
				startActivity(intent);
			}
			
			finish();
		}
	}

}
