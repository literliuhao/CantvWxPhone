package com.cantv.wechatphoto.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.bridge.BaseEffectBridge;
import com.cantv.wechatphoto.bridge.OpenEffectBridge;

public class PopView extends FrameLayout {

	private final float DEFUALT_SCALE = 1.0f;

	private BaseEffectBridge mEffectBridge;

	public PopView(Context context) {
		super(context, null, 0);
		if (context != null && context instanceof Activity) {
			attach2Window((Activity) context);
		}
		init(context, null);
	}

	private void attach2Window(Activity activity) {
		ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		rootView.addView(this, layoutParams);
		rootView.setClipChildren(false);
		rootView.setClipToPadding(false);
	}

	public PopView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		init(context, attrs);
	}

	public PopView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		setWillNotDraw(false);
		initEffectBridge();
		if (attrs != null) {
			TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.PopView);// 获取配置属性
			Drawable drawableUpRect = tArray.getDrawable(R.styleable.PopView_upImageRes); // 顶层图片.
			Drawable drawableShadow = tArray.getDrawable(R.styleable.PopView_shadowImageRes); // 阴影图片.
			setUpRectDrawable(drawableUpRect);
			setShadowDrawable(drawableShadow);
			tArray.recycle();
		}
	}

	private void initEffectBridge() {
		BaseEffectBridge baseEffectBridge = new OpenEffectBridge();
		baseEffectBridge.onInitBridge(this);
		baseEffectBridge.setMainUpView(this);
		setEffectBridge(baseEffectBridge);
	}

	public void setUpRectResource(int resId) {
		if (mEffectBridge != null)
			mEffectBridge.setUpRectResource(resId);
	}

	public void setUpRectDrawable(Drawable upRectDrawable) {
		if (mEffectBridge != null)
			mEffectBridge.setUpRectDrawable(upRectDrawable);
	}

	public Drawable getUpRectDrawable() {
		if (mEffectBridge != null) {
			return mEffectBridge.getUpRectDrawable();
		}
		return null;
	}

	public void setDrawUpRectPadding(int size) {
		setDrawUpRectPadding(new Rect(size, size, size, size));
	}

	public void setDrawUpRectPadding(Rect rect) {
		if (mEffectBridge != null) {
			mEffectBridge.setDrawUpRectPadding(rect);
			invalidate();
		}
	}

	/**
	 * 获取最上层图片 间距矩形(Rect).
	 */
	public Rect getDrawUpRect() {
		if (mEffectBridge != null) {
			return mEffectBridge.getDrawUpRect();
		}
		return null;
	}

	public void setShadowResource(int resId) {
		if (mEffectBridge != null) {
			this.mEffectBridge.setShadowResource(resId);
			invalidate();
		}
	}

	/**
	 * 当图片边框不自带阴影的话，可以自行设置阴影图片. 设置阴影.
	 */
	public void setShadowDrawable(Drawable shadowDrawable) {
		if (mEffectBridge != null) {
			this.mEffectBridge.setShadowDrawable(shadowDrawable);
			invalidate();
		}
	}

	/**
	 * 获取阴影图片.
	 */
	public Drawable getShadowDrawable() {
		if (mEffectBridge != null) {
			return this.mEffectBridge.getShadowDrawable();
		}
		return null;
	}

	public void setDrawShadowPadding(int size) {
		setDrawShadowRectPadding(new Rect(size, size, size, size));
	}

	public void setDrawShadowRectPadding(Rect rect) {
		if (mEffectBridge != null) {
			mEffectBridge.setDrawShadowRectPadding(rect);
			invalidate();
		}
	}

	public Rect getDrawShadowRect() {
		if (mEffectBridge != null) {
			return mEffectBridge.getDrawShadowRect();
		}
		return null;
	}

	public void setFocusView(View newView, View oldView, float scale) {
		setFocusView(newView, scale);
		setUnFocusView(oldView);
	}

	public void setFocusView(View view, float scale) {
		setFocusView(view, scale, scale);
	}

	public void setFocusView(View view, float scaleX, float scaleY) {
		if (this.mEffectBridge != null)
			this.mEffectBridge.onFocusView(view, scaleX, scaleY);
	}

	public void setUnFocusView(View view) {
		setUnFocusView(view, DEFUALT_SCALE, DEFUALT_SCALE);
	}

	public void setUnFocusView(View view, float scaleX, float scaleY) {
		if (this.mEffectBridge != null)
			this.mEffectBridge.onOldFocusView(view, scaleX, scaleY);
	}

	public void setEffectBridge(BaseEffectBridge effectBridge) {
		this.mEffectBridge = effectBridge;
		if (this.mEffectBridge != null) {
			this.mEffectBridge.onInitBridge(this);
			this.mEffectBridge.setMainUpView(this);
			invalidate();
		}
	}

	public BaseEffectBridge getEffectBridge() {
		return this.mEffectBridge;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (this.mEffectBridge != null) {
			if (this.mEffectBridge.onDrawMainUpView(canvas)) {
				return;
			}
		}
		super.onDraw(canvas);
	}

}
