package com.cantv.wechatphoto.push.domain;

import com.google.gson.JsonObject;

public interface IPushMessage {

	public IPushMessage parse(JsonObject data);
	
	public boolean isLegal();
}
