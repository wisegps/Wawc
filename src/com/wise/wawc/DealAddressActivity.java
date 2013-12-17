package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DealAddressActivity extends Activity{
	List<DealAdressData> dealAdressDatas = new ArrayList<DealAdressData>();
	DealAdressAdapter dealAdressAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dealadress);
		ListView lv_activity_dealadress = (ListView)findViewById(R.id.lv_activity_dealadress);
		GetData();
		dealAdressAdapter = new DealAdressAdapter();
		lv_activity_dealadress.setAdapter(dealAdressAdapter);
	}
	
	private void GetData(){
		for(int i = 0 ; i < 10 ; i++){
			DealAdressData dealAdressData = new DealAdressData();
			dealAdressData.setName("深圳市交通局");
			dealAdressData.setAdress("地址：沙河西路1号");
			dealAdressData.setPhone("电话：0755-8787878787");
			dealAdressDatas.add(dealAdressData);
		}
	}
	
	public class DealAdressAdapter extends BaseAdapter{
		LayoutInflater mInflater = LayoutInflater.from(DealAddressActivity.this);
		@Override
		public int getCount() {
			return dealAdressDatas.size();
		}
		@Override
		public Object getItem(int position) {
			return dealAdressDatas.get(position);
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
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			DealAdressData dealAdressData = dealAdressDatas.get(position);
			holder.tv_item_dealadress_name.setText(dealAdressData.getName());
			holder.tv_item_dealadress_adress.setText(dealAdressData.getAdress());
			holder.tv_item_dealadress_phone.setText(dealAdressData.getPhone());
			return convertView;
		}
		private class ViewHolder {
			TextView tv_item_dealadress_name,tv_item_dealadress_adress,tv_item_dealadress_phone;
		}
	}
	public class DealAdressData{
		private String name;
		private String adress;
		private String phone;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAdress() {
			return adress;
		}
		public void setAdress(String adress) {
			this.adress = adress;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		
	}
}