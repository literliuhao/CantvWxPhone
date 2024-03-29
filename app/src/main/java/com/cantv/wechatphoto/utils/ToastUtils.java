package com.cantv.wechatphoto.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.cantv.wechatphoto.R;

/**
 * Toast工具类
 */
public class ToastUtils {
	private static Handler mHandler = new Handler(Looper.getMainLooper());
	private static Toast mToast = null;
	private static TextView mTextView;
	private static Object synObj = new Object();


	/**
	 * Toast发送消息，默认Toast.LENGTH_SHORT
	 * 
	 * @param act
	 * @param msg
	 */
	public static void showMessage(final Context act, final String msg) {
		showMessage(act, msg, Toast.LENGTH_SHORT);
	}

	/**
	 * Toast发送消息，默认Toast.LENGTH_LONG
	 * 
	 * @param act
	 * @param msg
	 */
	public static void showMessageLong(final Context act, final String msg) {
		showMessage(act, msg, Toast.LENGTH_LONG);
	}

	/**
	 * Toast发送消息，默认Toast.LENGTH_LONG
	 * 
	 * @param act
	 */
	public static void showMessageLong(final Context act, int resId) {
		showMessage(act, act.getResources().getString(resId), Toast.LENGTH_LONG);
	}

	/**
	 * Toast发送消息
	 * 
	 * @param act
	 * @param msg
	 * @param len
	 */
	public static void showMessage(final Context act, final String msg, final int len) {
		if (mToast != null) {
			mToast.cancel();
		}
		new Thread(new Runnable() {
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						synchronized (synObj) {
							if (mToast == null) {
								mToast = new Toast(act);
							}
							if (mTextView == null) {
								mTextView = new TextView(act);
								mTextView.setBackgroundResource(R.drawable.shape_toast);
								mTextView.setTextColor(Color.WHITE);
								mTextView.setPadding((int) act.getResources().getDimension(R.dimen.dimen_50), (int) act.getResources().getDimension(R.dimen.dimen_30), (int) act.getResources().getDimension(R.dimen.dimen_50), (int) act.getResources().getDimension(R.dimen.dimen_30));
								mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int) act.getResources().getDimension(R.dimen.dimen_30));
							}
							mTextView.setText(msg);
							mToast.setDuration(len);
							mToast.setView(mTextView);
							mToast.show();
						}
					}
				});
			}
		}).start();
	}

	/**
	 * 关闭当前Toast
	 */
	public static void cancelCurrentToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}
}
