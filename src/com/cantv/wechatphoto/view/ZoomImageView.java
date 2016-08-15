package com.cantv.wechatphoto.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * @author liuhao
 */
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {
	public static final float SCALE_MAX = 4.0f;

	/**
	 * 用于存放矩阵的9个值
	 */
	private final float[] matrixValues = new float[9];

	private final Matrix mScaleMatrix = new Matrix();

	private int imagePosition;
	private final int angle = 3;
	private final int rotateAngle = 90;
	private float mWidthP;
	private float mHeightP;

	private boolean rotate_enable = true; // 是否可以旋转,旋转的时候设置为false

	public ZoomImageView(Context context) {
		this(context, null);
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setFocusable(true);
	}

	public void setPosition(int position) {
		this.imagePosition = position;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("ZoomImage onKeyDown ", keyCode + " imagePosition " + imagePosition);
		// 电视屏幕宽高
		mWidthP = getWidth();
		mHeightP = getHeight();
		if (rotate_enable) { // 正在旋转的时候就不接收旋转命令,执行完本次旋转才能旋转
			if (keyCode == 19) {
				if (imagePosition != 0) {
					ZoomImageView.this.postDelayed(new RotateRunnable(rotateAngle, angle), 20);
				}
			} else if (keyCode == 20) {
				if (imagePosition != 0) {
					ZoomImageView.this.postDelayed(new RotateRunnable(-rotateAngle, -angle), 20);
				}
			}
		}
		return false;
	}

	private class RotateRunnable implements Runnable {
		final float mDegree;
		private float mDegreeCount;
		private float mCurrentDegree;

		public RotateRunnable(float degree, float currentDegree) {
			this.mDegree = Math.abs(degree);
			this.mCurrentDegree = currentDegree;
			rotate_enable = false;
			ZoomImageView.this.setScaleType(ImageView.ScaleType.MATRIX);
		}

		@Override
		public void run() {
			// 进行旋转
			mScaleMatrix.postRotate(mCurrentDegree, getWidth() / 2, getHeight() / 2);
			mDegreeCount += Math.abs(mCurrentDegree);

			RectF rect = getMatrixRectF();
			float w = rect.width();
			float h = rect.height();
			// 缩放操作
			onRotaScale(h, w);

			checkBorderAndCenterWhenScale();
			setImageMatrix(mScaleMatrix);

			// 如果值在合法范围内，继续缩放
			if (mDegreeCount < mDegree) {
				ZoomImageView.this.postDelayed(this, 20);
			} else {
				rotate_enable = true;
			}
		}
	}

	/**
	 * 在缩放时，进行图片显示范围的控制
	 */
	private void checkBorderAndCenterWhenScale() {
		RectF rect = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;
		int width = getWidth();
		int height = getHeight();

		// 如果宽或高大于屏幕，则控制范围
		if (rect.width() >= width) {
			if (rect.left > 0) {
				deltaX = -rect.left;
			}
			if (rect.right < width) {
				deltaX = width - rect.right;
			}
		}
		if (rect.height() >= height) {
			if (rect.top > 0) {
				deltaY = -rect.top;
			}
			if (rect.bottom < height) {
				deltaY = height - rect.bottom;
			}
		}
		// 如果宽或高小于屏幕，则让其居中
		if (rect.width() < width) {
			deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
		}
		if (rect.height() < height) {
			deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
		}
		mScaleMatrix.postTranslate(deltaX, deltaY);

	}

	/**
	 * 根据当前图片的Matrix获得图片的范围
	 * 
	 * @return
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d) {
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}

	/**
	 * 获得当前的缩放比例
	 * 
	 * @return
	 */
	public final float getScale() {
		mScaleMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		// if (once) {
//		Drawable d = getDrawable();
//		if (d == null)
//			return;
//		// Log.e(TAG, d.getIntrinsicWidth() + " , " + d.getIntrinsicHeight());
//		int width = getWidth();
//		int height = getHeight();
//		// 拿到图片的宽和高
//		int dw = d.getIntrinsicWidth();
//		int dh = d.getIntrinsicHeight();

//		// setScaleType(ScaleType.CENTER);
//		float scale = 1.0f;
//		// 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
//		if (dw > width && dh <= height) {
//			scale = width * 1.0f / dw;
//		}
//		if (dh > height && dw <= width) {
//			scale = height * 1.0f / dh;
//		}
//		// 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
//		if (dw > width && dh > height) {
//			scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
//		}
	}

	/**
	 * 在旋转的时候对图片进行缩放操作
	 * 
	 * @param h
	 * @param w
	 */
	private void onRotaScale(float h, float w) {
		float scalePer = 1;
		// 比率越小就是越适合缩放的方向
		if (mHeightP / h < mWidthP / w) { // 按高的方向缩放

			if (mHeightP > h) {
				scalePer = mHeightP / h;
			} else {
				scalePer = scalePer - (1 - mHeightP / h);
			}

		} else {

			if (mWidthP > w) {
				scalePer = mWidthP / w;
			} else {
				scalePer = scalePer - (1 - mWidthP / w);
			}

		}
		mScaleMatrix.postScale(scalePer, scalePer, mWidthP / 2, mHeightP / 2);
	}

	/**
	 * @param imgPath
	 * @param degree
	 * @return
	 */
	public Bitmap rotatePicture(final String imgPath, final int degree) {
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBitmap;
	}
}
