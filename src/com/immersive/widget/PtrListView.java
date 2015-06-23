package com.immersive.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import com.code.immersivemode.R;
import com.immersive.widget.refreshablelistview.ListBottomView;
import com.immersive.widget.refreshablelistview.RefreshableListView;




public class PtrListView extends RefreshableListView {

	public interface Pagingable {
		void onLoadMoreItems();
	}

	private boolean isLoading;
	private boolean hasMoreItems;
	private Pagingable pagingableListener;
	private ListBottomView bottomView;

    private OnScrollListener onScrollListener;

	public PtrListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PtrListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		addPullDownRefreshFeature(getContext());
		
		isLoading = false;
		
		bottomView = new ListBottomView(getContext(), this);
		
		final View view = LayoutInflater.from(getContext()).inflate(R.layout.loading_view,
				mListHeaderView, false);
		bottomView.addView(view);
		
		addFooterView(bottomView);
		super.setOnScrollListener(new OnScrollListener() {
	        @Override
	        public void onScrollStateChanged(AbsListView view, int scrollState) {
	            //Dispatch to child OnScrollListener
	            if (onScrollListener != null) {
	                onScrollListener.onScrollStateChanged(view, scrollState);
	            }
	        }
	
	        @Override
	        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	
	            //Dispatch to child OnScrollListener
	            if (onScrollListener != null) {
	                onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	            }
	
	            if (totalItemCount > 0) {
	                int lastVisibleItem = firstVisibleItem + visibleItemCount;
	                if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount)) {
	                    if (pagingableListener != null) {
	                        isLoading = true;
	                        pagingableListener.onLoadMoreItems();
	                    }
	
	                }
	            }
	        }
	    });
	}
	
	private void addPullDownRefreshFeature(final Context context) {

		setContentView(R.layout.pull_to_refresh);
		//mListHeaderView.setBackgroundColor(0x6633B5E5);
		setOnHeaderViewChangedListener(new OnHeaderViewChangedListener() {

			@Override
			public void onViewChanged(View v, boolean canUpdate) {
				ImageView img = (ImageView) v.findViewById(R.id.refresh_icon);
				Animation anim;
				if (canUpdate) {
					anim = AnimationUtils.loadAnimation(context,
							R.anim.rotate_up);
				} else {
					anim = AnimationUtils.loadAnimation(context,
							R.anim.rotate_down);
				}
				img.startAnimation(anim);
			}

			@Override
			public void onViewUpdating(View v) {
				ImageView img = (ImageView) v.findViewById(R.id.refresh_icon);
				ProgressBar pb = (ProgressBar) v
						.findViewById(R.id.refresh_loading);
				pb.setVisibility(View.VISIBLE);
				img.clearAnimation();
				img.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onViewUpdateFinish(View v) {
				ImageView img = (ImageView) v.findViewById(R.id.refresh_icon);
				ProgressBar pb = (ProgressBar) v
						.findViewById(R.id.refresh_loading);

				pb.setVisibility(View.INVISIBLE);
				img.setVisibility(View.VISIBLE);
			}

		});
	}

	public boolean isLoading() {
		return this.isLoading;
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public void setPagingableListener(Pagingable pagingableListener) {
		this.pagingableListener = pagingableListener;
	}

	public void setHasMoreItems(boolean hasMoreItems) {
		this.hasMoreItems = hasMoreItems;
		if(!this.hasMoreItems) {
			//bottomView.close();
			removeFooterView(bottomView);
		}
	}

	public boolean hasMoreItems() {
		return this.hasMoreItems;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onFinishLoading(boolean hasMoreItems, List<? extends Object> newItems) {
		setHasMoreItems(hasMoreItems);
		setIsLoading(false);
		if(newItems != null && newItems.size() > 0) {
			ListAdapter adapter = ((HeaderViewListAdapter)getAdapter()).getWrappedAdapter();
			if(adapter instanceof PagingBaseAdapter ) {
				((PagingBaseAdapter)adapter).addMoreItems(newItems);
			}
		}
	}

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }
    
    public abstract class PagingBaseAdapter<T> extends BaseAdapter {

    	protected List<T> items;

    	public PagingBaseAdapter() {
    		this.items = new ArrayList<T>();
    	}

    	public PagingBaseAdapter(List<T> items) {
    		this.items = items;
    	}

    	public void addMoreItems(List<T> newItems) {
    		this.items.addAll(newItems);
    		notifyDataSetChanged();
    	}

    	public void removeAllItems() {
    		this.items.clear();
    		notifyDataSetChanged();
    	}


    }
}
