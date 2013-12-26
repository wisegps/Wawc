package com.wise.extend;

import java.util.List;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.data.AdressData;
import com.wise.pubclas.Config;
import com.wise.pubclas.GetSystem;
import com.wise.wawc.MyCollectionActivity;
import com.wise.wawc.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
/**
 * 地点信息
 * @author honesty
 */
public class AdressAdapter extends BaseAdapter{
	private static final String TAG = "AdressAdapter";
	Context context;
	Activity mActivity;
	List<AdressData> adressDatas;
	LayoutInflater mInflater;
	
	public AdressAdapter(Context context,List<AdressData> adressDatas,Activity mActivity){
		this.context = context;
		this.adressDatas = adressDatas;
		this.mActivity = mActivity;
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
			holder.tv_item_dealadress_distance = (TextView) convertView.findViewById(R.id.tv_item_dealadress_distance);
			holder.tv_item_dealadress_adress = (TextView)convertView.findViewById(R.id.tv_item_dealadress_adress);
			holder.tv_item_dealadress_phone = (TextView)convertView.findViewById(R.id.tv_item_dealadress_phone);
			holder.bt_item_dealadress_collection = (Button)convertView.findViewById(R.id.bt_item_dealadress_collection);
			holder.bt_item_dealadress_call = (Button)convertView.findViewById(R.id.bt_item_dealadress_call);
			holder.bt_item_dealadress_navigation = (Button)convertView.findViewById(R.id.bt_item_dealadress_navigation);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AdressData adressData = adressDatas.get(position);
		holder.tv_item_dealadress_name.setText(adressData.getName());
		if(adressData.getDistance() != -1){
			holder.tv_item_dealadress_distance.setText(adressData.getDistance() + "m");
		}
		
		holder.tv_item_dealadress_adress.setText(adressData.getAdress());
		holder.tv_item_dealadress_phone.setText("电话：" +adressData.getPhone());
		System.out.println("电话：" + adressData.getPhone());
		if(adressData.getPhone() == null || adressData.getPhone().equals("")){
			System.out.println("隐藏");
			holder.bt_item_dealadress_call.setVisibility(View.GONE);
		}else{
			System.out.println("显示");
			holder.bt_item_dealadress_call.setVisibility(View.VISIBLE);
		}
		holder.bt_item_dealadress_collection.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				mActivity.startActivity(new Intent(mActivity, MyCollectionActivity.class));
			}
		});
		holder.bt_item_dealadress_call.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ adressData.getPhone()));  
				mActivity.startActivity(intent);
			}
		});
		holder.bt_item_dealadress_navigation.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				GeoPoint point = new GeoPoint((int) (Config.Lat * 1E6),(int) (Config.Lon * 1E6));
				GeoPoint point1 = new GeoPoint((int) (adressData.getLat() * 1E6),(int) (adressData.getLon() * 1E6));
				System.out.println(Config.Lat + "/" + Config.Lat);
				System.out.println(adressData.getLat() + "/" + adressData.getLat());
				GetSystem.FindCar(mActivity, point, point1, "point", "point1");
			}
		});
		return convertView;
	}
	private class ViewHolder {
		TextView tv_item_dealadress_name,tv_item_dealadress_adress,tv_item_dealadress_phone,tv_item_dealadress_distance;
		Button bt_item_dealadress_collection,bt_item_dealadress_call,bt_item_dealadress_navigation;
	}
}
