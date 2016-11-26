package com.zhaolong.android.sbbx.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.beans.Company;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.sbbx.windows.LoadingDialog;

public class CompanyChooseActivity extends Activity {
	
	ListView listView;
	TextView tvTitle;
	
	MyListViewAdapter adapter;
	int userclass;//0医技 1工程师
	List<Company> list = new ArrayList<Company>();
	//分页刷新
	private int p; 
	private int page_size;
	private boolean success;//是否更新成功
	private boolean isQuery;//是否正在更新
	private boolean isFinish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company_choose);
		
		userclass = getIntent().getIntExtra("userclass", -1);
		if(userclass < 0 || userclass > 1){
			finish();
			return;
		}
		
		listView = (ListView) findViewById(R.id.listView_company);
		tvTitle = (TextView) findViewById(R.id.tv_company_title);
		
		switch (userclass) {
		case 0:
			tvTitle.setText("所属医院");
			break;

		case 1:
			tvTitle.setText("所属企业");
			break;
		}
		adapter = new MyListViewAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		
		findViewById(R.id.iv_company_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
		            		queryCompany();
		            	}
		            }
		        }
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				setResult(100, new Intent().putExtra("company", list.get(arg2)));
				finish();
			}
		});
		
		p = 1;
		page_size = 30;
		success = true;
		isQuery = true;
		isFinish = false;
		queryCompany();
	}
	
	
	private void queryCompany(){
		reload();
		isQuery = false;
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					String s = DataService.queryCompany(CompanyChooseActivity.this, userclass, p, page_size);
					mLog.d("http", "s:"+s);
					if (!HlpUtils.isEmpty(s)){
						final HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							success = true;
							if  (hr.isSuccess()){
								if(hr.getTotalPage() <= p){
									isFinish = true;
								}
								final List<Company> companys = JSON.parseArray(hr.getData().toString(),Company.class);
								runOnUiThread(new Runnable(){  
									@Override  
									public void run() {  
										adapter.setList(companys);
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
						}
					}else{
						runOnUiThread(new Runnable(){  
							@Override  
							public void run() {  
								Toast.makeText(getApplicationContext(), "请检查网络后重试！", Toast.LENGTH_SHORT).show();
							}  
						});
					}
				}catch (final Exception he) {
					he.printStackTrace();
					mLog.e("http", "Exception:"+he.getMessage());
					runOnUiThread(new Runnable(){  
						@Override  
						public void run() {  
							Toast.makeText(getApplicationContext(), he.getMessage(), Toast.LENGTH_SHORT).show();
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
	
	
	private class MyListViewAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater = null;
		
		private MyListViewAdapter(Context context)
	    {
	        this.mInflater = LayoutInflater.from(context);
	    }
		
		private void setList(List<Company> listc)
	    {
	        if(listc!=null){
	        	list.addAll(listc);
	        	notifyDataSetChanged();
	        }
	    }
	    
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return list.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
		    if(convertView == null)
		    {
		        holder = new ViewHolder();
		        convertView = mInflater.inflate(R.layout.item_textview, null);
		        holder.tvText = (TextView)convertView.findViewById(R.id.tv_item_textview);
		        
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		    
		    if(getCount()>0)
		    {
		    	Company detail = list.get(position);
		    	holder.tvText.setText(detail.getCompany());
		    }
		                                                                                                 
		    return convertView;
		}
		
		class ViewHolder
		{
			public TextView tvText;
		}

	}


}
