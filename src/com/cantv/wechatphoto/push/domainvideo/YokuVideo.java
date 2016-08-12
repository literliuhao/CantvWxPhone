package com.cantv.wechatphoto.push.domainvideo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class YokuVideo implements IVideo, Parcelable {

	protected String id;
	private String videoid;
	protected String length;
	protected String name;
	protected String volumncount;

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVolumncount() {
		return volumncount;
	}

	public void setVolumncount(String volumncount) {
		this.volumncount = volumncount;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return name;
	}

	@Override
	public String getEpisode() {
		return volumncount;
	}

	@Override
	public String toString() {
		return "YokuVideo [id=" + id + ", videoid=" + videoid + ", length=" + length + ", name=" + name
				+ ", volumncount=" + volumncount + "]";
	}

	public boolean isLegal() {
		return !TextUtils.isEmpty(id) && !TextUtils.isEmpty(videoid);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(videoid);
		dest.writeString(length);
		dest.writeString(name);
		dest.writeString(volumncount);
	}

	public static final Parcelable.Creator<YokuVideo> CREATOR = new Parcelable.Creator<YokuVideo>() {
		public YokuVideo createFromParcel(Parcel in) {
			YokuVideo yokuVideo = new YokuVideo();
			yokuVideo.setId(in.readString());
			yokuVideo.setVideoid(in.readString());
			yokuVideo.setLength(in.readString());
			yokuVideo.setName(in.readString());
			yokuVideo.setVolumncount(in.readString());
			return yokuVideo;
		}

		public YokuVideo[] newArray(int size) {
			return new YokuVideo[size];
		}
	};

}
