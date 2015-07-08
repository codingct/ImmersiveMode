package com.immersive.helper;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.code.immersivemode.R;
import com.immersive.adapter.TypeAdapter;

public class Tab2Helper {
	private View mLayout = null;
	private Activity mActivity = null;
	private ListView typeList;
	private TypeAdapter typeAdapter;
	public final int LOAD_PER_NUM = 10;
	public Tab2Helper(View layout, Activity activity) {
		this.mLayout = layout;
		this.mActivity = activity;
	}
	public void init() {
		typeList = (ListView)mLayout.findViewById(R.id.TypeList);
		typeAdapter = new TypeAdapter(mLayout.getContext());
		typeList.setAdapter(typeAdapter);
	}

}
