package com.cantv.wechatphoto.model;

import java.util.List;

import android.content.Context;

import com.cantv.wechatphoto.interfaces.IPhotoListener;
import com.cantv.wechatphoto.interfaces.IPushDataModel;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;

/**
 * Created by liuhao on 2016/6/13.
 */
public class PushDataModelImpl implements IPushDataModel {
	@Override
	public void getDBData(Context context, IPhotoListener listener) {
		/* 数据层操作 */
		DaoOpenHelper daoHelper = DaoOpenHelper.getInstance(context);
		List<PhotoBean> photoBean = daoHelper.PhotoQueryAllUser();
		listener.onSuccess(photoBean);
	}
}