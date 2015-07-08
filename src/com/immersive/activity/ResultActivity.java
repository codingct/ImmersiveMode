package com.immersive.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.code.immersivemode.R;
import com.immersive.controller.MapController;
import com.immersive.utils.ScreenShotUtils;
import com.immersive.utils.ShareUtils;


public class ResultActivity extends BaseActivity {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	public static final int TYPE_NORMAL = BaiduMap.MAP_TYPE_NORMAL;
	public static final int TYPE_SATELLITE = BaiduMap.MAP_TYPE_SATELLITE;

	private MapController mMapController = null;
	
	private OnClickListener mOnClickListener = null;
	
	private TextView topBar_title = null;
	private ImageView topBar_back = null;
	private Button btn_share = null;
	
	
	@SuppressWarnings("deprecation")
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
		setContentView(R.layout.page_result);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参值
		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.8
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度
		getWindow().setAttributes(p); // 设置生效
		
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
		/* 测试数据 */
		LatLng pt1 = new LatLng(23.057037, 113.408722);
		LatLng pt2 = new LatLng(23.058037, 113.405722);
		LatLng pt3 = new LatLng(23.059037, 113.407722);
		LatLng pt4 = new LatLng(23.056037, 113.409722);
		LatLng pt5 = new LatLng(23.056037, 113.401722);
		LatLng pt6 = new LatLng(23.053037, 113.403722);
		LatLng pt7 = new LatLng(23.057037, 113.404722);
		List<LatLng> mPoints = new ArrayList<LatLng>();
		mPoints.add(pt1);
		mPoints.add(pt2);
		mPoints.add(pt3);
		mPoints.add(pt4);
		mPoints.add(pt5);
		mPoints.add(pt6);
		mPoints.add(pt7);
		

		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(pt1, 16);
		mBaiduMap.animateMapStatus(u);
		mMapController.overlayPointLine(mPoints);
		mMapController.calDistance(mPoints);
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
