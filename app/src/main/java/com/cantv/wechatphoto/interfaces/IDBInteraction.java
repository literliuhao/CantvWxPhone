package com.cantv.wechatphoto.interfaces;

import java.util.List;

import com.cantv.wechatphoto.utils.greendao.PhotoBean;


public interface IDBInteraction {
	void updateData(List<PhotoBean> photoList);
}