package com.immersive.adapter;

import com.code.immersivemode.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int[] ItemDrawable;
	
	
	public CardAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		ItemDrawable = new int[] {R.drawable.iv1, R.drawable.iv2, R.drawable.iv3, R.drawable.iv4, 
				R.drawable.iv5};
		}
		

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ItemDrawable.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	static class ViewHolder {
		public ImageView iv_drawer;
		public TextView tv_drawer;
		public ViewHolder(View view) {
			iv_drawer = (ImageView) view.findViewById(R.id.iv_card);
			
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh;
		if(convertView != null) {
			vh = (ViewHolder)convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.item_card, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		}
		vh.iv_drawer.setImageDrawable(mContext.getResources().getDrawable(ItemDrawable[position]));;
		
		return convertView;
	}

}
