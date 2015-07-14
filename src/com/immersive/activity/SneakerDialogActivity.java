package com.immersive.activity;

import com.code.immersivemode.R;

import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class SneakerDialogActivity extends BaseActivity{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.page_result);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参值
		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.8
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度
		getWindow().setAttributes(p); // 设置生效
		
	}
	
	@Override
	public void finish() {
		super.finish();
		this.setResult(RESULT_OK);
	}
}
