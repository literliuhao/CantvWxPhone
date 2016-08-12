package com.cantv.wechatphoto;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cantv.wechatphoto.activity.GridViewActivity;
import com.cantv.wechatphoto.activity.QRCodePushActivity;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushManager.onClientIdUpdateListener;
import com.cantv.wechatphoto.utils.NetWorkUtils;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;

public class App extends Application {

	public static final String EPG_LAUNCHER = "epg_launcher";
	public static final String DOWNTASK_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tasks";
	private static String mClientId = "";
	public static int gScreenWidth = 1920;
	public static int gScreenHeight = 1080;
	public static int screenWidth;
	public static int screenHeight;
	public static int polling = -1;
	public static String gDeviceId;
	public static String gCanTemplateId = "";
	public static String gCanChannelId = "17";

	public static boolean isHomeKey = false;
	public static boolean isLogin = false;
	public static App gInstance = null;
	public static boolean gLocalAppsUpdate = false;
	public static boolean isOpenUserCenter = false;
	public static RequestQueue gReqQueue = null;

	public static String gRefreshTimeStamp = "";
	public static float density = 1;
	public static float percent = 1;
	/* CIBN */
	public static String gCibnTemplateId = "00080000000000000000000000000050"; // 国广固定值
	public static String gCibnChannelId = "009408625092972493479";

	@Override
	public void onCreate() {
		super.onCreate();
		gInstance = this;
		gDeviceId = NetWorkUtils.getEthernetMac().replaceAll(":", "");
		// 初始化个推服务
		PushManager.getInstance(this).init();
		//进行判断，选择打开界面
		/*DaoOpenHelper pushDaoOpenHelper = DaoOpenHelper.getInstance(getApplicationContext());
		long count = pushDaoOpenHelper.queryExpiredUserCount();
		if (count >= 1) {
			Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}*/
	}

	public static Context getContext() {
		return gInstance;
	}

	public static RequestQueue getVolleyRequestQueues() {
		if (gReqQueue == null) {
			gReqQueue = Volley.newRequestQueue(getContext());
		}
		return gReqQueue;
	}

	/**
	 * 获取PushManager clientId
	 */
	public static String getPushManangerClientId() {
		mClientId = PushManager.getInstance(gInstance).getClientId();
		if (TextUtils.isEmpty(mClientId)) {
			PushManager.getInstance(gInstance).setOnClientIdUpdateListener(new onClientIdUpdateListener() {
				@Override
				public void onUpdate(String clientId) {
					mClientId = clientId;
				}
			});
		}
		return mClientId;
	}
	
	/**截取canchnnelId的 |之前的字符串 */
	public static String getChannelID(String canChannelID){
		String channelID="17";
		if (canChannelID!=null&&canChannelID.contains("|")) {
			channelID=canChannelID.substring(0, canChannelID.indexOf("|"));
		}
		return channelID;
	}
	
}
