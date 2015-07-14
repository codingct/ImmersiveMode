package com.immersive.sneaker;

import java.util.List;

import android.util.Log;

public class SneakerCore {
	private static final String TAG = "SneakerCore"; 
	private static final double THRESHOLD_MIN = 0.5d;
	private static final double THRESHOLD_MAX = 20.0d;
	
	public SneakerCore() {
		Log.d(TAG, "SneakerCore Create");
	}

	/**
	 * 滤波算法
	 * @param list
	 * @return
	 */
	public int calStep(List<Double> list) {
		List<Double> acc_y = list;
		int position = 0;
		int step = 0;
		while (position < acc_y.size()-2) {
			if (acc_y.get(position) > acc_y.get(position+1)) {
				if (acc_y.get(position) - acc_y.get(position+1) > THRESHOLD_MIN && acc_y.get(position) - acc_y.get(position+1) < THRESHOLD_MAX) {
					if (acc_y.get(position+2) - acc_y.get(position+1) > THRESHOLD_MIN && acc_y.get(position) - acc_y.get(position+1) < THRESHOLD_MAX) {
						step++;
					}
				} 
			} else {
				if (acc_y.get(position+1) - acc_y.get(position) > THRESHOLD_MIN && acc_y.get(position) - acc_y.get(position+1) < THRESHOLD_MAX) {
					if (acc_y.get(position+1) - acc_y.get(position)+2 > THRESHOLD_MIN && acc_y.get(position) - acc_y.get(position+1) < THRESHOLD_MAX) {
					step++;
					}
				}
			}
			position += 2;
			
		}
		return step;
	}
	
	/**
	 * 二次滤波算法
	 * @param list
	 * @return
	 */
	public int calStep2(List<Double> list) {
		List<Double> acc_y = list;
		int position = 0;
		int step = 0;
		double largest = 0;
		double smallest = 0;
		while (position < acc_y.size() - 1) {
			int i = 1;
			
			if (acc_y.get(position) < acc_y.get(position + i)) {
				largest = acc_y.get(position);
				while (largest < acc_y.get(position + i)) {
					largest = acc_y.get(position + i); 
					i++;
					if (position + i > acc_y.size() - 1) {
						i--;
						break;
					}
				}
				
				smallest = largest;
				while (smallest > acc_y.get(position + i)) {
					smallest = acc_y.get(position + i);
					i++;
					if (position + i > acc_y.size() - 1) {
						i--;
						break;
					}
				}
				
				if (largest - acc_y.get(position) > THRESHOLD_MIN && largest - smallest > THRESHOLD_MIN 
						&& largest - acc_y.get(position) < THRESHOLD_MAX && largest - smallest < THRESHOLD_MAX) {
					step++;
					i--;
				}
				position += i;
			} else {
				position++;
			}
		}
		return step;
	}
	
	
}
