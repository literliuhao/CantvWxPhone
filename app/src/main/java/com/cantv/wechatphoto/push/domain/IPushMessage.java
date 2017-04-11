package com.cantv.wechatphoto.push.domain;

import com.google.gson.JsonObject;

public interface IPushMessage {

    IPushMessage parse(JsonObject data);

    boolean isLegal();
}
