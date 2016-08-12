package com.cantv.wechatphoto.push.domain;

import android.text.TextUtils;

import com.cantv.wechatphoto.push.domainvideo.CanVideo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class PushCANProgramMsg implements IPushMessage {

	private String programId;
	private int chargeType = 0;// 默认免费
	private int playIndex = 0;// 默认从第1集开始播放
	private int freePlayEpisode = -1;// 默认不限制免费播放集数
	private int freePlayTime = -1;// 默认不限制免费播放时长
	private CanVideo video;// 进播放器播放的单集影片

	@Override
	public PushCANProgramMsg parse(JsonObject data)
			throws JsonSyntaxException, NullPointerException, IllegalStateException {
		if (data == null) {
			return this;
		}
		programId = data.get("programId").getAsString();
		chargeType = data.get("chargeType").getAsInt();
		playIndex = data.get("playIndex").getAsInt();
		freePlayEpisode = data.get("freePlayEpisode").getAsInt();
		freePlayTime = data.get("freePlayTime").getAsInt();
		try {
			JsonObject videoJs = data.get("video").getAsJsonObject();
			video = new Gson().fromJson(videoJs, CanVideo.class);
		} catch (Exception e) {
			video = null;
		}
		return this;
	}

	@Override
	public boolean isLegal() {
		return !TextUtils.isEmpty(programId) || video != null;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public int getChargeType() {
		return chargeType;
	}

	public void setChargeType(int chargeType) {
		this.chargeType = chargeType;
	}

	public int getPlayIndex() {
		return playIndex;
	}

	public void setPlayIndex(int playIndex) {
		this.playIndex = playIndex;
	}

	public int getFreePlayEpisode() {
		return freePlayEpisode;
	}

	public void setFreePlayEpisode(int freePlayEpisode) {
		this.freePlayEpisode = freePlayEpisode;
	}

	public int getFreePlayTime() {
		return freePlayTime;
	}

	public void setFreePlayTime(int freePlayTime) {
		this.freePlayTime = freePlayTime;
	}

	public CanVideo getVideo() {
		return video;
	}

	public void setVideo(CanVideo video) {
		this.video = video;
	}

	@Override
	public String toString() {
		return "PushCANProgramMsg [programId=" + programId + ", chargeType=" + chargeType + ", playIndex=" + playIndex
				+ ", freePlayEpisode=" + freePlayEpisode + ", freePlayTime=" + freePlayTime + ", video=" + video + "]";
	}

}
