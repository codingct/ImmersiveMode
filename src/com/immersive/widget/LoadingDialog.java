package com.immersive.widget;

import com.code.immersivemode.R;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class LoadingDialog extends Dialog {
	private static int default_width = 300; // 默认宽度
	private static int default_height = 90;// 默认高度

	public LoadingDialog(Context context) {
		this(context, default_width, default_height);
	}

	public LoadingDialog(Context context, int width, int height) {
		super(context, R.style.Loading_dialog);
		// set content
		setContentView(R.layout.sneaker_progressbar);
		// set window params
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		// set width,height by density and gravity
		float density = getDensity(context);
		params.width = (int) (width * density);
		params.height = (int) (height * density);
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}

	private float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}
}