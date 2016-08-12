package com.cantv.wechatphoto.utils.volley;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public abstract class VolleyCallback<T> {

	private Listener<T> mListener;
	private ErrorListener mErrorListener;

	public abstract void onSuccess(T response);
	public abstract void onFail(VolleyError error);

	public VolleyCallback() {
	}

	public Listener<T> onResponseListener() {
		mListener = new Listener<T>() {

			@Override
			public void onResponse(T arg0) {
				onSuccess(arg0);
			}
		};
		return mListener;
	}
	
	public ErrorListener onErrorLister() {
		mErrorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				onFail(arg0);
			}

		};
		return mErrorListener;
	}
}
