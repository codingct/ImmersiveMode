package com.immersive.net;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.code.immersivemode.AppContext;
import com.immersive.activity.BaseActivity;

public class ImmersiveApi {
	public static MyAjax aq;
	public static String tmpWid;
	public static String tmpAccessToken;
	
	public static AQuery getAQuery() {
		return aq;
	}
	
	public static void register() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		//params.put("name", user.getName());
		
		
		String url = "";
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				
				
				BaseActivity.broadcast(newMsg(NetStatus.REGISTER_SUC));
			}

			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.REGISTER_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
	
	/*
	public static void login(final UserBean user) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		if(user.getQqId() != null) {
			params.put("id_type", NetContext.ID_TYPE_QQ);
			params.put("id", user.getQqId());
		} else if(user.getWeiboId() != null) {
			params.put("id_type", NetContext.ID_TYPE_WEIBO);
			params.put("id", user.getWeiboId());
		} else if(user.getRenrenId() != null) {
			params.put("id_type", NetContext.ID_TYPE_RENREN);
			params.put("id", user.getRenrenId());
		}
		
		String url = QueryStringUtils.suffix(NetContext.URL_USERS_LOGIN);
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				user.setAccessToken(json.getString("access_token"));
				
				JSONObject info = json.getJSONObject("userInfo");
				user.setWid(info.getString("wid"));
				user.setQqId(info.getString("qq_id"));
				user.setWeiboId(info.getString("weibo_id"));
				user.setRenrenId(info.getString("renren_id"));
				user.setName(info.getString("name"));
				user.setGender(info.getString("gender"));
				user.setBirthday(info.getString("birthday"));
				user.setProvinceId(info.getInt("province_id"));
				user.setCityId(info.getInt("city_id"));
				user.setLastOnline(info.getString("last_online"));
				
				tmpWid = user.getWid();
				tmpAccessToken = user.getAccessToken();

				BaseActivity.broadcast(newMsg(NetStatus.LOGIN_SUC));
			}
			
			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.LOGIN_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
	
	public static void updateOnlineStatus(final UserBean user) {
		String url = QueryStringUtils.suffix(NetContext.URL_USERS_UPDATE_ONLINE_STATUS, tmpWid, tmpAccessToken);
		aq.ajax(url, null, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				BaseActivity.broadcast(newMsg(NetStatus.UPDATE_ONLINE_STATUS_SUC));
			}
			
			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.UPDATE_ONLINE_STATUS_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
	
	public static void logout() {
		String url = QueryStringUtils.suffix(NetContext.URL_USERS_LOGOUT, tmpWid, tmpAccessToken);
		aq.ajax(url, null, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				tmpWid = null;
				tmpAccessToken = null;
				BaseActivity.broadcast(newMsg(NetStatus.LOGOUT_SUC));
			}
			
			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.LOGOUT_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
	*/
	
	
	
	public static void init() {
		aq = new MyAjax(AppContext.getAppContext());
		AjaxCallback.setTimeout(3000);
	}
	
	private static Message newMsg(int status) {
		return newMsg(status, null);
	}
	
	private static Message newMsg(int status, Object obj) {
		Message msg = new Message();
		msg.what = status;
		msg.obj = obj;
		return msg;
	}
	
	private static Message newMsg(int status, Object obj, int arg1) {
		Message msg = new Message();
		msg.what = status;
		msg.obj = obj;
		msg.arg1 = arg1;
		return msg;
	}
}
