package com.wise.service;
import com.wise.wawc.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
public class MyAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	public MyAdapter(Context context){
		inflater=LayoutInflater.from(context);
	}
	public int getCount() {
		return 10;
	}
	public Object getItem(int position) {
		return null;
	}
	public long getItemId(int position) {
		return 10;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.article_adapter, null);
		return convertView;
	}
}
