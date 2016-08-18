package com.cantv.wechatphoto.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cantv.wechatphoto.interfaces.IDBInteraction;
import com.cantv.wechatphoto.interfaces.IReceiverListener;
import com.cantv.wechatphoto.model.SyncModelImpl;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;

/**
 * Created by liuhao on 2016/6/13.
 */
public class DataReceiver extends BroadcastReceiver implements IReceiverListener {

	private IDBInteraction dbInteraction;
	private SyncModelImpl syncModel;
	public static final String TAG = "DataReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		long number = intent.getLongExtra("number", 0);
		syncModel = new SyncModelImpl();
		syncModel.getMoreData(context, this, number);
	}

	@Override
	public void onUpdate(List<PhotoBean> photoList) {
		dbInteraction.updateData(photoList);
	}

	public void setDBInteractionListener(IDBInteraction dbInteraction) {
		this.dbInteraction = dbInteraction;
	}

}
