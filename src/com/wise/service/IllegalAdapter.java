package com.wise.service;

import java.util.List;

import com.wise.data.IllegalCity;
import com.wise.wawc.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IllegalAdapter extends BaseAdapter {
	Context context;
	List<IllegalCity> illegalList;
	TextView name;
	LayoutInflater inflater;
	public IllegalAdapter(List<IllegalCity> illegalList,Context context) {
		this.context = context;
		this.illegalList = illegalList;
		inflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return illegalList.size();
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.illegal_adapter, null);
		name = (TextView) convertView.findViewById(R.id.illegal_city_name);
		name.setText(illegalList.get(position).getCityName());
		return convertView;
	}
	
	public void refresh(List<IllegalCity> illegalList){
		this.illegalList = illegalList;
		this.notifyDataSetChanged();
	}
}
