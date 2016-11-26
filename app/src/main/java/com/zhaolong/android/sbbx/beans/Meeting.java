package com.zhaolong.android.sbbx.beans;

import java.io.Serializable;

public class Meeting implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1031976874201834444L;
	private String meetid;
	private String meetname;
	private String starttime;
	private String address;
	private int issign;
	private String imgurl;
	private String url;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMeetid() {
		return meetid;
	}
	public void setMeetid(String meetid) {
		this.meetid = meetid;
	}
	public String getMeetname() {
		return meetname;
	}
	public void setMeetname(String meetname) {
		this.meetname = meetname;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getIssign() {
		return issign;
	}
	public void setIssign(int issign) {
		this.issign = issign;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	

}
