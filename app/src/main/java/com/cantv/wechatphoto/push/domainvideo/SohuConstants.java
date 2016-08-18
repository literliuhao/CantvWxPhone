package com.cantv.wechatphoto.push.domainvideo;

import android.util.Log;

public class SohuConstants {

	/**
	 * 视频清晰度
	 * 
	 * @author zhangbingyuan
	 *
	 */
	public interface DEFINITION {
		int SD = 2;
		int HD = 1;
		int UHD = 21;
		int ORIGINAL = 31;// 原画
		int BD = 32;// 蓝光
		int _4K = 51;
	}

	/**
	 * 视频比例
	 * 
	 * @author zhangbingyuan
	 *
	 */
	public interface VIDEO_RATIO {
		int ORIGINAL = 1;
		int FULLSCREEN = 2;
		int _16_9 = 3;
		int _4_3 = 4;
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
		case DEFINITION.ORIGINAL:
			value = PlayConstants.DEFINITION.ORIGINAL_VALUE;
			break;
		case DEFINITION.BD:
			value = PlayConstants.DEFINITION.BD_VALUE;
			break;
		case DEFINITION._4K:
			value = PlayConstants.DEFINITION._4K_VALUE;
			break;
		default:
			Log.w("SohuConstants", "Unknown Sohu-Definition : " + definition);
		}
		return value;
	}

	final public static String valueOfVideoRatio(int videoRatio) throws IllegalArgumentException {
		String value = "";
		switch (videoRatio) {
		case VIDEO_RATIO.ORIGINAL:
			value = PlayConstants.VIDEO_RATIO.ORIGINAL_VALUE;
			break;
		case VIDEO_RATIO.FULLSCREEN:
			value = PlayConstants.VIDEO_RATIO.FULLSCREEN_VALUE;
			break;
		case VIDEO_RATIO._16_9:
			value = PlayConstants.VIDEO_RATIO._16_9_VALUE;
			break;
		case VIDEO_RATIO._4_3:
			value = PlayConstants.VIDEO_RATIO._4_3_VALUE;
			break;
		default:
			Log.w("SohuConstants", "Unknown Sohu-VideoRatio : " + videoRatio);
		}
		return value;
	}

}
