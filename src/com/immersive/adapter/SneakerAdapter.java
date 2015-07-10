package com.immersive.adapter;

import java.util.List;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.Record;
import com.immersive.utils.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SneakerAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Record> mRecordList = null;
	
	public SneakerAdapter(Context context, List<Record> recordList) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mRecordList	= recordList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mRecordList.size();
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
		public ImageView iv_map;
		public TextView tv_step;
		public TextView tv_time;
		public TextView tv_dis;
		public ViewHolder(View view) {
			iv_map = (ImageView) view.findViewById(R.id.iv_sneaker);
			tv_step = (TextView) view.findViewById(R.id.sneaker_step);
			tv_time = (TextView) view.findViewById(R.id.sneaker_time);
			tv_dis = (TextView) view.findViewById(R.id.sneaker_distance);
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
			convertView = mInflater.inflate(R.layout.item_sneaker, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		}
//		vh.iv_drawer.setImageDrawable(mContext.getResources().getDrawable(ItemDrawable[position]));;
		String image_url = "file://" + AppContext.PATH_MAPSHOT + "/MapShot_" + mRecordList.get(position).getRecord_time().getTime();
		ImageLoader.getInstance().displayImage(image_url, vh.iv_map);
		vh.tv_step.setText(mContext.getString(R.string.step) +  mRecordList.get(position).getStep());
		vh.tv_time.setText(mContext.getString(R.string.time) +  StringUtils.formatTime(mRecordList.get(position).getTime()));
		vh.tv_dis.setText(mContext.getString(R.string.distance) +  mRecordList.get(position).getDistance());
		
		return convertView;
	}


}
