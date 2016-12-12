package com.zhaolong.android.sbbx.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.zhaolong.android.sbbx.MainActivity;
import com.zhaolong.android.sbbx.MyApplication;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.beans.Message;
import com.zhaolong.android.sbbx.ui.JudgeActivty;
import com.zhaolong.android.sbbx.ui.OrderMyActivity;
import com.zhaolong.android.sbbx.utils.mLog;

public class MessageReceiver extends XGPushBaseReceiver {
  public static final String MsgBroadCastAction = "com.s66.weiche.receiver.MsgBroadCastAction";
  public static final String LogTag = "MessageReceiver";
  private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");

  @SuppressWarnings("unused")
  private void show(Context context, String text) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
  }

  // 通知展示
  @Override
  public void onNotifactionShowedResult(Context context,
      XGPushShowedResult notifiShowedRlt) {
    if (context == null || notifiShowedRlt == null) {
      return;
    }
    //XGNotification notific = new XGNotification();
    //notific.setMsg_id(notifiShowedRlt.getMsgId());
    //notific.setTitle(notifiShowedRlt.getTitle());
    //notific.setContent(notifiShowedRlt.getContent());
    // notificationActionType==1为Activity，2为url，3为intent
    //		notific.setNotificationActionType(notifiShowedRlt.getNotificationActionType());
    //notific.setNotificationActionType(1);
    // Activity,url,intent都可以通过getActivity()获得
    //		notific.setActivity("com.s66.weiche.ui.OrderCenterActivity");//notifiShowedRlt.getActivity());
    //notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    //		.format(Calendar.getInstance().getTime()));
    //		NotificationService.getInstance(context).save(notific);
    //		context.sendBroadcast(intent);
    //		show(context, "有1条新订单, " + "请抢单 ， " + notifiShowedRlt.toString());
  }

  @Override
  public void onUnregisterResult(Context context, int errorCode) {
    if (context == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = "反注册成功";
    } else {
      text = "反注册失败" + errorCode;
    }
    mLog.d(LogTag, text);
    //		show(context, text);

  }

  @Override
  public void onSetTagResult(Context context, int errorCode, String tagName) {
    if (context == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = "\"" + tagName + "\"设置成功";
    } else {
      text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
    }
    mLog.d(LogTag, text);
    //		show(context, text);

  }

  @Override
  public void onDeleteTagResult(Context context, int errorCode, String tagName) {
    if (context == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = "\"" + tagName + "\"删除成功";
    } else {
      text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
    }
    mLog.d(LogTag, text);
    //		show(context, text);

  }

  // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
  @Override
  public void onNotifactionClickedResult(Context context,
      XGPushClickedResult message) {
    if (context == null || message == null) {
      return;
    }
    String text = "";
    if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
      // 通知在通知栏被点击啦。。。。。
      // APP自己处理点击的相关动作
      // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
      text = "通知被打开 :" + message;
      //			Intent it = new Intent(context,OrderCenterActivity.class);
      //			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //			context.startActivity(it);
    } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
      // 通知被清除啦。。。。
      // APP自己处理通知被清除后的相关动作
      text = "通知被清除 :" + message;
    }
    //		Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
    //				Toast.LENGTH_SHORT).show();
    //		// 获取自定义key-value
    //		String customContent = message.getCustomContent();
    //		if (customContent != null && customContent.length() != 0) {
    //			try {
    //				JSONObject obj = new JSONObject(customContent);
    //				// key1为前台配置的key
    //				if (!obj.isNull("key")) {
    //					String value = obj.getString("key");
    //					Log.d(LogTag, "get custom value:" + value);
    //				}
    //				// ...
    //			} catch (JSONException e) {
    //				e.printStackTrace();
    //			}
    //		}
    //		// APP自主处理的过程。。。
    //		Log.d(LogTag, text);
    //		show(context, text);
  }

  @Override
  public void onRegisterResult(Context context, int errorCode,
      XGPushRegisterResult message) {
    // TODO Auto-generated method stub
    if (context == null || message == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = message + "注册成功";
      // 在这里拿token
      String token = message.getToken();
      mLog.d(LogTag, "token:" + token);
      //			HlpUtils.showToast(context, text);
    } else {
      text = message + "注册失败，错误码：" + errorCode;
      //			SpData sp = new SpData(context);
      ////			HlpUtils.showToastLong(context, text);
      //			String account = sp.getStringValue(SpData.keyMobile,null);
      //			if (!HlpUtils.isEmpty(account)){
      //				MyApplication.getInstance().startPushService(account);
      //			}
    }
    mLog.d(LogTag, text);
    //		show(context, text);
  }

  // 消息透传
  @Override
  public void onTextMessage(Context context, XGPushTextMessage message) {
    // TODO Auto-generated method stub
    String text = "收到消息:" + message.toString();
    mLog.d("http", text);
    //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    //		HlpUtils.showToastLong(context, text);
    // 获取自定义key-value
    String customContent = message.getCustomContent();
    if (customContent != null && customContent.length() != 0) {
      //			try {
      //				MsgInfo mi = JSON.parseObject(customContent,MsgInfo.class);
      //				if (HlpUtils.isEmpty(mi.getContent())){
      //					mi.setContent(message.getContent());
      //				}
      //				if (HlpUtils.isEmpty(mi.getTitle())){
      //					mi.setTitle(message.getTitle());
      //				}
      //				Intent it = new Intent(MsgBroadCastAction);
      //				it.putExtra("msgInfo", mi);
      //				context.sendBroadcast(it);
      //				if (MsgInfo.MsgTypeAPP_ALL_NOTICE.equalsIgnoreCase(mi.getWcMsgType())){
      //					DatabaseConn db = new DatabaseConn(context);
      //					db.insertMsgInfo(mi);
      //				}
      //				ApplicationTool at = ApplicationTool.getInstance();
      //				boolean needNotify = true;
      //				if (at != null){
      //					Activity act = at.activitis.get(0);
      //					if (MsgInfo.MsgTypeGRAP_SUCCESS_NOTIFY.equals(mi.getWcMsgType())){
      //						if (!(act instanceof OrderingActivity) ){
      //							Intent newAct = new Intent(context,OrderingActivity.class);
      //							newAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //							context.startActivity(newAct);
      //							needNotify = false;
      //						}
      //						needNotify = false;
      //					}else if (MsgInfo.MsgTypeOWNER_ARRAY_TOPAY.equals(mi.getWcMsgType())){
      //						if (!(act instanceof OrderingActivity)){
      //							Intent newAct = new Intent(context,PayActivity.class);
      //							OrderInfo orderInfo = new OrderInfo();
      //							orderInfo.setId(mi.getOrderid());
      //							newAct.putExtra("orderInfo", orderInfo);
      //							newAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //							context.startActivity(newAct);
      //						}
      //						needNotify = false;
      //					}
      //				}
      //				if (needNotify){
      addNotification(context, message.getTitle(), message.getContent(),
          message.getCustomContent());
      //				}
      //			} catch (Exception e) {
      //				e.printStackTrace();
      //			}

    }
    //		// APP自主处理消息的过程...
    mLog.d(LogTag, text);
    //		show(context, text);
  }

  @SuppressWarnings("deprecation")
  public void addNotification(Context c, String title, String content, String msg) {
    mLog.d(LogTag, "title:" + title + "content:" + content + "msg:" + msg);

    NotificationManager manager =
        (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification;

    //notification.icon = R.drawable.app_logo;
    //notification.tickerText = title;
    //notification.defaults = Notification.DEFAULT_SOUND;
    //notification.flags = Notification.FLAG_AUTO_CANCEL;
    //notification.audioStreamType = android.media.AudioManager.VIBRATE_TYPE_NOTIFICATION;
    Intent intent = new Intent();
    if (MyApplication.getInstance().activitis.size() == 0) {
      intent = new Intent(c, MainActivity.class);
    } else {
      Message message = JSON.parseObject(msg, Message.class);
      String optye = message.getOpType();
      //医生端订单
      if ("OPEN_MEDICAL_WAIT_ACCEPT".equals(optye) //待接单
          || "OPEN_MEDICAL_Y_ACCEPT".equals(optye) //已接单
          || "OPEN_MEDICAL_Y_REFUSE".equals(optye)//已拒绝
          || "OPEN_MEDICAL_CANCEL".equals(optye)//已取消
          ) {
        intent = new Intent(MyApplication.getInstance().activitis.size() > 0
            ? MyApplication.getInstance().activitis.get(
            MyApplication.getInstance().activitis.size() - 1)
            : c, OrderMyActivity.class)
            .putExtra("type", 0);
      }
      //工程师端订单
      else if ("OPEN_ENGINEER_WAIT_ACCEPT".equals(optye) //待接单
          || "OPEN_ENGINEER_Y_ACCEPT".equals(optye)//已接单
          || "OPEN_ENGINEER_REFUSE".equals(optye)//已拒绝
          || "OPEN_ENGINEER_CANCEL".equals(optye)//已取消
          ) {
        intent = new Intent(MyApplication.getInstance().activitis.size() > 0
            ? MyApplication.getInstance().activitis.get(
            MyApplication.getInstance().activitis.size() - 1)
            : c, OrderMyActivity.class)
            .putExtra("type", 1);
      }
      //医生端评价
      else if ("OPEN_MEDICAL_N_EVALUATE".equals(optye) //未评价
          || "OPEN_MEDICAL_Y_EVALUATE".equals(optye)//已评价
          ) {
        intent = new Intent(MyApplication.getInstance().activitis.size() > 0
            ? MyApplication.getInstance().activitis.get(
            MyApplication.getInstance().activitis.size() - 1)
            : c, JudgeActivty.class)
            .putExtra("userclass", 0);
      }
      //工程师端评价
      else if ("OPEN_ENGINEER_N_EVALUATE".equals(optye) //未评价
          || "OPEN_ENGINEER_Y_EVALUATE".equals(optye)//已评价
          ) {
        intent = new Intent(MyApplication.getInstance().activitis.size() > 0
            ? MyApplication.getInstance().activitis.get(
            MyApplication.getInstance().activitis.size() - 1)
            : c, JudgeActivty.class)
            .putExtra("userclass", 1);
      }
    }
    PendingIntent pendingIntent =
        PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    //notification.setLatestEventInfo(c, title, content, pendingIntent);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
      notification = new NotificationCompat.Builder(c)
          .setSmallIcon(R.drawable.app_logo)//必须要先setSmallIcon，否则会显示默认的通知，不显示自定义通知
          //.setTicker(title)
          .setContentTitle(title)
          .setDefaults(Notification.DEFAULT_SOUND)
          .setContentText(content)
          .setContentIntent(pendingIntent)
          .build();
    } else {
      notification = new Notification.Builder(c)
          .setSmallIcon(R.drawable.app_logo)//必须要先setSmallIcon，否则会显示默认的通知，不显示自定义通知
          //.setTicker(title)
          .setContentTitle(title)
          .setContentText(content)
          .setDefaults(Notification.DEFAULT_SOUND)
          //.setContentText(msg)
          .setContentIntent(pendingIntent)
          .build();
    }
    manager.notify(0, notification);
    //		notifyId++;

  }
  //	int notifyId = 1;
}
