package com.cantv.wechatphoto.utils.volley;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.cantv.wechatphoto.App;

public class StrErrListener implements ErrorListener {

	@Override
	public void onErrorResponse(VolleyError arg0) {
		VolleyErrorHelper.getMessage(arg0, App.getAppContext());
	}
}
