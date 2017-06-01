package com.cantv.wechatphoto.utils.glide;

import android.os.Environment;

import com.cantv.wechatphoto.SampleApplicationLike;

import java.io.File;

import cn.can.tvlib.imageloader.module.DiskCacheModule;

/**
 * Created by zhangbingyuan on 2017/3/16.
 */

public class GlideDiskCacheModule extends DiskCacheModule {

    private final String mCacheDir = "glideCache";

    @Override
    public File getCacheDir() {
        File file;
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            file = new File(Environment.getExternalStorageDirectory(), mCacheDir);
        } else {
            file = new File(SampleApplicationLike.getAppContext().getFilesDir(), mCacheDir);
        }
        return file;
    }

    @Override
    public int getCacheSize() {
        return cacheSize50mInBytes;
    }
}
