package com.cantv.wechatphoto.model;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.cantv.wechatphoto.GetDataUtils;
import com.cantv.wechatphoto.activity.QRCodePushActivity;
import com.cantv.wechatphoto.utils.NetWorkUtils;
import com.cantv.wechatphoto.utils.PreferencesUtils;
import com.cantv.wechatphoto.utils.volley.VolleyCallback;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by liuhao on 2017/5/4.
 */

public class QRManager {
    private String mQrCodeUrl;
    private final int SUCCESS = 200;
    private String URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
    public final String CODE_URL = "CODE_URL";

    private QRManager(Context context) {
        this.mContext = context;
    }

    private static QRManager instance;
    private Context mContext;

    public static QRManager getInstance(Context mContext) {
        if (null == instance) {
            instance = new QRManager(mContext);
        }
        return instance;
    }

    public void getWXQRCode(String clientId, final QRCodePushActivity.OnRequestFinishCallback<String> callback) {
        if (TextUtils.isEmpty(clientId)) {
            if (callback != null) {
                callback.onFail(null);
            }
            return;
        }

        String mQrCodeUrl = PreferencesUtils.getString(mContext, CODE_URL);
        if (null != mQrCodeUrl && !mQrCodeUrl.equals("")) {
            if (callback != null) {
                callback.onSuccess(mQrCodeUrl, new String[]{});
            }
        } else {
            GetDataUtils.getIntance().requestWexinPushQRCode(NetWorkUtils.getEthernetMac(), clientId, "getWXQRCode", new VolleyCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    if (TextUtils.isEmpty(response)) {
                        response = "";
                    }
                    String scanId;
                    try {
                        JsonObject respJs = new JsonParser().parse(response).getAsJsonObject();
                        int status = respJs.get("status").getAsInt();
                        if (status == SUCCESS) {
                            JsonObject dataJs = respJs.get("data").getAsJsonObject();
                            String qrTicket = dataJs.get("qrTicket").getAsString();
                            scanId = dataJs.get("scanid").getAsString();
                            String mergeURL = URL + qrTicket;
                            PreferencesUtils.putString(mContext, CODE_URL, mergeURL);
                            if (callback != null) {
                                callback.onSuccess(mergeURL, scanId);
                            }
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }
                }

                @Override
                public void onFail(VolleyError error) {
                    if (callback != null) {
                        callback.onFail(error.getCause());
                    }
                }
            });
        }


    }
}
