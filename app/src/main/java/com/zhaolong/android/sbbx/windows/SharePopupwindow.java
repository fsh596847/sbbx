package com.zhaolong.android.sbbx.windows;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.zhaolong.android.sbbx.R;

public class SharePopupwindow extends PopupWindow {

	private CallBack callBack;
	
	
	public SharePopupwindow(Activity context){
		View customView = context.getLayoutInflater().inflate(R.layout.popupwindow_share, null, false); 
		setContentView(customView);
		
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		//setFocusable(true);
		//setOutsideTouchable(true);
		//setAnimationStyle(R.style.AnimationShare);  
		
		initListeners(customView);
	}
	
	public void setCallBack(CallBack callBack){
		this.callBack = callBack;
	}

	private void initListeners(View v) {
		v.findViewById(R.id.btn_popup_share).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
				if(callBack != null){
					callBack.dismiss();
				}
			}
		});
		
		v.findViewById(R.id.btn_popup_friend).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
				if(callBack != null){
					callBack.friend();
				}
			}
		});
		
		v.findViewById(R.id.btn_popup_group).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
				if(callBack != null){
					callBack.group();
				}
			}
		});
		
	}
	
	public interface CallBack {  
	    void dismiss(); 
	    void friend();
	    void group();
	}
	
}
