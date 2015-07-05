package com.immersive.controller;

import java.util.List;
import android.util.Log;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class MapController {

	private static final String TAG = "MapController";
	public static final int TYPE_NORMAL = BaiduMap.MAP_TYPE_NORMAL;
	public static final int TYPE_SATELLITE = BaiduMap.MAP_TYPE_SATELLITE;
	private BaiduMap mBaiduMap = null;
	
	public MapController(BaiduMap mBaiduMap) {
		this.mBaiduMap = mBaiduMap;
	}
	
	public void onDestroy() {
		this.mBaiduMap = null;
	}
	
	/**
	 * 设置地图类型
	 * @param type
	 */
	public void setMapType(int type) {
		switch (type) {
		case TYPE_NORMAL:
			mBaiduMap.setMapType(TYPE_NORMAL);
			break;
		case TYPE_SATELLITE:
			mBaiduMap.setMapType(TYPE_SATELLITE);
			break;
		}
	}
	
	/**
	 * 绘制轨迹
	 * @param points
	 */
	public void overlayPointLine(List<LatLng> points) {
		if (points == null || points.size() < 2) {
			return;
		}
		OverlayOptions mPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
		mBaiduMap.addOverlay(mPolyline);
	}
	
	/**
	 * 清除轨迹
	 */
	public void clearOverlay() {
		mBaiduMap.clear();
	}
	
	/**
	 * 计算轨迹总路程
	 * @param points
	 * @return distance
	 */
	public double calDistance(List<LatLng> points) {
		if (points == null || points.size() < 2) {
			return 0;
		}
		double distance = 0;
		for(int i = 0; i < points.size() - 1; i++) {
			distance += DistanceUtil.getDistance(points.get(i), points.get(i+1));
		}
		Log.d(TAG, "total distance:" + distance);
		return distance;
	}
}
