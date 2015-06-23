package com.immersive.activity;


import com.code.immersivemode.R;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class SplashActivity extends BaseActivity {
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 requestWindowFeature(Window.FEATURE_NO_TITLE); 
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
			finish();
		}
	}

}
