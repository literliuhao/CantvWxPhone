package com.cantv.wechatphoto.utils.volley;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.cantv.wechatphoto.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * AuthFailureError：如果在做一个HTTP的身份验证，可能会发生这个错误。
 * NetworkError：Socket关闭，服务器宕机，DNS错误都会产生这个错误。
 * NoConnectionError：和NetworkError类似，这个是客户端没有网络连接。
 * ParseError：在使用JsonObjectRequest或JsonArrayRequest时，如果接收到的JSON是畸形，会产生异常。
 * SERVERERROR：服务器的响应的一个错误，最有可能的4xx或5xx HTTP状态代码。
 * TimeoutError：Socket超时，服务器太忙或网络延迟会产生这个异常。
 * 默认情况下，Volley的超时时间为2.5秒。如果得到这个错误可以使用RetryPolicy。
 */
public class VolleyErrorHelper {

	/**
	 * Returns appropriate message which is to be displayed to the user against
	 * the specified error object.
	 * 
	 * @param error
	 * @param context
	 * @return
	 */
	public static String getMessage(Object error, Context context) {
		if (error instanceof TimeoutError) {
			return context.getResources().getString(R.string.generic_server_down);
		} else if (isServerProblem(error)) {
			return handleServerError(error, context);
		} else if (isNetworkProblem(error)) {
			return context.getResources().getString(R.string.no_internet);
		}
		return context.getResources().getString(R.string.generic_error);
	}

	/**
	 * Determines whether the error is related to network
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isNetworkProblem(Object error) {
		return (error instanceof NetworkError) || (error instanceof NoConnectionError);
	}

	/**
	 * Determines whether the error is related to server
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isServerProblem(Object error) {
		return (error instanceof ServerError) || (error instanceof AuthFailureError);
	}

	/**
	 * Handles the server error, tries to determine whether to show a stock
	 * message or to show a message retrieved from the server.
	 * 
	 * @param err
	 * @param context
	 * @return
	 */
	private static String handleServerError(Object err, Context context) {
		VolleyError error = (VolleyError) err;

		NetworkResponse response = error.networkResponse;

		if (response != null) {
			switch (response.statusCode) {
			case 404:
			case 422:
			case 401:
				try {
					HashMap<String, String> result = new Gson().fromJson(new String(response.data),
							new TypeToken<Map<String, String>>() {
							}.getType());

					if (result != null && result.containsKey("error")) {
						return result.get("error");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				return error.getMessage();
			default:
				return context.getResources().getString(R.string.generic_server_down);
			}
		}
		return context.getResources().getString(R.string.generic_error);
	}

	public static String getErrorCode(VolleyError error) {
		String ret = "000";// 网络异常
		if (error instanceof TimeoutError) {
			ret = "800";// 超时
		} else if (error instanceof ServerError || error instanceof AuthFailureError) {
			if (error.networkResponse != null) {
				ret = String.valueOf(error.networkResponse.statusCode);
			}
		} else if (error instanceof NetworkError || error instanceof NoConnectionError) {
			ret = "802";// 网络连接错误
		}
		return ret;
	}
}
