package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Button bt_activity_home_menu = (Button)findViewById(R.id.bt_activity_home_menu);
		bt_activity_home_menu.setOnClickListener(onClickListener);
		
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_activity_home_menu:
				ActivityFactory.A.ShowMenu();
				break;

			default:
				break;
			}
		}
	};
}