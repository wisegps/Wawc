package com.wise.wawc;

import com.wise.extend.CarAdapter;
import com.wise.pubclas.Config;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 车务提醒
 * @author honesty
 */
public class CarRemindActivity extends Activity{
	private static final String TAG = "CarRemindActivity";
	CarAdapter carAdapter;
	boolean isFinish = false;//false从菜单页跳转过来返回打开菜单，true从首页跳转返回关闭页面
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
		
		Intent intent = getIntent();
		isFinish = intent.getBooleanExtra("isFinish", false);
		
		GridView gv_activity_car_remind = (GridView)findViewById(R.id.gv_activity_car_remind);
        carAdapter = new CarAdapter(CarRemindActivity.this,Config.carDatas);
        gv_activity_car_remind.setAdapter(carAdapter);
        
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(Config.carDatas.size() * (px + 10),LayoutParams.WRAP_CONTENT);
		gv_activity_car_remind.setLayoutParams(params);
		gv_activity_car_remind.setColumnWidth(px);
		gv_activity_car_remind.setHorizontalSpacing(10);
		gv_activity_car_remind.setStretchMode(GridView.NO_STRETCH);
		gv_activity_car_remind.setNumColumns(Config.carDatas.size());
		gv_activity_car_remind.setOnItemClickListener(onItemClickListener);		
	}
	OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_car_remind_menu:
				if(isFinish){
					finish();
				}else{
					ActivityFactory.A.LeftMenu();
				}
				break;
			case R.id.iv_activity_car_remind_home:
				if(isFinish){
					finish();
				}else{
					ActivityFactory.A.ToHome();
				}
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
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			for(int i = 0 ; i < Config.carDatas.size() ; i++){
				Config.carDatas.get(i).setCheck(false);
			}
			Config.carDatas.get(arg2).setCheck(true);
			carAdapter.notifyDataSetChanged();
		}
	};
}