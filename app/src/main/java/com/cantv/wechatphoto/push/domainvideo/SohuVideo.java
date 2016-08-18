package com.cantv.wechatphoto.push.domainvideo;

import android.os.Parcel;
import android.os.Parcelable;

public class SohuVideo implements IVideo, Parcelable {

	private String name;
	private int position;
	private String volumncount;
	private SohuVideoParams sohu;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getVolumncount() {
		return volumncount;
	}

	public void setVolumncount(String volumncount) {
		this.volumncount = volumncount;
	}

	public SohuVideoParams getSohu() {
		return sohu;
	}

	public void setSohu(SohuVideoParams sohu) {
		this.sohu = sohu;
	}

	@Override
	public String toString() {
		return "SohuVideo [name=" + name + ", position=" + position + ", volumncount=" + volumncount + ", sohu=" + sohu
				+ "]";
	}

	@Override
	public String getId() {
		String id = null;
		if (sohu != null) {
			id = sohu.getVid();
		}
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
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(position);
		dest.writeString(volumncount);
		sohu.writeToParcel(dest, flags);
	}

	public static final Parcelable.Creator<SohuVideo> CREATOR = new Parcelable.Creator<SohuVideo>() {

		public SohuVideo createFromParcel(Parcel in) {
			SohuVideo sohuVideo = new SohuVideo();
			sohuVideo.setName(in.readString());
			sohuVideo.setPosition(in.readInt());
			sohuVideo.setVolumncount(in.readString());
			sohuVideo.setSohu(SohuVideoParams.CREATOR.createFromParcel(in));
			return sohuVideo;
		}

		public SohuVideo[] newArray(int size) {
			return new SohuVideo[size];
		}
	};

	public boolean isLegal() {
		return sohu != null && sohu.isLegal();
	}
}
