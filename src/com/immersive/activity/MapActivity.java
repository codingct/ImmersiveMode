package com.immersive.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.code.immersivemode.R;

public class MapActivity extends BaseActivity {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	public static final int TYPE_NORMAL = BaiduMap.MAP_TYPE_NORMAL;
	public static final int TYPE_SATELLITE = BaiduMap.MAP_TYPE_SATELLITE;
	private LocationClient mLocationClient = null;
	private BDLocationListener mBLocationListener = null;
	private LocationClientOption mLocationOption = null;
	
	
	
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
		setContentView(R.layout.map_view);
		initMap();
		startLocation();
	}
	
	private void initMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);								//开启定位图层
		
		mBLocationListener = new MyLocationListener();
		mLocationClient = new LocationClient(getApplicationContext());     	//声明LocationClient类
	    mLocationClient.registerLocationListener(mBLocationListener);    	//注册监听函数
	   
	    mLocationOption = new LocationClientOption();
	    mLocationOption.setLocationMode(LocationMode.Hight_Accuracy);		//设置定位模式
	    mLocationOption.setCoorType("bd09ll");								//返回的定位结果是百度经纬度,默认值gcj02
	    mLocationOption.setScanSpan(1000);									//设置发起定位请求的间隔时间为5000ms
	    mLocationOption.setIsNeedAddress(true);								//返回的定位结果包含地址信息
	    mLocationOption.setNeedDeviceDirect(true);							//返回的定位结果包含手机机头的方向
	    mLocationClient.setLocOption(mLocationOption);
	    mLocationClient.start();
	    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomBy(5);
		mBaiduMap.animateMapStatus(mapStatusUpdate);
	}
	
	private void startLocation() {
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else 
			Log.d("LocSDK5", "locClient is null or not started");
	}
	
	private void setMapType(int type) {
		switch (type) {
		case TYPE_NORMAL:
			mBaiduMap.setMapType(TYPE_NORMAL);
			break;
		case TYPE_SATELLITE:
			mBaiduMap.setMapType(TYPE_SATELLITE);
			break;
		}
	}
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null)
		            return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			} 
			Log.d("Location", sb.toString());
			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.direction(location.getDirection())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude())
					.build();
			mBaiduMap.setMyLocationData(locData);
			LatLng locationPosition = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(locationPosition);
			mBaiduMap.animateMapStatus(u);

			
 
		}
	}
	
	protected void finishThis() {
		this.finish();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
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
