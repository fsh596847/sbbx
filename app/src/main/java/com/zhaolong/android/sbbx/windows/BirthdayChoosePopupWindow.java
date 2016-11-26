package com.zhaolong.android.sbbx.windows;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.utils.mLog;
import com.zhaolong.android.views.PickerView;
import com.zhaolong.android.views.PickerView.onSelectListener;

@SuppressLint("ViewConstructor")
public class BirthdayChoosePopupWindow extends PopupWindow {
	
	private PickerView pvYear,pvMonth,pvDay,pvHH,pvMM,pvSS;
	private TextView tvCancel;
	private TextView tvOk;
	
	private int maxYear;
	private int minYear;
	/**
	 * 选择的日期
	 */
	private int yearSelect,monthSelect,daySelect,hhSelect,mmSelect,ssSelect;
	
	private CallBackBirthdayChoose callBack;
	
	public BirthdayChoosePopupWindow(Activity context){
		
		View customView = context.getLayoutInflater().inflate(R.layout.popupwindow_birthday_choose, null, false); 
		
		setContentView(customView);
		
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		//setAnimationStyle(R.style.AnimationFade);  
		
		initDatas();
		initViews(customView);
		initListeners(customView);
		initPickerView();
	}
	
	private void initDatas() {
		Calendar c = Calendar.getInstance();
		maxYear = c.get(Calendar.YEAR)+1;
		minYear = maxYear - 2;
		monthSelect = c.get(Calendar.MONTH)+1;
        daySelect = c.get(Calendar.DATE);
        hhSelect = 10;
        mmSelect = 0;
        ssSelect = 0;
	}

