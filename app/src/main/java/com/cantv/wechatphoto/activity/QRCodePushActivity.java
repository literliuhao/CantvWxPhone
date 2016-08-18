package com.cantv.wechatphoto.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.cantv.wechatphoto.GetDataUtils;
import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushManager.onClientIdUpdateListener;
import com.cantv.wechatphoto.utils.FakeX509TrustManager;
import com.cantv.wechatphoto.utils.NetWorkUtils;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.imageloader.ImageInfo;
import com.cantv.wechatphoto.utils.imageloader.ImageLoader;
import com.cantv.wechatphoto.utils.volley.VolleyCallback;
import com.cantv.wechatphoto.utils.volley.VolleyRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QRCodePushActivity extends Activity {

	private final String TAG = "QRCodePushActivity";

	private static final String ACTION_CLOSE_QRCODE_PAGE = "com.cantv.wechatphoto.ACTION_CLOSE_QRCODE_PAGE";
	private static final String ACTION_REGISTER_SUCCEED = "com.cantv.wechatphoto.action.REGISTER_SUCCESS";

	private ImageView mQRCodeIv;
	private ImageView mSucceedHint;

	private PushManager mPushManager;
	private BroadcastReceiver mClosePageReceiver;
	private IntentFilter mClosePageIntentFilter;
	private BroadcastReceiver mRegisterSucceedReceiver;
	private IntentFilter mRegisterSucceedIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 进行判断，选择打开界面
		DaoOpenHelper pushDaoOpenHelper = DaoOpenHelper.getInstance(getApplicationContext());
		long count = pushDaoOpenHelper.queryExpiredUserCount();
		if (count >= 1) {
			QRCodePushActivity.this.finish();
			Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		this.getWindow().setBackgroundDrawableResource(R.drawable.new_bg_qr_push);
		setContentView(R.layout.activity_qr_push_play);
		mQRCodeIv = (ImageView) findViewById(R.id.iv_qrcode);
		mSucceedHint = (ImageView) findViewById(R.id.iv_succeed_hint);

		mPushManager = PushManager.getInstance(this);

		String clientId = mPushManager.getClientId();
		if (!TextUtils.isEmpty(clientId)) {
			loadQRCode(clientId);
			return;
		}

		Log.i(TAG, "waiting for geTui-clientId to getWexinPushQRCode.");
		mPushManager.setOnClientIdUpdateListener(new onClientIdUpdateListener() {

			@Override
			public void onUpdate(String clientId) {
				loadQRCode(clientId);
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mClosePageReceiver == null) {
			mClosePageReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					Log.i(TAG, "received close qrcodePage broadcast.");
					finish();
				}
			};
			mClosePageIntentFilter = new IntentFilter(ACTION_CLOSE_QRCODE_PAGE);
		}
		registerReceiver(mClosePageReceiver, mClosePageIntentFilter);

		if (mRegisterSucceedReceiver == null) {
			mRegisterSucceedReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					Log.i(TAG, "received close qrcodePage broadcast.");
					mSucceedHint.setVisibility(View.VISIBLE);
				}
			};
			mRegisterSucceedIntentFilter = new IntentFilter(ACTION_REGISTER_SUCCEED);
		}
		registerReceiver(mRegisterSucceedReceiver, mRegisterSucceedIntentFilter);
	}

	@Override
	protected void onDestroy() {
		if (mClosePageReceiver != null) {
			unregisterReceiver(mClosePageReceiver);
			mClosePageReceiver = null;
			mClosePageIntentFilter = null;
		}
		if (mRegisterSucceedReceiver != null) {
			unregisterReceiver(mRegisterSucceedReceiver);
			mRegisterSucceedReceiver = null;
			mRegisterSucceedIntentFilter = null;
		}
		mPushManager = null;
		mSucceedHint.setVisibility(View.INVISIBLE);
		super.onDestroy();
	}

	private void loadQRCode(String clientId) {
		getWexinPushQRCode(clientId, new OnRequestFinishCallback<String>() {

			@Override
			public void onSuccess(String t, String... extras) {
				mPushManager.removeClientIdUpdateListener();
				int cornerDp = (int) getResources().getDimension(R.dimen.dimen_30px);
				FakeX509TrustManager.allowAllSSL();
				ImageInfo img = new ImageInfo.Builder().url(t).isSkipMemoryCache(true)
						.placeHolder(R.drawable.placeholder_logo).imgView(mQRCodeIv).cornerSizeDp(cornerDp).build();
				ImageLoader.getInstance().loadImage(QRCodePushActivity.this, img);
			}

			@Override
			public void onFail(Throwable e) {
				Log.w(TAG, "Failed to getWexinPushQRCode.", e);
			}
		});
	}

	public interface OnRequestFinishCallback<T> {

		public void onSuccess(T t, String... extras);

		public void onFail(Throwable e);
	}

	/**
	 * 获取微信推送二维码
	 * 
	 * @param callback
	 */
	public void getWexinPushQRCode(String clientId, final OnRequestFinishCallback<String> callback) {
		if (TextUtils.isEmpty(clientId)) {
			Log.w(TAG, "Failed to getWexinPushQRCode[ clientId = NULL].");
			if (callback != null) {
				callback.onFail(null);
			}
			return;
		}
		String mac = NetWorkUtils.getEthernetMac();
		Log.i(TAG, "getWexinPushQRCode ......");
		GetDataUtils.getIntance().requestWexinPushQRCode(mac, clientId, "getWexinPushQRCode",
				new VolleyCallback<String>() {

					@Override
					public void onSuccess(String response) {
						if (TextUtils.isEmpty(response)) {
							response = "";
						}
						Log.i(TAG, "getWexinPushQRCode ...... finish [ response = " + response + "].");
						String scanId = null;

						try {
							JsonObject respJs = new JsonParser().parse(response).getAsJsonObject();
							int status = respJs.get("status").getAsInt();
							if (status == 200) {
								JsonObject dataJs = respJs.get("data").getAsJsonObject();
								String qrTicket = dataJs.get("qrTicket").getAsString();
								scanId = dataJs.get("scanid").getAsString();
								if (callback != null) {
									callback.onSuccess(
											"https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + qrTicket, scanId);
								}
								return;
							}
							Log.w(TAG, "failed to getWexinPushQRCode, status = " + status);
						} catch (Exception e) {
							e.printStackTrace();
							if (callback != null) {
								callback.onFail(e);
							}
						}
					}

					@Override
					public void onFail(VolleyError error) {
						Log.w(TAG, "failed to getWexinPushQRCode: " + error.getClass().getSimpleName());
						if (callback != null) {
							callback.onFail(error.getCause());
						}
					}
				});
	}

	public void cancelAllRequest() {
		VolleyRequest.stopRequest("getWexinPushQRCode");
	}

}
