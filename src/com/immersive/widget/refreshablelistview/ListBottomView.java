package com.immersive.widget.refreshablelistview;


import com.immersive.utils.PxUtils;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.ViewGroup;

public class ListBottomView extends ViewGroup {
	private int mHeight = -1;
	private int mInitHeight;
	private RefreshableListView listView;
	
	private static final Interpolator sInterpolator = new Interpolator() {

		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}

	};
	
	public ListBottomView(Context context, RefreshableListView listView) {
		super(context);
		this.listView = listView;
	}
	
	public void close() {
		final CloseTimer timer = new CloseTimer(mInitHeight);
		timer.startTimer();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final View childView = getChildView();
		if (childView == null) {
			return;
		}

		final int childViewWidth = childView.getMeasuredWidth();
		final int childViewHeight = childView.getMeasuredHeight();
		childView.layout(0, 0, childViewWidth, childViewHeight);
	}
	
	protected View getChildView() {
		final int childCount = getChildCount();
		if (childCount != 1) {
			return null;
		}
		return getChildAt(0);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
//		if (mHeight < 0) {
//			mHeight = 0;
//		}
		if(mHeight == -1) {
			final View childView = getChildView();
			if (childView != null) {
				int padding = PxUtils.px2dip(this.getContext(), 20);
				childView.setPadding(0, padding, 0, padding);
				
				childView.measure(widthMeasureSpec, heightMeasureSpec);
				mInitHeight = childView.getMeasuredHeight();
				mHeight = mInitHeight;
			}
		}
		
		setMeasuredDimension(width, mHeight);
	}
	
	private class CloseTimer extends CountDownTimer {

		private long mStart;
		private float mDurationReciprocal;

		private static final int COUNT_DOWN_INTERVAL = 1;

		public CloseTimer(long millisInFuture) {
			super(millisInFuture, COUNT_DOWN_INTERVAL);
			mDurationReciprocal = 1.0f / millisInFuture;
		}

		public void startTimer() {
			mStart = AnimationUtils.currentAnimationTimeMillis();
			start();
		}

		@Override
		public void onFinish() {
			setHeight(0);
			listView.removeFooterView(ListBottomView.this);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			final int interval = (int) (AnimationUtils
					.currentAnimationTimeMillis() - mStart);
			float x = interval * mDurationReciprocal;
			x = sInterpolator.getInterpolation(x);
			setHeight((int) (mInitHeight - mInitHeight * x));
		}

	}
	
	private void setHeight(int height) {
		if (mHeight == height && height == 0) {
			// ignore duplicate 0 height setting.
			return;
		}
		
		mHeight = height;
		requestLayout();
		
		//listView.setSelection(listView.getAdapter().getCount() - 1);
	}

}
