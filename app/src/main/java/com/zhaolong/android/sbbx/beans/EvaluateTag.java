package com.zhaolong.android.sbbx.beans;

import java.io.Serializable;

public class EvaluateTag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2058412253004993101L;
	private String id;
	private String tagName;
	private int seq;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
}
