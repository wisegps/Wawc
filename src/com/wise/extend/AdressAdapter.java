package com.wise.extend;

import java.util.List;
import com.wise.data.AdressData;
import com.wise.wawc.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 地点信息
 * @author honesty
 */
public class AdressAdapter extends BaseAdapter{
	private static final String TAG = "AdressAdapter";
	Context context;
	List<AdressData> adressDatas;
	LayoutInflater mInflater;
	public AdressAdapter(Context context,List<AdressData> adressDatas){
		this.context = context;
		this.adressDatas = adressDatas;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return adressDatas.size();
	}
	@Override
	public Object getItem(int arg0) {
		return adressDatas.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_dealadress, null);
			holder = new ViewHolder();
			holder.tv_item_dealadress_name = (TextView) convertView.findViewById(R.id.tv_item_dealadress_name);
			holder.tv_item_dealadress_adress = (TextView)convertView.findViewById(R.id.tv_item_dealadress_adress);
			holder.tv_item_dealadress_phone = (TextView)convertView.findViewById(R.id.tv_item_dealadress_phone);
			holder.rl_item_dealadress_collection = (RelativeLayout)convertView.findViewById(R.id.rl_item_dealadress_collection);
			holder.rl_item_dealadress_call = (RelativeLayout)convertView.findViewById(R.id.rl_item_dealadress_call);
			holder.rl_item_dealadress_navigation = (RelativeLayout)convertView.findViewById(R.id.rl_item_dealadress_navigation);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AdressData adressData = adressDatas.get(position);
		holder.tv_item_dealadress_name.setText(adressData.getName());
		holder.tv_item_dealadress_adress.setText(adressData.getAdress());
		holder.tv_item_dealadress_phone.setText(adressData.getPhone());
		holder.rl_item_dealadress_collection.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				//DealAddressActivity.this.startActivity(new Intent(DealAddressActivity.this, MyCollectionActivity.class));
			}
		});
		holder.rl_item_dealadress_call.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		holder.rl_item_dealadress_navigation.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				//FindCar();
			}
		});
		return convertView;
	}
	private class ViewHolder {
		TextView tv_item_dealadress_name,tv_item_dealadress_adress,tv_item_dealadress_phone;
		RelativeLayout rl_item_dealadress_collection,rl_item_dealadress_call,rl_item_dealadress_navigation;
	}
}
