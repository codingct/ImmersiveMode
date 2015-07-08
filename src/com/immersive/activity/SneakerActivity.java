package com.immersive.activity;

import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.immersive.controller.MapController;
import com.immersive.service.SneakerRecordService;
import com.immersive.utils.ServiceUtils;
import com.immersive.utils.StringUtils;


public class SneakerActivity extends BaseActivity {

	public static final String TAG = "SneakerActivity";
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	public static final int TYPE_NORMAL = BaiduMap.MAP_TYPE_NORMAL;
	public static final int TYPE_SATELLITE = BaiduMap.MAP_TYPE_SATELLITE;
	private MapController mMapController = null;
	
	private OnClickListener mOnClickListener = null;
	
	private View cover = null;
	private TextView topBar_title = null;
	private ImageView topBar_back = null;
	private TextView tv_timer,tv_distance = null;
	private Button btn_start, btn_pause, btn_over = null;
	
	private SneakerReceiver mSneakerReceiver = null;
	private IntentFilter filter = null;
	private double distance = 0;

	
	private class SneakerReceiver extends BroadcastReceiver{

	    public void onReceive(Context context, Intent intent) {
	        Bundle extras = intent.getExtras();
	        if (extras != null) {
	            if(extras.containsKey("value")){
	            	// 频率为5s一次
	                // 标记定位位置
	    			MyLocationData locData = new MyLocationData.Builder()
	    					.accuracy((Float) extras.get("radius"))
	    					.direction((Float) extras.get("direction"))
	    					.latitude((Double) extras.get("latitude"))
	    					.longitude((Double) extras.get("lontitude"))
	    					.build();
	    			mBaiduMap.setMyLocationData(locData);
	    			LatLng locationPosition = new LatLng((Double) extras.get("latitude"), (Double) extras.get("lontitude"));
	    			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(locationPosition);
	    			mBaiduMap.animateMapStatus(u);
	    			if (SneakerRecordService.mPoints != null && AppContext.isRecordStart) {
	    				UpdatePointsLine(SneakerRecordService.mPoints);
	    			}
	                
	            } else if (extras.containsKey("time")) {
	            	// 频率为1s一次
	            	// 更新记录时间
	            	String str_time = StringUtils.formatTime(extras.getInt("time"));
	            	tv_timer.setText(getString(R.string.time) + "   " + str_time);
	            }
	        }     
	    }
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.page_sneaker);
		initListener();
		initWidget();
		initMap();
		initBroadcastReceiver();
		initService();
		
	}
	private void initListener() {
		
		mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.topbar_opv:
					if (AppContext.isRecordStart) {
						showDialog();
					} else {
						finishThis();
					}
					break;
				case R.id.opv_start:
					AppContext.isRecordStart = true;
					if (SneakerRecordService.mRecordHandler != null) {
						SneakerRecordService.mRecordHandler.sendEmptyMessage(SneakerRecordService.MSG_CHECK);
						btn_start.setVisibility(View.GONE);
						btn_over.setVisibility(View.VISIBLE);
						btn_pause.setVisibility(View.VISIBLE);
						tv_distance.setText(getString(R.string.distance) + "   " + "N/A");
					} else {
						Log.e(TAG, "Service Handler error");
					}
					break;
				case R.id.opv_pause:
					break;
				case R.id.opv_over:
					AppContext.isRecordStart = false;
					if (SneakerRecordService.mRecordHandler != null) {
						SneakerRecordService.mRecordHandler.sendEmptyMessage(SneakerRecordService.MSG_CHECK);
						btn_start.setVisibility(View.VISIBLE);
						btn_over.setVisibility(View.GONE);
						btn_pause.setVisibility(View.GONE);
					} else {
						Log.e(TAG, "Service Handler error");
					}
					cover.setVisibility(View.VISIBLE);
					Intent resultIntent = new Intent(SneakerActivity.this, ResultActivity.class);
					startActivity(resultIntent);
					
					break;
				}
			}
		};
	}
	
	private void showDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("Sneaker is running, it will run at background");
		builder.setTitle("tips");
		
		builder.setPositiveButton("know", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
				}
			});
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				}
//			});
		builder.create().show();
	}
	private void initWidget() {
		topBar_title = (TextView) findViewById(R.id.topbar_title);
		topBar_back = (ImageView) findViewById(R.id.topbar_opv);
		topBar_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
		topBar_back.setOnClickListener(mOnClickListener);
		
		tv_timer = (TextView) findViewById(R.id.opv_timer);
		tv_distance = (TextView) findViewById(R.id.opv_distance);
		cover = (View) findViewById(R.id.cover);
		
		btn_start = (Button) findViewById(R.id.opv_start);
		btn_start.setOnClickListener(mOnClickListener);
		btn_pause = (Button) findViewById(R.id.opv_pause);
		btn_pause.setOnClickListener(mOnClickListener);
		btn_over = (Button) findViewById(R.id.opv_over);
		btn_over.setOnClickListener(mOnClickListener);
		
		if (AppContext.isRecordStart) {
			btn_start.setVisibility(View.GONE);
			btn_over.setVisibility(View.VISIBLE);
			btn_pause.setVisibility(View.VISIBLE);
		}
	}
	
	private void initMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.showZoomControls(true);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);								//开启定位图层
		
		mMapController = new MapController(mBaiduMap);
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
		mBaiduMap.animateMapStatus(u);
		
	}
	
	
	private void initBroadcastReceiver() {
		mSneakerReceiver = new SneakerReceiver();
        filter = new IntentFilter();
        filter.addAction("com.immersive.broadcast.SneakerReceiver");
        registerReceiver(mSneakerReceiver, filter);
	}
	
	private void initService() {
		if (!ServiceUtils.isWorked(SneakerActivity.this, "com.immersive.service.SneakerRecordService")) {
			Intent Serviceintent = new Intent(this, SneakerRecordService.class);
			startService(Serviceintent);
			Log.e("service", "service not exist, create");
		} else {
			Log.e("service", "service exist");
		}
		if (SneakerRecordService.mLastLatitude != -1 && SneakerRecordService.mLastLongitude != -1) {
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(
					new LatLng(SneakerRecordService.mLastLatitude,SneakerRecordService.mLastLongitude));
			Log.e(TAG, "find last point");
			mBaiduMap.animateMapStatus(update);
		}

	}
	
	private void stopService() {
		AppContext.isRecordStart = false;
		Intent Serviceintent = new Intent(this, SneakerRecordService.class);
		stopService(Serviceintent);
	}
	
	
	private void UpdatePointsLine(List<LatLng> mPoints) {
		/* 测试数据 */
//		LatLng pt1 = new LatLng(23.057037, 113.408722);
//		LatLng pt2 = new LatLng(23.058037, 113.405722);
//		LatLng pt3 = new LatLng(23.059037, 113.407722);
//		LatLng pt4 = new LatLng(23.056037, 113.409722);
//		LatLng pt5 = new LatLng(23.056037, 113.401722);
//		LatLng pt6 = new LatLng(23.053037, 113.403722);
//		LatLng pt7 = new LatLng(23.057037, 113.404722);
//		List<LatLng> mPoints = new ArrayList<LatLng>();
//		mPoints.add(pt1);
//		mPoints.add(pt2);
//		mPoints.add(pt3);
//		mPoints.add(pt4);
//		mPoints.add(pt5);
//		mPoints.add(pt6);
//		mPoints.add(pt7);
//		
		if (mPoints.size() < 2) {
			Log.e(TAG, "mPoint size invalid==>" + mPoints.size());
			tv_distance.setText(getString(R.string.distance) + "   " + "N/A");
			return;
		}
		mMapController.clearOverlay();
		mMapController.overlayPointLine(mPoints);
		distance = mMapController.calDistance(mPoints);
		tv_distance.setText(getString(R.string.distance) + "   " + distance);
	}
	
	
	protected void finishThis() {
		this.finish();
		stopService();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBaiduMap.setMyLocationEnabled(false);
		//stopService();
		unregisterReceiver(mSneakerReceiver);	// 注销广播接收器
		mMapView.onDestroy();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if (AppContext.isRecordStart) {
				showDialog();
			} else {
				finishThis();
			}
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
}
