package com.zhaolong.android.sbbx.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;

import com.zhaolong.android.sbbx.ConfigSbbx;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.HttpUtil;
import com.zhaolong.android.sbbx.utils.mLog;

public class DataService {
	
	/**
	 * 设备历史维修记录
	 * @param context
	 * @param equipment
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String equiphistory(Context context,String equipment,int p,int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "equipment", equipment, true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_equiphistory, params);
		return s;
	}
	/**
	 * 工程师快捷报修接口
	 * @param context
	 * @param userid
	 * @param mobile
	 * @param date
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String shortcutRepair(Context context,String userid,String mobile,String date,int p,int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "date", date, true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_shortcutRepair, params);
		return s;
	}
	/**
	 * 更新消息接口
	 * @param context
	 * @param id
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public static String updateMyNotice(Context context,String id,int state) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "id", id, true);
		putParams(params, "state", String.valueOf(state), true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_updateMyNotice, params);
		return s;
	}
	/**
	 * 消息查询接口
	 * @param context
	 * @param mobile
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String getMyNotice(Context context,String mobile,int p,int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "mobile", mobile, true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_getMyNotice, params);
		return s;
	}
	/**
	 * 我的资料修改接口
	 * @param context
	 * @param userid
	 * @param headimgurl
	 * @param name
	 * @param mobile
	 * @param email
	 * @param address
	 * @param company
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	public static String updateMyInfo(Context context,String userid,String headimgurl,String name,String mobile,String email
			,String address,String company,String companyId) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "headimgurl", headimgurl, false);
		putParams(params, "name", name, false);
		putParams(params, "mobile", mobile, true);
		putParams(params, "email", email, false);
		putParams(params, "address", address, false);
		putParams(params, "company", company, false);
		putParams(params, "companyId", companyId, false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_myInfo, params);
		return s;
	}
	/**
	 * 我的资料医院/企业查询接口
	 * @param context
	 * @param userclass 0医技 1工程师
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String queryCompany(Context context,int userclass,int p,int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userclass", String.valueOf(userclass), true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryCompany, params);
		return s;
	}
	/**
	 * 我的订单查询
	 * @param context
	 * @param userid
	 * @param userclass
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String queryMyOrder(Context context,String userid,int userclass,int p,int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "userclass", String.valueOf(userclass), true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryMyOrder, params);
		return s;
	}
	/**
	 * 会议报名
	 * @param context
	 * @param userid
	 * @param meetid
	 * @return
	 * @throws Exception
	 */
	public static String meetSubmit(Context context,String userid,String meetid) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "meetid", meetid, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_meetSubmit, params);
		return s;
	}
	/**
	 * 医生端维修评价
	 * @param context
	 * @param userid
	 * @param mobile
	 * @param orderid
	 * @param starnum
	 * @param comments
	 * @param tag
	 * @return
	 * @throws Exception
	 */
	public static String addJudgeOrder(Context context,String userid,String mobile,String orderid
			,int starnum,String comments,String tag) throws Exception{
		mLog.d("http", ConfigSbbx.phone_addJudgeOrder+"?userid="+userid+"&mobile="+mobile+"&orderid="+orderid+"&starnum="+starnum+"&comments="+comments+"&tag="+tag);
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "orderid", orderid, true);
		putParams(params, "starnum", String.valueOf(starnum), true);
		putParams(params, "comments", comments, false);
		putParams(params, "tag", tag, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_addJudgeOrder, params);
		return s;
	}
	/**
	 * 评价标签查询
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String queryTagByOrder(Context context,int scoreclass,String tag) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "scoreclass", String.valueOf(scoreclass), true);
		putParams(params, "tag", tag, false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryTagByOrder, params);
		return s;
	}
	/**
	 * 工程师会议培训查询
	 * @param context
	 * @param userid
	 * @param p
	 * @param pagesize
	 * @param isuser 值为Y 时 查询已报名会议
	 * @return
	 * @throws Exception
	 */
	public static String queryMeetinfo(Context context,String userid,String mobile,int p,int pagesize,String isuser) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		putParams(params, "isuser", isuser, false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryMeetinfo, params);
		return s;
	}
	/**
	 * 医生端取消订单
	 * @param context
	 * @param userid
	 * @param orderid
	 * @return
	 * @throws Exception
	 */
	public static String cancelOrder(Context context,String userid,String orderid) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "orderid", orderid, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_cancelOrder, params);
		return s;
	}
	/**
	 * 维修完成
	 * @param context
	 * @param userid
	 * @param orderid
	 * @param parts 维修信息
	 * @return
	 * @throws Exception
	 */
	public static String FinishOrder(Context context, String userid, String orderid, String parts,
			String ischangeparts, String results) throws Exception {
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "orderid", orderid, true);
		putParams(params, "parts", parts, false);
		putParams(params, "ischangeparts", ischangeparts, true);
		putParams(params, "results", results, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_FinishOrder, params);
		return s;
	}
	/**
	 * 评价查询
	 * @param context
	 * @param userid
	 * @param isevaluate
	 * @param p
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public static String queryJudgeOrder(Context context,String userid,int isevaluate,int userclass,int p,int pagesize) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "isevaluate", String.valueOf(isevaluate), false);
		putParams(params, "userclass", String.valueOf(userclass), true);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryJudgeOrder, params);
		return s;
	}
	/**
	 * 工程师签到
	 * @param context
	 * @param userid
	 * @param mobile
	 * @param equipCode
	 * @return
	 * @throws Exception
	 */
	public static String signRepairOrder(Context context,String userid,String mobile,String equipCode) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "equipCode", equipCode, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_signRepairOrder, params);
		return s;
	}
	/**
	 * 工程师订单接受、拒绝
	 * @param context
	 * @param userid
	 * @param mobile
	 * @param orderid
	 * @param isAccept 是否接受
	 * @return
	 * @throws Exception
	 */
	public static String acceptRepairOrder(Context context,String userid,String mobile,String orderid,boolean isAccept) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "orderid", orderid, true);
		String s = HttpUtil.postUrl(context, isAccept?ConfigSbbx.phone_acceptRepairOrder:ConfigSbbx.phone_refuseRepairOrder, params);
		return s;
	}
	/**
	 * 我的订单
	 * @param context
	 * @param userid
	 * @param state state =0 待接单 state =1已接单 state=2 已拒绝 state =3已完成
	 * @param p
	 * @param pagesize
	 * @param type 1，医生端；2，工程师端
	 * @return
	 * @throws Exception
	 */
	public static String queryRepairOrder(Context context,String userid,int state,int p,int pagesize,int type) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "state", String.valueOf(state), false);
		putParams(params, "p", String.valueOf(p), false);
		putParams(params, "pagesize", String.valueOf(pagesize), false);
		String s = HttpUtil.postUrl(context, type==1?ConfigSbbx.phone_queryMedicalRepairOrder:ConfigSbbx.phone_queryEngineerRepairOrder, params);
		return s;
	}
	/**
	 * 用户信息
	 * @param context
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public static String queryUserInfo(Context context,String userid) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryUserInfo, params);
		return s;
	}
	/**
	 * 查询工程师设备
	 * @param context
	 * @param userid
	 * @param mobile
	 * @param p
	 * @param pagesize
	 * @param hospitalid
	 * @return
	 * @throws Exception
	 */
	public static String queryEngineerEquip(Context context,String userid,String mobile,int p,int pagesize,
			String hospitalid,String equipname) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "p", String.valueOf(p), true);
		putParams(params, "pagesize", String.valueOf(pagesize), true);
		putParams(params, "hospitalid", hospitalid, false);
		putParams(params, "equipname", equipname, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryEngineerEquip, params);
		return s;
	}
	/**
	 * 查询医生端设备
	 * @param context
	 * @param userid
	 * @param mobile
	 * @param p
	 * @param pagesize
	 * @param depar
	 * @param equipClass 设备类型  1：诊断设备  2：治疗设备
	 * @return
	 * @throws Exception
	 */
	public static String queryMedicalEquip(Context context,String userid,String mobile,int p,int pagesize,String depar,int equipClass) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "p", String.valueOf(p), true);
		putParams(params, "pagesize", String.valueOf(pagesize), true);
		putParams(params, "depar", depar, false);
		putParams(params, "equipClass", equipClass==0?null:String.valueOf(equipClass), false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryMedicalEquip, params);
		return s;
	}

	/**
	 * 查询医生端设备
	 *
	 * @throws Exception
	 */
	public static String queryMedicalEquip(Context context, String userid, String mobile, int p,
			int pagesize, String equipname) throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "p", String.valueOf(p), true);
		putParams(params, "pagesize", String.valueOf(pagesize), true);
		putParams(params, "equipname", equipname, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryMedicalEquip, params);
		return s;
	}

	/**
	 * 科室列表查询
	 *
	 * @throws Exception
	 */
	public static String querydepart(Context context) throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_querydepart, params);
		return s;
	}
	/**
	 * 医院列表查询
	 * @param context
	 * @param userid
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String queryHospitaldoc(Context context,String userid,String mobile) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "mobile", mobile, true);
		putParams(params, "p", "1", true);
		putParams(params, "pagesize", "500", true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryHospitaldoc, params);
		return s;
	}
	/**
	 * 医生端报修
	 * @param context
	 * @param userid
	 * @param equipId
	 * @param equipCode 编码
	 * @param equipName 名称
	 * @param engineerId 
	 * @param bookingTime
	 * @param equipAddress
	 * @param faultDesc 描述
	 * @param faultImg1
	 * @param faultImg2
	 * @param faultImg3
	 * @return
	 * @throws Exception
	 */
	public static String addRepair(Context context,String userid,String equipId,String equipCode
			,String equipName,String engineerId,String bookingTime,String equipAddress,String faultDesc
			,String faultImg1,String faultImg2,String faultImg3,String equipType) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "equipId", equipId, true);
		putParams(params, "equipCode", equipCode, false);
		putParams(params, "equipName", equipName, false);
		putParams(params, "engineerId", engineerId, false);
		putParams(params, "bookingTime", bookingTime, false);
		putParams(params, "equipAddress", equipAddress, false);
		putParams(params, "faultDesc", faultDesc, false);
		putParams(params, "faultImg1", faultImg1, false);
		putParams(params, "faultImg2", faultImg2, false);
		putParams(params, "faultImg3", faultImg3, false);
		putParams(params, "equipType", equipType, false);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_addRepair, params);
		return s;
	}
	/**
	 * 上传图片
	 * @param context
	 * @param userid
	 * @param iosimg
	 * @return
	 * @throws Exception
	 */
	public static String uploadUserImg(Context context,String userid,String iosimg) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "uploadImage", "uploadImage", true);
		putParams(params, "imgtype", "userlogoimg", true);
		putParams(params, "iosimg", iosimg, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_uploadUserImg, params);
		return s;
	}
	/**
	 * 
	 * @param context
	 * @param userid
	 * @param imgtype 0,医技、1，工程师端
	 * @return
	 * @throws Exception
	 */
	public static String getHomeImg(Context context,String userid,String imgtype) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "imgtype", imgtype, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_homeImg, params);
		return s;
	}
	/**
	 * 
	 * @param context
	 * @param userid
	 * @param equipId
	 * @return
	 * @throws Exception
	 */
	public static String isAction(Context context,String userid,String equipId) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "equipId", equipId, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_IsAction, params);
		return s;
	}
	/**
	 * 查询设备是否扫描激活接口
	 * @param context
	 * @param userid
	 * @param equipCode
	 * @return
	 * @throws Exception
	 */
	public static String queryIsAction(Context context,String userid,String equipCode) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "userid", userid, true);
		putParams(params, "equipCode", equipCode, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_queryIsAction, params);
		return s;
	}
	/**
	 * 登录
	 * @param context
	 * @param mobile
	 * @param mcode
	 * @return
	 * @throws Exception
	 */
	public static String login(Context context,String mobile,String mcode) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "mobile", mobile, true);
		putParams(params, "mcode", mcode, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_registUser, params);
		return s;
	}
	/**
	 * 获取验证码
	 * @param context
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	public static String getCode(Context context,String mobile) throws Exception{
		Map<String,String> params = new LinkedHashMap<String, String>();
		putParams(params, "mobile", mobile, true);
		String s = HttpUtil.postUrl(context, ConfigSbbx.phone_getmcode, params);
		return s;
	}
	/** 
     * Get image from newwork 
     * @param path The path of image 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] getImage(String path) throws Exception{  
    	if(path == null || !path.contains(".")){
    		return null;
    	}
    	mLog.d("http", "path:"+path);
    	byte[] by = null;
        /*try {*/
			URL url = new URL(path);  
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
			conn.setRequestMethod("GET");  
			conn.setConnectTimeout(30000);  
			conn.setReadTimeout(30000);
			InputStream inStream = conn.getInputStream();  
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){  
				by = readStream(inStream);  
			}  
			inStream.close();
			conn.disconnect();
		/*} catch (Exception e) {
			e.printStackTrace();
		}*/
        return by;    
    } 
    /** 
     * Get data from stream 
     * @param inStream 
     * @return byte[] 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        inStream.close();  
        return outStream.toByteArray();  
    }
	
	
	
	

	private static void putParams(Map<String,String> params,String key,String obj,boolean must) throws Exception{
		if (HlpUtils.isEmpty(obj)){
			if (must){
				//throw new Exception("请求失败，信息不完整");
			}
		}else{
			params.put(key, obj);
		}
	}
	
	
	
}
