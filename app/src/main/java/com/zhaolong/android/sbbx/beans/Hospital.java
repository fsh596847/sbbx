package com.zhaolong.android.sbbx.beans;

import java.io.Serializable;

public class Hospital implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6934709842148901705L;
	private String hospitalName;
	private String hospitalNick;
	private String address;
	private String id;
	private String departName;
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getHospitalNick() {
		return hospitalNick;
	}
	public void setHospitalNick(String hospitalNick) {
		this.hospitalNick = hospitalNick;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

}
