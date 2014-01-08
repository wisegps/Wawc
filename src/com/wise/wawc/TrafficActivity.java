package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import com.wise.extend.CarAdapter;
import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
/**
 * 车辆违章
 * @author honesty
 */
public class TrafficActivity extends Activity{
	
	CarAdapter carAdapter;
	List<TrafficData> trafficDatas = new ArrayList<TrafficData>();
	TrafficAdapter trafficAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		ImageView iv_activity_traffic_back = (ImageView)findViewById(R.id.iv_activity_traffic_back);
		iv_activity_traffic_back.setOnClickListener(onClickListener);
		ListView lv_activity_traffic = (ListView)findViewById(R.id.lv_activity_traffic);
		GetData();
		trafficAdapter = new TrafficAdapter();
		lv_activity_traffic.setAdapter(trafficAdapter);
		ImageView iv_activity_traffic_help = (ImageView)findViewById(R.id.iv_activity_traffic_help);
		iv_activity_traffic_help.setOnClickListener(onClickListener);
		
		GridView gv_activity_traffic = (GridView)findViewById(R.id.gv_activity_traffic);
        carAdapter = new CarAdapter(TrafficActivity.this,Variable.carDatas);
        gv_activity_traffic.setAdapter(carAdapter);
        
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(Variable.carDatas.size() * (px + 10),LayoutParams.WRAP_CONTENT);
		gv_activity_traffic.setLayoutParams(params);
		gv_activity_traffic.setColumnWidth(px);
		gv_activity_traffic.setHorizontalSpacing(10);
		gv_activity_traffic.setStretchMode(GridView.NO_STRETCH);
		gv_activity_traffic.setNumColumns(Variable.carDatas.size());
		gv_activity_traffic.setOnItemClickListener(onItemClickListener);		
	}
	
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_traffic_back:
				finish();
				break;
			case R.id.iv_activity_traffic_help:
				TrafficActivity.this.startActivity(new Intent(TrafficActivity.this, DealAddressActivity.class));
				break;
			}
		}
	};
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			for(int i = 0 ; i < Variable.carDatas.size() ; i++){
			    Variable.carDatas.get(i).setCheck(false);
			}
			Variable.carDatas.get(arg2).setCheck(true);
			carAdapter.notifyDataSetChanged();
		}
	};
	
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