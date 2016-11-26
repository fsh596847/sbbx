package com.zhaolong.android.sbbx.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
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
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.zhaolong.android.sbbx.windows.LoadingDialog;
import com.zhaolong.android.views.CalendarView;

public class EngineerCalendarFragment extends Fragment {
	
	private CalendarView calendar;
	private ImageButton calendarLeft;
	private TextView calendarCenter;
	private ImageButton calendarRight;
	
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_engineer_calendar, container, false); 



		//获取日历控件对象
		calendar = (CalendarView)v.findViewById(R.id.calendar);
		calendar.setSelectMore(false); //单选  
		calendarLeft = (ImageButton)v.findViewById(R.id.calendarLeft);
		calendarCenter = (TextView)v.findViewById(R.id.calendarCenter);
		calendarRight = (ImageButton)v.findViewById(R.id.calendarRight);
		//设置日历日期
		Date d = new Date();
		date = HlpUtils.dateFormatter.format(d);
		calendar.setCalendarData(d);

		//获取日历中年月 ya[0]为年，ya[1]为月（格式大家可以自行在日历控件中改）
		/*String[] ya = calendar.getYearAndmonth().split("-"); 
				calendarCenter.setText(ya[0]+"年"+ya[1]+"月");*/
		calendarCenter.setText(calendar.getYearAndmonth());
		calendarLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//点击上一月 同样返回年月 
				String leftYearAndmonth = calendar.clickLeftMonth(); 
				String[] ya = leftYearAndmonth.split("-"); 
				calendarCenter.setText(ya[0]+"-"+ya[1]);
			}
		});

		calendarRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//点击下一月
				String rightYearAndmonth = calendar.clickRightMonth();
				String[] ya = rightYearAndmonth.split("-"); 
				calendarCenter.setText(ya[0]+"-"+ya[1]);
			}
		});

		//设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
		calendar.setOnItemClickListener(new com.zhaolong.android.views.CalendarView.OnItemClickListener() {

			@Override
			public void OnItemClick(Date selectedStartDate,
					Date selectedEndDate, Date downDate) {
				if(calendar.isSelectMore()){
					//Toast.makeText(getApplicationContext(), format.format(selectedStartDate)+"到"+format.format(selectedEndDate), Toast.LENGTH_SHORT).show();
				}else{
					date = HlpUtils.dateFormatter.format(downDate);
					clearDatas(true);
					//Toast.makeText(getActivity(), HlpUtils.dateFormatter.format(downDate), Toast.LENGTH_SHORT).show();
				}
			}
		});

		orderListView = (ListView) v.findViewById(R.id.listView_calendar);
		orderAdapter = new OrderAdapter(getActivity());
		orderListView.setAdapter(orderAdapter);
		orderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position= arg2;
				switch (orderList.get(arg2).getState()) {
				case 0:
					startActivityForResult(new Intent(getActivity(), OrderWaitActivity.class)
					.putExtra("device", orderList.get(arg2))
					.putExtra("type", 1),4);
					break;

				case 1:
					startActivityForResult(new Intent(getActivity(), OrderWaitActivity.class)
					.putExtra("device", orderList.get(arg2))
					.putExtra("type", 2),4);
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
							queryRepairOrder(true);
						}
					}
				}
			}
		});
		clearDatas(false);

		return v;
	}
	String date;
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 4:
			clearDatas(true);
			break;
			
		default:
			break;
		}
	}
	int position;
	
	private void clearDatas(boolean hint) {
		p = 1;
		page_size = 20;
		success = true;
		isQuery = true;
		isFinish = false;
		orderList.clear();
		queryRepairOrder(hint);
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
		        v = mInflater.inflate(R.layout.item_order_calendar, null);
		        h = new ViewHolder();
		        h.tvName = (TextView) v.findViewById(R.id.textView_order_calendar_name);
		        h.tvDe = (TextView) v.findViewById(R.id.textView_order_calendar_state);
		        h.tvTime = (TextView) v.findViewById(R.id.textView_order_calendar_time);
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
					name = "接单";
					break;

				case 1:
					name = "签到";
					break;
					
				}
		    	h.tvDe.setText(name);
		    	h.tvName.setText((a.getEquipName()==null ? "" :a.getEquipName()));
		    	h.tvTime.setText("预约时间："+((a.getBookingTime()==null ? "" :a.getBookingTime())));
		    	
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
	
	private void queryRepairOrder(final boolean hint){
		if(hint){
			reload();
		}
		isQuery = false;
		new Thread(new Runnable() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				try {
					String response = DataService.shortcutRepair(getActivity()
							, new SpData(getActivity()).getStringValue(SpData.keyId, null)
							, new SpData(getActivity()).getStringValue(SpData.keyPhone, null)
							, date, p, page_size);
					mLog.d("http", "p:"+p+",response："+response);
					if(response == null){
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
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
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() { 
										orderAdapter.setList();
									}  
								});
							}
							else{
								if(hint){
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
											orderList.clear();
											orderAdapter.setList();
										}  
									});
								}
							}
						}else{
							getActivity().runOnUiThread(new Runnable(){  
								@Override  
								public void run() { 
									Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
								}  
							});
						}

					}
				} catch (final Exception e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
						}  
					});
				}
				isQuery = true;
				if(hint){
					closeLoadingDialog();
				}
			}
		}).start();

	}
	
	private void reload(){
		if (loadingDialog == null){
			loadingDialog = LoadingDialog.createLoadingDialog(getActivity());
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
