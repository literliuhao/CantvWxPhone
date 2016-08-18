package com.cantv.wechatphoto.interfaces;

import android.content.Context;

/**
 * Created by liuhao on 2016/6/13.
 */
public interface IAddDataModel {
	void getMoreData(Context context, IReceiverListener listener, long number);
}