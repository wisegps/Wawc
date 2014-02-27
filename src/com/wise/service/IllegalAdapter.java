package com.wise.service;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class IllegalAdapter extends BaseAdapter {

	public IllegalAdapter() {
	}
	public int getCount() {
		return 0;
	}
	public Object getItem(int position) {
		return null;
	}
	public long getItemId(int position) {
		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
}
