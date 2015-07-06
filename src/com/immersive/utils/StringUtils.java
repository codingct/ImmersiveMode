package com.immersive.utils;

public class StringUtils {

	public static String formatTime(int time) {

		int sec, min, hour = 0;
		hour = time / 3600;
		min = (time - hour * 3600) / 60;
		sec = time - hour * 3600 - min * 60;
		String format = String.format("%1$02d", hour)+":"+String.format("%1$02d", min)+":"+String.format("%1$02d", sec);
		
		return format;
	}
}
