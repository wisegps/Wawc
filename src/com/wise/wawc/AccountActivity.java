package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 我的账户
 * @author honesty
 */
public class AccountActivity extends Activity{
	private View view = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		view = findViewById(R.id.account_to_my_vehicle);
		view.setOnClickListener(new ClickListener());
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.account_to_my_vehicle:
				startActivity(new Intent(AccountActivity.this,MyVehicleActivity.class));
				break;
			}
		}
	}
}
