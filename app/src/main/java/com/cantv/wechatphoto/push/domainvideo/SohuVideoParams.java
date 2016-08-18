package com.cantv.wechatphoto.push.domainvideo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class SohuVideoParams implements Parcelable {

	private String sid;
	private String cid;
	private String vid;
	private String catecode;
	private List<Integer> definition;
	private String volumncount;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getCatecode() {
		return catecode;
	}

	public void setCatecode(String catecode) {
		this.catecode = catecode;
	}

	public List<Integer> getDefinition() {
		return definition;
	}

	public void setDefinition(List<Integer> definition) {
		this.definition = definition;
	}

	public String getVolumncount() {
		return volumncount;
	}

	public void setVolumncount(String volumncount) {
		this.volumncount = volumncount;
	}

	@Override
	public String toString() {
		return "SohuVideoParams [sid=" + sid + ", cid=" + cid + ", vid=" + vid + ", catecode=" + catecode
				+ ", definition=" + definition + ", volumncount=" + volumncount + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sid);
		dest.writeString(cid);
		dest.writeString(vid);
		dest.writeString(catecode);
		dest.writeString(volumncount);
		dest.writeList(definition);
	}

	public static final Parcelable.Creator<SohuVideoParams> CREATOR = new Parcelable.Creator<SohuVideoParams>() {

		public SohuVideoParams createFromParcel(Parcel in) {
			SohuVideoParams params = new SohuVideoParams();
			params.setSid(in.readString());
			params.setCid(in.readString());
			params.setVid(in.readString());
			params.setCatecode(in.readString());
			params.setVolumncount(in.readString());
			ArrayList<Integer> list = new ArrayList<Integer>();
			in.readList(list, null);
			params.setDefinition(list);
			return params;
		}

		public SohuVideoParams[] newArray(int size) {
			return new SohuVideoParams[size];
		}
	};

	public boolean isLegal() {
		return !TextUtils.isEmpty(sid) && !TextUtils.isEmpty(cid) && !TextUtils.isEmpty(vid)
				&& !TextUtils.isEmpty(catecode);
	}

}
