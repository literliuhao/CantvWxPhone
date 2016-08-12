package com.cantv.wechatphoto.push.domain;


import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import android.util.Log;
/**
 * 
 * @author Administrator
 *
 */
public class PushSuccessProgramMsg implements IPushMessage {
	
	private String info;
	private String openid;
	private String userName;
	private String userPicUrl;
	private static final String TAG = "PushMsgHandler";

	@Override
	public PushSuccessProgramMsg parse(JsonObject data)
			throws JsonSyntaxException, NullPointerException, IllegalStateException {
		if (data == null) {
			return this;
		}
		Log.i(TAG, "received push message. " + "登录成功开始解析");
		info = data.get("info").getAsString();
		openid = data.get("openid").getAsString();
		userName = data.get("userName").getAsString();
		userPicUrl = data.get("userPicUrl").getAsString();
		return this;
	}

	@Override
	public boolean isLegal() {
		return info != null ;
	}


	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPicUrl() {
		return userPicUrl;
	}

	public void setUserPicUrl(String userPicUrl) {
		this.userPicUrl = userPicUrl;
	}

	@Override
	public String toString() {
		return "PushSuccessProgramMsg [info=" + info + ", openid=" + openid + ", userName=" + userName
				+ ", userPicUrl=" + userPicUrl + "]";
	}
	
}