	private void initListeners(View customView) {
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
				if(callBack != null){
					callBack.dismiss();
				}
			}
		});
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dismiss();
				if(callBack != null){
					callBack.select(yearSelect, monthSelect, daySelect, hhSelect, mmSelect, ssSelect);
				}
			}
		});
		
		pvYear.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				yearSelect = Integer.parseInt(text);
				daySelect = daySelect <= getDays(yearSelect, monthSelect) ? daySelect : getDays(yearSelect, monthSelect);
				List<String> dayData = getDayData(yearSelect, monthSelect, daySelect);
				pvDay.setData(dayData);
				mLog.d("birthday", "yearSelect:"+yearSelect);
			}
		});
		
		pvMonth.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				monthSelect = Integer.parseInt(text);
				daySelect = daySelect <= getDays(yearSelect, monthSelect) ? daySelect : getDays(yearSelect, monthSelect);
				List<String> dayData = getDayData(yearSelect, monthSelect, daySelect);
				pvDay.setData(dayData);
				mLog.d("birthday", "monthSelect:"+monthSelect);
			}
		});
		
		pvDay.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				daySelect = Integer.parseInt(text);
				mLog.d("birthday", "daySelect:"+daySelect);
			}
		});
		
		pvHH.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				hhSelect = Integer.parseInt(text);
				mLog.d("birthday", "hhSelect:"+hhSelect);
			}
		});
		
		pvMM.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				mmSelect = Integer.parseInt(text);
				mLog.d("birthday", "mmSelect:"+mmSelect);
			}
		});
		
		pvSS.setOnSelectListener(new onSelectListener()
		{

			@Override
			public void onSelect(String text)
			{
				ssSelect = Integer.parseInt(text);
				mLog.d("birthday", "ssSelect:"+ssSelect);
			}
		});
	}
	
	private void initPickerView() {
		yearSelect = maxYear-1;
		List<String> yearData = getYearData(yearSelect);
		List<String> monthData = getMonthData(monthSelect);
		List<String> dayData = getDayData(yearSelect, monthSelect, daySelect);
		List<String> hhData = new ArrayList<String>(); 
		List<String> mmData = new ArrayList<String>(); 
		List<String> ssData = new ArrayList<String>(); 
		for (int i = 0; i < 24; i++) {
			String s = String.valueOf(i);
			if(s.length() == 1){
				s = "0"+s;
			}
			hhData.add(s);
		}
		if(hhSelect-1 - 23/2 > 0){
			for (int i = 1; i <= hhSelect-1 - 23/2; i++) {
				String head = hhData.get(0);
				hhData.remove(0);
				hhData.add(head);
			}
		}else if(hhSelect-1 - 23/2 < 0){
			for (int i = 1; i <= 23/2 - hhSelect+1; i++) {
				String tail = hhData.get(hhData.size() - 1);
				hhData.remove(hhData.size() - 1);
				hhData.add(0, tail);
			}
		}
		for (int i = 0; i < 60; i++) {
			String s = String.valueOf(i);
			if(s.length() == 1){
				s = "0"+s;
			}
			mmData.add(s);
			ssData.add(s);
		}
		if(mmSelect-1 - 59/2 > 0){
			for (int i = 1; i <= mmSelect-1 - 59/2; i++) {
				String head = mmData.get(0);
				mmData.remove(0);
				mmData.add(head);
			}
		}else if(mmSelect-1 - 59/2 < 0){
			for (int i = 1; i <= 59/2 - mmSelect+1; i++) {
				String tail = mmData.get(mmData.size() - 1);
				mmData.remove(mmData.size() - 1);
				mmData.add(0, tail);
			}
		}
		if(ssSelect-1 - 59/2 > 0){
			for (int i = 1; i <= ssSelect-1 - 59/2; i++) {
				String head = ssData.get(0);
				ssData.remove(0);
				ssData.add(head);
			}
		}else if(ssSelect-1 - 59/2 < 0){
			for (int i = 1; i <= 59/2 - ssSelect+1; i++) {
				String tail = ssData.get(ssData.size() - 1);
				ssData.remove(ssData.size() - 1);
				ssData.add(0, tail);
			}
		}
		pvYear.setData(yearData);
		pvMonth.setData(monthData);
		pvDay.setData(dayData);
		pvHH.setData(hhData);
		pvMM.setData(mmData);
		pvSS.setData(ssData);
	}
	
	private void initViews(View customView) {
		
		pvYear = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_year);
		pvMonth = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_month);
		pvDay = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_day);
		pvHH = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_hh);
		pvMM = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_mm);
		pvSS = (PickerView) customView.findViewById(R.id.pickerview_birthday_choose_ss);
		tvCancel = (TextView) customView.findViewById(R.id.tv_birthday_choose_cancel);
		tvOk = (TextView) customView.findViewById(R.id.tv_birthday_choose_ok);
		
	}
	
	/**
	 * 
	 * @param year 要显示的当前（或选择）年份
	 * @return
	 */
	private List<String> getYearData(int year){
		List<String> yearData = new ArrayList<String>();
		for (int i = maxYear; i >= minYear; i--)
		{
			yearData.add(String.valueOf(i));
		}
		if(year - (maxYear-(maxYear-minYear)/2) < 0){
			for (int i = 1; i <= (maxYear-(maxYear-minYear)/2) - year; i++) {
				String head = yearData.get(0);
				yearData.remove(0);
				yearData.add(head);
			}
		}else if(year - (maxYear-(maxYear-minYear)/2) > 0){
			for (int i = 1; i <= year - (maxYear-(maxYear-minYear)/2); i++) {
				String tail = yearData.get(yearData.size() - 1);
				yearData.remove(yearData.size() - 1);
				yearData.add(0, tail);
			}
		}
		return yearData;
	}

	/**
	 * view月的list
	 * @param month 要显示的当前（或选择）月份
	 * @return
	 */
	private List<String> getMonthData(int month){
		List<String> monthData = new ArrayList<String>();
		for (int i = 1; i <= 12; i++)
		{
			monthData.add(String.valueOf(i));
		}
		if(month-1 - 6 > 0){
			for (int i = 1; i <= month-1 - 6; i++) {
				String head = monthData.get(0);
				monthData.remove(0);
				monthData.add(head);
			}
		}else if(month-1 - 6 < 0){
			for (int i = 1; i <= 6 - month+1; i++) {
				String tail = monthData.get(monthData.size() - 1);
				monthData.remove(monthData.size() - 1);
				monthData.add(0, tail);
			}
		}
		return monthData;
	}

	/**
	 * view日的list
	 * @param year
	 * @param month 
	 * @param day 要显示的当前（或选择）日
	 * @return
	 */
	private List<String> getDayData(int year, int month, int day){
		List<String> dayData = new ArrayList<String>();
		for (int i = 1; i <= getDays(year, month); i++)
		{
			dayData.add(String.valueOf(i));
		}
		if(day-1 - getDays(year, month)/2 > 0){
			for (int i = 1; i <= day-1 - getDays(year, month)/2; i++) {
				String head = dayData.get(0);
				dayData.remove(0);
				dayData.add(head);
			}
		}else if(day-1 - getDays(year, month)/2 < 0){
			for (int i = 1; i <= getDays(year, month)/2 - day+1; i++) {
				String tail = dayData.get(dayData.size() - 1);
				dayData.remove(dayData.size() - 1);
				dayData.add(0, tail);
			}
		}
		return dayData;
	}

	/**
	 * 某年某月的天数
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDays(int year, int month){
		int days = 31;
		if(month == 4 || month == 6 || month == 9 || month == 11){
			days = 30;
		}
		else if(month == 2){
			if (year % 4 == 0 && year % 100!=0||year%400==0) {  
				days = 29;
			} else{
				days = 28;
			}
		}
		return days;
	}
	
	public void setCallBackBirthdayChoose(CallBackBirthdayChoose callBack){
		this.callBack = callBack;
	}
	
	public interface CallBackBirthdayChoose {  
	    void dismiss(); 
	    void select(int yearSelect, int monthSelect, int daySelect, int hhSelect, int mmSelect, int ssSelect);
	} 
	
	/**
	 * minYear ~ maxYear
	 * @param maxYear 
	 * @param minYear
	 */
	public void setYearMaxMin(int maxYear, int minYear) {
		if(maxYear > 1000 && minYear > 1000 && maxYear > minYear+10){
			this.maxYear = maxYear;
			this.minYear = minYear;
			initPickerView();
		}
	}

}
