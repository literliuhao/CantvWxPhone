package com.cantv.wechatphoto.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushMsgHandler;
import com.igexin.sdk.PushConsts;

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
public class PushReceiver extends BroadcastReceiver {

	private final String TAG = "PushReceiver";

	private final String META_DATA_PUSH_APPID = "PUSH_APPID";
	private final String PUSH_ACTION_PREFIX = "com.igexin.sdk.action.";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean pushEnable = PushManager.getInstance(context).isPushEnable();
		if (!pushEnable) {
			Log.i(TAG, "Failed to receive pushMsg [pushEnable == false].");
			return;
		}

		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			PushManager.getInstance(context).init();
			Log.d(TAG, "GeTui-pushService has initialized after boot complete.");

		} else if (getPushAppId(context).equals(action)) {
			Bundle bundle = intent.getExtras();
			int pushAction = bundle.getInt(PushConsts.CMD_ACTION);
			switch (pushAction) {
			// 1002
			case PushConsts.GET_CLIENTID:
				String clientId = bundle.getString("clientid");
				PushManager.getInstance(context).setClientId(clientId);
				break;
			// 1001
			case PushConsts.GET_MSG_DATA:
				// 获取透传数据
				byte[] payload = bundle.getByteArray("payload");
				if (payload != null) {
					String msg = new String(payload);
					Log.d(TAG, "Received pushMsg = " + msg);
					PushMsgHandler.solveMessage(context, msg);
				}
				// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
				// String taskid = bundle.getString("taskid");
				// String messageid = bundle.getString("messageid");
				// boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid,
				// 90001);
				// System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
				// String appid = bundle.getString("appid");
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 读取个推appId
	 * 
	 * @param context
	 * @return
	 */
	private String getPushAppId(Context context) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (appInfo != null) {
			String appId = appInfo.metaData.getString(META_DATA_PUSH_APPID);
			return new StringBuffer(PUSH_ACTION_PREFIX).append(appId).toString();
		}
		return "";
	}

}
