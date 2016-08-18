package com.cantv.wechatphoto.view;

import android.view.View;
import android.view.ViewGroup;

public class WidgetTvViewBring {

	private int position = 0;

	public WidgetTvViewBring() {
	}

	public WidgetTvViewBring(ViewGroup vg) {
		vg.setClipChildren(false);
		vg.setClipToPadding(false);
	}

	public void bringChildToFront(ViewGroup vg, View child) {
		position = vg.indexOfChild(child);
		if (position != -1) {
			vg.postInvalidate();
		}
	}

	public int getChildDrawingOrder(int childCount, int i) {
		if (position != -1) {
			if (i == childCount - 1)
				return position;
			if (i == position)
				return childCount - 1;
		}
		return i;
	}

}
