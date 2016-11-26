package com.zhaolong.android.sbbx.windows;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaolong.android.sbbx.R;

public class LoadingDialog extends Dialog {

	private Context context;
    private TextView loadingTips = null;
	
	public LoadingDialog(Context context) {
		super(context);
	}
	
	public LoadingDialog(Context context,int theme){
		super(context,theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setCanceledOnTouchOutside(false);
		LayoutInflater mLayoutInflater = LayoutInflater.from(context);
		View reNameView = mLayoutInflater.inflate(R.layout.dialog_loading, null); 
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.setContentView(reNameView,params);
		
		ImageView loading = (ImageView)findViewById(R.id.loading);
		loadingTips = (TextView)findViewById(R.id.loading_tips);
		
	    Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);  
	    LinearInterpolator lin = new LinearInterpolator();  
	    operatingAnim.setInterpolator(lin);  
	    loading.startAnimation(operatingAnim);
	}
	
	
	public void setLoadingTips(int tips) {
		if(null != loadingTips) {
			loadingTips.setText(context.getString(tips));
		}
	}
	
	/**
	 * 加载框
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context) {
		LoadingDialog loadingDialog = new LoadingDialog(context,R.style.MyLoading);
		Window window = loadingDialog.getWindow();
		window.setGravity(Gravity.CENTER);
		//window.setWindowAnimations(R.style.mystyle);
		return loadingDialog;
	}

}
