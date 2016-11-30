package com.zhaolong.android.sbbx.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.zhaolong.android.sbbx.MyApplication;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.utils.HlpUtils;

public class EngineerMinaActivity extends Activity implements OnClickListener {

  private Button btnLeft, btnRight;

  private EngineerHomeFragment fEngineerHome;
  private EngineerCalendarFragment fEngineerCentre;
  private EngineerMyFragment fEngineerMy;

  private FragmentManager manager;
  private FragmentTransaction transaction;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_engineer_main);
    MyApplication.getInstance().activitis.add(this);//把当前Activity放入集合中

    btnLeft = (Button) findViewById(R.id.btn_engineer_main_home);
    btnRight = (Button) findViewById(R.id.btn_engineer_main_my);

    btnLeft.setOnClickListener(this);
    btnRight.setOnClickListener(this);
    findViewById(R.id.imageView_engineer_main_find).setOnClickListener(this);

    manager = getFragmentManager();
    transaction = manager.beginTransaction();
    fEngineerHome = new EngineerHomeFragment();
    transaction.replace(R.id.fl_engineer_main, fEngineerHome);
    //hideFragment(transaction);
    //fEngineerHome = new EngineerHomeFragment();
    //transaction.add(R.id.fl_engineer_main,fEngineerHome);
    //transaction.show(fEngineerHome);
    transaction.commit();
  }

  void clearTabUI() {
    btnLeft.setCompoundDrawables(null,
        HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_h_g), null, null);
    btnLeft.setTextColor(getResources().getColor(R.color.color_gray));
    btnRight.setCompoundDrawables(null,
        HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_m_g), null, null);
    btnRight.setTextColor(getResources().getColor(R.color.color_gray));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    MyApplication.getInstance().activitis.remove(this);
  }

  @Override
  public void onClick(View v) {

    transaction = manager.beginTransaction();

    switch (v.getId()) {
      case R.id.btn_engineer_main_home:
        /**
         * 为了防止重叠，需要点击之前先移除其他Fragment
         */
        clearTabUI();
        btnLeft.setCompoundDrawables(null,
            HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_h_b), null, null);
        btnLeft.setTextColor(getResources().getColor(R.color.color_major_blue));
        hideFragment(transaction);
        if (fEngineerHome == null) {
          fEngineerHome = new EngineerHomeFragment();
        }
        //transaction.add(R.id.fl_engineer_main,fEngineerHome);
        //transaction.show( fEngineerHome);
        transaction.replace(R.id.fl_engineer_main, fEngineerHome);
        transaction.commit();

        break;
      case R.id.imageView_engineer_main_find:
        clearTabUI();
        hideFragment(transaction);
        if (fEngineerCentre == null) {
          fEngineerCentre = new EngineerCalendarFragment();
        }
        //transaction.add(R.id.fl_engineer_main,fEngineerCentre);
        //transaction.show( fEngineerCentre);
        transaction.replace(R.id.fl_engineer_main, fEngineerCentre);
        transaction.commit();

        break;
      case R.id.btn_engineer_main_my:
        clearTabUI();
        btnRight.setCompoundDrawables(null,
            HlpUtils.getCompoundDrawable(getApplicationContext(), R.drawable.m_m_b), null, null);
        btnRight.setTextColor(getResources().getColor(R.color.color_major_blue));
        hideFragment(transaction);
        if (fEngineerMy == null) {
          fEngineerMy = new EngineerMyFragment();
        }
        //transaction.add(R.id.fl_engineer_main,fEngineerMy);
        //transaction.show( fEngineerMy);
        transaction.replace(R.id.fl_engineer_main, fEngineerMy);
        transaction.commit();
        break;

      default:
        break;
    }
  }

  /*
    * 去除（隐藏）所有的Fragment
    * */
  private void hideFragment(FragmentTransaction transaction) {
    if (fEngineerHome != null) {
      //transaction.hide(fEngineerHome);
      transaction.remove(fEngineerHome);
    }
    if (fEngineerCentre != null) {
      //transaction.hide(fEngineerCentre);
      transaction.remove(fEngineerCentre);
    }
    if (fEngineerMy != null) {
      //transaction.hide(fEngineerMy);
      transaction.remove(fEngineerMy);
    }
  }
}
