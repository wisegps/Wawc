package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
			case R.id.bt_activity_home_help:
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, ShareLocationActivity.class));
				break;
			case R.id.bt_activity_home_risk:
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, ShareLocationActivity.class));
				break;
			}
		}
	};
}