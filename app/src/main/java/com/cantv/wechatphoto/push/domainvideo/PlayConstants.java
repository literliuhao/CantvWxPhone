package com.cantv.wechatphoto.push.domainvideo;

public class PlayConstants {

	public interface DEFINITION {
		int SD = 0;// 标清
		int HD = 1;// 高清
		int UHD = 2;// 超清
		int ORIGINAL = 3;// 原画
		int BD = 4;// 蓝光
		int _4K = 5;// 4K

		String SD_VALUE = "标清";
		String HD_VALUE = "高清";
		String UHD_VALUE = "超清";
		String ORIGINAL_VALUE = "原画";
		String BD_VALUE = "蓝光";
		String _4K_VALUE = "4K";
	}

	public interface VIDEO_RATIO {
		int FULLSCREEN = 0;
		int ORIGINAL = 1;
		int _16_9 = 2;
		int _4_3 = 3;

		String FULLSCREEN_VALUE = "自动全屏";
		String ORIGINAL_VALUE = "原始比例";
		String _16_9_VALUE = " 16：9";
		String _4_3_VALUE = "   4：3";
		String _2d4_1_VALUE = "2.39：1";
	}

	public interface PLAY_TYPE {
		/**
		 * 入口界面通过Bundle传递播放数据 to 播放器
		 */
		int NORMAL = 0;
		/**
		 * 入口界面提供节目ID to 播放器，由播放器自己拉取数据
		 */
		int PUSH = 1;
	}

	final public static String valueOfDefinition(int definition) throws IllegalArgumentException {
		String value = "";
		switch (definition) {
		case DEFINITION.SD:
			value = DEFINITION.SD_VALUE;
			break;
		case DEFINITION.HD:
			value = DEFINITION.HD_VALUE;
			break;
		case DEFINITION.UHD:
			value = DEFINITION.UHD_VALUE;
			break;
		case DEFINITION.ORIGINAL:
			value = DEFINITION.ORIGINAL_VALUE;
			break;
		case DEFINITION.BD:
			value = DEFINITION.BD_VALUE;
			break;
		case DEFINITION._4K:
			value = DEFINITION._4K_VALUE;
			break;
		default:
			throw new IllegalArgumentException("Unknown definition : " + definition);
		}
		return value;
	}

	final public static String valueOfVideoRatio(int videoRatio) {
		String value = "";
		switch (videoRatio) {
		case VIDEO_RATIO.FULLSCREEN:
			value = VIDEO_RATIO.FULLSCREEN_VALUE;
			break;
		case VIDEO_RATIO.ORIGINAL:
			value = VIDEO_RATIO.ORIGINAL_VALUE;
			break;
		case VIDEO_RATIO._16_9:
			value = VIDEO_RATIO._16_9_VALUE;
			break;
		case VIDEO_RATIO._4_3:
			value = VIDEO_RATIO._4_3_VALUE;
			break;
		default:
			throw new IllegalArgumentException("Unknown videoRatio : " + videoRatio);
		}
		return value;
	}

}
