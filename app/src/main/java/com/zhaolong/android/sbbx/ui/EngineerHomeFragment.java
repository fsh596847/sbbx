package com.zhaolong.android.sbbx.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.zhaolong.android.sbbx.ConfigSbbx;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.SpData;
import com.zhaolong.android.sbbx.beans.HttpResult;
import com.zhaolong.android.sbbx.beans.ImageHome;
import com.zhaolong.android.sbbx.services.DataService;
import com.zhaolong.android.sbbx.utils.HlpUtils;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zxing.scan.CaptureActivity;

public class EngineerHomeFragment extends Fragment implements OnClickListener, OnPageChangeListener {
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {  
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {  
			// 如果有更新就提示  
			switch (msg.what) {
			case 0:
				if(!isF && mImageViews != null){
					selectItems += 1;
					viewPager.setCurrentItem(selectItems);
					setImageBackground(selectItems % mImageViews.length);
				}
				break;
				
			case 2:
				initPublicity(listHomeImg.size());
				break;
				
			case 3:
				//msg.arg1;
				mImageViews[msg.arg1].setBackground(imgIdArray[msg.arg1]);
				break;
			}

		};  
	}; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_engineer_home, container, false); 
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		RelativeLayout ad_div = (RelativeLayout)view.findViewById(R.id.layout_engineer_home_imag);
		//广告图的大小是16（长）:9（宽），所以做此调整
		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 9*dm.widthPixels/16);
		ad_div.setLayoutParams(layout);
		
		view.findViewById(R.id.btn_engineer_home_activate).setOnClickListener(this);
		view.findViewById(R.id.btn_doctor_home_sign).setOnClickListener(this);
		view.findViewById(R.id.btn_engineer_home_find).setOnClickListener(this);
		view.findViewById(R.id.btn_engineer_home_order).setOnClickListener(this);
		view.findViewById(R.id.btn_engineer_home_plan).setOnClickListener(this);
		view.findViewById(R.id.btn_engineer_home_call).setOnClickListener(this);
		view.findViewById(R.id.btn_engineer_main_statement).setOnClickListener(this);
		
		group = (ViewGroup)view.findViewById(R.id.viewGroup_engineer_homepage);
		viewPager = (ViewPager) view.findViewById(R.id.viewPager_engineer_homepage);
		
		getImageInfo();
		return view;
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_engineer_home_activate://扫码激活
			startActivity(new Intent(getActivity(), CaptureActivity.class)
			.putExtra("type", CaptureActivity.COM_ACTIVATE));
			break;

		case R.id.btn_doctor_home_sign://扫码签到
			startActivity(new Intent(getActivity(), CaptureActivity.class)
			.putExtra("type", CaptureActivity.COM_SGIN));
			break;
			
		case R.id.btn_engineer_home_find://设备查询
			startActivity(new Intent(getActivity(), EngineerDeviceActivity.class));
			break;
			
		case R.id.btn_engineer_home_order:
			startActivity(new Intent(getActivity(), OrderActivty.class)
			.putExtra("type", 2));
			break;
			
		case R.id.btn_engineer_home_plan://培训会议
			startActivity(new Intent(getActivity(), EngineerMeetingActivity.class));
			break;
			
		case R.id.btn_engineer_home_call:
			Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:4001235698"));  
			startActivity(intent); 
			break;
			
		case R.id.btn_engineer_main_statement:
			startActivity(new Intent(getActivity(), WebActivity.class)
			.putExtra("url", ConfigSbbx.phone_engineerIndex
					+"?userid="+new SpData(getActivity()).getStringValue(SpData.keyId, null)
					+"&mobile="+new SpData(getActivity()).getStringValue(SpData.keyPhone, null))
			.putExtra("title", "业务统计"));
			break;
		}
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		isF = false;
		stopPlay();
	}

	@Override
	public void onStop() {
		super.onStop();
		isF = true;
	}
	
	boolean isF = false;
	private void stopPlay(){
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (!isF) {
					try {
						Thread.sleep(4000);
						handler.sendEmptyMessage(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}
	
	/**
	 * 获取后台图片信息
	 */
	private void getImageInfo(){
		if(getImgSuccess){
			mLog.w("http", "getImageInfo   return");
			return;
		}
		getImgSuccess = true;
		//reload("正在努力加载……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					SpData sp = new SpData(getActivity());
					String ss = DataService.getHomeImg(getActivity(), sp.getStringValue(SpData.keyId, null), "1");
					mLog.e("http", "ss："+ss);
					if (!HlpUtils.isEmpty(ss)){
						HttpResult hr = JSON.parseObject(ss,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								listHomeImg = JSON.parseArray(hr.getData().toString(), ImageHome.class);
								if(!isF && listHomeImg != null){
									//先显示默认图片
									imgIdArray = new Drawable[listHomeImg.size()];
									handler.sendEmptyMessage(2);
									//获取后台图片
									for (int i = 0; i < listHomeImg.size(); i++) {
										//getImage(listHomeImg.get(i).getImgUrl(), i);
										byte[] data;
										for (int j = 0; j < 3; j++) {
											try {
												data = DataService.getImage(listHomeImg.get(i).getImgurl());
												if(data!=null){ 
													getImgSuccess = true;
													j = 100;
													if(!isF){
														final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
														imgIdArray[i] = new BitmapDrawable(getResources(), mBitmap);
														Message msg = new Message();
														msg.arg1 = i;
														msg.what = 3;
														handler.sendMessage(msg);
													}
												}
												else{
													getImgSuccess = false;
													Thread.sleep(3000);
												}
											} catch (Exception e) {
												j = 0;
												e.printStackTrace();
											}
										}
									}
								}
							}else{
								getImgSuccess = false;
							}
						}else{
							getImgSuccess = false;
						}
					}else{
						getImgSuccess = false;
					}
					/*String s = DataService.getDoctorHomeImg(getActivity());
					if (!HlpUtils.isEmpty(s)){
						HttpResult hr = JSON.parseObject(s,HttpResult.class);
						if (hr != null){
							if  (hr.isSuccess()){
								listHomeImg = JSON.parseArray(hr.getData().toString(), HomeImg.class);
								if(!isF && listHomeImg != null){
									//获取后台图片
									for (int i = 0; i < listHomeImg.size(); i++) {
										getImage(listHomeImg.get(i).getImgUrl(), i);
									}
									//先显示默认图片
									getActivity().runOnUiThread(new Runnable(){  
										@Override  
										public void run() {  
											initPublicity(listHomeImg.size());
										}  
									});
								}
							}else{
							}
						}else{
						}
					}else{
					}*/
				}catch (Exception he) {
					mLog.w("http", "Exception"+he.getMessage());
					getImgSuccess = false;
					he.printStackTrace();
				}
				//closeLoadingDialog();

			}
		}).start();
	}
	boolean getImgSuccess = false;
	List<ImageHome> listHomeImg;
	
	@SuppressLint("NewApi")
	private void initPublicity(int length) {
		if(length <= 0 /*|| (imgIdArray != null && imgIdArray.length > 0)*/){
			return;
		}

		//imgIdArray = new Drawable[length];
		for (int i = 0; i < length; i++) {
			imgIdArray[i] = getResources().getDrawable(R.drawable.login_logo);
		}
		tips = new ImageView[imgIdArray.length];

		//将点点加入到ViewGroup中  
		tips = new ImageView[imgIdArray.length];  
		for(int i=0; i<tips.length; i++){  
			ImageView imageView = new ImageView(getActivity());  
			imageView.setLayoutParams(new LayoutParams(10,10));  
			tips[i] = imageView;  
			if(i == 0){  
				tips[i].setBackgroundResource(R.drawable.round_hb);  
			}else{  
				tips[i].setBackgroundResource(R.drawable.round_hg);  
			}  

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,    
					LayoutParams.WRAP_CONTENT));  
			layoutParams.leftMargin = 5;  
			layoutParams.rightMargin = 5;  
			group.addView(imageView, layoutParams);  
		}

		//将图片装载到数组中  
		mImageViews = new ImageView[imgIdArray.length];  
		for(int i=0; i<mImageViews.length; i++){  
			ImageView imageView = new ImageView(getActivity());  
			mImageViews[i] = imageView;  
			imageView.setBackground(imgIdArray[i]);  
		} 

		//设置Adapter  
		MyAdapter myAdapter = new MyAdapter();
		viewPager.setAdapter(myAdapter);  
		//设置监听，主要是设置点点的背景  
		viewPager.setOnPageChangeListener(this);  
		//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动  
		viewPager.setCurrentItem(mImageViews.length * 100); 

	}
	private ViewPager viewPager;
	private ImageView[] tips;
	private ImageView[] mImageViews;
	private Drawable[] imgIdArray ;
	private ViewGroup group;
	
	private int selectItems = 0;
	private void setImageBackground(int selectItems){  
		Log.d("wht", "selectItems:"+selectItems);
		//this.selectItems = selectItems;
		for(int i=0; i<tips.length; i++){  
			if(i == selectItems){  
				tips[i].setBackgroundResource(R.drawable.round_hb);  
			}else{  
				tips[i].setBackgroundResource(R.drawable.round_hg);  
			}  
		}  
	}
	
	public class MyAdapter extends PagerAdapter{


		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//Warning：不要在这里调用removeView 
			//			((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			//对ViewPager页号求模取出View列表中要显示的项  
			position %= mImageViews.length;  
			if (position<0){  
				position = mImageViews.length+position;  
			}  
			ImageView view = mImageViews[position];  
			//如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。  
			ViewParent vp =view.getParent();  
			if (vp!=null){  
				ViewGroup parent = (ViewGroup)vp;  
				parent.removeView(view);  
			}  
			container.addView(view);    
			//add listeners here if necessary  
			final int p = position;
			return view; 

			//			((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0); 
			//			return mImageViews[position % mImageViews.length];
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		selectItems = arg0;
		setImageBackground(selectItems % mImageViews.length);
	}

}
