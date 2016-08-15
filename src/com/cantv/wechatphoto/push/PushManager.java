package com.cantv.wechatphoto.push;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cantv.wechatphoto.utils.PreferencesUtils;

public class PushManager {

	private final String TAG = "cn.cibntv.ott.push.PushManager";

	private final String KEY_PUSH_ENABLE = "pushEnable";
	private final String KEY_CLIENT_ID = "clientId";

	private Context mContext;
	private String mClientId;
	// 是否接受推送
	private boolean pushEnable;
	private static PushManager mPushManager;
	private onClientIdUpdateListener mClientIdUpdateListener;
	private com.igexin.sdk.PushManager gxPushManager;

	private PushManager(Context context) {
		this.mContext = context.getApplicationContext();
		mClientId = PreferencesUtils.getString(mContext, KEY_CLIENT_ID, "");
		pushEnable = PreferencesUtils.getBoolean(mContext, KEY_PUSH_ENABLE, true);
	}

	public interface onClientIdUpdateListener {
		public void onUpdate(String clientId);
	}

	public static PushManager getInstance(Context context) {
		if (mPushManager == null) {
			synchronized (PushManager.class) {
				if (mPushManager == null) {
					mPushManager = new PushManager(context);
				}
			}
		}
		return mPushManager;
	}

	public void init() {
		gxPushManager = com.igexin.sdk.PushManager.getInstance();
		gxPushManager.initialize(mContext);
	}

	public boolean isPushEnable() {
		return pushEnable;
	}

	public boolean setPushEnable(boolean enable) {
		return PreferencesUtils.putBoolean(mContext, KEY_PUSH_ENABLE, enable);
	}

	public String getClientId() {
		return mClientId;
	}

	public boolean setClientId(String clientId) {
		Log.i(TAG, "setClientId... " + clientId);
		boolean success = false;
		if (!TextUtils.isEmpty(clientId)) {
			success = PreferencesUtils.putString(mContext, KEY_CLIENT_ID, clientId);
			if (success) {
				this.mClientId = clientId;
				if (mClientIdUpdateListener != null) {
					mClientIdUpdateListener.onUpdate(clientId);
				}
			}
		}
		return success;
	}

	public void setOnClientIdUpdateListener(onClientIdUpdateListener listener) {
		this.mClientIdUpdateListener = listener;
	}

	public void removeClientIdUpdateListener() {
		if (mClientIdUpdateListener != null) {
			this.mClientIdUpdateListener = null;
		}
	}
}
