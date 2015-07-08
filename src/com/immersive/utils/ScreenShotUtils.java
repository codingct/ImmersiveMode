package com.immersive.utils;

import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
  


import android.app.Activity;  
import android.app.Dialog;
import android.graphics.Bitmap;  
import android.graphics.Rect;  
import android.os.Environment;
import android.util.Log;  
import android.view.View;  
  
/** 
 * 进行截屏工具类 
 * @author tong.chen
 * @time 2015/06/20 
 */  
public class ScreenShotUtils {
	private final static String TAG = "ScreenShotUtils";
	private final static String PATH = Environment.getExternalStorageDirectory() + "/Sneaker/ScreenShot";
    /** 
     * 进行截取屏幕   
     * @param pActivity 
     * @return bitmap 
     */  
    public static Bitmap takeScreenShot(Activity pActivity)  
    {  
        Bitmap bitmap=null;  
        View view=pActivity.getWindow().getDecorView();  
        // 设置是否可以进行绘图缓存  
        view.setDrawingCacheEnabled(true);  
        // 如果绘图缓存无法，强制构建绘图缓存  
        view.buildDrawingCache();  
        // 返回这个缓存视图   
        bitmap=view.getDrawingCache();  
        if (bitmap == null) {
        	Log.d(TAG, "Bitmap NULL!");
        }
        // 获取状态栏高度  
        Rect frame=new Rect();  
        // 测量屏幕宽和高  
        view.getWindowVisibleDisplayFrame(frame);  
        int stautsHeight=frame.top;  
        Log.d("TAG", "状态栏的高度为:"+stautsHeight);  
        int width=(int) (pActivity.getWindowManager().getDefaultDisplay().getWidth() * 0.9);  
        int height=(int) (pActivity.getWindowManager().getDefaultDisplay().getHeight()*0.8);  
        Log.d("TAG", "width:"+width + "height:"+height);  
        // 根据坐标点和需要的宽和高创建bitmap  
        bitmap=Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height-stautsHeight);  
        return bitmap;  
    }  
    
    /** 
     * 进行截取屏幕   
     * @param pActivity 
     * @return bitmap 
     */  
    public static Bitmap takeDialogShot(Dialog mDialog)  
    {  
        Bitmap bitmap=null;  
        View view=mDialog.getWindow().getDecorView();  
        // 设置是否可以进行绘图缓存  
        view.setDrawingCacheEnabled(true);  
        // 如果绘图缓存无法，强制构建绘图缓存  
        view.buildDrawingCache();  
        // 返回这个缓存视图   
        bitmap=view.getDrawingCache();  
        if (bitmap == null) {
        	Log.d(TAG, "Bitmap NULL!");
        }
        // 获取状态栏高度  
        Rect frame=new Rect();  
        // 测量屏幕宽和高  
        view.getWindowVisibleDisplayFrame(frame);  
        int stautsHeight=frame.top;  
        Log.d("TAG", "状态栏的高度为:"+stautsHeight);  
        int width=mDialog.getWindow().getWindowManager().getDefaultDisplay().getWidth();  
        int height=mDialog.getWindow().getWindowManager().getDefaultDisplay().getHeight();  
        Log.d("TAG", "width:"+width + "height:"+height);  
        // 根据坐标点和需要的宽和高创建bitmap  
        bitmap=Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height-stautsHeight);  
        return bitmap;  
    }  
      
      
    /** 
     * 保存图片到sdcard中 
     * @param pBitmap 
     */  
    private static boolean savePic(Bitmap pBitmap,String strName)  
    {  
      FileOutputStream fos=null;  
      try {  
        fos=new FileOutputStream(strName);  
        if(null!=fos)  
        {  
            pBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);  
            fos.flush();  
            fos.close();  
            return true;  
        }  
          
    } catch (FileNotFoundException e) {  
        e.printStackTrace();  
    }catch (IOException e) {  
        e.printStackTrace();  
    }  
      return false;  
    }   
    /** 
     * 截图 
     * @param pActivity  
     * @return 截图并且保存sdcard成功返回true，否则返回false 
     */  
    public static String shotAndSave(Activity pActivity)  
    {   
    	long mImgKey = System.currentTimeMillis();
        ScreenShotUtils.savePic(takeScreenShot(pActivity), PATH + "/ScreenShot" + mImgKey+".png");  
        return PATH + "/ScreenShot" + mImgKey+".png";
    }  
    
      
}  