package com.zhaolong.android.sbbx.beans;

import java.io.Serializable;

public class HttpResult implements Serializable {
	private static final long serialVersionUID = -1948042386850953730L;
	private Integer status;// 1-服务器返回处理成功，0-服务器返回失败结果，2-用户不存在，-9999 表示App处理出错
	private Object data;// 交互数据
	
	private String id;
	private int usertype;
	String filename; 
	int totalRows;
	int totalPage;
	
	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRowss() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public boolean isSuccess(){
		return status != null && 1==status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
