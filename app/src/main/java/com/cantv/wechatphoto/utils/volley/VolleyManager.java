package com.cantv.wechatphoto.utils.volley;

import java.util.Map;

import okhttp3.OkHttpClient;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cantv.wechatphoto.App;

public class VolleyManager {

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static class VolleyManagerHolder {
		private static final VolleyManager INSTANCE = new VolleyManager(App.getAppContext());
	}

	/**
	 * @param context
	 */
	private VolleyManager(Context context) {
		mRequestQueue = Volley.newRequestQueue(context, new OkHttp3Stack(new OkHttpClient())); // new
																								// OkHttpClient.Builder().build()
		mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(context));
	}

	/**
	 * 
	 * @return VolleyManager instance
	 */
	public static VolleyManager newInstance() {
		return VolleyManagerHolder.INSTANCE;
	}

	/**
	 * 
	 */
	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	public <T> Request<T> add(Request<T> request) {
		return mRequestQueue.add(request);
	}

	/**
	 * @param tag
	 * @param method
	 * @param url
	 * @param listener
	 * @param errorListener
	 * @return
	 */
	public StringRequest strRequest(Object tag, int method, String url, Response.Listener<String> listener,
			Response.ErrorListener errorListener) {
		StringRequest request = new StringRequest(method, url, listener, errorListener);
		request.setTag(tag);
		add(request);
		return request;
	}

	public StringRequest strPostRequest(Object tag, String url, final Map<String, String> headers,
			final Map<String, String> params, Response.Listener<String> listener,
			Response.ErrorListener errorListener) {
		StringRequest request = new StringRequest(Method.POST, url, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				if (headers != null) {
					return headers;
				}
				return super.getHeaders();
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (params != null) {
					return params;
				}
				return super.getParams();
			}
		};
		request.setTag(tag);
		add(request);
		return request;
	}

	/**
	 * Get方法
	 * 
	 * @param tag
	 * @param url
	 * @param clazz
	 * @param listener
	 * @param errorListener
	 * @param <T>
	 * @return
	 */
	public <T> GsonRequest<T> gsonGetRequest(Object tag, String url, Class<T> clazz, Response.Listener<T> listener,
			StrErrListener errorListener) {
		GsonRequest<T> request = new GsonRequest<T>(url, clazz, listener, errorListener);
		request.setTag(tag);
		add(request);
		return request;
	}

	/**
	 * Post方式1：Map参数
	 * 
	 * @param tag
	 * @param params
	 * @param url
	 * @param clazz
	 * @param listener
	 * @param errorListener
	 * @param <T>
	 * @return
	 */
	public <T> GsonRequest<T> gsonPostRequest(Object tag, Map<String, String> params, String url, Class<T> clazz,
			Response.Listener<T> listener, StrErrListener errorListener) {
		GsonRequest<T> request = new GsonRequest<T>(Request.Method.POST, params, url, clazz, listener, errorListener);
		request.setTag(tag);
		add(request);
		return request;
	}

	/**
	 * Post方式2：json字符串
	 * 
	 * @param url
	 * @param jsonObject
	 * @param listener
	 * @param errorListener
	 */
	public void postjsonRequest(Object tag, String url, JSONObject jsonObject, Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		JsonObjectRequest jsonObjectRequest;
		jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, listener, errorListener);
		jsonObjectRequest.setTag(tag);
		add(jsonObjectRequest);

	}

	/**
	 * ImageRequest
	 * 
	 * @param tag
	 * @param url
	 * @param listener
	 * @param maxWidth
	 * @param maxHeight
	 * @param scaleType
	 * @param decodeConfig
	 * @param errorListener
	 * @return
	 */
	public ImageRequest imageRequest(Object tag, String url, Response.Listener<Bitmap> listener, int maxWidth,
			int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig,
			Response.ErrorListener errorListener) {
		ImageRequest request = new ImageRequest(url, listener, maxWidth, maxHeight, scaleType, decodeConfig,
				errorListener);
		request.setTag(tag);
		add(request);
		return request;
	}

	/**
	 * ImageLoader 图片默认大小
	 * 
	 * @param imageView
	 * @param imgViewUrl
	 * @param defaultImageResId
	 * @param errorImageResId
	 */
	public void imageLoaderRequest(ImageView imageView, String imgViewUrl, int defaultImageResId, int errorImageResId) {
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, defaultImageResId,
				errorImageResId);
		mImageLoader.get(imgViewUrl, listener);
	}

	/**
	 * ImageLoader 指定图片大小
	 * 
	 * @param imageView
	 * @param imgViewUrl
	 * @param defaultImageResId
	 * @param errorImageResId
	 * @param maxWidth
	 * @param maxHeight
	 */
	public void imageLoaderRequest(ImageView imageView, String imgViewUrl, int defaultImageResId, int errorImageResId,
			int maxWidth, int maxHeight) {
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, defaultImageResId,
				errorImageResId);
		mImageLoader.get(imgViewUrl, listener, maxWidth, maxHeight);
	}

	/**
	 * 取消请求
	 * 
	 * @param tag
	 */
	public void cancel(Object tag) {
		mRequestQueue.cancelAll(tag);
	}
}
