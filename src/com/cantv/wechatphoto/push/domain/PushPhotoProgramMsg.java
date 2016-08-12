package com.cantv.wechatphoto.push.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
/**
 * 微信相册解析类
 * @author Administrator
 *
 */
public class PushPhotoProgramMsg implements IPushMessage {
	
	private int totalNumber;
	private List<PhotoBean> photoList;
	private static final String TAG = "PushMsgHandler";

	@Override
	public PushPhotoProgramMsg parse(JsonObject data)
			throws JsonSyntaxException, NullPointerException, IllegalStateException {
		if (data == null) {
			return this;
		}
		Log.i(TAG, "received push message. " + "开始解析");
		photoList = new ArrayList<PhotoBean>();
		totalNumber = data.get("totalNumber").getAsInt();
		JsonElement photoArray=data.get("photoList");
		
		if(photoArray.isJsonArray()){
		   JsonArray jsonArray= photoArray.getAsJsonArray();
		   for(Iterator<JsonElement> iter=jsonArray.iterator();iter.hasNext();){
		      JsonObject obj=(JsonObject) iter.next();
		      PhotoBean photoInfo = new PhotoBean();
		      photoInfo.setUserid(obj.get("userid").getAsString());
		      photoInfo.setWxname(obj.get("wxname").getAsString());
		      photoInfo.setWxheadimgurl(obj.get("wxheadimgurl").getAsString());
		      photoInfo.setUploadtime(obj.get("uploadtime").getAsString());
		      photoInfo.setPhotourl(obj.get("photourl").getAsString());
		      photoInfo.setExpiredtime(obj.get("expiredtime").getAsLong());
		      Log.i(TAG, "信息："+obj.toString());
		      photoList.add(photoInfo);
		   }
		}
		return this;
	}

	@Override
	public boolean isLegal() {
		return totalNumber != 0 || photoList.size() != 0;
	}

	public int getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}

	public List<PhotoBean> getPhotoList() {
		return photoList;
	}

	public void setPhotoList(List<PhotoBean> photoList) {
		this.photoList = photoList;
	}

	@Override
	public String toString() {
		return "PushPhotoProgramMsg [totalNumber=" + totalNumber + ", photoList=" + photoList + "]";
	}

	
}
