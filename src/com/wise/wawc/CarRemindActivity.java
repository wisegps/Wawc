package com.wise.wawc;

import android.app.Activity;
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
	}
	OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_car_remind_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.iv_activity_car_remind_home:
				ActivityFactory.A.ToHome();
				break;
			}
		}		
	};
}