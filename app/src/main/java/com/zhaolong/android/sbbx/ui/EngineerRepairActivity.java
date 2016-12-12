package com.zhaolong.android.sbbx.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class EngineerRepairActivity extends Activity {

	TextView tvEngineer;
	TextView tvOrderMan;
	private String code;
	private Device device;
	private boolean isOnlySee;
	private boolean isTtime;
	private TextView tvRepairCode, tvEquipSeq, tvEquipBar, tvEquipName, tvHospital, tvDepar,
			tvEquipAddress;
	private EditText etDescribe;
	private Button btnOk;
	private Dialog loadingDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_engineer_repair);
		code = getIntent().getStringExtra("code");
		device = (Device) getIntent().getSerializableExtra("device");
		isOnlySee = getIntent().getBooleanExtra("see", false);

		initViews();

		if(device == null){
			signRepairOrder(code);
		}
		else{
			HlpUtils.setText(tvEngineer, device.getRepairName());
			HlpUtils.setText(tvOrderMan, device.getOrderName() + "(" + device.getRepairMobile() + ")");
			HlpUtils.setText(tvRepairCode, device.getOrdercode());
			HlpUtils.setText(tvEquipSeq, device.getEquipCode());
			HlpUtils.setText(tvEquipBar, device.getEquipType());
			HlpUtils.setText(tvEquipName, device.getEquipName());
			HlpUtils.setText(tvHospital, device.getHospital());
			HlpUtils.setText(tvDepar, device.getDeparName());
			HlpUtils.setText(tvEquipAddress, device.getEquipAddress());
			HlpUtils.setText(etDescribe, device.getFalultDesc());
			if(isOnlySee){
				etDescribe.setEnabled(false);
				btnOk.setText("返回");
			}
		}
	}

	private void initViews() {
		tvOrderMan = (TextView) findViewById(R.id.tv_order_man);
		tvEngineer = (TextView) findViewById(R.id.tv_engineer);
		tvRepairCode = (TextView) findViewById(R.id.tv_repair_repairCode);
		tvEquipSeq = (TextView) findViewById(R.id.tv_repair_equipSeq);
		tvEquipName = (TextView) findViewById(R.id.tv_repair_equipName);
		tvEquipBar = (TextView) findViewById(R.id.tv_repair_equipBar);
		tvHospital = (TextView) findViewById(R.id.tv_repair_hospital);
		tvDepar = (TextView) findViewById(R.id.tv_repair_depar);
		tvEquipAddress = (TextView) findViewById(R.id.tv_repair_equipAddress);
		etDescribe = (EditText) findViewById(R.id.et_repair_describe);
		btnOk = (Button) findViewById(R.id.btn_repair_ok);
		findViewById(R.id.iv_repair_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isOnlySee || device == null) {
					finish();
				}else{
					FinishOrder(device.getId(),etDescribe.getText().toString());

				}
			}
		});
	}
	
	private void FinishOrder(final String orderid,final String parts){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.FinishOrder(EngineerRepairActivity.this
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, orderid,parts);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								finish();
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
									}
								});
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
					}else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}
						});
					}
				}catch (final Exception he) {
					he.printStackTrace();
					runOnUiThread(new Runnable() {
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
	
	private void signRepairOrder(final String equipCode){
		reload();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.signRepairOrder(EngineerRepairActivity.this
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, new SpData(getApplicationContext()).getStringValue(SpData.keyPhone, null)
							, equipCode);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								device = JSON.parseObject(hr.getData().toString(),Device.class);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(EngineerRepairActivity.this, "扫描成功", Toast.LENGTH_SHORT).show();
										HlpUtils.setText(tvEngineer, device.getRepairName());
										HlpUtils.setText(tvOrderMan,
												device.getOrderName() + "(" + device.getRepairMobile() + ")");
										HlpUtils.setText(tvRepairCode, device.getOrdercode());
										HlpUtils.setText(tvEquipSeq, device.getEquipCode());
										HlpUtils.setText(tvEquipBar, device.getEquipType());
										HlpUtils.setText(tvEquipName, device.getEquipName());
										HlpUtils.setText(tvHospital, device.getHospital());
										HlpUtils.setText(tvDepar, device.getDeparName());
										HlpUtils.setText(tvEquipAddress, device.getEquipAddress());
										HlpUtils.setText(etDescribe, device.getFalultDesc());
									}
								});
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										isTtime = true;
										btnOk.setText("返回");
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
					}else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}
						});
					}
				}catch (final Exception he) {
					he.printStackTrace();
					runOnUiThread(new Runnable() {
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

	private void closeLoadingDialog() {
		if(null != loadingDialog) {	 
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
	
}
