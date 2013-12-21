package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import com.wise.data.CarData;
import com.wise.extend.CarAdapter;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
/**
 * 我的终端
 * @author honesty
 */
public class MyDevicesActivity extends Activity{
	private static final String TAG = "MyDevicesActivity";
	CarAdapter carAdapter;
	List<CarData> carDatas;
	
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
		GetData();
		GridView gv_activity_devices = (GridView)findViewById(R.id.gv_activity_devices);
        carAdapter = new CarAdapter(MyDevicesActivity.this,carDatas);
        gv_activity_devices.setAdapter(carAdapter);
        
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(carDatas.size() * (px + 10),LayoutParams.WRAP_CONTENT);
		gv_activity_devices.setLayoutParams(params);
		gv_activity_devices.setColumnWidth(px);
		gv_activity_devices.setHorizontalSpacing(10);
		gv_activity_devices.setStretchMode(GridView.NO_STRETCH);
		gv_activity_devices.setNumColumns(carDatas.size());
		gv_activity_devices.setOnItemClickListener(onItemClickListener);	
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_devices_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.iv_activity_devices_home:
				ActivityFactory.A.ToHome();
				break;
			case R.id.tv_activity_devices_renewals:
				MyDevicesActivity.this.startActivity(new Intent(MyDevicesActivity.this, OrderServiceActivity.class));
				break;
			}
		}
	};
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg2 == (carDatas.size() - 1)){
				MyDevicesActivity.this.startActivity(new Intent(MyDevicesActivity.this, OrderDeviceActivity.class));
			}else{
				for(int i = 0 ; i < carDatas.size() ; i++){
					carDatas.get(i).setCheck(false);
				}
				carDatas.get(arg2).setCheck(true);
				carAdapter.notifyDataSetChanged();
			}			
		}
	};
	public void GetData(){
		carDatas = new ArrayList<CarData>();
		for(int i = 0 ; i < 5; i++){
			CarData carData = new CarData();
			carData.setCarLogo(1);
			carData.setCarNumber("43420432");
			carData.setCheck(false);
			carDatas.add(carData);
		}
		
		CarData carData = new CarData();
		carData.setCarLogo(0);
		carData.setCarNumber("");
		carData.setCheck(false);
		carDatas.add(carData);
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}