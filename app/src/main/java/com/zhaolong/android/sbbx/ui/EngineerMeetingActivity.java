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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.beans.Meeting;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.SyncImageLoaderListview;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class EngineerMeetingActivity extends Activity {
	
	MyAdapter myAdapter;
	ListView listView;
	List<Meeting> meetingList = new ArrayList<Meeting>();
	//分页刷新
	private int p; 
	private int page_size;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	DisplayMetrics dm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_engineer_meeting);
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		listView = (ListView) findViewById(R.id.listView_meeting);
		myAdapter = new MyAdapter(getApplicationContext(), listView);
		listView.setAdapter(myAdapter);
		
		listView.setOnScrollListener(new OnScrollListener() {
			
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
		            		queryMeetinfo();
		            	}
		            }
		        }
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				startActivityForResult(new Intent(EngineerMeetingActivity.this, WebActivity.class)
				.putExtra("url", meetingList.get(arg2).getUrl())
				.putExtra("title", "会议"),100);
				position = arg2;
				
			}
		});
		findViewById(R.id.iv_meeting_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		p = 1;
		page_size = 20;
		success = true;
		isQuery = true;
		isFinish = false;
		queryMeetinfo();
	}
	int position;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 100:
			meetingList.get(position).setIssign(1);
			myAdapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}
	
	private class MyAdapter extends BaseAdapter{
		
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
		
		public MyAdapter(Context context, ListView lvExpert){
			this.mInflater = LayoutInflater.from(context);
			mContext = context;
			mListView = lvExpert;
			mListView.setOnScrollListener(onScrollListener);
		}
		
		public void setList(){
			if(meetingList.size()>0){
				if(drawables != null){
					Drawable[] d = new Drawable[meetingList.size()];
					for (int i = 0; i < drawables.length; i++) {
						d[i] = drawables[i];
						drawables = d;
					}
				}else{
					drawables = new Drawable[meetingList.size()];
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return meetingList.size();
		}

		@Override
		public Object getItem(int position) {
			return meetingList.get(position);
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
		        v = mInflater.inflate(R.layout.item_meeting, null);
				RelativeLayout ad_div = (RelativeLayout)v.findViewById(R.id.rl_meeting);
				//广告图的大小是16（长）:9（宽），所以做此调整
				LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 9*dm.widthPixels/16);
				ad_div.setLayoutParams(layout);
		        h = new ViewHolder();
		        h.iv = (ImageView) v.findViewById(R.id.ImageView_meeting);
		        h.tvName = (TextView) v.findViewById(R.id.textView_meeting_name);
		        h.tvTime = (TextView) v.findViewById(R.id.textView_meeting_time);
		        h.tvAddress = (TextView) v.findViewById(R.id.textView_meeting_address);
		        h.tvIssign = (TextView) v.findViewById(R.id.textView_meeting_issign);
		        v.setTag(h);
		    }else
		    {
		        h = (ViewHolder)v.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	Meeting a = meetingList.get(position);
		    	h.tvName.setText(a.getMeetname()==null ? "" :a.getMeetname());
		    	h.tvTime.setText(a.getStarttime()==null ? "" :a.getStarttime());
		    	h.tvAddress.setText(a.getAddress()==null ? "" :a.getAddress());
		    	switch (a.getIssign()) {
				case 1:
					h.tvIssign.setText("已报名");
					h.tvIssign.setBackgroundColor(mContext.getResources().getColor(R.color.color_gray));
					break;

				default:
					h.tvIssign.setText("未报名");
					h.tvIssign.setBackgroundColor(mContext.getResources().getColor(R.color.color_major_blue));
					break;
				}
		    	
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
			TextView tvTime;
			TextView tvAddress;
			TextView tvIssign;
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
	
	private void queryMeetinfo(){
		reload();
		isQuery = false;
		new Thread(new Runnable() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				try {
					String response = DataService.queryMeetinfo(getApplicationContext()
							, new SpData(getApplicationContext()).getStringValue(SpData.keyId, null)
							, new SpData(getApplicationContext()).getStringValue(SpData.keyPhone, null)
							, p, page_size,null);
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
								meetingList.addAll(JSON.parseArray(hr.getData().toString(), Meeting.class));
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										myAdapter.setList();
									}  
								});
							}else{
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
