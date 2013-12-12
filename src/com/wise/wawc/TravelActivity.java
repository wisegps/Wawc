package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 车辆行程
 * @author honesty
 */
public class TravelActivity extends Activity{
	List<TravelData> travelDatas = new ArrayList<TravelData>();
	TravelAdapter travelAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travel);
		ListView lv_activity_travel = (ListView)findViewById(R.id.lv_activity_travel);
		GetData();
		travelAdapter = new TravelAdapter();
		lv_activity_travel.setAdapter(travelAdapter);
	}
	
	private void GetData(){		
		for(int i = 0 ; i < 8; i++){
			TravelData travelData = new TravelData();
			travelData.setStartTime("19:27");
			travelData.setStopTime("20:04");
			travelData.setSpacingTime("37分钟");
			travelData.setStartPlace("桃源村");
			travelData.setStopPlace("世界之窗");
			travelData.setSpacingTime("15.35KM");
			travelData.setAverageOil("百公里油耗：9.9L");
			travelData.setOil("油耗：1.52L");
			travelData.setSpeed("平均速度：25.75km/h");
			travelData.setCost("话费：11.34");
			travelDatas.add(travelData);
		}
	}
	
	
	private class TravelAdapter extends BaseAdapter{
		LayoutInflater mInflater = LayoutInflater.from(TravelActivity.this);
		@Override
		public int getCount() {
			return travelDatas.size();
		}
		@Override
		public Object getItem(int position) {
			return travelDatas.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_travel, null);
				holder = new ViewHolder();
				holder.tv_item_travel_startTime = (TextView) convertView.findViewById(R.id.tv_item_travel_startTime);
				holder.tv_item_travel_stopTime = (TextView)convertView.findViewById(R.id.tv_item_travel_stopTime);
				holder.tv_item_travel_spacingTime = (TextView)convertView.findViewById(R.id.tv_item_travel_spacingTime);
				holder.tv_item_travel_startPlace = (TextView)convertView.findViewById(R.id.tv_item_travel_startPlace);
				holder.tv_item_travel_stopPlace = (TextView)convertView.findViewById(R.id.tv_item_travel_stopPlace);
				holder.tv_item_travel_spacingDistance = (TextView)convertView.findViewById(R.id.tv_item_travel_spacingDistance);
				holder.tv_item_travel_averageOil = (TextView)convertView.findViewById(R.id.tv_item_travel_averageOil);
				holder.tv_item_travel_oil = (TextView)convertView.findViewById(R.id.tv_item_travel_oil);
				holder.tv_item_travel_speed = (TextView)convertView.findViewById(R.id.tv_item_travel_speed);
				holder.tv_item_travel_cost = (TextView)convertView.findViewById(R.id.tv_item_travel_cost);
				holder.iv_item_travel_map = (ImageView)convertView.findViewById(R.id.iv_item_travel_map);
				holder.iv_item_travel_share = (ImageView)convertView.findViewById(R.id.iv_item_travel_share);				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TravelData travelData = travelDatas.get(position);
			holder.tv_item_travel_startTime.setText(travelData.getStartTime());
			holder.tv_item_travel_stopTime.setText(travelData.getStopTime());
			holder.tv_item_travel_spacingTime.setText(travelData.getSpacingTime());
			holder.tv_item_travel_startPlace.setText(travelData.getStartPlace());
			holder.tv_item_travel_stopPlace.setText(travelData.getStopPlace());
			holder.tv_item_travel_spacingDistance.setText(travelData.getSpacingDistance());
			
			holder.tv_item_travel_averageOil.setText(travelData.getAverageOil());
			holder.tv_item_travel_oil.setText(travelData.getOil());
			holder.tv_item_travel_speed.setText(travelData.getSpeed());
			holder.tv_item_travel_cost.setText(travelData.getCost());
			return convertView;
		}
		private class ViewHolder {
			TextView tv_item_travel_startTime,tv_item_travel_stopTime,tv_item_travel_spacingTime,
					tv_item_travel_startPlace,tv_item_travel_stopPlace,tv_item_travel_spacingDistance,
					tv_item_travel_averageOil,tv_item_travel_oil,tv_item_travel_speed,tv_item_travel_cost;
			ImageView iv_item_travel_map,iv_item_travel_share;
		}
	}
	
	private class TravelData{
		String startTime;
		String stopTime;
		String spacingTime;
		String startPlace;
		String stopPlace;
		String spacingDistance;
		String oil;
		String averageOil;
		String speed;
		String cost;
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getStopTime() {
			return stopTime;
		}
		public void setStopTime(String stopTime) {
			this.stopTime = stopTime;
		}
		public String getSpacingTime() {
			return spacingTime;
		}
		public void setSpacingTime(String spacingTime) {
			this.spacingTime = spacingTime;
		}
		public String getStartPlace() {
			return startPlace;
		}
		public void setStartPlace(String startPlace) {
			this.startPlace = startPlace;
		}
		public String getStopPlace() {
			return stopPlace;
		}
		public void setStopPlace(String stopPlace) {
			this.stopPlace = stopPlace;
		}
		public String getSpacingDistance() {
			return spacingDistance;
		}
		public void setSpacingDistance(String spacingDistance) {
			this.spacingDistance = spacingDistance;
		}
		public String getOil() {
			return oil;
		}
		public void setOil(String oil) {
			this.oil = oil;
		}
		public String getAverageOil() {
			return averageOil;
		}
		public void setAverageOil(String averageOil) {
			this.averageOil = averageOil;
		}
		public String getSpeed() {
			return speed;
		}
		public void setSpeed(String speed) {
			this.speed = speed;
		}
		public String getCost() {
			return cost;
		}
		public void setCost(String cost) {
			this.cost = cost;
		}		
	}
}