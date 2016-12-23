package com.zhaolong.android.sbbx.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.zhaolong.android.sbbx.MainActivity;
import com.zhaolong.android.sbbx.R;

/**
 * Created by HIPAA on 2016/12/23.
 */

public class StartActivity extends Activity {

  private Handler handler;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);
    handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
      }
    }, 2000);
  }
}
