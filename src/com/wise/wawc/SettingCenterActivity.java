package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingCenterActivity extends Activity {
	private Button setCenterMenu;
	private Button setCenterHome;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_center);
		setCenterMenu = (Button) findViewById(R.id.setting_center_menu);
		setCenterHome = (Button) findViewById(R.id.setting_center_home);
		
		setCenterMenu.setOnClickListener(new ClickListener());
		setCenterHome.setOnClickListener(new ClickListener());
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.setting_center_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.setting_center_home:
				ActivityFactory.A.ToHome();
				break;
			default:
				return;
			}
		}
	}
}
