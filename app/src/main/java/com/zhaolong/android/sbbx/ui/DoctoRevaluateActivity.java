package com.zhaolong.android.sbbx.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.Device;
import com.zhaolong.android.sbbx.beans.EvaluateTag;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.utils.mToast;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class DoctoRevaluateActivity extends Activity implements OnClickListener {

  MyAdapter myAdapter;
  ListView listView;
  EditText etComments;
  ImageView ivX1, ivX2, ivX3, ivX4, ivX5;
  int scoreclass = -1;
  private List<EvaluateTag> listTag = new ArrayList<EvaluateTag>();
  private int index = -1;
  private Device device;
  private Dialog loadingDialog = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_doctor_evaluate);
    device = (Device) getIntent().getSerializableExtra("device");
    if (device == null) {
      finish();
      return;
    }

    etComments = (EditText) findViewById(R.id.et_evaluate);

    ivX1 = (ImageView) findViewById(R.id.iv_evaluate_xx1);
    ivX2 = (ImageView) findViewById(R.id.iv_evaluate_xx2);
    ivX3 = (ImageView) findViewById(R.id.iv_evaluate_xx3);
    ivX4 = (ImageView) findViewById(R.id.iv_evaluate_xx4);
    ivX5 = (ImageView) findViewById(R.id.iv_evaluate_xx5);
    myAdapter = new MyAdapter(getApplicationContext());
    listView = (ListView) findViewById(R.id.listView_evaluate_judge);
    listView.setAdapter(myAdapter);

    findViewById(R.id.iv_doctor_evaluate_back).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });

    findViewById(R.id.tv_evaluate_call).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4001235698"));
        startActivity(intent);
      }
    });

    if (device.getIsevaluate() == 0) {
      //未评价
      findViewById(R.id.btn_evaluate).setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          addJudgeOrder(etComments.getText().toString());
        }
      });
      ivX1.setOnClickListener(this);
      ivX2.setOnClickListener(this);
      ivX3.setOnClickListener(this);
      ivX4.setOnClickListener(this);
      ivX5.setOnClickListener(this);
    } else {
      //已评价
      findViewById(R.id.btn_evaluate).setVisibility(View.GONE);
      etComments.setEnabled(false);
      etComments.setText(device.getComments() == null ? "" : device.getComments());
      queryTagByOrder(device.getStarnum(), device.getTag());
    }
  }

  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.iv_evaluate_xx1:
        queryTagByOrder(1, null);
        break;

      case R.id.iv_evaluate_xx2:
        queryTagByOrder(2, null);
        break;

      case R.id.iv_evaluate_xx3:
        queryTagByOrder(3, null);
        break;

      case R.id.iv_evaluate_xx4:
        queryTagByOrder(4, null);
        break;

      case R.id.iv_evaluate_xx5:
        queryTagByOrder(5, null);
        break;
    }
  }

  void UIxxShow(int position) {
    scoreclass = position;
    ivX1.setImageResource(position >= 1 ? R.drawable.xx_o : R.drawable.xx_g);
    ivX2.setImageResource(position >= 2 ? R.drawable.xx_o : R.drawable.xx_g);
    ivX3.setImageResource(position >= 3 ? R.drawable.xx_o : R.drawable.xx_g);
    ivX4.setImageResource(position >= 4 ? R.drawable.xx_o : R.drawable.xx_g);
    ivX5.setImageResource(position >= 5 ? R.drawable.xx_o : R.drawable.xx_g);
  }

  private void addJudgeOrder(final String comments) {
    if (index == -1) {
      mToast.showToast(this, "请选择标签");
      return;
    }
    reload();
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String s = DataService.addJudgeOrder(DoctoRevaluateActivity.this
              , new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
              , new SpData(getApplicationContext()).getStringValue(SpData.keyPhone, null)
              , device.getOrderid(), scoreclass, comments, listTag.get(index).getId());
          mLog.d("http", "s:" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                setResult(100);
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

  private void queryTagByOrder(final int score, final String tag) {
    if (scoreclass == score) {
      return;
    }
    UIxxShow(score);
    reload();
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String s = DataService.queryTagByOrder(DoctoRevaluateActivity.this, score, tag);
          mLog.d("http", "s:" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                listTag = JSON.parseArray(hr.getData().toString(), EvaluateTag.class);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    index = -1;
                    myAdapter.notifychange();
                  }
                });
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    UIxxShow(-1);
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
                UIxxShow(-1);
                Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
              }
            });
          }
        } catch (final Exception he) {
          he.printStackTrace();
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              UIxxShow(-1);
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

  void onClickTag(int position) {
    if (device.getIsevaluate() == 0 && index != position) {
      //未评价
      index = position;

      myAdapter.notifychange();
    }
  }

  private class MyAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;

    public MyAdapter(Context context) {
      this.mInflater = LayoutInflater.from(context);
    }

    public void notifychange() {
      if (listTag != null) {
        notifyDataSetChanged();
      }
    }

    @Override
    public int getCount() {
      return (listTag.size() / 2) + (listTag.size() % 2);
    }

    @Override
    public Object getItem(int arg0) {
      return listTag.get(arg0);
    }

    @Override
    public long getItemId(int position) {

      return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup arg2) {
      final ViewHolder h;
      if (v == null) {
        h = new ViewHolder();
        v = mInflater.inflate(R.layout.item_bank_choose, null);
        h.btn1 = (Button) v.findViewById(R.id.btn_item_bank_1);
        h.btn2 = (Button) v.findViewById(R.id.btn_item_bank_2);

        v.setTag(h);
      } else {
        h = (ViewHolder) v.getTag();
      }

      if (getCount() > 0) {
        listTag.get(position);
        //点击背景区分
        if (position * 2 == index) {
          h.btn1.setBackgroundResource(R.drawable.box_b);
        } else {
          h.btn1.setBackgroundResource(R.drawable.box_g);
        }
        if (position * 2 + 1 == index) {
          h.btn2.setBackgroundResource(R.drawable.box_b);
        } else {
          h.btn2.setBackgroundResource(R.drawable.box_g);
        }

        h.btn1.setText(listTag.get(position * 2).getTagName());
        if (position * 2 + 1 < listTag.size()) {
          h.btn2.setText(listTag.get(position * 2 + 1).getTagName());
          h.btn2.setVisibility(View.VISIBLE);
        } else {
          h.btn2.setVisibility(View.INVISIBLE);
        }
        //点击监听
        h.btn1.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            onClickTag(position * 2);
          }
        });
        h.btn2.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            onClickTag(position * 2 + 1);
          }
        });
      }

      return v;
    }

    class ViewHolder {
      public Button btn1;
      public Button btn2;
    }
  }
}
