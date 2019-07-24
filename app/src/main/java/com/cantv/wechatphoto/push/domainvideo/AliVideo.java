package com.cantv.wechatphoto.push.domainvideo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class AliVideo implements IVideo, Parcelable {

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
		return "AliVideo [id=" + id + ", videoid=" + videoid + ", length=" + length + ", name=" + name
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

	public static final Creator<AliVideo> CREATOR = new Creator<AliVideo>() {
		public AliVideo createFromParcel(Parcel in) {
			AliVideo aVideo = new AliVideo();
			aVideo.setId(in.readString());
			aVideo.setVideoid(in.readString());
			aVideo.setLength(in.readString());
			aVideo.setName(in.readString());
			aVideo.setVolumncount(in.readString());
			return aVideo;
		}

		public AliVideo[] newArray(int size) {
			return new AliVideo[size];
		}
	};

}
