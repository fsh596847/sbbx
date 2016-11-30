package com.zhaolong.android.sbbx.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.zhaolong.android.sbbx.MyApplication;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.utils.mToast;
import com.zhaolong.android.sbbx.windows.SharePopupwindow;
import com.zhaolong.android.sbbx.windows.SharePopupwindow.CallBack;

public class MyShareActivity extends Activity {
	
	private SharePopupwindow popup;
	private LinearLayout viewParent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_share);
		
		viewParent = (LinearLayout) findViewById(R.id.layout_my_share);
		findViewById(R.id.iv_my_share_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.btn_my_share).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popup.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
			}
		});
		
		popup = new SharePopupwindow(this);
		popup.setOutsideTouchable(true);
		popup.setCallBack(new CallBack() {
			
			@Override
			public void group() {
				wxShare(1);
			}
			
			@Override
			public void friend() {
				wxShare(0);
			}
			
			@Override
			public void dismiss() {
			}
		});
	}
	
	/** 
	  * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码） 
	  * @param flag(0:分享到微信好友，1：分享到微信朋友圈) 
	  */  
	 private void wxShare(int flag){ 
		 if (!MyApplication.wxApi.isWXAppInstalled()) {
				//提醒用户没有按照微信
				mToast.showToast(this, "没有安装微信");
				return;
			}
	     WXWebpageObject webpage = new WXWebpageObject();  
	     webpage.webpageUrl = "https://hao.qq.com/?unc=o400493_1&s=o400493_1";  
	     WXMediaMessage msg = new WXMediaMessage(webpage);
		 msg.title = "易报修";
		 msg.description = "设备保修、管理不用愁！";
	     //这里替换一张自己工程里的图片资源  
	     Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);  
	     msg.setThumbImage(thumb);  
	       
	     SendMessageToWX.Req req = new SendMessageToWX.Req();  
	     req.transaction = String.valueOf(System.currentTimeMillis());  
	     req.message = msg;  
	     req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;  
	     MyApplication.wxApi.sendReq(req);
	 } 

}
