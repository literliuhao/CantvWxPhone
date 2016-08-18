package com.cantv.wechatphoto.push.domainvideo;

public interface IVideo {

	String getId();
	
	/**
	 * 标题
	 * @return
	 */
	String getTitle();
	
	/**
	 * 当前是第几集
	 * @return
	 */
	String getEpisode();
	
}
