package com.zhaolong.android.sbbx.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.beans.Message;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class MessageFragment extends Fragment {

	private ListView listView;
	private List<Message> listMessage;
	private MyAdapter adapter;
	//分页刷新
	private int p; 
	private int page;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message, container, false);
		
		p = 1;
		page = 20;
		success = true;
		isQuery = true;
		isFinish = false;
		listMessage = new ArrayList<Message>();
		
		
		listView = (ListView) v.findViewById(R.id.listView_message);
		adapter = new MyAdapter(getActivity());
		listView.setAdapter(adapter);
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
		            	if(success && isQuery){
		            		p+=1;
		            		getMyNotice(p,page);
		            	}
		            }
		        }
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String optye = listMessage.get(arg2).getOptye();
				//医生端订单
				if("OPEN_MEDICAL_WAIT_ACCEPT".equals(optye) //待接单
						|| "OPEN_MEDICAL_Y_ACCEPT".equals(optye) //已接单
						|| "OPEN_MEDICAL_Y_REFUSE".equals(optye)//已拒绝
						|| "OPEN_MEDICAL_CANCEL".equals(optye)//已取消
						){
					startActivity(new Intent(getActivity(), OrderMyActivity.class)
					.putExtra("type", 0));
				}
				//工程师端订单
				else if("OPEN_ENGINEER_WAIT_ACCEPT".equals(optye) //待接单
						|| "OPEN_ENGINEER_Y_ACCEPT".equals(optye)//已接单
						|| "OPEN_ENGINEER_REFUSE".equals(optye)//已拒绝
						|| "OPEN_ENGINEER_CANCEL".equals(optye)//已取消
						){
					startActivity(new Intent(getActivity(), OrderMyActivity.class)
					.putExtra("type", 1));
				}
				//医生端评价
				else if("OPEN_MEDICAL_N_EVALUATE".equals(optye) //未评价
						|| "OPEN_MEDICAL_Y_EVALUATE".equals(optye)//已评价 
						){
					startActivity(new Intent(getActivity(), JudgeActivty.class)
					.putExtra("userclass", 0));
				}
				//工程师端评价
				else if("OPEN_ENGINEER_N_EVALUATE".equals(optye) //未评价
						|| "OPEN_ENGINEER_Y_EVALUATE".equals(optye)//已评价 
						){
					startActivity(new Intent(getActivity(), JudgeActivty.class)
					.putExtra("userclass", 1));
				}
				updateMyNotice(arg2);
			}
		});
		//返回
		ivBack = (ImageView) v.findViewById(R.id.iv_message_back);
		ivBack.setVisibility(visibility);
		ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getActivity().finish();
			}
		});
		
		getMyNotice(p,page);
		
		return v;
	}
	ImageView ivBack;
	
	public void setIvBackVisibilityGONE(){
		visibility = View.GONE;
	}
	int visibility = View.VISIBLE;
	
	private void updateMyNotice(final int position) {
		//reload("正在努力加载……");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String s = DataService.updateMyNotice(getActivity(), listMessage.get(position).getId(),1);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if(hr != null){
							if(hr.isSuccess()){
								if(!isDestroy){
									listMessage.get(position).setState(1);
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() { 
											adapter.setList();
										}  
									});
								}
							}else{
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
						}
					}else{
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				//closeLoadingDialog();
			}
		}).start();
	}
	
	private void getMyNotice(final int p,final int pagesize) {
		isQuery = false;
		reload();
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String s = DataService.getMyNotice(getActivity()
							,new SpData(getActivity()).getStringValue(SpData.keyPhone, null)
							, p, pagesize);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if(hr != null){
							success = true;
							if(hr.isSuccess()){
								if(!isDestroy){
									if(hr.getTotalPage()<=p){
										isFinish = true;
									}
									final List<Message> list = JSON.parseArray(hr.getData().toString(), Message.class);
									if(list != null && list.size()>0){
										listMessage.addAll(list);
										getActivity().runOnUiThread(new Runnable(){  
											@Override  
											public void run() { 
												adapter.setList();
											}  
										});
									}
								}
							}else{
								getActivity().runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										Toast.makeText(getActivity(), hr.getData().toString(), Toast.LENGTH_SHORT).show();
									}  
								});
							}
						}else{
							success = false;
						}
					}else{
						success = false;
						getActivity().runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getActivity(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (Exception he) {
					he.printStackTrace();
				}
				
				closeLoadingDialog();
				isQuery = true;

			}
		}).start();
	}
	
	
	public class MyAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater = null;
		
		
	    public MyAdapter(Context context)
	    {
	        this.mInflater = LayoutInflater.from(context);
	    }
	    
	    public void setList(){
	    	if(listMessage!=null){
	    		notifyDataSetChanged();
	    	}
	    }
	    

		@Override
		public int getCount() {
			return listMessage.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return listMessage.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
		    if(convertView == null)
		    {
		        holder = new ViewHolder();
		        convertView = mInflater.inflate(R.layout.item_message, null);
		        holder.ivPoint = (ImageView) convertView.findViewById(R.id.iv_item_message_point);
		        holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_item_message_title);
		        holder.tvContent = (TextView)convertView.findViewById(R.id.tv_item_message_content);
		        holder.tvTime = (TextView)convertView.findViewById(R.id.tv_item_message_time);
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	Message detail = listMessage.get(position);
		    	holder.ivPoint.setVisibility(detail.getState()==0?View.VISIBLE:View.GONE);
		    	holder.tvTitle.setText(detail.getNtitle()==null?"":detail.getNtitle());
		    	holder.tvContent.setText(detail.getNcontent()==null?"":detail.getNcontent());
		    	holder.tvTime.setText(detail.getCreatetime()==null?"":detail.getCreatetime());
		    }
		                                                                                                 
		    return convertView;
		}
		
		class ViewHolder
		{
			public ImageView ivPoint;
			public TextView tvTitle;
			public TextView tvContent;
			public TextView tvTime;
		}

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
	
	boolean isDestroy = false;
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
