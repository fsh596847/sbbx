package com.zhaolong.android.sbbx.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class mToast {
	
	public static void showToast(Context context, String msg) {
		Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}

	public static void showToastLong(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

}
