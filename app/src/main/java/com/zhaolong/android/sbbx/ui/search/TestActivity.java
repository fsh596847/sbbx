package com.zhaolong.android.sbbx.ui.search;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import com.zhaolong.android.sbbx.MainActivity;
import com.zhaolong.android.sbbx.R;

/**
 * Created by HIPAA on 2016/12/6.
 */

public class TestActivity extends Activity {

  NotificationManager manager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
  }

  public void onClick(View view) {
    manager =
        (NotificationManager) TestActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification;
    Intent intent = new Intent(this, MainActivity.class);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(TestActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    //notification.setLatestEventInfo(c, title, content, pendingIntent);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
      notification = new NotificationCompat.Builder(TestActivity.this)
          .setSmallIcon(R.drawable.app_logo)//必须要先setSmallIcon，否则会显示默认的通知，不显示自定义通知
          //.setTicker("标题")
          .setContentTitle("内容标题")
          .setDefaults(Notification.DEFAULT_SOUND)
          .setContentText("内容")
          .setContentIntent(pendingIntent)
          .build();
    } else {
      notification = new Notification.Builder(TestActivity.this)
          .setSmallIcon(R.drawable.app_logo)//必须要先setSmallIcon，否则会显示默认的通知，不显示自定义通知
          //.setTicker("标题")
          .setContentTitle("内容标题")
          .setContentText("内容")
          .setDefaults(Notification.DEFAULT_SOUND)
          .setContentIntent(pendingIntent)
          .build();
    }
    manager.notify(0, notification);
  }
}
