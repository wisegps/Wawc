package com.wise.service;
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
	private ImageView maintainLogo = null;
	private TextView maintainName = null;
	public MaintainAdapter(Context context){
		inflater=LayoutInflater.from(context);
		this.context = context;
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
		convertView = inflater.inflate(R.layout.maintain_item, null);
		maintainLogo = (ImageView) convertView.findViewById(R.id.vehicle_maintain_shop);
		maintainLogo.setBackgroundResource(R.drawable.image);
		maintainName = (TextView) convertView.findViewById(R.id.vehicle_maintain_name);
		maintainName.setText((position+1)+"号保养店");
		
		return convertView;
	}
}
