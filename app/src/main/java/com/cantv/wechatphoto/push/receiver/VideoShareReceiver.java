package com.cantv.wechatphoto.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushMsgHandler;

/**
 * 微信投屏
 * <p>
 * canshare.apk调用时action："cn.cibntv.ott.ACTION_SHARE_VIDEO"
 * 
 * <p>
 * params: "pushData" 推送数据
 * 
 * @author zhangbingyuan
 *
 */
public class VideoShareReceiver extends BroadcastReceiver {

	private final String TAG = "VideoPushReceiver";

	private final String KEY_CLIENT_ID = "clientId";
	private final String KEY_PUSH_DATA = "pushData";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean pushEnable = PushManager.getInstance(context).isPushEnable();
		if (!pushEnable) {
			Log.i(TAG, "Failed to receive pushMsg [pushEnable == false].");
			return;
		}

		String pushData = intent.getStringExtra(KEY_PUSH_DATA);
		PushMsgHandler.solveMessage(context, pushData);

		String clientId = intent.getStringExtra(KEY_CLIENT_ID);
		PushManager.getInstance(context).setClientId(clientId);
	}
}
