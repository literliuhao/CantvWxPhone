package com.cantv.wechatphoto.push.domainvideo;

import android.util.Log;

public class YokuConstants {

	/**
	 * 视频清晰度
	 * 
	 * @author zhangbingyuan
	 *
	 */
	public interface DEFINITION {
		int SD = 1;
		int HD = 2;
		int UHD = 3;
	}

	/**
	 * 视频比例
	 * 
	 * @author zhangbingyuan
	 *
	 */
	public interface VIDEO_RATIO {
		int FULLSCREEN = -1;
		int ORIGINAL = 100;
	}

	final public static String valueOfDefinition(int definition) throws IllegalArgumentException {
		String value = "";
		switch (definition) {
		case DEFINITION.SD:
			value = PlayConstants.DEFINITION.SD_VALUE;
			break;
		case DEFINITION.HD:
			value = PlayConstants.DEFINITION.HD_VALUE;
			break;
		case DEFINITION.UHD:
			value = PlayConstants.DEFINITION.UHD_VALUE;
			break;
		default:
			Log.w("YokuConstants", "Unknown Yoku-Definition : " + definition);
		}
		return value;
	}

	final public static String valueOfVideoRatio(int videoRatio) throws IllegalArgumentException {
		String value = "";
		switch (videoRatio) {
		case VIDEO_RATIO.FULLSCREEN:
			value = PlayConstants.VIDEO_RATIO.FULLSCREEN_VALUE;
			break;
		case VIDEO_RATIO.ORIGINAL:
			value = PlayConstants.VIDEO_RATIO.ORIGINAL_VALUE;
			break;
		default:
			Log.w("YokuConstants", "Unknown Yoku-VideoRatio : " + videoRatio);
		}
		return value;
	}

}
