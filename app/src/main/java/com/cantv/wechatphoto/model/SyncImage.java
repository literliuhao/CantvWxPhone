package com.cantv.wechatphoto.model;

import android.content.Context;

import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;

/**
 * Created by liuhao on 16/9/13.
 */

public class SyncImage {
    private Context mContext;

    private SyncImage(Context context) {
        this.mContext = context;
    }

    private static SyncImage instance = null;


    public static SyncImage getInstance(Context ctx) {
        if (ctx == null) {
            return null;
        }

        if (instance == null) {
            synchronized (SyncImage.class) {
                if (instance == null) {
                    instance = new SyncImage(ctx.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void saveRotation(PhotoBean photoBean) {
        /* 数据层操作 */
        DaoOpenHelper daoHelper = DaoOpenHelper.getInstance(mContext);
        daoHelper.updatePhoto(photoBean);
    }
}
