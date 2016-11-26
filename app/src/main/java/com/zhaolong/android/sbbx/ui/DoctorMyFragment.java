package com.zhaolong.android.sbbx.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.MainActivity;
import com.zhaolong.android.sbbx.MyApplication;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.beans.UserInfo;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class DoctorMyFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_doctor_my, container, false); 
		initViews(view);
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 100:
			queryUserInfo();
			break;
		}
	}

	private void initViews(View v) {
		ivHead = (ImageView) v.findViewById(R.id.ImageView_engineer_my_head);
		tvName = (TextView) v.findViewById(R.id.tv_doc_my_info_name);
		v.findViewById(R.id.btn_doc_my_exit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyApplication.getInstance().stopPushService();
				startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();
			}
		});
		v.findViewById(R.id.layout_doc_my_info).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getActivity(), MyInfoActivity.class).putExtra("userclass", 0),100);
			}
		});
		v.findViewById(R.id.layout_doc_my_order).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), OrderMyActivity.class)
				.putExtra("type", 0));
			}
		});
		v.findViewById(R.id.layout_doc_my_share).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), MyShareActivity.class));
			}
		});
		v.findViewById(R.id.layout_doc_about_us).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AboutUsActivity.class));
			}
		});
		queryUserInfo();
	}
	TextView tvName;
	ImageView ivHead;
	
	private void getImg(String headImg){
		if(headImg != null){
			try {
				byte[] data = DataService.getImage(headImg);
				if(data != null){
					final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap 
					getActivity().runOnUiThread(new Runnable(){  
						@Override  
						public void run() { 
							//设置图片
							ivHead.setImageBitmap(mBitmap);
						}  
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void queryUserInfo(){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryUserInfo(getActivity()
							, new SpData(getActivity()).getStringValue(SpData.keyId, null));
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								final UserInfo device = JSON.parseObject(hr.getData().toString(),UserInfo.class);
								if(device.getHeadimgurl() != null){
									getImg(device.getHeadimgurl());
								}
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										HlpUtils.setText(tvName, device.getUsername());
									}  
								});
							}else{
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}
					}else{
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (final Exception he) {
					he.printStackTrace();
					mLog.e("http", "Exception:"+he.getMessage());
					getActivity().runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getActivity(), he.getMessage(), Toast.LENGTH_SHORT).show();
						}  
					});
				}
				closeLoadingDialog();
			}
		}).start();
	}
	
	private void reload(){
		if (loadingDialog == null){
			loadingDialog = LoadingDialog.createLoadingDialog(getActivity());
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
