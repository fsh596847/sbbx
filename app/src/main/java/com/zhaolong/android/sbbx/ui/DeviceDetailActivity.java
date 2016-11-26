package com.zhaolong.android.sbbx.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.Device;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.HintPopupWindow;
import com.zhaolong.android.sbbx.windows.HintPopupWindow.CallBack;
import com.zhaolong.android.sbbx.windows.LoadingDialog;
import com.zxing.scan.CaptureActivity;

public class DeviceDetailActivity extends Activity {
	
	private String code;
	private Device device;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activate);
		initViews();
		device = (Device) getIntent().getSerializableExtra("device");
		if(device != null){
			HlpUtils.setText(tvEquipCode, device.getEquipCode());
			HlpUtils.setText(tvEquipSeq, device.getEquipSeq());
			HlpUtils.setText(tvEquipClass, device.getEquipClass());
			HlpUtils.setText(tvEquipType, device.getEquipType());
			HlpUtils.setText(tvEquipName, device.getEquipName());
			HlpUtils.setText(tvHospital, device.getHospital());
			HlpUtils.setText(tvDepar, device.getDepar());
			HlpUtils.setText(tvEquipAddress, device.getEquipAddress());
			HlpUtils.setText(tvUseState, device.getUseState()==0?"使用中":"停用");
			HlpUtils.setText(tvOperCyc, device.getOperCyc());
			HlpUtils.setText(tvEngineerName, device.getEngineerName());
			HlpUtils.setText(tvOperFirm, device.getOperFirm());
			HlpUtils.setText(tvSaleFirm, device.getSaleFirm());
			HlpUtils.setText(tvOutFactoryDate, device.getOutFactoryDate());
			HlpUtils.setText(tvIntDate, device.getIntDate());
			btnActivate.setText("返回");
			btnActivate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			if(device.getImg() != null){
				reload();
				new Thread(new Runnable() {
					@Override
					public void run() {

						getImg(device.getImg());
						closeLoadingDialog();
					}
				}).start();
			}
		}
		else{
			code = getIntent().getStringExtra("code");//"1dafadf12wqewqewqeeeeadfda";//getIntent().getStringExtra("code");
			String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
			queryIsAction(userid, code);
		}
	}

	private void initViews() {
		findViewById(R.id.iv_activate_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.tv_activate_order_record).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DeviceDetailActivity.this,RecordServiceActivty.class).putExtra("equipment", device.getId()));
			}
		});
		ivImg = (ImageView) findViewById(R.id.iv_activate);
		tvEquipCode = (TextView) findViewById(R.id.tv_activate_equipCode);
		tvEquipSeq = (TextView) findViewById(R.id.tv_activate_equipSeq);
		tvEquipClass = (TextView) findViewById(R.id.tv_activate_equipClass);
		tvEquipType = (TextView) findViewById(R.id.tv_activate_equipType);
		tvEquipName = (TextView) findViewById(R.id.tv_activate_equipName);
		tvHospital = (TextView) findViewById(R.id.tv_activate_hospital);
		tvDepar = (TextView) findViewById(R.id.tv_activate_depar);
		tvEquipAddress = (TextView) findViewById(R.id.tv_activate_equipAddress);
		tvUseState = (TextView) findViewById(R.id.tv_activate_useState);
		tvOperFirm = (TextView) findViewById(R.id.tv_activate_operFirm);
		tvSaleFirm = (TextView) findViewById(R.id.tv_activate_saleFirm);
		tvOutFactoryDate = (TextView) findViewById(R.id.tv_activate_outFactoryDate);
		tvIntDate = (TextView) findViewById(R.id.tv_activate_intDate);
		tvOperCyc = (TextView) findViewById(R.id.tv_activate_upUseCyc);
		tvEngineerName = (TextView) findViewById(R.id.tv_activate_engineerName);
		btnActivate = (Button) findViewById(R.id.btn_activate);
		viewParent = (RelativeLayout) findViewById(R.id.layout_activate);
	}
	private TextView tvEquipSeq,tvEquipCode,tvEquipClass,tvEquipType,tvEquipName,tvHospital,tvDepar,tvEquipAddress
	,tvUseState,tvOutFactoryDate,tvIntDate,tvOperCyc,tvEngineerName,tvOperFirm,tvSaleFirm;
	Button btnActivate;
	RelativeLayout viewParent;
	ImageView ivImg;

	private void getImg(String headImg){
		if(headImg != null){
			mLog.d("http", "headImg:"+headImg);
			try {
				byte[] data = DataService.getImage(headImg);
				if(data != null){
					final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() { 
							//设置图片
							ivImg.setImageBitmap(mBitmap);
						}  
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void queryIsAction(final String userid,final String equipCode){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryIsAction(DeviceDetailActivity.this, userid, equipCode);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								device = JSON.parseObject(hr.getData().toString(),Device.class);
								if(device.getImgurl() != null){
									getImg(device.getImgurl());
								}
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										HlpUtils.setText(tvEquipCode, device.getEquipCode());
										HlpUtils.setText(tvEquipSeq, device.getEquipSeq());
										HlpUtils.setText(tvEquipClass, device.getEquipClass());
										HlpUtils.setText(tvEquipType, device.getEquipType());
										HlpUtils.setText(tvEquipName, device.getEquipName());
										HlpUtils.setText(tvHospital, device.getHospital());
										HlpUtils.setText(tvDepar, device.getDepar());
										HlpUtils.setText(tvEquipAddress, device.getEquipAddress());
										HlpUtils.setText(tvUseState, device.getUseState()==0?"使用中":"停用");
										HlpUtils.setText(tvOperCyc, device.getOperCyc());
										HlpUtils.setText(tvEngineerName, device.getEngineerName());
										HlpUtils.setText(tvOperFirm, device.getOperFirm());
										HlpUtils.setText(tvSaleFirm, device.getSaleFirm());
										HlpUtils.setText(tvOutFactoryDate, device.getOutFactoryDate());
										HlpUtils.setText(tvIntDate, device.getIntDate());
										switch (device.getIsAction()) {
										case 0:
											btnActivate.setText("激活");
											btnActivate.setOnClickListener(new OnClickListener() {
												
												@Override
												public void onClick(View v) {
													isAction(userid, device.getId());
												}
											});
											break;

										case 1:
											btnActivate.setText("已激活");
											btnActivate.setBackgroundColor(getResources().getColor(R.color.color_gray_button_bg));
											break;
										}
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
					mLog.e("http", "Exception:"+he.getMessage());
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
	
	private void isAction(final String userid,final String equipId){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.isAction(DeviceDetailActivity.this, userid, equipId);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								if(hintPopupWindow == null){
									hintPopupWindow = new HintPopupWindow(DeviceDetailActivity.this,"激活成功！谢谢！","返回主页","继续激活");
									hintPopupWindow.setCallBack(new CallBack() {
										
										@Override
										public void ok() {
											startActivity(new Intent(DeviceDetailActivity.this, CaptureActivity.class)
											.putExtra("type", CaptureActivity.COM_ACTIVATE));
											finish();
										}
										
										@Override
										public void cancel() 
										{
											finish();
										}
									});
								}
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										hintPopupWindow.showAtLocation(viewParent, Gravity.CENTER, 0, 0);
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
					mLog.e("http", "Exception:"+he.getMessage());
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
	private HintPopupWindow hintPopupWindow;
	
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

}
