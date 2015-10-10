package com.immersive.net;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.code.immersivemode.AppContext;
import com.code.immersivemode.Step;
import com.code.immersivemode.User;
import com.immersive.activity.BaseActivity;

public class SneakerApi {
	public static final String UserType = "user";
	public static MyAjax aq;
	public static String tmpid;
	public static String tmpAccessToken;
	
	
	public static AQuery getAQuery() {
		return aq;
	}
	
	
	/**
	 * 用户注册
	 * @param user
	 */
	public static void userRegister(final User user) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("email", user.getEmail());
		params.put("password", user.getPassword());
		params.put("identity", UserType);
		
		String url = NetContext.suffix(NetContext.URL_USER_REGISTER);
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				JSONObject info = json.getJSONObject("data");
				user.setId(Long.valueOf(info.getString("uid")));
				
				AppContext.tmpAccessToken = info.getString("access_token");
				AppContext.user_id = Integer.valueOf(info.getString("uid"));
				tmpid = info.getString("uid");
				tmpAccessToken = info.getString("access_token");
				
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
	
	
	/**
	 * 用户登录
	 * @param user
	 */
	public static void userLogin(final User user) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("email", user.getEmail());
		params.put("password", user.getPassword());
		params.put("identity", UserType);
		
		String url = NetContext.suffix(NetContext.URL_USER_LOGIN);
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				
				JSONObject info = json.getJSONObject("data");
				user.setId(Long.valueOf(info.getString("uid")));
				
				AppContext.tmpAccessToken = info.getString("access_token");
				AppContext.user_id = Integer.valueOf(info.getString("uid"));
				tmpid = info.getString("uid");
				tmpAccessToken = info.getString("access_token");

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
	
	/**
	 * 获取用户信息
	 * @param user
	 */
	public static void userInfo_get(final User user) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("email", user.getEmail());
		params.put("access_token", AppContext.tmpAccessToken);
		params.put("identity", UserType);
		
		String url = NetContext.suffix(NetContext.URL_USER_INFO_GET);
		
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				JSONObject info = json.getJSONObject("data");
				user.setId(Long.valueOf(info.getString("uid")));
				user.setName(info.getString("username"));
				user.setGender(Integer.valueOf(info.getString("sex")));
				user.setBirthday(info.getString("year"));
				user.setHeight(info.getString("height"));
				user.setWeight(info.getString("weight"));
				
				BaseActivity.broadcast(newMsg(NetStatus.USERINFO_GET_SUC));
			}
			
			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.USERINFO_GET_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
	
	public static void userInfo_set(final User user) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("uid", user.getId());
		params.put("access_token", AppContext.tmpAccessToken);
		params.put("username", user.getName());
		params.put("year", user.getBirthday());
		params.put("sex", user.getGender());
		params.put("height", user.getHeight());
		params.put("weight", user.getWeight());
		
		String url = NetContext.suffix(NetContext.URL_USER_INFO_MODIFY);
		
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				BaseActivity.broadcast(newMsg(NetStatus.USERINFO_SET_SUC));
			}
			
			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.USERINFO_SET_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
	
	public static void dailyReocrd_get(int user_id, final List<Step> step) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("uid", user_id);
		params.put("access_token", AppContext.tmpAccessToken);
//		Log.e("", "uid" + user_id);
//		Log.e("", "accessToken:" + AppContext.tmpAccessToken);
		
		
		String url = NetContext.suffix(NetContext.URL_RECORD_DAILY_GET);
		
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {

				JSONArray recordArray = json.getJSONArray("data");
				for (int i = 0; i < recordArray.length(); i++) {
					Step mStep = new Step();
					mStep.setStep_date(recordArray.getJSONObject(i).getString("date"));

					String steps = recordArray.getJSONObject(i).getString("allhour");
					Log.e("fuck", steps);
					String hour_steps[] = steps.split(",");
					for (int j = 0; j < hour_steps.length; j++) {
						try {
							Field stepField = Step.class
									.getDeclaredField("step_" + j);
							stepField.setAccessible(true);
							stepField.set(mStep, Integer.valueOf(hour_steps[j]));
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					step.add(mStep);
				}
				
				BaseActivity.broadcast(newMsg(NetStatus.RECORD_DAILY_GET_SUC));
			}
			
			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.RECORD_DAILY_GET_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
	
	public static void dailyReocrd_set(final Step step) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("uid", step.getUser_id());
		params.put("access_token", AppContext.tmpAccessToken);
		params.put("date", step.getStep_date());
		String hour = "";
		for (int i = 0; i < 24; i++) {
			try {
				Field stepField = Step.class.getDeclaredField("step_" + i);
				stepField.setAccessible(true);
				if (i == 23) {
					hour += stepField.get(step);
				} else {
					hour += stepField.get(step) + ",";
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		params.put("hour", hour);
		
		String url = NetContext.suffix(NetContext.URL_RECORD_DAILY_ADD);
		
		aq.ajax(url, params, new MyAjax.CallBack() {
			@Override
			public void suc(JSONObject json) throws JSONException {
				
				BaseActivity.broadcast(newMsg(NetStatus.RECORD_DAILY_SET_SUC));
			}
			
			@Override
			public void fail(NetStatus status) throws JSONException {
				if(status.getStatusCode() == -1) {
					BaseActivity.broadcast(newMsg(NetStatus.RECORD_DAILY_SET_SUC>>2, status.getStatusMessage()));
				} else {
					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
				}
			}
		});
	}
//	
//	public static void logout() {
//		String url = QueryStringUtils.suffix(NetContext.URL_USERS_LOGOUT, tmpWid, tmpAccessToken);
//		aq.ajax(url, null, new MyAjax.CallBack() {
//			@Override
//			public void suc(JSONObject json) throws JSONException {
//				tmpWid = null;
//				tmpAccessToken = null;
//				BaseActivity.broadcast(newMsg(NetStatus.LOGOUT_SUC));
//			}
//			
//			@Override
//			public void fail(NetStatus status) throws JSONException {
//				if(status.getStatusCode() == -1) {
//					BaseActivity.broadcast(newMsg(NetStatus.LOGOUT_SUC>>2, status.getStatusMessage()));
//				} else {
//					BaseActivity.broadcast(newMsg(status.getStatusCode(), status.getStatusMessage()));
//				}
//			}
//		});
//	}
//	
	
	
	
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
