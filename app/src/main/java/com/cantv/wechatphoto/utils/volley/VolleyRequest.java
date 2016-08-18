package com.cantv.wechatphoto.utils.volley;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.cantv.wechatphoto.App;

public class VolleyRequest {

	private final static int TIMEOUT_MS = 3000;
	private final static int MAX_RETRIES = 1;
	private final static float BACKOFF_MULT = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;

	public static void stopRequest(String tag){
		App.getVolleyRequestQueues().cancelAll(tag);
	}
	
	public static void stringRequestByGet(String url, String tag, VolleyCallback<String> callback) {
		StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, callback.onResponseListener(),
				callback.onErrorLister());
		mStringRequest.setTag(tag);
		mStringRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
		App.getVolleyRequestQueues().add(mStringRequest);
	}

	public static void stringRequestByPost(String url, String tag, final Map<String, String> params,
			final Map<String, String> header, VolleyCallback<String> callback) {
		App.getVolleyRequestQueues().cancelAll(tag);
		StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, callback.onResponseListener(),
				callback.onErrorLister()) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (params != null) {
					return params;
				}
				return super.getParams();
			}
			
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (header != null) {
//					header.put("Accept", "application/json");
//					header.put("Content-Type", "application/x-www-form-urlencoded");
					return header;
				}
				return super.getHeaders();
			}
		};
		mStringRequest.setTag(tag);
		mStringRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
		App.getVolleyRequestQueues().add(mStringRequest);
	}

	public static void jsonRequestByGet(String url, String tag, VolleyCallback callback) {
		App.getVolleyRequestQueues().cancelAll(tag);
		JsonObjectRequest mJsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
				callback.onResponseListener(), callback.onErrorLister());
		mJsonRequest.setTag(tag);
		mJsonRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
		App.getVolleyRequestQueues().add(mJsonRequest);
	}

//	public static void jsonRequestByGet(String url, String tag, VolleyCallback callback,
//			final Map<String, String> header) {
//		App.getVolleyRequestQueues().cancelAll(tag);
//		JsonObjectRequest mJsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//				callback.onResponseListener(), callback.onErrorLister()) {
//			@Override
//			public Map<String, String> getHeaders() {
//				return header;
//			}
//
//			@Override
//			protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//				HashMap<String, String> headers = (HashMap<String, String>) response.headers;
//				String tamp = headers.get("X-Cibn-Received-Millis");
//				if (tamp != null)
//					EventBus.getDefault().post(new PortalFreshEvent(tamp));
//				return super.parseNetworkResponse(response);
//			}
//		};
//		mJsonRequest.setTag(tag);
//		mJsonRequest.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
//		App.getVolleyRequestQueues().add(mJsonRequest);
//	}
	
//	/**
//	 * 
//	 * @param fullurl
//	 * @param responseProc
//	 * @param errorListener
//	 * @param isCache
//	 * @param headers
//	 * @param tag
//	 */
//	public static void epgJSONRequest(String fullurl, Response.Listener<JSONObject> responseProc,
//			final Response.ErrorListener errorListener, boolean isCache, final HashMap<String, String> headers,
//			String tag) {
//		Log.i(TAG, "Got a epgJSONRequest: " + fullurl);
//		JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET, fullurl, null, responseProc,
//				new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						errorListener.onErrorResponse(error);
//					}
//				}) {
//			@Override
//			public Map<String, String> getHeaders() {
//				HashMap<String, String> h = new HashMap<String, String>();
//				if (headers != null)
//					h.putAll(headers);
//				return h;
//			}
//
//			@Override
//			protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//				HashMap<String, String> headers = (HashMap<String, String>) response.headers;
//				String tamp = headers.get("X-Cibn-Received-Millis");
//				if (tamp != null)
//					EventBus.getDefault().post(new PortalFreshEvent(tamp));
//				return super.parseNetworkResponse(response);
//			}
//		};
//		jr.setTag(tag);
//		jr.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, MAX_RETRIES, BACKOFF_MULT));
//		jr.setShouldCache(isCache);
//
//		if (!isCache) {
//			App.getVolleyRequestQueues().getCache().clear();
//		}
//		App.getVolleyRequestQueues().add(jr);
//		Log.i(TAG, "Start...");
//	}
	
	public static String appendParameter(String url, Map<String, String> params) {
		Uri uri = Uri.parse(url);
		Uri.Builder builder = uri.buildUpon();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			builder.appendQueryParameter(entry.getKey(), entry.getValue());
		}
		return builder.build().getQuery();
	}

	public static String getErrorCode(VolleyError error) {
		String ret = "000";// 网络异常
		if (error instanceof TimeoutError) {
			ret = "800";// 超时
		} else if (error instanceof ServerError || error instanceof AuthFailureError) {
			if (error.networkResponse != null) {
				ret = String.valueOf(error.networkResponse.statusCode);// HTTP
																		// STATUS
																		// CODE
			}
		} else if (error instanceof NetworkError || error instanceof NoConnectionError) {
			ret = "802";// 网络连接错误
		}
		return ret;
	}
	
	 /**
     * 解析Gizp压缩后的字节流为字符串
     * 
     * @param data
     * @return
     */
    public static String convertGzipByte2Str(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        byte[] h = new byte[2];
        h[0] = (data)[0];
        h[1] = (data)[1];
        int head = getShort(h);
        boolean t = head == 0x1f8b;
        InputStream in;
        StringBuilder sb = new StringBuilder();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            if (t) {
                in = new GZIPInputStream(bis);
            } else {
                in = bis;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in),
                    1000);
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static int getShort(byte[] data) {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }
}
