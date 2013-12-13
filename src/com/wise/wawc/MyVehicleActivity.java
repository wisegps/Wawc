package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 我的爱车
 * @author honesty
 */
public class MyVehicleActivity extends Activity {
	private Button menu = null;
	private Button home = null;
	private ImageView addCar = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_vehicle);
		menu = (Button) findViewById(R.id.my_vechile_menu);
		home = (Button) findViewById(R.id.my_vechile_home);
		addCar = (ImageView) findViewById(R.id.add_car);
		menu.setOnClickListener(new ClickListener());
		home.setOnClickListener(new ClickListener());
		addCar.setOnClickListener(new ClickListener());
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.my_vechile_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.my_vechile_home:
				ActivityFactory.A.ToHome();
				break;
			case R.id.add_car:
				Toast.makeText(getApplicationContext(), "添加新车辆", 0).show();
				startActivity(new Intent(MyVehicleActivity.this,NewVehicleActivity.class));
				//添加新的车辆
				break;
			default:
				return;
			}
		}
	}
}
