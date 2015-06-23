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

public class DrawerAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private int[] ItemDrawable;
	private int[] ItemText;
	
	public DrawerAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		ItemDrawable = new int[] {R.drawable.ico_new, R.drawable.ico_favorite, R.drawable.ico_hot, 
				R.drawable.ico_random, R.drawable.ico_feedback, R.drawable.ico_upload, 
				R.drawable.ico_setting};
		ItemText = new int[] {R.string.drawer_new, R.string.drawer_favorite, R.string.drawer_hot, 
				R.string.drawer_random, R.string.drawer_feedback, R.string.drawer_upload,
				R.string.drawer_setting};
				
		}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ItemText.length;
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
			iv_drawer = (ImageView) view.findViewById(R.id.iv_drawitem);
			tv_drawer= (TextView) view.findViewById(R.id.tv_drawitem);
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
			convertView = mInflater.inflate(R.layout.item_drawer, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		}
		vh.iv_drawer.setImageDrawable(mContext.getResources().getDrawable(ItemDrawable[position]));;
		vh.tv_drawer.setText(mContext.getResources().getString(ItemText[position]));
		
		return convertView;
	}

}
