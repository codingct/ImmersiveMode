package com.immersive.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addHandler(mHandler);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
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
