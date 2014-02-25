package com.wise.service;
import java.util.List;

import com.wise.wawc.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 车友圈文章列表
 * @author 王庆文
 */
public class MaintainAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private Context context;
	private List<String> maintainList = null;
	public MaintainAdapter(Context context,List<String> maintainList){
		inflater=LayoutInflater.from(context);
		this.maintainList = maintainList;
		this.context = context;
	}
	public int getCount() {
		return maintainList.size();
	}
	public Object getItem(int position) {
		return maintainList.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.maintain_item, null);
		TextView maintainName = (TextView) convertView.findViewById(R.id.vehicle_maintain_name);
		maintainName.setText(maintainList.get(position));
		return convertView;
	}
}
