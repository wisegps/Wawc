package com.wise.service;
import com.wise.wawc.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
public class MyAdapter extends BaseAdapter implements OnClickListener{
	private LayoutInflater inflater;
	private ImageView saySomething;
	private View view;
	public boolean isClick = false;
	public MyAdapter(Context context,View v){
		inflater=LayoutInflater.from(context);
		this.view = v;
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
		saySomething = (ImageView) convertView.findViewById(R.id.list_say_somthing);
		saySomething.setOnClickListener(this);
		return convertView;
	}
	public void onClick(View v) {
		//编辑框不可见，设置为可见
		Log.e("onClick",String.valueOf(isClick));
		if(!isClick){
			isClick = true;
			view.setVisibility(View.VISIBLE);
		//编辑框可见，设置为不可见	
		}else if(isClick){
			isClick = false;
			view.setVisibility(View.GONE);
		}
	}
}
