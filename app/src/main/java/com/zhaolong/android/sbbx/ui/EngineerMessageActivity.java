package com.zhaolong.android.sbbx.ui;

import com.zhaolong.android.sbbx.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class EngineerMessageActivity extends Activity {
	
	private MessageFragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_engineer_message);
		
		fragment = new MessageFragment();
		setFragment(fragment);
		
	}
	
	
	private void setFragment(Fragment setFragment)  
    {  
        FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();  
        transaction.replace(R.id.framelayout_message, setFragment);  
        transaction.commit();  
    }

}
