package com.cantv.wechatphoto.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class BottomRoundImageView extends ImageView {

	private RectF mRect;
	private float cornerRadius;
	private Path path;

	public BottomRoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setScaleType(ScaleType.FIT_XY);

		cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10,
				context.getResources().getDisplayMetrics());

		mRect = new RectF();
		mRect.top = -cornerRadius;

		path = new Path();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mRect.right = w;
		mRect.bottom = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		path.addRoundRect(mRect, cornerRadius, cornerRadius, Direction.CW);
		canvas.clipPath(path);
		super.onDraw(canvas);
	}

}
