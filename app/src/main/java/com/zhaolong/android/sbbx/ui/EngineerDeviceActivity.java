package com.zhaolong.android.sbbx.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.Device;
import com.zhaolong.android.sbbx.beans.Hospital;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.ui.search.EngineerDeviceSearchActivity;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.SyncImageLoaderListview;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;
import java.util.ArrayList;
import java.util.List;

public class EngineerDeviceActivity extends Activity {

  TextView tvHospital;

  List<Hospital> hospitalList = new ArrayList<Hospital>();//医院列表
  HospitalAdapter hospitalAdapter;
  ListView hospitalListView;
  Hospital hospital = new Hospital();

  DeviceAdapter deviceAdapter;
  ListView deviceListView;
  List<Device> deviceList = new ArrayList<Device>();
  //分页刷新
  private int p;
  private int page_size;
  private boolean success;//是否更新成功
  private boolean isQuery;//是否正在更新
  private boolean isFinish;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_engineer_device);
    hospitalListView = (ListView) findViewById(R.id.listView_device_hospital);
    deviceListView = (ListView) findViewById(R.id.listView_device_device);
    tvHospital = (TextView) findViewById(R.id.tv_device_hospital);
    hospitalAdapter = new HospitalAdapter(getApplicationContext());
    hospitalListView.setAdapter(hospitalAdapter);
    deviceAdapter = new DeviceAdapter(getApplicationContext(), deviceListView);
    deviceListView.setAdapter(deviceAdapter);

    hospitalListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
          long arg3) {
        deviceListView.setVisibility(View.VISIBLE);
        hospitalListView.setVisibility(View.GONE);
        Drawable drawable = getResources().getDrawable(R.drawable.triangle_down);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvHospital.setCompoundDrawables(null, null, drawable, null);
        hospital = hospitalList.get(arg2);
        tvHospital.setText(hospital.getHospitalName());
        clearDatas();
        deviceAdapter.notifyDataSetChanged();
        queryEngineerEquip();
      }
    });
    deviceListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
          long arg3) {
        startActivity(new Intent(EngineerDeviceActivity.this, DeviceDetailActivity.class)
            .putExtra("device", deviceList.get(arg2)));
      }
    });
    deviceListView.setOnScrollListener(new OnScrollListener() {

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
          int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem != 0 && !isFinish) {
          //判断可视Item是否能在当前页面完全显示
          if (visibleItemCount + firstVisibleItem == totalItemCount) {
            if (isQuery) {
              if (success) {
                p += 1;
              }
              queryEngineerEquip();
            }
          }
        }
      }
    });
    //返回
    findViewById(R.id.iv_device_back).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });
    findViewById(R.id.iv_device_find).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(EngineerDeviceActivity.this, EngineerDeviceSearchActivity.class);
        startActivity(intent);
      }
    });

    //医院下拉
    tvHospital.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Drawable drawable;
        if (hospitalListView.getVisibility() == View.GONE) {
          hospitalListView.setVisibility(View.VISIBLE);
          deviceListView.setVisibility(View.GONE);
          drawable = getResources().getDrawable(R.drawable.triangle_up);
          drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
          if (hospitalList == null || hospitalList.size() == 0) {
            queryHospitaldoc();
          }
        } else {
          deviceListView.setVisibility(View.VISIBLE);
          hospitalListView.setVisibility(View.GONE);
          drawable = getResources().getDrawable(R.drawable.triangle_down);
          drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        tvHospital.setCompoundDrawables(null, null, drawable, null);
      }
    });

    clearDatas();
    queryHospitaldoc();
    queryEngineerEquip();
  }

  private void clearDatas() {
    p = 1;
    page_size = 20;
    success = true;
    isQuery = true;
    isFinish = false;
    deviceList.clear();
  }

  private class DeviceAdapter extends BaseAdapter {

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case 0:
            notifyDataSetChanged();
            break;

          default:
            break;
        }
      }

      ;
    };

    ListView mListView;
    private Drawable[] drawables;

    private LayoutInflater mInflater = null;
    private Context mContext;

    public DeviceAdapter(Context context, ListView lvExpert) {
      this.mInflater = LayoutInflater.from(context);
      mContext = context;
      mListView = lvExpert;
      mListView.setOnScrollListener(onScrollListener);
    }

    public void setList() {
      if (deviceList.size() > 0) {
        if (drawables != null) {
          Drawable[] d = new Drawable[deviceList.size()];
          for (int i = 0; i < drawables.length; i++) {
            d[i] = drawables[i];
            drawables = d;
          }
        } else {
          drawables = new Drawable[deviceList.size()];
        }
      }
      notifyDataSetChanged();
    }

    @Override
    public int getCount() {
      return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
      return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
      ViewHolder h;
      if (v == null) {
        v = mInflater.inflate(R.layout.item_device, null);
        h = new ViewHolder();
        h.iv = (ImageView) v.findViewById(R.id.imageView_item_device);
        h.tvName = (TextView) v.findViewById(R.id.textView_item_device_name);
        h.tvCode = (TextView) v.findViewById(R.id.textView_item_device_code);
        h.tvType = (TextView) v.findViewById(R.id.textView_item_device_type);
        v.setTag(h);
      } else {
        h = (ViewHolder) v.getTag();
      }

      if (getCount() > 0) {
        Device a = deviceList.get(position);
        h.tvName.setText(a.getEquipName() == null ? "" : a.getEquipName());
        h.tvCode.setText("设备编号  " + (a.getEquipCode() == null ? "" : a.getEquipCode()));
        h.tvType.setText("设备分类  " + (a.getEquipClass() == null ? "" : a.getEquipClass()));

        String img = a.getImg();
        Drawable d = position >= drawables.length ? null : drawables[position];
        if (d != null) {
          h.iv.setImageDrawable(d);
        } else if (img != null) {
          h.iv.setImageResource(R.drawable.app_logo);
          syncImageLoader.loadImage(mContext, position, img, imageLoadListener, 0);
        }
      }
      return v;
    }

    class ViewHolder {
      ImageView iv;
      TextView tvName;
      TextView tvCode;
      TextView tvType;
    }

    SyncImageLoaderListview.OnImageLoadListener imageLoadListener =
        new SyncImageLoaderListview.OnImageLoadListener() {

          @Override
          public void onImageLoad(Integer t, Drawable drawable, Integer index) {
            drawables[t] = drawable;
            handler.sendEmptyMessage(0);
          }

          @Override
          public void onError(Integer t) {
          }
        };

    SyncImageLoaderListview syncImageLoader = new SyncImageLoaderListview();

    public void loadImage() {
      int start = mListView.getFirstVisiblePosition();
      int end = mListView.getLastVisiblePosition();
      if (end >= getCount()) {
        end = getCount() - 1;
      }
      syncImageLoader.setLoadLimit(start, end);
      syncImageLoader.unlock();
    }

    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
          case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
            syncImageLoader.lock();
            break;
          case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
            loadImage();
            break;
          case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            syncImageLoader.lock();
            break;

          default:
            break;
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
          int visibleItemCount, int totalItemCount) {
      }
    };
  }

  public void queryEngineerEquip() {
    isQuery = false;
    new Thread(new Runnable() {

      @SuppressLint("SimpleDateFormat")
      @Override
      public void run() {
        try {
          String response = DataService.queryEngineerEquip(getApplicationContext()
              , new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
              , new SpData(getApplicationContext()).getStringValue(SpData.keyPhone, null)
              , p, page_size, hospital.getId(),null);
          mLog.d("http", "p:" + p + ",response：" + response);
          if (response == null) {

          } else {
            HttpResult hr = JSON.parseObject(response, HttpResult.class);
            if (hr != null) {
              success = true;
              if (hr.isSuccess()) {
                if (hr.getTotalPage() <= p) {
                  isFinish = true;
                }
                deviceList.addAll(JSON.parseArray(hr.getData().toString(), Device.class));
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    deviceAdapter.setList();
                  }
                });
              } else {
              }
            } else {

            }
          }
        } catch (Exception e) {
          mLog.e("http", "Exception :" + e.getMessage());
          e.printStackTrace();
        }
        isQuery = true;
      }
    }).start();
  }

  private class HospitalAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;

    public HospitalAdapter(Context context) {
      this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
      return hospitalList.size();
    }

    @Override
    public Object getItem(int arg0) {

      return hospitalList.get(arg0);
    }

    @Override
    public long getItemId(int position) {

      return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
      final ViewHolder holder;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.item_hospital, null);
        holder.tvHospitalName = (TextView) convertView.findViewById(R.id.tv_item_hospital);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      if (getCount() > 0) {
        final Hospital detail = hospitalList.get(position);
        holder.tvHospitalName.setText(detail.getHospitalName());
      }

      return convertView;
    }

    class ViewHolder {
      public TextView tvHospitalName;
    }
  }

  private void queryHospitaldoc() {
    reload();
    new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          String userid = new SpData(getApplicationContext()).getStringValue(SpData.keyId, null);
          String mobile = new SpData(getApplicationContext()).getStringValue(SpData.keyPhone, null);
          String s = DataService.queryHospitaldoc(EngineerDeviceActivity.this, userid, mobile);
          mLog.d("http", "s:" + s);
          if (!HlpUtils.isEmpty(s)) {
            final HttpResult hr = JSON.parseObject(s, HttpResult.class);
            if (hr != null) {
              if (hr.isSuccess()) {
                hospitalList = JSON.parseArray(hr.getData().toString(), Hospital.class);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    hospitalAdapter.notifyDataSetChanged();
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

  private Dialog loadingDialog = null;

  private void closeLoadingDialog() {
    if (null != loadingDialog) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }
}
