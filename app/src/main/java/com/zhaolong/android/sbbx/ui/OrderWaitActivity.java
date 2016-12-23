package com.zhaolong.android.sbbx.ui;

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
import android.widget.LinearLayout;
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
import com.zhaolong.android.sbbx.windows.HintPopupWindow;
import com.zhaolong.android.sbbx.windows.HintPopupWindow.CallBack;
import com.zhaolong.android.sbbx.windows.LoadingDialog;
import com.zxing.scan.CaptureActivity;

public class OrderWaitActivity extends Activity {

  TextView tvRepairCode, tvEquipSeq, tvEquipBar, tvEquipName, tvHospital, tvDepar, tvEquipAddress,
      tvMan, tvTime;
  TextView tvEngineer;
  LinearLayout viewParent;
  EditText etDescribe;
  ImageView ivAdd1, ivAdd2, ivAdd3;
  Button btnSign, btnLong, btnDetail;
  LinearLayout llAccept, llSign;
  boolean isDestroy = false;
  private Device device;
  private int type;//1、工程师端接单；2、工程师端签到；3、医生端未接单；4、工程师端详情
  private HintPopupWindow hintPopupWindow;
  private Dialog loadingDialog = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_engineer_order_wait);
    device = (Device) getIntent().getSerializableExtra("device");
    type = getIntent().getIntExtra("type", -1);
    initViews();
    if (device != null) {
      HlpUtils.setText(tvEngineer, device.getRepairName());
      HlpUtils.setText(tvRepairCode, device.getOrdercode());
      HlpUtils.setText(tvEquipSeq, device.getEquipCode());
      HlpUtils.setText(tvEquipBar, device.getEquipType());
      HlpUtils.setText(tvEquipName, device.getEquipName());
      HlpUtils.setText(tvHospital, device.getHospital());
      HlpUtils.setText(tvDepar, device.getDeparName());
      HlpUtils.setText(tvEquipAddress, device.getEquipAddress());
      HlpUtils.setText(tvMan, device.getOrderName() + "(" + device.getRepairMobile() + ")");
      HlpUtils.setText(tvTime, device.getBookingTime());
      HlpUtils.setText(etDescribe, device.getFalultDesc());
    } else {
      finish();
    }

    if (device.getFaultImg1() != null) {
      getImage(device.getFaultImg1(), 1);
    } else {
      ivAdd1.setVisibility(View.GONE);
    }
  }

  private void initViews() {
    tvEngineer = (TextView) findViewById(R.id.tv_engineer);
    tvRepairCode = (TextView) findViewById(R.id.tv_order_repairCode);
    tvEquipSeq = (TextView) findViewById(R.id.tv_order_equipSeq);
    tvEquipBar = (TextView) findViewById(R.id.tv_order_equipBar);
    tvEquipName = (TextView) findViewById(R.id.tv_order_equipName);
    tvHospital = (TextView) findViewById(R.id.tv_order_hospital);
    tvDepar = (TextView) findViewById(R.id.tv_order_depar);
    tvEquipAddress = (TextView) findViewById(R.id.tv_order_equipAddress);
    tvMan = (TextView) findViewById(R.id.tv_order_man);
    tvTime = (TextView) findViewById(R.id.tv_order_time);
    viewParent = (LinearLayout) findViewById(R.id.layout_order);
    etDescribe = (EditText) findViewById(R.id.et_order_describe);
    ivAdd1 = (ImageView) findViewById(R.id.iv_order_image1);
    ivAdd2 = (ImageView) findViewById(R.id.iv_order_image2);
    ivAdd3 = (ImageView) findViewById(R.id.iv_order_image3);
    btnSign = (Button) findViewById(R.id.btn_order_sign);
    btnLong = (Button) findViewById(R.id.btn_order_long);
    btnDetail = (Button) findViewById(R.id.btn_order_detail);
    llAccept = (LinearLayout) findViewById(R.id.layout_order_accept);
    llSign = (LinearLayout) findViewById(R.id.layout_order_sign);
    switch (type) {
      case 1:
        llAccept.setVisibility(View.VISIBLE);
        break;

      case 2:
        llSign.setVisibility(View.VISIBLE);
        break;

      case 3:
        btnDetail.setVisibility(View.VISIBLE);
        btnDetail.setText("取消");
        break;

      case 4:
        btnDetail.setVisibility(View.VISIBLE);
        btnDetail.setText("返回");
        break;
    }
    findViewById(R.id.iv_order_back).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        setResult(4);
        finish();
      }
    });
    findViewById(R.id.btn_order_cancel).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        acceptRepairOrder(device.getOrderid(), false);
      }
    });
    findViewById(R.id.btn_order_ok).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        acceptRepairOrder(device.getOrderid(), true);
      }
    });
    btnSign.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity(new Intent(OrderWaitActivity.this, CaptureActivity.class)
            .putExtra("type", CaptureActivity.COM_SGIN));
      }
    });
    btnLong.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        device.setId(device.getOrderid());
        startActivity(new Intent(OrderWaitActivity.this, EngineerRepairActivity.class)
            .putExtra("device", device));
        finish();
      }
    });
    btnDetail.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        switch (type) {
          case 3:
            cancelOrder(device.getOrderid());
            break;

          case 4:
            finish();
            break;
        }
      }
    });
    findViewById(R.id.tv_order_record).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity(
            new Intent(OrderWaitActivity.this, RecordServiceActivty.class).putExtra("equipment",
                device.getEquipment()));
      }
    });
  }

  private void getImage(final String path, final int position) {
    reload();
    new Thread(new Runnable() {

      @Override
      public void run() {

        byte[] data;
        try {

          data = DataService.getImage(path);
          if (data != null && !isDestroy) {
            final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
            switch (position) {
              case 1:
                if (mBitmap != null) {
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      ivAdd1.setImageBitmap(mBitmap);
                    }
                  });
                }
                if (device.getFaultImg2() != null) {
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      getImage(device.getFaultImg2(), 2);
                    }
                  });
                }
                break;

              case 2:
                if (mBitmap != null) {
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      ivAdd2.setVisibility(View.VISIBLE);
                      ivAdd2.setImageBitmap(mBitmap);
                    }
                  });
                }
                if (device.getFaultImg3() != null) {
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      getImage(device.getFaultImg3(), 3);
                    }
                  });
                }
                break;

              case 3:
                if (mBitmap != null) {
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      ivAdd3.setVisibility(View.VISIBLE);
                      ivAdd3.setImageBitmap(mBitmap);
                    }
                  });
                }
                break;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        closeLoadingDialog();
      }
    }).start();
  }

  private void cancelOrder(final String orderid) {
    reload();
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String s = DataService.cancelOrder(OrderWaitActivity.this
              , new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
              , orderid);
          mLog.d("http", "s:" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                setResult(4);
                finish();
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getApplicationContext(), hr.getData().toString(),
                        Toast.LENGTH_SHORT).show();
                  }
                });
              }
            }
          } else {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (final Exception he) {
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

  private void acceptRepairOrder(final String orderid, final boolean isAccept) {
    reload();
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String s = DataService.acceptRepairOrder(OrderWaitActivity.this
              , new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
              , new SpData(getApplicationContext()).getStringValue(SpData.keyPhone, null)
              , orderid, isAccept);
          mLog.d("http", "s:" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                setResult(4);
                if (hintPopupWindow == null) {
                  hintPopupWindow = new HintPopupWindow(OrderWaitActivity.this,
                      isAccept ? "接单成功\n请按时到达客户现场" : "您已拒绝此单\n此单将由后台客服处理", "", "确定");
                  hintPopupWindow.setCallBack(new CallBack() {

                    @Override
                    public void ok() {
                      finish();
                    }

                    @Override
                    public void cancel() {
                      finish();
                    }
                  });
                }
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    hintPopupWindow.showAtLocation(viewParent, Gravity.CENTER, 0, 0);
                  }
                });
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(getApplicationContext(), hr.getData().toString(),
                        Toast.LENGTH_SHORT).show();
                  }
                });
              }
            }
          } else {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (final Exception he) {
          he.printStackTrace();
          mLog.e("http", "Exception:" + he.getMessage());
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

  private void reload() {
    if (loadingDialog == null) {
      loadingDialog = LoadingDialog.createLoadingDialog(this);
    }
    if (loadingDialog != null && !loadingDialog.isShowing()) {
      loadingDialog.show();
    }
  }

  private void closeLoadingDialog() {
    if (null != loadingDialog) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }

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
