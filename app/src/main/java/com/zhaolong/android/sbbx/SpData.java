package com.zhaolong.android.sbbx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 专门的SharedPreferences类用于本项目
 * @author Administrator
 *
 */
public class SpData {
	
	public final static String keyPhone="keyPhone";//手机号
	public final static String keyCode = "keyCode";
	public final static String keyId="keyId";
	
	public SpData(Context context) {
		sp = context.getSharedPreferences("sbbx", Activity.MODE_PRIVATE);
	}
	SharedPreferences sp = null;// 声明一个SharedPreferences
	
	public String getStringValue(String key,String defaultValue){
		String ret = sp.getString(key, defaultValue);
		return ret;
	}
	public void setStringValue(String key,String value){
		Editor e = sp.edit();
		e.putString(key, value);
		e.commit();
	}
	public boolean getBooleanValue(String key,boolean defaultValue){
		boolean ret = sp.getBoolean(key, defaultValue);
		return ret;
	}
	public void setBooleanValue(String key,boolean value){
		Editor e = sp.edit();
		e.putBoolean(key, value);
		e.commit();
	}
	

	public float getFloatValue(String key,float defaultValue){
		float ret = sp.getFloat(key, defaultValue);
		return ret;
	}
	public void setFloatValue(String key,float value){
		Editor e = sp.edit();
		e.putFloat(key, value);
		e.commit();
	}

	public int getIntValue(String key,int defaultValue){
		int ret = sp.getInt(key, defaultValue);
		return ret;
	}
	public void setIntValue(String key,int value){
		Editor e = sp.edit();
		e.putInt(key, value);
		e.commit();
	}

	public long getLongValue(String key,long defaultValue){
		long ret = sp.getLong(key, defaultValue);
		return ret;
	}
	public void setLongValue(String key,long value){
		Editor e = sp.edit();
		e.putLong(key, value);
		e.commit();
	}
	
	public void clrea(){
		setStringValue(SpData.keyPhone, null);
		setStringValue(SpData.keyCode, null);
		setStringValue(SpData.keyId, null);
	}

}
