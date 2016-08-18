package com.cantv.wechatphoto.utils.imageloader;

import android.content.Context;

/**
 * 
 */
public interface BaseImageLoader {
   void loadImage(Context ctx, ImageInfo img);
   void clearImageAllCache();
   void clearMemCache();
}
