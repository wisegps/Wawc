package com.wise.wawc;

import com.wise.extend.HScrollLayout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 首页
 * @author honesty
 */
public class HomeActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ImageView iv_activity_home_menu = (ImageView)findViewById(R.id.iv_activity_home_menu);
		iv_activity_home_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_car_home_search = (ImageView)findViewById(R.id.iv_activity_car_home_search);
		iv_activity_car_home_search.setOnClickListener(onClickListener);
		Button bt_activity_home_help = (Button)findViewById(R.id.bt_activity_home_help);
		bt_activity_home_help.setOnClickListener(onClickListener);
		Button bt_activity_home_risk = (Button)findViewById(R.id.bt_activity_home_risk);
		bt_activity_home_risk.setOnClickListener(onClickListener);
		//ScrollLayout_car
		HScrollLayout ScrollLayout_car = (HScrollLayout)findViewById(R.id.ScrollLayout_car);
		HScrollLayout ScrollLayout_other = (HScrollLayout)findViewById(R.id.ScrollLayout_other);
		LayoutInflater mLayoutInflater = LayoutInflater.from(HomeActivity.this);
        for (int i = 0; i < 5; i++) {
        	View mapView = mLayoutInflater.inflate(R.layout.item_home_car, null);
        	ScrollLayout_car.addView(mapView);
        	ImageView item_home_car_icon = (ImageView)mapView.findViewById(R.id.item_home_car_icon);
        	item_home_car_icon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					HomeActivity.this.startActivity(new Intent(HomeActivity.this, MyVehicleActivity.class));
				}
			});
        	TextView item_home_car_number = (TextView)findViewById(R.id.item_home_car_number);
        	item_home_car_number.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					HomeActivity.this.startActivity(new Intent(HomeActivity.this, MyVehicleActivity.class));
				}
			});
        	ImageView item_home_car_share = (ImageView)mapView.findViewById(R.id.item_home_car_share);
        	item_home_car_share.setOnClickListener(onClickListener);
        	ImageView item_home_car_traffic = (ImageView)mapView.findViewById(R.id.item_home_car_traffic);
        	item_home_car_traffic.setOnClickListener(onClickListener);
        	ImageView item_home_car_vehicle_event = (ImageView)mapView.findViewById(R.id.item_home_car_vehicle_event);
        	item_home_car_vehicle_event.setOnClickListener(onClickListener);
        	ImageView item_home_car_vehicle_status = (ImageView)mapView.findViewById(R.id.item_home_car_vehicle_status);
        	item_home_car_vehicle_status.setOnClickListener(onClickListener);
        	TextView tv_item_home_car_adress = (TextView)mapView.findViewById(R.id.tv_item_home_car_adress);
        	tv_item_home_car_adress.setOnClickListener(onClickListener);
		}
        for (int i = 0; i < 2; i++) {
        	View mapView = mLayoutInflater.inflate(R.layout.item_home_car, null);
        	ScrollLayout_other.addView(mapView);
        	ImageView item_home_car_icon = (ImageView)mapView.findViewById(R.id.item_home_car_icon);
        	item_home_car_icon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					Toast.makeText(HomeActivity.this, "onclicklistener", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_home_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.iv_activity_car_home_search:
				ActivityFactory.A.RightMenu();
				break;
			case R.id.bt_activity_home_help://救援
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, ShareLocationActivity.class));
				break;
			case R.id.bt_activity_home_risk://报险
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, ShareLocationActivity.class));
				break;
			case R.id.item_home_car_share://位置分享
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, ShareLocationActivity.class));
				break;
			case R.id.item_home_car_traffic://车辆违章
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, TrafficActivity.class));
				break;
			case R.id.item_home_car_vehicle_event://车务提醒
				Intent eventIntent = new Intent(HomeActivity.this, CarRemindActivity.class);
				eventIntent.putExtra("isFinish", true);
				HomeActivity.this.startActivity(eventIntent);
				break;
			case R.id.item_home_car_vehicle_status:
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, VehicleStatusActivity.class));
				break;
			case R.id.tv_item_home_car_adress:
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, CarLocationActivity.class));
				break;
			}
		}
	};
}