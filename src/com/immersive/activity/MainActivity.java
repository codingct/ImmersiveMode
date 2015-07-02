package com.immersive.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.code.immersivemode.R;
import com.immersive.adapter.DrawerAdapter;
import com.immersive.helper.Tab1Helper;
import com.immersive.helper.Tab2Helper;
import com.immersive.utils.ScreenShotUtils;
import com.immersive.utils.ShareUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



@TargetApi(Build.VERSION_CODES.KITKAT)
public class MainActivity extends BaseActivity {
	private ViewPager viewPager;//页卡内容  
    private ImageView imageView;// 动画图片  
    private TextView textView1,textView2,textView3;  
    private List<View> views;// Tab页面列表  
    private ImageView drawerToggler;
    private ListView drawerList;
    private Button FABAdd;
    private DrawerAdapter drawerAdapter;
    private int offset = 0;// 动画图片偏移量  
    private int currIndex = 0;// 当前页卡编号  
    private int bmpW;// 动画图片宽度  
    private View view1,view2,view3;//各个页卡  
    private DrawerLayout mDrawerLayout = null;
    private boolean isDrawOpened = false;
    private static boolean canExit = false;
    
    private final static String TAG = "MainActivity";
    private OnItemClickListener mDrawerItemClickListener = null;
    private OnClickListener mOnClickListener = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		 }
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 setContentView(R.layout.page_main);
		 
		 InitListener();
		 InitCursor();  
	     InitTab();  
	     InitViewPager();
	     InitDrawer();
	     InitWidget();
	}
	
	private Timer ExitTimer = new Timer();
	private class ExitCleanTask extends TimerTask{
	        @Override
	        public void run() {
	            canExit = false;
	        }
	    } 
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if (canExit) {
    			canExit = false;
    			finish();
    		} 
    		else  {
    			canExit = true;
    			Toast.makeText(MainActivity.this, getText(R.string.exit_again), Toast.LENGTH_SHORT).show();
    			ExitTimer.schedule(new ExitCleanTask(), 2000);
    		}
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
	
	private void InitWidget() {
		FABAdd = (Button) findViewById(R.id.fab_add);
		FABAdd.setOnClickListener(mOnClickListener);
	}
	
	private void InitListener() {
		mDrawerItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String ImgPath = ScreenShotUtils.shotAndSave(MainActivity.this);
				ShareUtils.shareMsg("测试分享", "测试分享", ImgPath, MainActivity.this);
			}
		};
		mOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.fab_add:
					Intent intent = new Intent(MainActivity.this, MapActivity.class);
					MainActivity.this.startActivity(intent);
					break;
					
				}
			}
			
		};
		
		
	}
	private void InitDrawer() {
		// TODO Auto-generated method stub
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggler = (ImageView) findViewById(R.id.topbar_opv);
        drawerToggler.setVisibility(View.VISIBLE);
        drawerToggler.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	if(isDrawOpened == false)
            		// 按钮按下，将抽屉打开
            		mDrawerLayout.openDrawer(Gravity.LEFT);
            	else
            		mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        mDrawerLayout.setDrawerListener(new DrawerListener(){

			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				isDrawOpened = false;
				drawerToggler.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));
			}

			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
				isDrawOpened = true;
				drawerToggler.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerAdapter = new DrawerAdapter(this);
        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(mDrawerItemClickListener);
        
	}

	@SuppressLint("InflateParams")
	private void InitViewPager() {  
        viewPager=(ViewPager) findViewById(R.id.vPager);  
        views=new ArrayList<View>();  
        LayoutInflater inflater=getLayoutInflater();  
        view1=inflater.inflate(R.layout.page_layout1, null);
        view2=inflater.inflate(R.layout.page_layout2, null);  
        view3=inflater.inflate(R.layout.page_layout3, null);  
        views.add(view1);  
        views.add(view2);  
        views.add(view3);  
        Tab1Helper helper1 = new Tab1Helper(view1);
        Tab2Helper helper2 = new Tab2Helper(view2);
        helper1.init();
        helper2.init();
        viewPager.setAdapter(new MyViewPagerAdapter(views));  
        viewPager.setCurrentItem(0);  
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());  
    }  

    private void InitTab() {  
        textView1 = (TextView) findViewById(R.id.tab1);  
        textView2 = (TextView) findViewById(R.id.tab2);  
        textView3 = (TextView) findViewById(R.id.tab3);  
  
        textView1.setOnClickListener(new MyOnClickListener(0));  
        textView2.setOnClickListener(new MyOnClickListener(1));  
        textView3.setOnClickListener(new MyOnClickListener(2));  
    }  
  
    private void InitCursor() {  
        imageView= (ImageView) findViewById(R.id.cursor);  
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor).getWidth();// 获取图片宽度  
        DisplayMetrics dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        int screenW = dm.widthPixels;// 获取分辨率宽度  
        offset = (screenW / 3 - bmpW) / 2;// 计算偏移量  
        Matrix matrix = new Matrix();  
        matrix.postTranslate(offset, 0);  
        imageView.setImageMatrix(matrix);// 设置动画初始位置  
    }  
    
    private class MyOnClickListener implements OnClickListener{  
        private int index=0;  
        public MyOnClickListener(int i){  
            index=i;  
        }  
        public void onClick(View v) {  
            viewPager.setCurrentItem(index);              
        }  
          
    }  
      
    private class MyViewPagerAdapter extends PagerAdapter{  
        private List<View> mListViews;  
          
        public MyViewPagerAdapter(List<View> mListViews) {  
            this.mListViews = mListViews;  
        }  
  
        @Override  
        public void destroyItem(ViewGroup container, int position, Object object)   {     
            container.removeView(mListViews.get(position));  
        }  
  
  
        @Override  
        public Object instantiateItem(ViewGroup container, int position) {            
             container.addView(mListViews.get(position), 0);  
             return mListViews.get(position);  
        }  
  
        @Override  
        public int getCount() {           
            return  mListViews.size();  
        }  
          
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {             
            return arg0==arg1;  
        }  
    }  
  
    private class MyOnPageChangeListener implements OnPageChangeListener{  
  
        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量  
        //int two = one * 2;// 页卡1 -> 页卡3 偏移量  
        public void onPageScrollStateChanged(int arg0) {  
              
              
        }  
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
              
              
        }  
  
        public void onPageSelected(int arg0) {  
            /*两种方法，这个是一种，下面还有一种，显然这个比较麻烦 
            Animation animation = null; 
            switch (arg0) { 
            case 0: 
                if (currIndex == 1) { 
                    animation = new TranslateAnimation(one, 0, 0, 0); 
                } else if (currIndex == 2) { 
                    animation = new TranslateAnimation(two, 0, 0, 0); 
                } 
                break; 
            case 1: 
                if (currIndex == 0) { 
                    animation = new TranslateAnimation(offset, one, 0, 0); 
                } else if (currIndex == 2) { 
                    animation = new TranslateAnimation(two, one, 0, 0); 
                } 
                break; 
            case 2: 
                if (currIndex == 0) { 
                    animation = new TranslateAnimation(offset, two, 0, 0); 
                } else if (currIndex == 1) { 
                    animation = new TranslateAnimation(one, two, 0, 0); 
                } 
                break; 
                 
            } 
            */  
            Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);//显然这个比较简洁，只有一行代码。  
            currIndex = arg0;  
            animation.setFillAfter(true);// True:图片停在动画结束位置  
            animation.setDuration(300);  
            imageView.startAnimation(animation);  
            
        }  
          
    }  
    
    public void handler(Message msg) {
		switch (msg.what) {
		}
	}
    
}  
	
