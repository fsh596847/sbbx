package com.zhaolong.android.sbbx.windows;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhaolong.android.sbbx.R;

@SuppressLint("ViewConstructor")
public class HintPopupWindow extends PopupWindow {
	
	private TextView tvTitle;
	private TextView tvCancel;
	private TextView tvOk;
	
	private CallBack callBack;
	
	public HintPopupWindow(Activity context,String titleText,String cancelText,String okText){
		
		View customView = context.getLayoutInflater().inflate(R.layout.pop_hint, null, false); 
		initViews(customView);
		initListeners(customView);
		tvTitle.setText(titleText);
		tvCancel.setText(cancelText);
		tvOk.setText(okText);
		setContentView(customView);
		
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		//setAnimationStyle(R.style.AnimationFade);  
		
	}
	
	private void initListeners(View customView) {
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
				if(callBack != null){
					callBack.cancel();
				}
			}
		});
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dismiss();
				if(callBack != null){
					callBack.ok();
				}
			}
		});
		
	}
	
	
	private void initViews(View customView) {
		
		tvTitle = (TextView) customView.findViewById(R.id.tv_pop_hint_title);
		tvCancel = (TextView) customView.findViewById(R.id.btn_pop_hint_cancel);
		tvOk = (TextView) customView.findViewById(R.id.btn_pop_hint_ok);
		
	}
	
	
	public void setCallBack(CallBack callBack){
		this.callBack = callBack;
	}
	
	public interface CallBack {  
	    void cancel(); 
	    void ok();
	} 
	

}
