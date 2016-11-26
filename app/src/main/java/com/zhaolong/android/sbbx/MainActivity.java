package com.zhaolong.android.sbbx;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.ui.DoctorMinaActivity;
import com.zhaolong.android.sbbx.ui.EngineerMinaActivity;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class MainActivity extends Activity {
	
	private Button btnGetCode;
	private EditText etMobile;
	private EditText etCode;
	private RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyApplication.getInstance().activitis.add(this);//把当前Activity放入集合中
		
		layout = (RelativeLayout) findViewById(R.id.layout_autologin);
		etMobile = (EditText) findViewById(R.id.editText_main_phone);
		btnGetCode = (Button) findViewById(R.id.button_main_getcode);
		etCode = (EditText) findViewById(R.id.editText_main_code);
		
		SpData sp = new SpData(getApplicationContext());
		String mobile = sp.getStringValue(SpData.keyPhone, null);
		String code = sp.getStringValue(SpData.keyCode, null);
		if(!HlpUtils.isEmpty(mobile) && !HlpUtils.isEmpty(code)){
			etMobile.setText(mobile);
			etCode.setText(code);
			layout.setVisibility(View.VISIBLE);
			login(mobile,code,true);
		}
		
		//获取验证码
		btnGetCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(ifCanGetCode){
					
					String mobile = etMobile.getText().toString().trim();
					if(HlpUtils.checkPhone(mobile)){
						getCode(mobile);
					}else{
						Toast.makeText(getApplicationContext(), "请输入正确的11位手机号码", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		//登录
		findViewById(R.id.button_main_login).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String mobile = etMobile.getText().toString().trim();
				String code = etCode.getText().toString().trim();
				if(!HlpUtils.checkPhone(mobile)){
					Toast.makeText(getApplicationContext(), "请输入正确的11位手机号码", Toast.LENGTH_SHORT).show();
				}
				else if(HlpUtils.isEmpty(mobile)){
					Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
				}
				else{
					login(mobile,code,false);
				}
			}
		});
	}
	
	/**
	 * 登录
	 * @param mobile
	 * @param code
	 */
	private void login(final String mobile,final String code,final boolean isAuto){
		if(!isAuto){
			reload("正在努力加载……");
		}
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					if(isAuto){
						Thread.sleep(3000);
					}
					String s = DataService.login(MainActivity.this, mobile, code);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								MyApplication.getInstance().startPushService(mobile);
								HttpResult data = JSON.parseObject(hr.getData().toString(),HttpResult.class);
								SpData sp = new SpData(getApplicationContext());
								sp.setStringValue(SpData.keyPhone, mobile);
								sp.setStringValue(SpData.keyCode, code);
								sp.setStringValue(SpData.keyId, data.getId());
								switch (data.getUsertype()) {
								case 0:
									startActivity(new Intent(MainActivity.this, DoctorMinaActivity.class));
									finish();
									break;

								case 1:
									startActivity(new Intent(MainActivity.this, EngineerMinaActivity.class));
									finish();
									break;
								}
							}else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										if(isAuto){
											layout.setVisibility(View.GONE);
										}
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() { 
								if(isAuto){
									layout.setVisibility(View.GONE);
								}
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (final Exception he) {
					he.printStackTrace();
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							if(isAuto){
								layout.setVisibility(View.GONE);
							}
							Toast.makeText(getApplicationContext(), he.getMessage(), Toast.LENGTH_SHORT).show();
						}  
					});
				}
				closeLoadingDialog();
			}
		}).start();
	}
	
	/**
	 * 获取后台图片信息
	 */
	private void getCode(final String mobile){
		if(isGetCodeING){
			return;
		}
		isGetCodeING = true;
		reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.getCode(MainActivity.this, mobile);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
									runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											readSms();
											Toast.makeText(getApplicationContext(), "获取验证码成功！", Toast.LENGTH_SHORT).show();
											getCodeUIThread();
										}  
									});
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
				isGetCodeING = false;
			}
		}).start();
	}
	boolean isGetCodeING = false;
	
	/**
	 * 倒计时60秒，60秒内不允许获取验证码,按钮上显示倒计时
	 */
	private void getCodeUIThread(){
		new Thread() {  
            public void run() {  
                while(!isDestroy && --time>0){
                	
                	ifCanGetCode = false;
                	
                	runOnUiThread(new Runnable(){  
                        @Override  
                        public void run() {  
                        	btnGetCode.setText(""+time);  
                        }  
                    });
                	
                	try {
                		Thread.sleep(1000);
                	} catch (InterruptedException e) {
                		e.printStackTrace();
                	}
                }
                
                ifCanGetCode = true;
            	time = 60;
            	
            	if(!isDestroy)
            	{
            		runOnUiThread(new Runnable(){  
            			@Override  
            			public void run() {  
            				btnGetCode.setText("获取验证码");
            			}  
            		});
            	}
                
            }  
        }.start();
	}
	private boolean ifCanGetCode = true;
	int time = 60;
	
	private void reload(String text){
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
	
	protected void readSms() {
		sendDate = new Date();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i=0;
				while (i<600 && !isDestroy){
					boolean ret = getSmsFromPhone();
					if (ret){
						break;
					}else{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					i++;
				}
			}
		}).start();
	}
	private Uri SMS_INBOX = Uri.parse("content://sms/");
	Date sendDate = null;
	private boolean getSmsFromPhone() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { "body" ,"_id", "address", "person", "date", "type"};//
		String where = " body like '%易报修%' ";
		Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
		if (null == cur)
			return false;
		if (cur.moveToNext()) {
//			String number = cur.getString(cur.getColumnIndex("address"));//手机号
			long dateStr = cur.getLong(cur.getColumnIndex("date"));
			Date date = new Date(dateStr);
			if (date.getTime()>sendDate.getTime()){
				String body = cur.getString(cur.getColumnIndex("body"));
				//这里我是要获取自己短信服务号码中的验证码~~
				Pattern pattern = Pattern.compile("[0-9]{4}");
				final Matcher matcher = pattern.matcher(body);
				if (matcher.find() && !isDestroy){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							 if ( etCode.getText().toString().trim().length() == 0) {
								String res = matcher.group();//.substring(1, 5);
								etCode.setText(res);
							 }
						}
					});
					return true;
				}
			}
			
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		isDestroy = false;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
		MyApplication.getInstance().activitis.remove(this);
	}
	boolean isDestroy = false;

}
