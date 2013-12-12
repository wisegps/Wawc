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
/**
 * 车辆违章
 * @author honesty
 */
public class TrafficActivity extends Activity{
	List<TrafficData> trafficDatas = new ArrayList<TrafficData>();
	TrafficAdapter trafficAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		ListView lv_activity_traffic = (ListView)findViewById(R.id.lv_activity_traffic);
		GetData();
		trafficAdapter = new TrafficAdapter();
		lv_activity_traffic.setAdapter(trafficAdapter);
	}
	
	private void GetData(){
		for(int i = 0; i < 10 ; i++){
			TrafficData trafficData = new TrafficData();
			trafficData.setDate("违章时间：2013-06-21");
			trafficData.setAdress("违章地点：广深高速");
			trafficData.setContent("违章内容：超速行驶");
			trafficData.setFraction("违章扣分：3分");
			trafficData.setMoney("违章罚款：150元");
			trafficDatas.add(trafficData);
		}
	}
	
	private class TrafficAdapter extends BaseAdapter{
		LayoutInflater mInflater = LayoutInflater.from(TrafficActivity.this);
		@Override
		public int getCount() {
			return trafficDatas.size();
		}
		@Override
		public Object getItem(int position) {
			return trafficDatas.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_traffic, null);
				holder = new ViewHolder();
				holder.tv_item_traffic_data = (TextView) convertView.findViewById(R.id.tv_item_traffic_data);
				holder.tv_item_traffic_adress = (TextView)convertView.findViewById(R.id.tv_item_traffic_adress);
				holder.tv_item_traffic_content = (TextView)convertView.findViewById(R.id.tv_item_traffic_content);
				holder.tv_item_traffic_fraction = (TextView)convertView.findViewById(R.id.tv_item_traffic_fraction);
				holder.tv_item_traffic_money = (TextView)convertView.findViewById(R.id.tv_item_traffic_money);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TrafficData trafficData = trafficDatas.get(position);
			holder.tv_item_traffic_data.setText(trafficData.getDate());
			holder.tv_item_traffic_adress.setText(trafficData.getAdress());
			holder.tv_item_traffic_content.setText(trafficData.getContent());
			holder.tv_item_traffic_fraction.setText(trafficData.getFraction());
			holder.tv_item_traffic_money.setText(trafficData.getMoney());
			return convertView;
		}	
		private class ViewHolder {
			TextView tv_item_traffic_data,tv_item_traffic_adress,tv_item_traffic_content,
						tv_item_traffic_fraction,tv_item_traffic_money;
		}
	}
	
	private class TrafficData{
		String date;
		String adress;
		String content;
		String fraction;
		String money;
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getAdress() {
			return adress;
		}
		public void setAdress(String adress) {
			this.adress = adress;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getFraction() {
			return fraction;
		}
		public void setFraction(String fraction) {
			this.fraction = fraction;
		}
		public String getMoney() {
			return money;
		}
		public void setMoney(String money) {
			this.money = money;
		}		
	}
}