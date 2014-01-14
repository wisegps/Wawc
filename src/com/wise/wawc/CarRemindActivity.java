package com.wise.wawc;

import com.wise.data.CarData;
import com.wise.extend.CarAdapter;
import com.wise.extend.OpenDateDialog;
import com.wise.extend.OpenDateDialogListener;
import com.wise.pubclas.Variable;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 车务提醒
 * @author honesty
 */
public class CarRemindActivity extends Activity{
	private static final String TAG = "CarRemindActivity";
	/**
	 * 年检提醒
	 */
	private static final int inspection = 1;
	/**
	 * 车辆续保
	 */
	private static final int renewal = 2;
	/**
	 * 驾照年审
	 */
	private static final int examined = 3;
	/**
	 * 驾照换证
	 */
	private static final int replacement = 4;
	TextView tv_activity_car_remind_inspection,tv_activity_car_remind_remind_renewal;
	
	CarAdapter carAdapter;
	CarData carData;//默认指定第0个
	
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
		
		tv_activity_car_remind_inspection = (TextView)findViewById(R.id.tv_activity_car_remind_inspection);
		tv_activity_car_remind_remind_renewal = (TextView)findViewById(R.id.tv_activity_car_remind_remind_renewal);
		
		Intent intent = getIntent();
		isJump = intent.getBooleanExtra("isJump", false);
		
		GridView gv_activity_car_remind = (GridView)findViewById(R.id.gv_activity_car_remind);
        carAdapter = new CarAdapter(CarRemindActivity.this,Variable.carDatas);
        gv_activity_car_remind.setAdapter(carAdapter);
        
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(Variable.carDatas.size() * (px + 10),LayoutParams.WRAP_CONTENT);
		gv_activity_car_remind.setLayoutParams(params);
		gv_activity_car_remind.setColumnWidth(px);
		gv_activity_car_remind.setHorizontalSpacing(10);
		gv_activity_car_remind.setStretchMode(GridView.NO_STRETCH);
		gv_activity_car_remind.setNumColumns(Variable.carDatas.size());
		gv_activity_car_remind.setOnItemClickListener(onItemClickListener);	
		
		ShowText(carData);
		
		OpenDateDialog.SetCustomDateListener(new OpenDateDialogListener() {			
			@Override
			public void OnDateChange(String Date,int index) {
				// TODO Auto-generated method stub
				System.out.println(Date);
				switch (index) {
                case inspection:
                    //TODO 更新年检时间
                    break;

                default:
                    break;
                }
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
				ShowDate(inspection);
				break;
			case R.id.iv_activity_car_remind_renewal_note://车辆续保
				ShowDate(renewal);
				break;
			case R.id.iv_activity_car_remind_examined_note://驾照年审
				ShowDate(examined);
				break;
			case R.id.iv_activity_car_remind_replacement_note://驾照换证
				ShowDate(replacement);
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
	private void ShowDate(int index){
		OpenDateDialog.ShowDate(CarRemindActivity.this,index);
	}
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			for(int i = 0 ; i < Variable.carDatas.size() ; i++){
			    Variable.carDatas.get(i).setCheck(false);
			}
			Variable.carDatas.get(arg2).setCheck(true);
			carAdapter.notifyDataSetChanged();
			carData = Variable.carDatas.get(arg2);
			ShowText(carData);
		}
	};
	/**
	 * 清空文本数据
	 */
	private void ShowText(CarData carData){
	    if(carData.getAnnual_inspect_date() != null){
            String Annual_inspect_date = String.format(getResources().getString(R.string.inspection_content), carData.getAnnual_inspect_date());
            tv_activity_car_remind_inspection.setText(Annual_inspect_date);
        }
        if(carData.getInsurance_date() != null){
            String Insurance_date = String.format(getResources().getString(R.string.renewal_content), carData.getInsurance_date());
            tv_activity_car_remind_remind_renewal.setText(Insurance_date);
        }
	}
	
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