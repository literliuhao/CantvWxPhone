package com.cantv.wechatphoto.model;

import java.util.List;

import android.content.Context;

import com.cantv.wechatphoto.interfaces.IAddDataModel;
import com.cantv.wechatphoto.interfaces.IReceiverListener;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;

/**
 * Created by liuhao on 2016/6/15.
 */
public class SyncModelImpl implements IAddDataModel {
	@Override
	public void getMoreData(Context context, IReceiverListener listener, long number) {
		// 数据层操作
		DaoOpenHelper daoHelper = DaoOpenHelper.getInstance(context);
		List<PhotoBean> photoBean = daoHelper.queryNewPhoto(number);
		listener.onUpdate(photoBean);
	}
}