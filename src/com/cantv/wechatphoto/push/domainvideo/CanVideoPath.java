package com.cantv.wechatphoto.push.domainvideo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

/**
 * 原生播放器视频URI
 * 
 * @author zhangbingyuan
 *
 */
public class CanVideoPath implements Parcelable {

	private String type;
	private String playurl;
	private int definition = -1;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlayurl() {
		return playurl;
	}

	public void setPlayurl(String playurl) {
		this.playurl = playurl;
	}

	public int getDefinition() {
		if (definition != -1) {
			return definition;
		}

		if (PlayConstants.DEFINITION.SD_VALUE.equals(type) || "流畅".equals(type)) {
			definition = CanConstants.DEFINITION.SD;
		} else if (PlayConstants.DEFINITION.HD_VALUE.equals(type)) {
			definition = CanConstants.DEFINITION.HD;
		} else if (PlayConstants.DEFINITION.UHD_VALUE.equals(type)) {
			definition = CanConstants.DEFINITION.UHD;
		} else if (PlayConstants.DEFINITION._4K_VALUE.equalsIgnoreCase(type)) {
			definition = CanConstants.DEFINITION._4K;
		} else {
			Log.w("CanVideoPath", "Unknown definition type : " + (TextUtils.isEmpty(type) ? "NULL" : type));
			definition = CanConstants.DEFINITION.UHD;
		}
		return definition;
	}

	public void setDefinition(int definition) {
		this.definition = definition;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(type);
		dest.writeString(playurl);
		dest.writeInt(definition);
	}

	public static final Parcelable.Creator<CanVideoPath> CREATOR = new Parcelable.Creator<CanVideoPath>() {

		public CanVideoPath createFromParcel(Parcel in) {
			CanVideoPath path = new CanVideoPath();
			path.setType(in.readString());
			path.setPlayurl(in.readString());
			path.setDefinition(in.readInt());
			return path;
		}

		public CanVideoPath[] newArray(int size) {
			return new CanVideoPath[size];
		}
	};
}
