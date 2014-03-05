package com.wise.service;

import java.util.List;

import com.wise.wawc.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeriesAdapter extends BaseAdapter {
	List<String[]> carSeriesList;
	Context context;
	LayoutInflater layoutInflater;
	TextView seriesName;
	int action = 0;
	List<String> typeList;
	public SeriesAdapter(List<String[]> carSeriesList,Context context,int action,List<String> typeList) {
		this.carSeriesList = carSeriesList;
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.action = action;
		this.typeList = typeList; 
	}
	public int getCount() {
		int count = 0;
		if(action == 1){
			count = carSeriesList.size();
		}else if(action == 2){
			count = typeList.size();
		}
		return count;
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.insurance_adapter, null);
		seriesName = (TextView) convertView.findViewById(R.id.insurance_item_name);
		if(action == 1){
			seriesName.setText(carSeriesList.get(position)[1]);
		}
		if(action == 2){
			seriesName.setText(typeList.get(position));
		}
		return convertView;
	}
	
	public void refresh(int action,List<String> typeList){
		this.action = action;
		this.typeList = typeList;
		this.notifyDataSetChanged();
	}
}
