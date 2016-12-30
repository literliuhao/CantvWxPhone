package com.cantv.wechatphoto.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.DisplayMetrics;

public class Utils {

	/**
	 * 获取SDK版本
	 */
	public static int getSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
		}
		return version;
	}

	public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return bm;
	}

	public static int[] getRealResolution(Activity activity){
		Point point = new Point();
		activity.getWindowManager().getDefaultDisplay().getRealSize(point);
		int[] realpixels = new int[2];
		realpixels[0] = point.x;
		realpixels[1] = point.y;
		return realpixels;
	}

	public static int[]  getResolution(Activity activity){
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int[] pixels = new int[2];
		pixels[0] = metrics.widthPixels;
		pixels[1] = metrics.heightPixels;
		return pixels;
	}



}
