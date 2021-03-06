package com.immersive.helper;

import com.code.immersivemode.R;
import com.immersive.adapter.CardAdapter;
import com.immersive.widget.PtrListView;


import com.immersive.widget.PtrListView.Pagingable;
import com.immersive.widget.refreshablelistview.RefreshableListView.OnNeedUpdateListener;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class Tab1Helper implements OnItemClickListener{
	private View mLayout;
	private PtrListView cardList;
	private CardAdapter cardAdapter;
	public final int LOAD_PER_NUM = 10;
	public Tab1Helper(View layout) {
		this.mLayout = layout;
	}

	public void init() {
		cardList = (PtrListView)mLayout.findViewById(R.id.list1);
		cardAdapter = new CardAdapter(mLayout.getContext());
		cardList.setAdapter(cardAdapter);
		cardList.setPullToRefresh(true);
		cardList.setOnNeedUpdateListener(new OnNeedUpdateListener() {
			@Override
			public void needUpdate() {
				
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
					}
				}, 500);
			}
		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//FavourBean favour = this.mList.get(arg2 - mListView.getHeaderViewsCount());
		
	}
	
}
