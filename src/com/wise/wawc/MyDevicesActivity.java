package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 我的终端
 * @author honesty
 */
public class MyDevicesActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devices);
		ImageView iv_activity_devices_menu = (ImageView)findViewById(R.id.iv_activity_devices_menu);
		iv_activity_devices_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_devices_home = (ImageView)findViewById(R.id.iv_activity_devices_home);
		iv_activity_devices_home.setOnClickListener(onClickListener);
		TextView tv_activity_devices_renewals = (TextView)findViewById(R.id.tv_activity_devices_renewals);
		tv_activity_devices_renewals.setOnClickListener(onClickListener);
		GridView gv_activity_devices = (GridView)findViewById(R.id.gv_activity_devices);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_devices_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.iv_activity_devices_home:
				MyDevicesActivity.this.startActivity(new Intent(MyDevicesActivity.this, OrderDeviceActivity.class));
				break;
			case R.id.tv_activity_devices_renewals:
				MyDevicesActivity.this.startActivity(new Intent(MyDevicesActivity.this, OrderServiceActivity.class));
				break;
			}
		}
	};
}