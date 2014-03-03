package com.wise.service;

import java.util.List;

import com.wise.data.IllegalCity;
import com.wise.data.ProvinceModel;
import com.wise.wawc.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
/**
 * 选择城市迭代器
 * @author Mr.Wang
 */
public class IllegalCityAdapter extends BaseAdapter{
	Context context;
	TextView name;
	List<IllegalCity> provinceModelList;
	LayoutInflater inflater;
	LayoutParams params;
	public IllegalCityAdapter(List<IllegalCity> provinceModelList,Context context) {
		this.context = context;
		this.provinceModelList = provinceModelList;
		inflater = LayoutInflater.from(context);
		params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	}
	public int getCount() {
		return provinceModelList.size();
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	}
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.illegal_city_adapter, null);
		name = (TextView) convertView.findViewById(R.id.illegal_city_name);
		name.setText(provinceModelList.get(position).getCityName());
		return convertView;
	}
}
