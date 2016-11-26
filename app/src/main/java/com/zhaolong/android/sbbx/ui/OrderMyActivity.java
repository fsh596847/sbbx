package com.zhaolong.android.sbbx.ui;

import java.util.ArrayList;
import java.util.List;

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
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.SyncImageLoaderListview;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class OrderMyActivity extends Activity {
	
	private int MAIN_TYPE;//0，医生端；1，工程师端
	
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
		setContentView(R.layout.activity_order_my);
		MAIN_TYPE = getIntent().getIntExtra("type", 1);
		
		orderListView = (ListView) findViewById(R.id.listView_order_my);
		orderAdapter = new OrderAdapter(getApplicationContext(),orderListView);
		orderListView.setAdapter(orderAdapter);
		orderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position= arg2;
				switch (MAIN_TYPE) {
				case 1:
					switch (orderList.get(arg2).getState()) {
					case 0:
						startActivityForResult(new Intent(OrderMyActivity.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 1),4);
						break;

					case 1:
						startActivity(new Intent(OrderMyActivity.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 2));
						break;
						
					case 3:
						startActivity(new Intent(OrderMyActivity.this, JudgeActivty.class)
						.putExtra("userclass", 1));
						break;
						
					default:
						startActivity(new Intent(OrderMyActivity.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 4));
						break;
					}
					break;

				case 0:
					switch (orderList.get(arg2).getState()) {
					case 0:
						startActivityForResult(new Intent(OrderMyActivity.this, OrderWaitActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("type", 3),4);
						break;
						
					case 3:
						startActivity(new Intent(OrderMyActivity.this, EngineerRepairActivity.class)
						.putExtra("device", orderList.get(arg2))
						.putExtra("see", true));
						/*startActivity(new Intent(OrderMyActivity.this, JudgeActivty.class)
						.putExtra("userclass", 0));*/
						/*startActivity(new Intent(OrderMyActivity.this, DoctoRevaluateActivity.class)
						.putExtra("device", orderList.get(arg2)));*/
						/*startActivity(new Intent(OrderMyActivity.this, DoctoRevaluateActivity.class)
						.putExtra("device", orderList.get(arg2)));*/
						break;

					default:
						startActivity(new Intent(OrderMyActivity.this, OrderWaitActivity.class)
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
		findViewById(R.id.iv_order_my_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		clearDatas();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 4:
			clearDatas();
			break;
			
		default:
			break;
		}
	}
	int position;
	
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
		
		ListView mListView;
		private Drawable[] drawables;
		
		private LayoutInflater mInflater = null;
		private Context mContext;
		
		public OrderAdapter(Context context, ListView lvExpert){
			this.mInflater = LayoutInflater.from(context);
			mContext = context;
			mListView = lvExpert;
			mListView.setOnScrollListener(onScrollListener);
		}
		
		public void setList(){
			if(orderList.size()>0){
				if(drawables != null){
					Drawable[] d = new Drawable[orderList.size()];
					for (int i = 0; i < drawables.length; i++) {
						d[i] = drawables[i];
						drawables = d;
					}
				}else{
					drawables = new Drawable[orderList.size()];
				}
			}
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
		        v = mInflater.inflate(R.layout.item_device, null);
		        h = new ViewHolder();
		        h.iv = (ImageView) v.findViewById(R.id.imageView_item_device);
		        h.tvName = (TextView) v.findViewById(R.id.textView_item_device_name);
		        h.tvCode = (TextView) v.findViewById(R.id.textView_item_device_code);
		        h.tvType = (TextView) v.findViewById(R.id.textView_item_device_type);
		        v.setTag(h);
		    }else
		    {
		        h = (ViewHolder)v.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	Device a = orderList.get(position);
		    	String name = "";
		    	switch (a.getState()) {
				case 0:
					name = "待接单";
					break;

				case 1:
					name = "已接单";
					break;
					
				case 2:
					name = "已拒绝";
					break;
					
				case 3:
					name = "已完成";
					break;
					
				case 4:
					name = "已取消";
					break;
					
				case 5:
					name = "已关闭";
					break;
				}
		    	h.tvName.setText(name);
		    	h.tvCode.setText(a.getEquipName()==null ? "" :a.getEquipName());
		    	h.tvType.setText(a.getRepairTime()==null ? "" :a.getRepairTime());
		    	
		    	String img = a.getImgurl();
		    	Drawable d = position>=drawables.length?null:drawables[position];
				if(d != null){
					h.iv.setImageDrawable(d);
				}
				else if(img != null){
					h.iv.setImageResource(R.drawable.app_logo);
					syncImageLoader.loadImage(mContext,position,img,imageLoadListener,0);
		    	}
		    }
			return v;
		}
		
		class ViewHolder
		{
			ImageView iv;
			TextView tvName;
			TextView tvCode;
			TextView tvType;
		}
		
		SyncImageLoaderListview.OnImageLoadListener imageLoadListener = new SyncImageLoaderListview.OnImageLoadListener(){

			@Override
			public void onImageLoad(Integer t, Drawable drawable,Integer index) {
				drawables[t] = drawable;
				handler.sendEmptyMessage(0);
			}
			@Override
			public void onError(Integer t) {
			}

		};

		SyncImageLoaderListview syncImageLoader = new SyncImageLoaderListview();
		public void loadImage(){
			int start = mListView.getFirstVisiblePosition();
			int end =mListView.getLastVisiblePosition();
			if(end >= getCount()){
				end = getCount() -1;
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
	
	private void queryRepairOrder(){
		reload();
		isQuery = false;
		new Thread(new Runnable() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				try {
					String response = DataService.queryMyOrder(getApplicationContext()
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, MAIN_TYPE, p, page_size);
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
									}  
								});
							}
						}else{
							runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
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
