package com.immersive.net;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.ImageOptions;
import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;


public class MyAjax extends AQuery {
	private static Bitmap preset;
	public MyAjax(Context context) {
		super(context);
	}
	
	public void ajax(String url, Map<String,?> params, final CallBack cb) {
		super.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
			public void callback(String url, JSONObject json, AjaxStatus ajaxStatus) {
				try {
					NetStatus status = null;
					Log.e("fuck", ajaxStatus.getMessage());
					if (!ajaxStatus.getMessage().equals("OK")) {
						status = new NetStatus(-1, ajaxStatus.getMessage());
					} else if(json.getInt("success") != 1) {
						status = new NetStatus(json.getInt("code"));
//								json.getString("error_message"));
					}
					
					if(status == null) {
						cb.suc(json);
					} else {
						Log.e("_NetWork", status.getStatusCode()+"");
						cb.fail(status);
					}
					
				} catch (JSONException e) {
					Log.e("_JsonError", e.getMessage());
				}
			}
		});
	}
	
	
	public static ImageOptions newOptions() {
		if(preset == null) {
			preset = ((BitmapDrawable)AppContext.getAppContext().getResources()
					.getDrawable(R.drawable.ic_head)).getBitmap();
		}
		
		ImageOptions options = new ImageOptions();
		options.animation = AQuery.FADE_IN_NETWORK;
		options.memCache = true;
		options.fileCache = true;
		options.fallback = R.drawable.ic_head;
		//options.preset = preset;
		options.round = 1000;
		
		return options;
	}
	
	public static interface CallBack {
		public void suc(JSONObject json) throws JSONException;
		public void fail(NetStatus status) throws JSONException;
	}
}
