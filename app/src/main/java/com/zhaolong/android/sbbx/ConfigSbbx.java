package com.zhaolong.android.sbbx;

public class ConfigSbbx {

	public static final int serverPort = 8888;// 用于接口调用端口，而不是普通文件（例如图片）的访问接口
	
	private static final String SETTING = "http://121.40.126.229:8082/sbbx";//测试环境
	
	public static final String phone_getmcode = SETTING + "/phone_getmcode.action";//获取验证码
	public static final String phone_registUser = SETTING + "/phone_registUser.action";//登录
	public static final String phone_queryIsAction = SETTING + "/phone_queryIsAction.action";//查询设备是否扫描激活接口
	public static final String phone_IsAction = SETTING + "/phone_IsAction.action";//激活
	public static final String phone_homeImg = SETTING + "/phone_homeImg.action";//首页图
	public static final String phone_uploadUserImg = SETTING + "/phone_uploadUserImg.action";//上传图片
	public static final String phone_addRepair = SETTING + "/phone_addRepair.action";//医生端报修
	public static final String phone_queryHospitaldoc = SETTING + "/phone_queryHospitaldoc.action";//医院列表
	public static final String phone_queryEngineerEquip = SETTING + "/phone_queryEngineerEquip.action";//查询工程师设备
	public static final String phone_queryMedicalEquip = SETTING + "/phone_queryMedicalEquip.action";//查询医生端设备
	public static final String phone_queryUserInfo = SETTING + "/phone_queryUserInfo.action";//用户信息
	public static final String phone_queryMedicalRepairOrder = SETTING + "/phone_queryMedicalRepairOrder.action";//医生端我的订单
	public static final String phone_queryEngineerRepairOrder = SETTING + "/phone_queryEngineerRepairOrder.action";//工程师端我的订单
	public static final String phone_refuseRepairOrder = SETTING + "/phone_refuseRepairOrder.action";//工程师订单拒绝
	public static final String phone_acceptRepairOrder = SETTING + "/phone_acceptRepairOrder.action";//工程师订单接受
	public static final String phone_signRepairOrder = SETTING + "/phone_signRepairOrder.action";//工程师签到
	public static final String phone_queryJudgeOrder = SETTING + "/phone_queryJudgeOrder.action";//医生端评价查询
	public static final String phone_FinishOrder = SETTING + "/phone_FinishOrder.action";//维修完成
	public static final String phone_cancelOrder = SETTING + "/phone_cancelOrder.action";//医生端取消订单
	public static final String phone_queryMeetinfo = SETTING + "/phone_queryMeetinfo.action";//工程师培训会议查询
	public static final String phone_querydepart = SETTING + "/phone_querydepart.action";//科室列表查询
	public static final String phone_queryTagByOrder = SETTING + "/phone_queryTagByOrder.action";//评价标签查询
	public static final String phone_addJudgeOrder = SETTING + "/phone_addJudgeOrder.action";//医生端评价
	public static final String phone_meetSubmit = SETTING + "/phone_meetSubmit.action";//会议报名
	public static final String phone_queryMyOrder = SETTING + "/phone_queryMyOrder.action";//我的订单查询接口
	public static final String phone_queryCompany = SETTING + "/phone_queryCompany.action";//我的资料医院/企业查询接口
	public static final String phone_myInfo = SETTING + "/phone_myInfo.action";//我的资料修改接口
	public static final String phone_getMyNotice = SETTING + "/phone_getMyNotice.action";//我的消息查询接口
	public static final String phone_updateMyNotice = SETTING + "/phone_updateMyNotice.action";//更新消息接口
	public static final String phone_shortcutRepair = SETTING + "/phone_shortcutRepair.action";//工程师快捷报修查询接口
	public static final String phone_equiphistory = SETTING + "/phone_equiphistory.action";//设备维修历史记录
	public static final String phone_medicalIndex = SETTING + "/phone_medicalIndex.action";//医生业务报表
	public static final String phone_engineerIndex = SETTING + "/phone_engineerIndex.action";//工程师业务报表
}
