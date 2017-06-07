package com.cantv.wechatphoto.utils.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by zhangbingyuan on 2017/4/7.
 * <p>
 * fix APPSTORE-266 APPSTORE-321 修复glide在某些设备上加载图片错乱的问题  by_zby_20170407
 */

public class NoBitmapPoolModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int defaultPoolCacheSize = (new MemorySizeCalculator(context)).getBitmapPoolSize();
        int defaultMemoryCacheSize = (new MemorySizeCalculator(context)).getMemoryCacheSize();
        builder.setBitmapPool(new LruBitmapPool((int) (0.5F * (float) defaultPoolCacheSize)));
        builder.setMemoryCache(new LruResourceCache((int) (0.5F * (float) defaultMemoryCacheSize)));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
