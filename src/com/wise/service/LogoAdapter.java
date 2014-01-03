package com.wise.service;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class LogoAdapter extends BaseAdapter {
	private Context context;
	private int[] images;
	public LogoAdapter(Context context,int[] images){
		this.context = context;
		this.images = images;
	}
	public int getCount() {
		return images.length;
	}
	public Object getItem(int position) {
		return null;
	}
	public long getItemId(int position) {
		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		imageView.setBackgroundResource(images[position]);
		imageView.setLayoutParams(new GridView.LayoutParams(80, 80));
		return imageView;
	}
}
