package com.cantv.wechatphoto.push.domainvideo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class CanVideo implements IVideo, Parcelable {

	private String id;
	private String name;
	private String length;
	private String videoid;
	private String volumncount;
	private List<CanVideoPath> playlist = new ArrayList<CanVideoPath>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public String getVolumncount() {
		return volumncount;
	}

	public void setVolumncount(String volumncount) {
		this.volumncount = volumncount;
	}

	public List<CanVideoPath> getPlaylist() {
		return playlist;
	}

	public void setPlaylist(List<CanVideoPath> playlist) {
		this.playlist = playlist;
	}

	@Override
	public String toString() {
		return "CanVideo [id=" + id + ", name=" + name + ", length=" + length + ", videoid=" + videoid
				+ ", volumncount=" + volumncount + ", playlist=" + playlist + "]";
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
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(length);
		dest.writeString(videoid);
		dest.writeString(volumncount);
		dest.writeTypedList(playlist);
	}

	public static final Parcelable.Creator<CanVideo> CREATOR = new Parcelable.Creator<CanVideo>() {

		public CanVideo createFromParcel(Parcel in) {
			CanVideo video = new CanVideo();
			video.setId(in.readString());
			video.setName(in.readString());
			video.setLength(in.readString());
			video.setVideoid(in.readString());
			video.setVolumncount(in.readString());
			List<CanVideoPath> list = new ArrayList<CanVideoPath>();
			in.readTypedList(list, CanVideoPath.CREATOR);
			video.setPlaylist(list);
			return video;
		}

		public CanVideo[] newArray(int size) {
			return new CanVideo[size];
		}
	};

	public boolean isLegal() {
		return !TextUtils.isEmpty(id) && playlist != null && playlist.size() > 0;
	}
}
