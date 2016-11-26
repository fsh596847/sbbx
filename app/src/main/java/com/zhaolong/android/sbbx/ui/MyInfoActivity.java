package com.zhaolong.android.sbbx.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.Company;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.beans.UserInfo;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.ImgUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.utils.mToast;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class MyInfoActivity extends Activity {

	private EditText tvName,tvPhone,tvEmail,tvAddrss;
	private TextView tvcompany,tvcompanyTitle;
	private ImageView ivHeadImg;
	private Button btnEdit;

	private int userclass;
	Company company;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);

		userclass = getIntent().getIntExtra("userclass", -1);
		if(userclass < 0 || userclass > 1){
			finish();
			return;
		}

		btnEdit = (Button) findViewById(R.id.btn_my_info_edit);
		ivHeadImg = (ImageView) findViewById(R.id.iv_my_info);
		tvName = (EditText) findViewById(R.id.et_my_info_name);
		tvPhone = (EditText) findViewById(R.id.et_my_info_phone);
		tvEmail = (EditText) findViewById(R.id.et_my_info_email);
		tvAddrss = (EditText) findViewById(R.id.et_my_info_addrs);
		tvcompany = (TextView) findViewById(R.id.tv_my_info_company);
		tvcompanyTitle = (TextView) findViewById(R.id.tv_my_info_company_title);
		switch (userclass) {
		case 0:
			tvcompanyTitle.setText("所属医院");
			break;

		case 1:
			tvcompanyTitle.setText("所属企业");
			break;
		}
		findViewById(R.id.iv_my_info_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ivHeadImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(btnEdit.getText().toString().equals("保存")){
					getPhoto(200, 200);
				}
			}
		});
		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(btnEdit.getText().toString().equals("保存")){
					updateMyInfo(capturePicturePath, tvName.getText().toString(), tvPhone.getText().toString()
							, tvEmail.getText().toString(), tvAddrss.getText().toString());
					
				}else{
					btnEdit.setText("保存");
					tvName.setEnabled(true);
					tvEmail.setEnabled(true);
					tvAddrss.setEnabled(true);
				}
			}
		});
		findViewById(R.id.layout_my_info_company).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(btnEdit.getText().toString().equals("保存")){
					startActivityForResult(new Intent(MyInfoActivity.this,CompanyChooseActivity.class)
					.putExtra("userclass", userclass), 100);
				}
			}
		});

		queryUserInfo();
	}

	void getPhoto(int outputX, int outputY){
		startActivityForResult(new Intent(MyInfoActivity.this, PhotoSetActivity.class)
		.putExtra("title", "头像").putExtra("outputX", outputX).putExtra("outputY", outputY), 
		RESULTCODE_PHOTO_SET);
	}

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
				capturePhotoBitmap = ImgUtils.rotaingImageView(degree, capturePhotoBitmap, 1, ivHeadImg.getWidth());
				if (capturePhotoBitmap != null) {
					//设置图片
					ivHeadImg.setImageBitmap(capturePhotoBitmap);
				}
			}
			break;

		case 100:
			company = (Company) data.getSerializableExtra("company");
			HlpUtils.setText(tvcompany, company.getCompany());
			break;
		}
	}
	private static final int RESULTCODE_PHOTO_SET = 510;
	private String capturePicturePath;

	private void updateMyInfo(final String imgpath,final String name,final String phone,final String email,final String address){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					if(imgpath != null){
						if(!uploadAttestationImg(1, ImgUtils.getImageToBase64Str(imgpath))){
							closeLoadingDialog();
							return;
						}
					}
					String s = DataService.updateMyInfo(MyInfoActivity.this
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, headimgurl, name, phone, email, address
							, company==null?null:company.getCompany(), company==null?null:company.getCompanyId());
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								if(imgpath != null){
									setResult(100);
								}
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										btnEdit.setText("编辑");
										tvName.setEnabled(false);
										tvEmail.setEnabled(false);
										tvAddrss.setEnabled(false);
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

	private boolean uploadAttestationImg(final int index, final String bas64){
		boolean re = false;
		try {
			String s = DataService.uploadUserImg(getApplicationContext(), 
					new SpData(getApplicationContext()).getStringValue(SpData.keyId, null), bas64);
			if (!HlpUtils.isEmpty(s)){
				final HttpResult hr = JSON.parseObject(s,HttpResult.class);
				if (hr != null){
					if  (hr.isSuccess()){
						headimgurl = hr.getFilename();
						if(headimgurl != null){
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
							mToast.showToast(getApplicationContext(), "头像上次失败，请重试");
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
			runOnUiThread(new Runnable(){  
				@Override  
				public void run() {  
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}  
			});
		}
		return re;
	}
	String headimgurl;


	private void queryUserInfo(){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryUserInfo(MyInfoActivity.this
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null));
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								final UserInfo device = JSON.parseObject(hr.getData().toString(),UserInfo.class);
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										HlpUtils.setText(tvName, device.getUsername());
										HlpUtils.setText(tvAddrss, device.getAddress());
										HlpUtils.setText(tvcompany, device.getCompany());
										HlpUtils.setText(tvEmail, device.getEmail());
										HlpUtils.setText(tvPhone, device.getMobile());
									}  
								});
								String headImg = device.getHeadimgurl();
								if(headImg != null){
									byte[] data = DataService.getImage(headImg);
									if(data != null){
										final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
										runOnUiThread(new Runnable(){  
											@Override  
											public void run() { 
												//设置图片
												ivHeadImg.setImageBitmap(mBitmap);
											}  
										});
									}
								}
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
