package com.wise.service;

import com.wise.wawc.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 我的收藏自定义迭代器
 */
public class CollectionAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private ImageView call;
	private TextView tel;
	public CollectionAdapter(Context context){
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return 10;
	}
	public Object getItem(int position) {
		return null;
	}
	public long getItemId(int position) {
		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.collection_list, null);
		call = (ImageView) convertView.findViewById(R.id.collection_call);
		tel = (TextView) convertView.findViewById(R.id.collection_tel);
		call.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String temp = tel.getText().toString();
				String phoneno = temp.substring(temp.indexOf("：")+1).trim();
				Log.e("电话--->",phoneno);
				Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneno));
				context.startActivity(intent);
			}
		});
		return convertView;
	}
}
