package com.immersive.widget;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	@Override
	public void setImageDrawable(Drawable drawable) {
		if (null == drawable) {
			super.setImageDrawable(drawable);
			return;
		}
		
		Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
		if (null == bm) {
			super.setImageDrawable(drawable);
			return;
		}
		
		Bitmap roundBitmap = getCroppedBitmap(bm);
		
		super.setImageDrawable(new BitmapDrawable(this.getContext().getResources(), roundBitmap));
	}

	public static Bitmap getCroppedBitmap(Bitmap bmp) {
		Bitmap output = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(bmp.getWidth() / 2 + 0.7f,
				bmp.getHeight() / 2 + 0.7f, bmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bmp, rect, rect, paint);

		return output;
	}

}