package com.zhaolong.android.sbbx.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.Device;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class OrderActivty extends Activity {
	
	private int MAIN_TYPE;//1，医生端；2，工程师端
	private int state = 0;//state =0 待接单 state =1已接单 state=2 已拒绝 state =3已完成
	
	OrderAdapter orderAdapter;
	ListView orderListView;
	List<Device> orderList = new ArrayList<Device>();
	//分页刷新
	private int p; 
	private int page_size;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		MAIN_TYPE = getIntent().getIntExtra("type", 1);
		
		if(MAIN_TYPE == 1){
			TextView tvTitle = (TextView) findViewById(R.id.tv_order_title);
			tvTitle.setText("我的报修");
		}
		
		tvWait = (TextView) findViewById(R.id.tv_order_wait);
		tvAlready = (TextView) findViewById(R.id.tv_order_already);
		tvFinish = (TextView) findViewById(R.id.tv_order_finish);
		ivWait = (TextView) findViewById(R.id.iv_order_wait);
		ivAlready = (TextView) findViewById(R.id.iv_order_already);
		ivFinish = (TextView) findViewById(R.id.iv_order_finish);
		orderListView = (ListView) findViewById(R.id.listView_order);
		
		orderAdapter = new OrderAdapter(getApplicationContext());
		orderListView.setAdapter(orderAdapter);
		orderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position = arg2;
				switch (MAIN_TYPE) {
				case 2:
					switch (state) {
					case 0:
						startActivityForResult(new Intent(OrderActivty.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 1),4);
						break;

					case 1:
						startActivity(new Intent(OrderActivty.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 2));
						break;
						
					case 3:
						startActivity(new Intent(OrderActivty.this, JudgeActivty.class)
						.putExtra("userclass", 1));
						break;
					}
					break;

				case 1:
					switch (state) {
					case 0:
						startActivityForResult(new Intent(OrderActivty.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 3),4);
						break;

					case 3:
						startActivity(new Intent(OrderActivty.this, EngineerRepairActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("see", true));
						/*startActivity(new Intent(OrderActivty.this, JudgeActivty.class)
						.putExtra("userclass", 0));*/
						/*startActivity(new Intent(OrderActivty.this, DoctoRevaluateActivity.class)
						.putExtra("device", orderList.get(arg2)));*/
						/*startActivity(new Intent(OrderActivty.this, DoctoRevaluateActivity.class)
						.putExtra("device", orderList.get(arg2)));*/
						break;

					default:
						startActivity(new Intent(OrderActivty.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 4));
						break;
					}
					break;
				}
			}
		});
		orderListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem != 0 && !isFinish) {
		            //判断可视Item是否能在当前页面完全显示
		            if (visibleItemCount+firstVisibleItem == totalItemCount) {
		            	if(isQuery){
		            		if(success){
		            			p+=1;
		            		}
		            		queryRepairOrder();
		            	}
		            }
		        }
			}
		});
		findViewById(R.id.iv_order_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.layout_order_wait).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initTabUI(0);
			}
		});
		findViewById(R.id.layout_order_already).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initTabUI(1);
			}
		});
		findViewById(R.id.layout_order_finish).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initTabUI(3);
			}
		});
		clearDatas();
	}
	private TextView tvWait,tvAlready,tvFinish,ivWait,ivAlready,ivFinish;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 4:
			orderList.remove(position);
			orderAdapter.setList();
			break;
			
		default:
			break;
		}
	}
	int position;
	
	void initTabUI(int sta){
		if(state == sta){
			return;
		}
		tvWait.setTextColor(getResources().getColor(sta==0?R.color.color_major_blue:R.color.color_gray_light));
		tvAlready.setTextColor(getResources().getColor(sta==1?R.color.color_major_blue:R.color.color_gray_light));
		tvFinish.setTextColor(getResources().getColor(sta==3?R.color.color_major_blue:R.color.color_gray_light));
		ivWait.setVisibility(sta==0?View.VISIBLE:View.GONE);
		ivAlready.setVisibility(sta==1?View.VISIBLE:View.GONE);
		ivFinish.setVisibility(sta==3?View.VISIBLE:View.GONE);
		state = sta;
		clearDatas();
	}
	
	private void clearDatas() {
		p = 1;
		page_size = 20;
		success = true;
		isQuery = true;
		isFinish = false;
		orderList.clear();
		queryRepairOrder();
	}
	
	private class OrderAdapter extends BaseAdapter{
		
		@SuppressLint("HandlerLeak")
		Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					notifyDataSetChanged();
					break;

				default:
					break;
				}
			};};
		
		private LayoutInflater mInflater = null;
		
		public OrderAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public void setList(){
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return orderList.size();
		}

		@Override
		public Object getItem(int position) {
			return orderList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			ViewHolder h;
			if(v == null)
		    {
		        v = mInflater.inflate(R.layout.item_order, null);
		        h = new ViewHolder();
		        h.tvName = (TextView) v.findViewById(R.id.textView_order_name);
		        h.tvDe = (TextView) v.findViewById(R.id.textView_order_falultDesc);
		        h.tvTime = (TextView) v.findViewById(R.id.textView_order_time);
		        v.setTag(h);
		    }else
		    {
		        h = (ViewHolder)v.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	Device a = orderList.get(position);
		    	h.tvName.setText(a.getEquipName()==null ? "" :a.getEquipName());
		    	h.tvDe.setText((a.getFalultDesc()==null ? "" :a.getFalultDesc()));
		    	h.tvTime.setText((a.getBookingTime()==null ? "" :a.getBookingTime()));
		    	
		    }
			return v;
		}
		
		class ViewHolder
		{
			TextView tvName;
			TextView tvDe;
			TextView tvTime;
		}
		
	}
	
	private void queryRepairOrder(){
		reload();
		isQuery = false;
		new Thread(new Runnable() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				try {
					String response = DataService.queryRepairOrder(getApplicationContext()
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, state, p, page_size, MAIN_TYPE);
					mLog.d("http", "p:"+p+",response："+response);
					if(response == null){
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}else {
						final HttpResult hr = JSON.parseObject(response,HttpResult.class);
						if (hr != null){
							success = true;
							if  (hr.isSuccess()){
								if(hr.getTotalPage() <= p){
									isFinish = true;
								}
								orderList.addAll(JSON.parseArray(hr.getData().toString(), Device.class));
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										orderAdapter.setList();
									}  
								});
							}
							else{
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										Toast.makeText(getApplicationContext(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
										orderAdapter.setList();
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
									orderAdapter.setList();
								}  
							});
						}

					}
				} catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						}  
					});
				}
				isQuery = true;
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
	
	private Dialog loadingDialog = null;
	private void closeLoadingDialog() {
		if(null != loadingDialog) {	 
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}

}
