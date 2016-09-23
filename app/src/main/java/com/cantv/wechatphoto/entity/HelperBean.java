package com.cantv.wechatphoto.entity;

import com.cantv.wechatphoto.utils.greendao.PhotoBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuhao on 2016/6/13.
 */

public class HelperBean {
	public static List<PhotoBean> photoList = new ArrayList<>();

	public static void updateBean(PhotoBean photoBean){
		for (int i = 0; i < photoList.size(); i++) {
			if(photoList.get(i).getId() == photoBean.getId()){
				photoList.get(i).setDirection(photoBean.getDirection());
			}
		}
	}
}
