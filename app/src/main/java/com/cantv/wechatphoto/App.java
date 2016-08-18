package com.cantv.wechatphoto;


import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushManager.onClientIdUpdateListener;

public class App extends Application {

	private static String mClientId = "";
	public static RequestQueue gReqQueue = null;
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		// 初始化个推服务
		PushManager.getInstance(mContext).init();
	}

	public static RequestQueue getVolleyRequestQueues() {
		if (gReqQueue == null) {
			gReqQueue = Volley.newRequestQueue(mContext);
		}
		return gReqQueue;
	}

	public static Context getAppContext(){
		return mContext;
	}
	/**
	 * 获取PushManager clientId
	 */
	public static String getPushManangerClientId() {
		mClientId = PushManager.getInstance(mContext).getClientId();
		if (TextUtils.isEmpty(mClientId)) {
			PushManager.getInstance(mContext).setOnClientIdUpdateListener(new onClientIdUpdateListener() {
				@Override
				public void onUpdate(String clientId) {
					mClientId = clientId;
				}
			});
		}
		return mClientId;
	}
	
}
