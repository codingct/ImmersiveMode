package com.immersive.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.Step;
import com.immersive.adapter.DrawerAdapter;
import com.immersive.helper.CommunityHelper;
import com.immersive.helper.DailyRecordHelper;
import com.immersive.helper.SneakerReocrdHelper;
import com.immersive.net.NetStatus;
import com.immersive.net.SneakerApi;
import com.immersive.service.SneakerGuardService;
import com.immersive.utils.GreenDaoUtils;
import com.immersive.utils.ScreenShotUtils;
import com.immersive.utils.ServiceUtils;
import com.immersive.utils.ShareUtils;
import com.immersive.widget.LoadingDialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends BaseActivity {
	private ViewPager viewPager;//页卡内容  
    private ImageView imageView;// 动画图片  
    private TextView textView1,textView2,textView3;  
    private List<View> views;// Tab页面列表  
    private ImageView drawerToggler;
    private ImageView btnSyc;
    private ListView drawerList;
    private RelativeLayout FABAdd;
    private DrawerAdapter drawerAdapter;
    private int offset = 0;// 动画图片偏移量  
    private int currIndex = 0;// 当前页卡编号  
    private int bmpW;// 动画图片宽度  
    private View view1, view2, view3;//各个页卡  
    private DrawerLayout mDrawerLayout = null;
    private TextView mDrawerTitle = null;
    private ImageView mDrawerAvatar = null;
    private boolean isDrawOpened = false;
    private static boolean canExit = false;
    
    public View cover = null;
    public final static String TAG = "MainActivity";
    private OnItemClickListener mDrawerItemClickListener = null;
    private OnClickListener mOnClickListener = null;
    
    private ImageView btn_syc = null;
    private GreenDaoUtils mDBUtils = null;
    private List<Step> mStepList = null;
	private LoadingDialog mLoadingDialog;
	private List<Step> remoteList = null;
	private int sycSize = -1;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 setContentView(R.layout.page_main);
		 mDBUtils = GreenDaoUtils.getInstance(this);
		 
		 initListener(); 
	     initTab();  
	     initViewPager();
	     initDrawer();
	     initWidget();
	     initService();
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
	
	private void RecordSyc() {
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.show();
		mStepList = mDBUtils.getAllStep(AppContext.user_id);
		remoteList = new ArrayList<Step>();
		SneakerApi.dailyReocrd_get(AppContext.user_id, remoteList);
		
	}
	
	private void saveStepInfo() {
		for (int i = 0; i < remoteList.size(); i++) {
			remoteList.get(i).setUser_id(AppContext.user_id);
			mDBUtils.addToStepTable(remoteList.get(i));
		}
		if (remoteList.size() > 0) {
			Log.d(TAG, "new syc record add");
		}
		mLoadingDialog.dismiss();
		Toast.makeText(this, "云同步成功，新增" + remoteList.size() + "条记录，请刷新列表查看", Toast.LENGTH_SHORT).show();
	}
	
	private void initService() {
		if (ServiceUtils.isWorked(this, "com.immersive.service.SneakerGuardService")) {
			Log.e(TAG, "SneakerGuardService already run");
			return;
		}
		Intent Serviceintent = new Intent(this, SneakerGuardService.class);
	    startService(Serviceintent);
	    Log.e(TAG, "SneakerGuardService create");
	}
	
	private void initWidget() {
		FABAdd = (RelativeLayout) findViewById(R.id.fab_add);
		FABAdd.setOnClickListener(mOnClickListener);
		btnSyc = (ImageView) findViewById(R.id.topbar_syc);
		btnSyc.setOnClickListener(mOnClickListener);
		cover = (View) findViewById(R.id.cover);
	}
	
	private void initListener() {
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
					Intent intent_sneaker = new Intent(MainActivity.this, SneakerActivity.class);
					MainActivity.this.startActivity(intent_sneaker);
					break;
				case R.id.iv_head:
					Intent intent_login = new Intent(MainActivity.this, LoginActivity.class);
					MainActivity.this.startActivityForResult(intent_login, 1);
					break;
				case R.id.topbar_syc:
					if (AppContext.user_id == -1) {
						showDialog();
					} else {
						RecordSyc();
					}
					break;
				}
			}
		};
		
		
		
	}
	
	private void showDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.dialog_skip));
		builder.setTitle("您还没有登录，无法使用云同步功能！");
		
		builder.setPositiveButton(getString(R.string.dialog_skip_konw), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				}
			});
		builder.setNegativeButton(getString(R.string.login), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent_login = new Intent(MainActivity.this, LoginActivity.class);
				MainActivity.this.startActivityForResult(intent_login, 1);
				}
			});
		builder.create().show();
	}
	
	private void initDrawer() {
		// TODO Auto-generated method stub
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		mDrawerTitle = (TextView) findViewById(R.id.iv_title);
		mDrawerAvatar = (ImageView) findViewById(R.id.iv_head);
		mDrawerAvatar.setOnClickListener(mOnClickListener);
		
		if (AppContext.user_id != -1) {
			mDrawerTitle.setText("hi, "+ AppContext.user.getName() + "~  ");
			if (AppContext.user.getGender() == 1) {
				mDrawerAvatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar_male));
			} else {
				mDrawerAvatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar_female));
			}
		}

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
	private void initViewPager() {  
        viewPager=(ViewPager) findViewById(R.id.vPager);  
        views=new ArrayList<View>();  
        LayoutInflater inflater=getLayoutInflater();  
        view1=inflater.inflate(R.layout.mainpage_daily, null);
        view2=inflater.inflate(R.layout.mainpage_sneaker, null);  
        view3=inflater.inflate(R.layout.mainpage_community, null);  
        views.add(view1);  
        views.add(view2);  
        views.add(view3);  
        DailyRecordHelper helper1 = new DailyRecordHelper(view1, this);
        SneakerReocrdHelper helper2 = new SneakerReocrdHelper(view2, this);
        CommunityHelper helper3 = new CommunityHelper(view3, this);
        helper1.init();
        helper2.init();
        helper3.init();
        viewPager.setAdapter(new MyViewPagerAdapter(views));  
        viewPager.setCurrentItem(0);  
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());  
    }  

    private void initTab() {  
        textView1 = (TextView) findViewById(R.id.tab1);  
        textView2 = (TextView) findViewById(R.id.tab2);  
        textView3 = (TextView) findViewById(R.id.tab3);  
  
        textView1.setOnClickListener(new MyOnClickListener(0));  
        textView2.setOnClickListener(new MyOnClickListener(1));  
        textView3.setOnClickListener(new MyOnClickListener(2));  
        
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
  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cover.setVisibility(View.GONE);
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
            
            if (currIndex == 2) {
            	FABAdd.setVisibility(View.GONE);
            } else {
            	FABAdd.setVisibility(View.VISIBLE);
            }
        }  
          
    }  
    
    @Override
	public void handler(Message msg) {
		switch (msg.what) {
		case NetStatus.RECORD_DAILY_GET_SUC:
			List<Step> tmpList = new ArrayList<Step>();
			List<Step> tmpList2 = new ArrayList<Step>();
			for (int i = 0; i < remoteList.size(); i++) {
				for (int j = 0; j < mStepList.size(); j++) {
					if (mStepList.get(j).getStep_date().equals(remoteList.get(i).getStep_date())) {
						tmpList.add(mStepList.get(j));
						tmpList2.add(remoteList.get(i));
					}
				}
			}
			for (int i = 0; i < tmpList.size(); i++) {
				mStepList.remove(tmpList.get(i));
				remoteList.remove(tmpList2.get(i));
			}
			
			sycSize = mStepList.size();
			if (sycSize > 0) {
				for (int i = 0; i < mStepList.size(); i++) {
					SneakerApi.dailyReocrd_set(mStepList.get(i));
				}
			} else if (remoteList.size() > 0 ) {
				saveStepInfo();
			} else {
				mLoadingDialog.dismiss();
				Toast.makeText(MainActivity.this, "云端记录已是最新，无须同步", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case NetStatus.RECORD_DAILY_GET_SUC>>2:
			mLoadingDialog.dismiss();
			Toast.makeText(MainActivity.this, "网络错误，请检查手机网络", Toast.LENGTH_SHORT).show();
			break;
			
		case NetStatus.RECORD_DAILY_SET_SUC:
			Log.d(TAG, "set success：" + sycSize);
			sycSize --;
			if (sycSize == 0) {
				
				saveStepInfo();
			}
			break;
			
		case NetStatus.DATABASE_NODATA:
			sycSize = mStepList.size();
			for (int i = 0; i < mStepList.size(); i++) {
				SneakerApi.dailyReocrd_set(mStepList.get(i));
			}
			break;
			
		case NetStatus.RECORD_DAILY_SET_SUC>>2:
			sycSize --;
			mLoadingDialog.dismiss();
			Toast.makeText(MainActivity.this, "网络错误，请检查手机网络", Toast.LENGTH_SHORT).show();
			break;
		}
	}
   
    
}  
	
