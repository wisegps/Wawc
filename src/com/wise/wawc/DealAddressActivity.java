package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.pubclas.Config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 处理地点
 * @author honesty
 */
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
		
		ImageView iv_activity_dealadress_back = (ImageView)findViewById(R.id.iv_activity_dealadress_back);
		iv_activity_dealadress_back.setOnClickListener(onClickListener);
	}
	
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_dealadress_back:
				finish();
				break;

			default:
				break;
			}
		}
	};
	
	private void GetData(){
		for(int i = 0 ; i < 10 ; i++){
			DealAdressData dealAdressData = new DealAdressData();
			dealAdressData.setName("深圳市交通局");
			dealAdressData.setAdress("地址：沙河西路1号");
			dealAdressData.setPhone("电话：0755-8787878787");
			dealAdressDatas.add(dealAdressData);
		}
	}
	
	private void FindCar() {
		GeoPoint pt1 = new GeoPoint((int) (Config.Lat * 1E6),
				(int) (Config.Lon * 1E6));
		GeoPoint pt2 = new GeoPoint((int) ((Config.Lat + 1) * 1E6),
				(int) ((Config.Lon + 1) * 1E6));
		NaviPara para = new NaviPara();
		para.startPoint = pt1; // 起点坐标
		para.startName = "从这里开始";
		para.endPoint = pt2; // 终点坐标
		para.endName = "到这里结束";
		try {
			// 调起百度地图客户端导航功能,参数this为Activity。
			BaiduMapNavigation.openBaiduMapNavi(para, this);
		} catch (BaiduMapAppNotSupportNaviException e) {
			// 在此处理异常
			e.printStackTrace();
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
				holder.rl_item_dealadress_collection = (RelativeLayout)convertView.findViewById(R.id.rl_item_dealadress_collection);
				holder.rl_item_dealadress_call = (RelativeLayout)convertView.findViewById(R.id.rl_item_dealadress_call);
				holder.rl_item_dealadress_navigation = (RelativeLayout)convertView.findViewById(R.id.rl_item_dealadress_navigation);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			DealAdressData dealAdressData = dealAdressDatas.get(position);
			holder.tv_item_dealadress_name.setText(dealAdressData.getName());
			holder.tv_item_dealadress_adress.setText(dealAdressData.getAdress());
			holder.tv_item_dealadress_phone.setText(dealAdressData.getPhone());
			holder.rl_item_dealadress_collection.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					DealAddressActivity.this.startActivity(new Intent(
							DealAddressActivity.this, MyCollectionActivity.class));
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
					FindCar();
				}
			});
			return convertView;
		}
		private class ViewHolder {
			TextView tv_item_dealadress_name,tv_item_dealadress_adress,tv_item_dealadress_phone;
			RelativeLayout rl_item_dealadress_collection,rl_item_dealadress_call,rl_item_dealadress_navigation;
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