package com.zhaolong.android.sbbx.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zhaolong.android.sbbx.MyApplication;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zxing.scan.CaptureActivity;

public class DoctorMinaActivity extends Activity {
	
	private Button btnLeft,btnRight;
	
	private DoctorHomeFragment fDoctorHome;
	private DoctorMyFragment fDoctorMy;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_main);
		MyApplication.getInstance().activitis.add(this);//把当前Activity放入集合中
		
		btnLeft = (Button) findViewById(R.id.btn_doctor_main_home);
		btnRight = (Button) findViewById(R.id.btn_doctor_main_my);
		
		btnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearTabUI();
				btnLeft.setCompoundDrawables(null, HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_h_b), null, null);
				btnLeft.setTextColor(getResources().getColor(R.color.color_major_blue));
				showFragment(fDoctorHome);
			}
		});
		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearTabUI();
				btnRight.setCompoundDrawables(null, HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_m_b), null, null);
				btnRight.setTextColor(getResources().getColor(R.color.color_major_blue));
				showFragment(fDoctorMy);
			}
		});
		findViewById(R.id.imageView_doctor_main_find).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(DoctorMinaActivity.this, CaptureActivity.class)
				.putExtra("type", CaptureActivity.COM_REPAIRS));
			}
		});
		
		FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction(); 
        fDoctorHome = new DoctorHomeFragment();
        fDoctorMy = new DoctorMyFragment();
        transaction.add(R.id.fl_doctor_main,fDoctorHome);
        transaction.add(R.id.fl_doctor_main,fDoctorMy);
        hideFfragment(transaction);
        transaction.show(fDoctorHome);
		transaction.commit();  
	}
	
	private void showFragment(Fragment setFragment)  
    {  
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction(); 
        hideFfragment(transaction);//隐藏所有的
        transaction.show(setFragment);//显示要显示的
        transaction.commit();  
    }
	
	void hideFfragment(FragmentTransaction transaction){
		transaction.hide(fDoctorHome);
		transaction.hide(fDoctorMy);
	}
	
	void clearTabUI(){
		btnLeft.setCompoundDrawables( null,HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_h_g), null, null);
		btnRight.setCompoundDrawables( null,HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_m_g), null, null);
		btnLeft.setTextColor(getResources().getColor(R.color.color_gray));
		btnRight.setTextColor(getResources().getColor(R.color.color_gray));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().activitis.remove(this);
	}
	
}
