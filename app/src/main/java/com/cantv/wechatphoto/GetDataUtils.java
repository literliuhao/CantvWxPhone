package com.cantv.wechatphoto;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;

import com.cantv.wechatphoto.utils.volley.VolleyCallback;
import com.cantv.wechatphoto.utils.volley.VolleyRequest;


public class GetDataUtils {
	private static final String TAG = "CANTV.EPG.GetDataUtils";
	private static GetDataUtils instance;

	public static GetDataUtils getIntance() {
		if (instance == null) {
			instance = new GetDataUtils();
		}
		return instance;
	}
	
	/**
	 * 请求微信推送二维码
	 * 
	 * @param mac
	 * @param clientId
	 *            个推客户端ID
	 * @param tag
	 * @param callback
	 */
	public void requestWexinPushQRCode(String mac, String clientId, String tag, VolleyCallback<String> callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("mac", mac);
		params.put("cid", clientId);
		String url = "http://bms.can.cibntv.net/api/UserCenter/Weixin/createPushQrcode";
		Log.d(TAG, "requestWexinPushQRCode：" + url);
		VolleyRequest.stringRequestByPost(url, tag, params, null, callback);
	}

}
