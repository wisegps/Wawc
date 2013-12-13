package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 车务提醒
 * @author honesty
 */
public class CarRemindActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_remind);
		ImageView iv_activity_car_remind_menu = (ImageView)findViewById(R.id.iv_activity_car_remind_menu);
		iv_activity_car_remind_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_home = (ImageView)findViewById(R.id.iv_activity_car_remind_home);
		iv_activity_car_remind_home.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_maintenance_note = (ImageView)findViewById(R.id.iv_activity_car_remind_maintenance_note);
		iv_activity_car_remind_maintenance_note.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_inspection_problem = (ImageView)findViewById(R.id.iv_activity_car_remind_inspection_problem);
		iv_activity_car_remind_inspection_problem.setOnClickListener(onClickListener);
	}
	OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_car_remind_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.iv_activity_car_remind_home:
				//ActivityFactory.A.ToHome();
				CarRemindActivity.this.startActivity(new Intent(CarRemindActivity.this, VehicleStatusActivity.class));
				break;
			case R.id.iv_activity_car_remind_maintenance_note:
				CarRemindActivity.this.startActivity(new Intent(CarRemindActivity.this, MyVehicleActivity.class));
				break;
			case R.id.iv_activity_car_remind_inspection_problem:
				CarRemindActivity.this.startActivity(new Intent(CarRemindActivity.this, DealAddressActivity.class));
				break;
			}
		}		
	};
}