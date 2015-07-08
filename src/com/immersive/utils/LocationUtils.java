package com.immersive.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class LocationUtils {
	private static final double VALID = 0.00001;

	public static boolean isValid(double lastLatitude, double lastLongitude, double mLatitude, double mLongitude) {
		if (lastLatitude == -1 || lastLongitude == -1) {
			return false;
		} else if (Math.abs(lastLatitude-mLatitude) < VALID || Math.abs(lastLongitude-mLongitude) < VALID) {
			return false;
		} else {
			if (DistanceUtil.getDistance(new LatLng(lastLatitude, lastLongitude), new LatLng(mLatitude, mLongitude)) < 100) {
				return true;
			} else {
				return false;
			}
		}
			
	}
}
