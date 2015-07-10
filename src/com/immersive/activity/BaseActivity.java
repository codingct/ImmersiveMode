package com.immersive.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;



import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends Activity {
	private static ArrayList<Handler> handlers = new ArrayList<Handler>();
	private MyHandler mHandler = new MyHandler(this);
	
	public static void addHandler(Handler handler) {
		handlers.add(handler);
	}
	
	public static void deleteHandler(Handler handler) {
		handlers.remove(handler);
	}
	
	public static void removeAll() {
		handlers.clear();
	}
	
	public static void broadcast(Message message) {
		for(Handler handler : handlers) {
			Message send = new Message();
			send.copyFrom(message);
			handler.sendMessage(send);
		}
	}
	
	public void sendMsg(Message message) {
		Message send = new Message();
		send.copyFrom(message);
		mHandler.sendMessage(send);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		 }
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		addHandler(mHandler);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void finish() {
		deleteHandler(mHandler);
		super.finish();
	}
	
	static class MyHandler extends Handler {
        private WeakReference<BaseActivity> mOuter;
 
        public MyHandler(BaseActivity activity) {
            mOuter = new WeakReference<BaseActivity>(activity);
        }
 
		@Override
        public void handleMessage(Message msg) {
        	final BaseActivity outer = mOuter.get();
            if (outer == null) {
            	return;
            }
            
            outer.handler(msg);
        }
	}
	
	public void handler(Message msg) {
		
	}
}
