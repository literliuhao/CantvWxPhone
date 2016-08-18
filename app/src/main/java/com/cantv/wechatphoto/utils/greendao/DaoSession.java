package com.cantv.wechatphoto.utils.greendao;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig wechatPhotoInfoDaoConfig;

    private final WechatPhotoInfoDao wechatPhotoInfoDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        wechatPhotoInfoDaoConfig = daoConfigMap.get(WechatPhotoInfoDao.class).clone();
        wechatPhotoInfoDaoConfig.initIdentityScope(type);

        wechatPhotoInfoDao = new WechatPhotoInfoDao(wechatPhotoInfoDaoConfig, this);

        registerDao(PhotoBean.class, wechatPhotoInfoDao);
    }
    
    public void clear() {
        wechatPhotoInfoDaoConfig.getIdentityScope().clear();
    }

    public WechatPhotoInfoDao getWechatPhotoInfoDao() {
        return wechatPhotoInfoDao;
    }

}
