package com.immersive.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.Record;
import com.immersive.activity.MainActivity;
import com.immersive.activity.ResultActivity;
import com.immersive.adapter.SneakerAdapter;
import com.immersive.utils.GreenDaoUtils;
import com.immersive.widget.PtrListView;
import com.immersive.widget.PtrListView.Pagingable;
import com.immersive.widget.refreshablelistview.RefreshableListView.OnNeedUpdateListener;

public class SneakerReocrdHelper implements OnItemClickListener {
	protected static final String TAG = "SneakerRecordHelper";
	private View mLayout = null;
	private Activity mActivity = null;
	private PtrListView cardList;
	private SneakerAdapter sneakerAdapter;
	public final int LOAD_PER_NUM = 10;
	
	private GreenDaoUtils mDBUtils = null;
	private List<Record> mRecordList = null;
	
	public SneakerReocrdHelper(View layout, Activity activity) {
		this.mLayout = layout;
		this.mActivity = activity;
	}
	
	public void init() {
		mDBUtils = GreenDaoUtils.getInstance(mActivity);
		loadSneakerData();
		initPtrList();

	}
	
	private void initPtrList() {
		
		cardList = (PtrListView)mLayout.findViewById(R.id.list_sneaker);
		sneakerAdapter = new SneakerAdapter(mLayout.getContext(), mRecordList);
		cardList.setAdapter(sneakerAdapter);
		cardList.setPullToRefresh(true);
		cardList.setOnNeedUpdateListener(new OnNeedUpdateListener() {
			@Override
			public void needUpdate() {
//				loadDailyRecord();
				sneakerAdapter.notifyDataSetChanged();
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
	
	private void loadSneakerData() {
		mRecordList = mDBUtils.getAllRecord(AppContext.user_id);
		if (mRecordList == null) {
			mRecordList = new ArrayList<Record>();
			Log.e(TAG, "mRecordList not exist");
			return;
		}
		Log.e(TAG, "mRecordList size:" + mRecordList.size());
		
		
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		((MainActivity)mActivity).cover.setVisibility(View.VISIBLE);
		Record record = mRecordList.get(arg2 - cardList.getHeaderViewsCount());
		Intent mapIntent = new Intent(mActivity, ResultActivity.class);
		mapIntent.putExtra("record_id", record.getId());
		mapIntent.putExtra("position", arg2 - cardList.getHeaderViewsCount());
		mapIntent.putExtra("step", record.getStep());
		mapIntent.putExtra("distance", record.getDistance());
		mapIntent.putExtra("time", record.getTime());
		mActivity.startActivityForResult(mapIntent, 1);
	}

}
