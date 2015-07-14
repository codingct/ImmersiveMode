package com.immersive.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.code.immersivemode.Location;
import com.code.immersivemode.R;
import com.immersive.controller.MapController;
import com.immersive.utils.GreenDaoUtils;
import com.immersive.utils.ScreenShotUtils;
import com.immersive.utils.ShareUtils;


public class ResultActivity extends SneakerDialogActivity {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	public static final int TYPE_NORMAL = BaiduMap.MAP_TYPE_NORMAL;
	public static final int TYPE_SATELLITE = BaiduMap.MAP_TYPE_SATELLITE;
	private static final String TAG = "ResultActivity";

	private MapController mMapController = null;
	
	private OnClickListener mOnClickListener = null;
	
	private TextView topBar_title = null;
	private ImageView topBar_back = null;
	private Button btn_share = null;
	
	private int currentPos = -1;
	private long currentId = -1;
	private double distance = 0;
	private int step = 0;
	private int time = 0;
	
	private List<Location> mLocationList = null;
	
	private GreenDaoUtils mDBUtils = null;
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		currentId = intent.getLongExtra("record_id", -1);
		currentPos = intent.getIntExtra("position", -1);
		distance = intent.getDoubleExtra("distance", 0);
		step = intent.getIntExtra("step", 0);
		time = intent.getIntExtra("time", 0);
		
		mDBUtils = GreenDaoUtils.getInstance(this);
		
		
		initListener();
		initWidget();
		initMap();
		OverlayPointsLine();
	}
	private void initListener() {
		mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.topbar_opv:
					finishThis();
					break;
				case R.id.opv_share:
					share();
					break;
				}
			}
		};
	}
	private void initWidget() {
		topBar_title = (TextView) findViewById(R.id.topbar_title);
		topBar_back = (ImageView) findViewById(R.id.topbar_opv);
		
		topBar_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
		topBar_back.setOnClickListener(mOnClickListener);
		btn_share = (Button) findViewById(R.id.opv_share);
		btn_share.setOnClickListener(mOnClickListener);
	}
	
	private void initMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);								//开启定位图层
		
		mMapController = new MapController(mBaiduMap);
	}
	
	private void OverlayPointsLine() {
		if (currentId == -1) {
			Log.e(TAG, "currentId Error");
			return;
		}
		mLocationList = mDBUtils.requestFindLocationByRecord(currentId);
		if (mLocationList == null) {
			Log.e(TAG, "LocationList cannot find!");
			return;
			
		}
		List<LatLng> mPoints = new ArrayList<LatLng>();
		LatLng point = null;
		for (int i = 0; i < mLocationList.size(); i++) {
			point = new LatLng(mLocationList.get(i).getLatitude(), mLocationList.get(i).getLongitude());
			mPoints.add(point);
		}
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 18);
		mBaiduMap.animateMapStatus(u);
		mMapController.overlayPointLine(mPoints);
	}
	
	
	private void share() {
		String ImgPath = ScreenShotUtils.shotAndSave(ResultActivity.this);
		ShareUtils.shareMsg("测试分享", "测试分享", ImgPath, ResultActivity.this);
	}
	
	protected void finishThis() {
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
}
