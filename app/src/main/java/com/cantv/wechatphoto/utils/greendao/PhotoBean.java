package com.cantv.wechatphoto.utils.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table WECHATPHOTO_TAB.
 */
public class PhotoBean {

	private Long id;
	private String userid;// 微信号
	private String wxname;// 微信昵称
	private String wxheadimgurl;// 微信头像
	private String district;// 区域
	private String description;// 描述
	private Integer photoid;// 照片Id
	private String photourl;// 照片地址
	private String photocdnurl;// 照片cdn地址
	private String photolocalurl;// 照片磁盘地址
	private String uploadtime;// 上传时间
	private Long expiredtime;// 上传时间戳
	private Long altertime;// 修改时间戳
	private Integer photowide;// 照片宽
	private Integer photohigh;// 照片高
	private Integer direction;// 照片方向
	private String back1;// 备用字段
	private String back2;
	private String back3;

    public PhotoBean() {
    }

    public PhotoBean(Long id) {
        this.id = id;
    }

    public PhotoBean(Long id, String userid, String wxname, String wxheadimgurl, String district, String description, Integer photoid, String photourl, String photocdnurl, String photolocalurl, String uploadtime, Long expiredtime, Long altertime, Integer photowide, Integer photohigh, Integer direction, String back1, String back2, String back3) {
        this.id = id;
        this.userid = userid;
        this.wxname = wxname;
        this.wxheadimgurl = wxheadimgurl;
        this.district = district;
        this.description = description;
        this.photoid = photoid;
        this.photourl = photourl;
        this.photocdnurl = photocdnurl;
        this.photolocalurl = photolocalurl;
        this.uploadtime = uploadtime;
        this.expiredtime = expiredtime;
        this.altertime = altertime;
        this.photowide = photowide;
        this.photohigh = photohigh;
        this.direction = direction;
        this.back1 = back1;
        this.back2 = back2;
        this.back3 = back3;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getWxname() {
        return wxname;
    }

    public void setWxname(String wxname) {
        this.wxname = wxname;
    }

    public String getWxheadimgurl() {
        return wxheadimgurl;
    }

    public void setWxheadimgurl(String wxheadimgurl) {
        this.wxheadimgurl = wxheadimgurl;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPhotoid() {
        return photoid;
    }

    public void setPhotoid(Integer photoid) {
        this.photoid = photoid;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getPhotocdnurl() {
        return photocdnurl;
    }

    public void setPhotocdnurl(String photocdnurl) {
        this.photocdnurl = photocdnurl;
    }

    public String getPhotolocalurl() {
        return photolocalurl;
    }

    public void setPhotolocalurl(String photolocalurl) {
        this.photolocalurl = photolocalurl;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }

    public Long getExpiredtime() {
        return expiredtime;
    }

    public void setExpiredtime(Long expiredtime) {
        this.expiredtime = expiredtime;
    }

    public Long getAltertime() {
        return altertime;
    }

    public void setAltertime(Long altertime) {
        this.altertime = altertime;
    }

    public Integer getPhotowide() {
        return photowide;
    }

    public void setPhotowide(Integer photowide) {
        this.photowide = photowide;
    }

    public Integer getPhotohigh() {
        return photohigh;
    }

    public void setPhotohigh(Integer photohigh) {
        this.photohigh = photohigh;
    }

    public Integer getDirection() {
        if(null == direction){
            direction = 0;
        }
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getBack1() {
        return back1;
    }

    public void setBack1(String back1) {
        this.back1 = back1;
    }

    public String getBack2() {
        return back2;
    }

    public void setBack2(String back2) {
        this.back2 = back2;
    }

    public String getBack3() {
        return back3;
    }

    public void setBack3(String back3) {
        this.back3 = back3;
    }

}
