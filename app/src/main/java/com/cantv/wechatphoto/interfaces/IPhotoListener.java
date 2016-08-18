package com.cantv.wechatphoto.interfaces;

import java.util.List;

import com.cantv.wechatphoto.utils.greendao.PhotoBean;


/**
 * Created by liuhao on 2016/6/13.
 */
public interface IPhotoListener {
	void onSuccess(List<PhotoBean> photoList);

	void onError();
}
