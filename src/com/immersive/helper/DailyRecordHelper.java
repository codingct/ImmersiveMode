package com.immersive.helper;

import java.util.ArrayList;
import java.util.List;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.Step;
import com.immersive.activity.ChartActivity;
import com.immersive.activity.MainActivity;
import com.immersive.adapter.CardAdapter;
import com.immersive.utils.GreenDaoUtils;
import com.immersive.widget.PtrListView;


import com.immersive.widget.PtrListView.Pagingable;
import com.immersive.widget.refreshablelistview.RefreshableListView.OnNeedUpdateListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class DailyRecordHelper implements OnItemClickListener{
	public static final int LOAD_PER_NUM = 10;
	public static final String TAG = "DailyRecordHelper";
	private View mLayout = null;
	private Activity mActivity = null;
	private PtrListView cardList;
	private CardAdapter cardAdapter;
	
	private List<Step> mDailyStep = null;
	private GreenDaoUtils mDBUtils = null;
	
	public DailyRecordHelper(View layout, Activity activity) {
		this.mLayout = layout;
		this.mActivity = activity;
	}
	
	public void init() {
		mDBUtils = GreenDaoUtils.getInstance(mActivity);
		
		initPtrList();
		loadDailyRecord();

	}

	private void initPtrList() {
		
		cardList = (PtrListView)mLayout.findViewById(R.id.list1);
		if (mDailyStep != null) {
			cardAdapter = new CardAdapter(mLayout.getContext(), mDailyStep);
			cardList.setAdapter(cardAdapter);
		}
		cardList.setPullToRefresh(true);
		cardList.setOnNeedUpdateListener(new OnNeedUpdateListener() {
			@Override
			public void needUpdate() {
				loadDailyRecord();
				if (cardAdapter != null) {
					cardAdapter.notifyDataSetChanged();
				}
				Log.e(TAG, "PullToRefresh");
				cardList.setPullToRefresh(true);
				cardList.updateFinished();
			}
		});

		cardList.setHasMoreItems(true);
		cardList.setPagingableListener(new Pagingable() {
			@Override
			public void onLoadMoreItems() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						/*
						if (mBuf.bufToList(mList, LOAD_PER_NUM)) {
							cardAdapter.notifyDataSetChanged();
							cardList.setIsLoading(false);
							cardList.setHasMoreItems(true);
						} else {
							int asize = mBuf.getAlreadyTakeSize();
							
						}
						*/
						cardList.setHasMoreItems(false);
					}
				}, 500);
			}
		});
		cardList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//FavourBean favour = this.mList.get(arg2 - mListView.getHeaderViewsCount());
		((MainActivity)mActivity).cover.setVisibility(View.VISIBLE);
		Intent chartIntent = new Intent(mActivity, ChartActivity.class);
		chartIntent.putExtra("step_id", mDailyStep.get(arg2 - cardList.getHeaderViewsCount()).getId());
		chartIntent.putExtra("position", arg2 - cardList.getHeaderViewsCount());
		mActivity.startActivityForResult(chartIntent, 1);
	}
	
	private void loadDailyRecord() {
		mDailyStep = mDBUtils.getAllStep(AppContext.user_id);
		if (mDailyStep != null) {
			Log.e(TAG, "mDailyStep size = " + mDailyStep.size());
		
			cardAdapter = new CardAdapter(mLayout.getContext(), mDailyStep);
			cardList.setAdapter(cardAdapter);
			cardAdapter.notifyDataSetChanged();
		}
//		for (int i = 0; i < mDailyStep.size(); i++) {
//			Log.e(TAG, "mDailyStep date=>" + mDailyStep.get(i).getStep_date());
//		}
		
		
	}
}
