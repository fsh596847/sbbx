package com.zhaolong.android.sbbx.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class WebActivity extends Activity {
	
	TextView tvTitle;
	WebView controlsView;
	ProgressBar bar;

	private String url;
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		
		url = getIntent().getStringExtra("url");
		String title = getIntent().getStringExtra("title");
		mLog.d("http", "url:"+url);
		
		bar = (ProgressBar) findViewById(R.id.myProgressBar_webView);
		controlsView = (WebView) findViewById(R.id.webView_web);
		tvTitle = (TextView) findViewById(R.id.tv_web_title);
		tvTitle.setText(title);
		

		// 设置setWebChromeClient对象
		controlsView.setWebChromeClient(wvcc);

		WebSettings webSettings = controlsView.getSettings();
		//设置编码
		webSettings.setDefaultTextEncodingName("utf-8"); 
		// 支持javascript
		webSettings.setJavaScriptEnabled(true);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(true);
		// 扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		// 去掉縮放工具 api最低版本11
		// webSettings.setDisplayZoomControls(false);
		// 设置控件属性，网页大小适应屏幕
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		controlsView.addJavascriptInterface(WebActivity.this, "sbbx");
		
		//loadUrl 
		controlsView.loadUrl(url);
		
		//如果页面中链接，如果希望点击链接继续在当前browser中响应，
		
		//而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象
		
		controlsView.setWebViewClient(new android.webkit.WebViewClient(){
			
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			
			{
				
				view.loadUrl(url);
				
				return true;
				
			}
			
			
		});
		
		findViewById(R.id.iv_web_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	WebChromeClient wvcc = new WebChromeClient() {
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				bar.setVisibility(View.GONE);
			} else {
				if (View.GONE == bar.getVisibility()) {
					bar.setVisibility(View.VISIBLE);
				}
				bar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	};
	
	@JavascriptInterface
	public void meetSubmit(final String meetid){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.meetSubmit(WebActivity.this
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, meetid);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								setResult(100);
								finish();
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (final Exception he) {
					he.printStackTrace();
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getApplicationContext(), he.getMessage(), Toast.LENGTH_SHORT).show();
						}  
					});
				}
				closeLoadingDialog();
			}
		}).start();
	}
	
	private void reload(){
		if (loadingDialog == null){
			loadingDialog = LoadingDialog.createLoadingDialog(this);
		}
		if (loadingDialog != null && !loadingDialog.isShowing()){
			loadingDialog.show();
		}
	}
	
	private Dialog loadingDialog = null;
	private void closeLoadingDialog() {
		if(null != loadingDialog) {	 
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		controlsView.clearCache(true);
		controlsView.destroy();
		controlsView = null;
	}
	
	@Override
	public void finish() {
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}
	
}
