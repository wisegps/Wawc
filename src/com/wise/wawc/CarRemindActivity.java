package com.wise.wawc;

import com.wise.extend.CarAdapter;
import com.wise.extend.OpenDateDialog;
import com.wise.extend.OpenDateDialogListener;
import com.wise.pubclas.Config;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
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
	boolean isJump = false;//false从菜单页跳转过来返回打开菜单，true从首页跳转返回关闭页面
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
		ImageView iv_activity_car_remind_inspection_note = (ImageView)findViewById(R.id.iv_activity_car_remind_inspection_note);
		iv_activity_car_remind_inspection_note.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_renewal_note = (ImageView)findViewById(R.id.iv_activity_car_remind_renewal_note);
		iv_activity_car_remind_renewal_note.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_examined_note = (ImageView)findViewById(R.id.iv_activity_car_remind_examined_note);
		iv_activity_car_remind_examined_note.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_replacement_note = (ImageView)findViewById(R.id.iv_activity_car_remind_replacement_note);
		iv_activity_car_remind_replacement_note.setOnClickListener(onClickListener);
		
		ImageView iv_activity_car_remind_inspection_problem = (ImageView)findViewById(R.id.iv_activity_car_remind_inspection_problem);
		iv_activity_car_remind_inspection_problem.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_renewal_problem = (ImageView)findViewById(R.id.iv_activity_car_remind_renewal_problem);
		iv_activity_car_remind_renewal_problem.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_maintenance_problem = (ImageView)findViewById(R.id.iv_activity_car_remind_maintenance_problem);
		iv_activity_car_remind_maintenance_problem.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_examined_problem = (ImageView)findViewById(R.id.iv_activity_car_remind_examined_problem);
		iv_activity_car_remind_examined_problem.setOnClickListener(onClickListener);
		ImageView iv_activity_car_remind_replacement_problem = (ImageView)findViewById(R.id.iv_activity_car_remind_replacement_problem);
		iv_activity_car_remind_replacement_problem.setOnClickListener(onClickListener);
		
		Intent intent = getIntent();
		isJump = intent.getBooleanExtra("isJump", false);
		
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
		
		OpenDateDialog.SetCustomDateListener(new OpenDateDialogListener() {			
			@Override
			public void OnDateChange(String Date) {
				// TODO Auto-generated method stub
				System.out.println(Date);
			}
		});
	}
	OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_car_remind_menu:
				if(isJump){
					finish();
				}else{
					ActivityFactory.A.LeftMenu();
				}
				break;
			case R.id.iv_activity_car_remind_home:
				if(isJump){
					finish();
				}else{
					ActivityFactory.A.ToHome();
				}
				break;
			case R.id.iv_activity_car_remind_maintenance_note://车辆保养
				Intent RemindIntent = new Intent(CarRemindActivity.this, MyVehicleActivity.class);
				RemindIntent.putExtra("isJump", true);
				CarRemindActivity.this.startActivity(RemindIntent);
				break;
			case R.id.iv_activity_car_remind_inspection_note://年检提醒
				ShowDate();
				break;
			case R.id.iv_activity_car_remind_renewal_note://车辆续保
				ShowDate();
				break;
			case R.id.iv_activity_car_remind_examined_note://驾照年审
				ShowDate();
				break;
			case R.id.iv_activity_car_remind_replacement_note://驾照换证
				ShowDate();
				break;				
			case R.id.iv_activity_car_remind_inspection_problem://年检提醒
				ToDealAdress(getString(R.string.inspection_title));
				break;
			case R.id.iv_activity_car_remind_examined_problem://驾照年审
				ToDealAdress(getString(R.string.examined_title));
				break;
			case R.id.iv_activity_car_remind_replacement_problem://驾照换证
				ToDealAdress(getString(R.string.replacement_title));
				break;
			case R.id.iv_activity_car_remind_renewal_problem://车辆续保
				ToCall("phone");
				break;
			case R.id.iv_activity_car_remind_maintenance_problem://车辆保养
				ToCall("phone");
				break;
			}
		}		
	};
	private void ToDealAdress(String Title){
		Intent intent = new Intent(CarRemindActivity.this, DealAddressActivity.class);
		intent.putExtra("Title", Title);
		startActivity(intent);
	}
	private void ToCall(String phone){
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ 10010));  
		startActivity(intent);
	}
	private void ShowDate(){
		OpenDateDialog.ShowDate(CarRemindActivity.this);
	}
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(isJump){
				finish();
			}
			return false;//拦截load页面的返回事件
		}
		return super.onKeyDown(keyCode, event);
	}
}