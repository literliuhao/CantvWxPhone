package com.cantv.wechatphoto.utils.volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonRequest<T> extends Request<T> {
	private final Listener<T> mListener;
	private static Gson mGson = new Gson();
	private Class<T> mClass;
	private Map<String, String> mParams;
	private TypeToken<T> mTypeToken;

	public GsonRequest(int method, Map<String, String> params, String url, Class<T> clazz, Listener<T> listener,
			StrErrListener errorListener) {
		super(method, url, errorListener);
		mClass = clazz;
		mListener = listener;
		mParams = params;
		setMyRetryPolicy();
	}

	public GsonRequest(int method, Map<String, String> params, String url, TypeToken<T> typeToken,
			Listener<T> listener, StrErrListener errorListener) {
		super(method, url, errorListener);
		mTypeToken = typeToken;
		mListener = listener;
		mParams = params;
		setMyRetryPolicy();
	}

	public GsonRequest(String url, Class<T> clazz, Listener<T> listener, StrErrListener errorListener) {
		this(Method.GET, null, url, clazz, listener, errorListener);
	}

	public GsonRequest(String url, TypeToken<T> typeToken, Listener<T> listener, StrErrListener errorListener) {
		this(Method.GET, null, url, typeToken, listener, errorListener);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			if (mTypeToken == null) {
				return Response.success(mGson.fromJson(jsonString, mClass),
						HttpHeaderParser.parseCacheHeaders(response));
			} else {
				return (Response<T>) Response.success(mGson.fromJson(jsonString, mTypeToken.getType()),
						HttpHeaderParser.parseCacheHeaders(response));
			}
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public void deliverError(VolleyError error) {
		super.deliverError(error);
	}

	@Override
	public ErrorListener getErrorListener() {
		return super.getErrorListener();
	}

	@Override
	protected String getParamsEncoding() {
		// TODO Auto-generated method stub
		return super.getParamsEncoding();
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		// TODO Auto-generated method stub
		return super.parseNetworkError(volleyError);
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	private void setMyRetryPolicy() {
		setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}
}
