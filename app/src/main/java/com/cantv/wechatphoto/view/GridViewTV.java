package com.cantv.wechatphoto.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class GridViewTV extends GridView {

	//是否在测量
	public boolean isOnMeasure = false;

	public GridViewTV(Context context) {
		super(context);
		init(context, null);
	}

	public GridViewTV(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			super.dispatchDraw(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	WidgetTvViewBring mWidgetTvViewBring;

	private void init(Context context, AttributeSet attrs) {
		this.setChildrenDrawingOrderEnabled(true);
		mWidgetTvViewBring = new WidgetTvViewBring(this);
	}

	@Override
	public void bringChildToFront(View child) {
		mWidgetTvViewBring.bringChildToFront(this, child);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		return mWidgetTvViewBring.getChildDrawingOrder(childCount, i);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		isOnMeasure = true;
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		isOnMeasure = false;
		super.onLayout(changed, l, t, r, b);
	}

}
