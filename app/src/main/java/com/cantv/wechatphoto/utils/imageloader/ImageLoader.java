package com.cantv.wechatphoto.utils.imageloader;

import android.content.Context;

public class ImageLoader {

	private static ImageLoader mInstance;
	private BaseImageLoader mLoader;

	private ImageLoader() {
		mLoader = new GlideImageLoader();
	}

	public static ImageLoader getInstance() {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader();
					return mInstance;
				}
			}
		}
		return mInstance;
	}

	/**
	 * 
	 * @param context
	 *            Activity/Fragment 当UI不可见需要取消任务时，需要传入Activity/Fragment的context
	 *            ApplicationContext 当不需要自动取消任务时，使用ApplicationContext
	 * @param img
	 */
	public void loadImage(Context context, ImageInfo img) {
		mLoader.loadImage(context, img);
	}

	public void clearImageAllCache() {
		mLoader.clearImageAllCache();
	}
	
	public void clearMemCache() {
		mLoader.clearMemCache();
	}

	public GlideImageLoader getLoader() {
		return (GlideImageLoader)mLoader;
	}
}