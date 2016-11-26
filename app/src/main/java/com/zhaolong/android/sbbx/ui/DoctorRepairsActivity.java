package com.zhaolong.android.sbbx.ui;

import java.util.Date;

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
import android.widget.EditText;
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
import com.zhaolong.android.sbbx.utils.ImgUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.utils.mToast;
import com.zhaolong.android.sbbx.windows.BirthdayChoosePopupWindow;
import com.zhaolong.android.sbbx.windows.BirthdayChoosePopupWindow.CallBackBirthdayChoose;
import com.zhaolong.android.sbbx.windows.HintPopupWindow;
import com.zhaolong.android.sbbx.windows.HintPopupWindow.CallBack;
import com.zhaolong.android.sbbx.windows.LoadingDialog;
import com.zxing.scan.CaptureActivity;

public class DoctorRepairsActivity extends Activity {
	
	private String code;
	private String[] imageUrl = new String[3];//影像资料
	private int imageIndex;
	
	private BirthdayChoosePopupWindow birthdayChoosePopupWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_repairs);
		
		code = getIntent().getStringExtra("code");//"1dafadf12wqewqewqeeeeadfda";//getIntent().getStringExtra("code");
		String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
		queryIsAction(userid, code);
		
		initViews();
	}

	private void initViews() {
		tvEquipSeq = (TextView) findViewById(R.id.tv_repairs_equipSeq);
		tvEquipBar = (TextView) findViewById(R.id.tv_repairs_equipBar);
		tvEquipName = (TextView) findViewById(R.id.tv_repairs_equipName);
		tvHospital = (TextView) findViewById(R.id.tv_repairs_hospital);
		tvDepar = (TextView) findViewById(R.id.tv_repairs_depar);
		tvEquipAddress = (TextView) findViewById(R.id.tv_repairs_equipAddress);
		tvMan = (TextView) findViewById(R.id.tv_repairs_man);
		tvTime = (TextView) findViewById(R.id.tv_repairs_time);
		btnActivate = (Button) findViewById(R.id.btn_repairs);
		viewParent = (RelativeLayout) findViewById(R.id.layout_repairs);
		etDescribe = (EditText) findViewById(R.id.et_repairs_describe);
		ivAdd1 = (ImageView) findViewById(R.id.iv_repairs_image1);
		ivAdd2 = (ImageView) findViewById(R.id.iv_repairs_image2);
		ivAdd3 = (ImageView) findViewById(R.id.iv_repairs_image3);
		findViewById(R.id.iv_repairs_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		birthdayChoosePopupWindow = new BirthdayChoosePopupWindow(this);
		birthdayChoosePopupWindow.setCallBackBirthdayChoose(new CallBackBirthdayChoose() {
			
			@Override
			public void select(int yearSelect, int monthSelect, int daySelect,int hh, int mm, int ss) {
				tvTime.setText(yearSelect + "-" + monthSelect + "-" + daySelect+" "
						+(hh<10?"0":"")+hh+":"+(mm<10?"0":"")+mm+":"+(ss<10?"0":"")+ss);
			}
			
			@Override
			public void dismiss() {
			}
		});
		findViewById(R.id.ll_repairs_time).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				birthdayChoosePopupWindow.showAtLocation(viewParent, Gravity.CENTER, 0, 0);
			}
		});
		//添加影像1
		ivAdd1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				getPhoto(400,400);
				imageIndex = 1;
			}
		});
		//添加影像2
		ivAdd2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				getPhoto(400,400);
				imageIndex = 2;
			}
		});
		//添加影像3
		ivAdd3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				getPhoto(400,400);
				imageIndex = 3;
			}
		});
		
	}
	TextView tvEquipSeq,tvEquipBar,tvEquipName,tvHospital,tvDepar,tvEquipAddress,tvMan,tvTime;
	Button btnActivate;
	RelativeLayout viewParent;
	EditText etDescribe;
	ImageView ivAdd1,ivAdd2,ivAdd3;
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULTCODE_PHOTO_SET:
			capturePicturePath = data.getStringExtra("capturePhotoPath");
			if(capturePicturePath != null){
				//capturePicturePath = capturePicturePath.replaceAll("file://", "");
				BitmapFactory.Options optionsCapture = new BitmapFactory.Options();
				//optionsCapture.inSampleSize = 2;
				Bitmap capturePhotoBitmap = BitmapFactory.decodeFile(capturePicturePath, optionsCapture);
				int degree = ImgUtils.readPictureDegree(capturePicturePath);
				capturePhotoBitmap = ImgUtils.rotaingImageView(degree, capturePhotoBitmap, 1, ivAdd1.getWidth());
				if (capturePhotoBitmap != null) {
					//设置图片
					setPhoto(capturePhotoBitmap);
				}
			}
			break;
		}
	}
	private static final int RESULTCODE_PHOTO_SET = 510;
	private String capturePicturePath;
	
	void getPhoto(int outputX, int outputY){
		startActivityForResult(new Intent(DoctorRepairsActivity.this, PhotoSetActivity.class)
		.putExtra("title", "图片").putExtra("outputX", outputX).putExtra("outputY", outputY), 
		RESULTCODE_PHOTO_SET);
	}

	void setPhoto(Bitmap capturePhotoBitmap){
		switch (imageIndex) {
		case 1:
			ivAdd1.setImageBitmap(capturePhotoBitmap);
			ivAdd2.setVisibility(View.VISIBLE);
			imageUrl[0] = capturePicturePath;
			break;

		case 2:
			ivAdd2.setImageBitmap(capturePhotoBitmap);
			ivAdd3.setVisibility(View.VISIBLE);
			imageUrl[1] = capturePicturePath;
			break;

		case 3:
			ivAdd3.setImageBitmap(capturePhotoBitmap);
			imageUrl[2] = capturePicturePath;
			break;
		}
	}
	
	
	private void addRepair(){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {
					if(imageUrl[0] != null){
						if(uploadAttestationImg(1, ImgUtils.getImageToBase64Str(imageUrl[0]))){

							if(imageUrl[1] != null){
								if(uploadAttestationImg(2, ImgUtils.getImageToBase64Str(imageUrl[1]))){

									if(imageUrl[2] != null){
										if(uploadAttestationImg(3, ImgUtils.getImageToBase64Str(imageUrl[2]))){
											addRepairNetwork(tvTime.getText().toString(), etDescribe.getText().toString());
										}else{
										}
									}else{//没有影像资料3
										addRepairNetwork(tvTime.getText().toString(), etDescribe.getText().toString());
									}

								}else{
								}
							}else{//没有影像资料2
								addRepairNetwork(tvTime.getText().toString(), etDescribe.getText().toString());
							}

						}else{
						}
					}else{//没有影像资料1
						addRepairNetwork(tvTime.getText().toString(), etDescribe.getText().toString());
					}
				/*}*/
					
					
				closeLoadingDialog();

			}
		}).start();
	}
	
	void addRepairNetwork(String time, String des){
		
		try {
			String s = DataService.addRepair(getApplicationContext(), new SpData(this).getStringValue(SpData.keyId, null)
					, device.getId(), device.getEquipCode(), device.getEquipName(), device.getEngineerId()
					, time, device.getEquipAddress(), des, patientimg[0], patientimg[1], patientimg[2],device.getEquipType());
			mLog.d("http", "s："+s);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						if(hintPopupWindow == null){
							hintPopupWindow = new HintPopupWindow(DoctorRepairsActivity.this,"报修成功！谢谢！","返回主页","继续报修");
							hintPopupWindow.setCallBack(new CallBack() {
								
								@Override
								public void ok() {
									startActivity(new Intent(DoctorRepairsActivity.this, CaptureActivity.class)
									.putExtra("type", CaptureActivity.COM_REPAIRS));
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
								mToast.showToast(getApplicationContext(), hr.getData().toString());
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							mToast.showToast(getApplicationContext(), "转诊失败，请重试");
						}  
					});
				}
			}else{
			}
			
		} catch (final Exception e) {
			e.printStackTrace();
			mLog.e("http", "Exception:"+e.getMessage());
			runOnUiThread(new Runnable(){  
				@Override  
				public void run() {  
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}  
			});
		}
	}
	private HintPopupWindow hintPopupWindow;
	
	private boolean uploadAttestationImg(final int index, final String bas64){
		boolean re = false;
		try {
			String s = DataService.uploadUserImg(getApplicationContext(), 
					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), bas64);
			if (!isDestroy && !HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						String imgName = hr.getFilename();
						if(imgName != null){
							mLog.d("http", "imgName :  "+imgName);
							this.patientimg[index-1] = imgName;
							re = true;
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}else{
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							mToast.showToast(getApplicationContext(), "图片上次失败，请重试");
						}  
					});
				}
			}else{
				runOnUiThread(new Runnable(){  
					@Override  
					public void run() {  
						Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
					}  
				});
			}
		}catch (final Exception e) {
			e.printStackTrace();
			mLog.e("http", "Exception:"+e.getMessage());
			runOnUiThread(new Runnable(){  
				@Override  
				public void run() {  
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}  
			});
		}
		return re;
	}
	String[] patientimg = new String[3];
	
	private void queryIsAction(final String userid,final String equipCode){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = //HttpUtil.getUrl(getApplicationContext(), ConfigSbbx.phone_queryIsAction+"?userid="+userid+"&equipCode="+equipCode);
					DataService.queryIsAction(DoctorRepairsActivity.this, userid, equipCode);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								device = JSON.parseObject(hr.getData().toString(),Device.class);
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										HlpUtils.setText(tvEquipSeq, device.getEquipCode());
										HlpUtils.setText(tvEquipBar, device.getEquipType());
										HlpUtils.setText(tvEquipName, device.getEquipName());
										HlpUtils.setText(tvHospital, device.getHospital());
										HlpUtils.setText(tvDepar, device.getDepar());
										HlpUtils.setText(tvEquipAddress, device.getEquipAddress());
										HlpUtils.setText(tvMan, new SpData(getApplicationContext()).getStringValue(SpData.keyPhone, ""));
										HlpUtils.setText(tvTime, HlpUtils.datetimeFormatters.format(new Date()));
										switch (device.getIsAction()) {
										case 0:
											btnActivate.setText("未激活");
											btnActivate.setBackgroundColor(getResources().getColor(R.color.color_gray_button_bg));
											break;

										case 1:
											btnActivate.setText("报修");
											btnActivate.setOnClickListener(new OnClickListener() {
												
												@Override
												public void onClick(View v) {
													addRepair();
												}
											});
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
	Device device;
	
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
	
	boolean isDestroy = false;
	@Override
	public void onStart() {
		super.onStart();
		isDestroy = false;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}

}
