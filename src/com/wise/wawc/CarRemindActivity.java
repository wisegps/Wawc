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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	LinearLayout ll_inspection,ll_renewal,ll_maintenance,ll_examined,ll_replacement;
	//RelativeLayout rl_inspection,rl_renewal,rl_maintenance,rl_examined,rl_replacement;
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
		
		//年检
		ll_inspection = (LinearLayout)findViewById(R.id.ll_inspection);
		RelativeLayout rl_inspection = (RelativeLayout)findViewById(R.id.rl_inspection);
		rl_inspection.setOnClickListener(onClickListener);
		ImageView iv_inspection = (ImageView)findViewById(R.id.iv_inspection);
		iv_inspection.setOnClickListener(onClickListener);
		Button bt_inspection_time = (Button)findViewById(R.id.bt_inspection_time);
		bt_inspection_time.setOnClickListener(onClickListener);
		Button bt_inspection_address = (Button)findViewById(R.id.bt_inspection_address);
		bt_inspection_address.setOnClickListener(onClickListener);
		//续保
		ll_renewal = (LinearLayout)findViewById(R.id.ll_renewal);
		RelativeLayout rl_renewal = (RelativeLayout)findViewById(R.id.rl_renewal);
		rl_renewal.setOnClickListener(onClickListener);
		ImageView iv_renewal = (ImageView)findViewById(R.id.iv_renewal);
		iv_renewal.setOnClickListener(onClickListener);
		Button bt_renewal_time = (Button)findViewById(R.id.bt_renewal_time);
		bt_renewal_time.setOnClickListener(onClickListener);
        Button bt_renewal_call = (Button)findViewById(R.id.bt_renewal_call);
        bt_renewal_call.setOnClickListener(onClickListener);
        //保养
        ll_maintenance = (LinearLayout)findViewById(R.id.ll_maintenance);
        RelativeLayout rl_maintenance = (RelativeLayout)findViewById(R.id.rl_maintenance);
        rl_maintenance.setOnClickListener(onClickListener);
        ImageView iv_maintenance = (ImageView)findViewById(R.id.iv_maintenance);
        iv_maintenance.setOnClickListener(onClickListener);
        Button bt_maintenance = (Button)findViewById(R.id.bt_maintenance);
        bt_maintenance.setOnClickListener(onClickListener);
        Button bt_maintenance_call = (Button)findViewById(R.id.bt_maintenance_call);
        bt_maintenance_call.setOnClickListener(onClickListener);
        //年审
        ll_examined = (LinearLayout)findViewById(R.id.ll_examined);
        RelativeLayout rl_examined = (RelativeLayout)findViewById(R.id.rl_examined);
        rl_examined.setOnClickListener(onClickListener);
        ImageView iv_examined = (ImageView)findViewById(R.id.iv_examined);
        iv_examined.setOnClickListener(onClickListener);
        Button bt_examined_time = (Button)findViewById(R.id.bt_examined_time);
        bt_examined_time.setOnClickListener(onClickListener);
        Button bt_examined_address = (Button)findViewById(R.id.bt_examined_address);
        bt_examined_address.setOnClickListener(onClickListener);
        //驾照
        ll_replacement = (LinearLayout)findViewById(R.id.ll_replacement);
        RelativeLayout rl_replacement = (RelativeLayout)findViewById(R.id.rl_replacement);
        rl_replacement.setOnClickListener(onClickListener);
        ImageView iv_replacement = (ImageView)findViewById(R.id.iv_replacement);
        iv_replacement.setOnClickListener(onClickListener);
        Button bt_replacement_time = (Button)findViewById(R.id.bt_replacement_time);
        bt_replacement_time.setOnClickListener(onClickListener);
        Button bt_replacement_address = (Button)findViewById(R.id.bt_replacement_address);
        bt_replacement_address.setOnClickListener(onClickListener);
		
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
		
		//ShowText(carData);
		
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
			case R.id.rl_inspection:
			    hideLinearlayout();
			    ll_inspection.setVisibility(View.VISIBLE);
			    break;
			case R.id.rl_renewal:
			    hideLinearlayout();
			    ll_renewal.setVisibility(View.VISIBLE);
                break;
			case R.id.rl_maintenance:
			    hideLinearlayout();
			    ll_maintenance.setVisibility(View.VISIBLE);
                break;
			case R.id.rl_examined:
			    hideLinearlayout();
			    ll_examined.setVisibility(View.VISIBLE);
                break;
			case R.id.rl_replacement:
			    hideLinearlayout();
			    ll_replacement.setVisibility(View.VISIBLE);
                break;
			case R.id.bt_maintenance://车辆保养
				Intent RemindIntent = new Intent(CarRemindActivity.this, MyVehicleActivity.class);
				RemindIntent.putExtra("isJump", true);
				CarRemindActivity.this.startActivity(RemindIntent);
				break;
			case R.id.bt_inspection_time://年检提醒
				ShowDate(inspection);
				break;
			case R.id.bt_renewal_time://车辆续保
				ShowDate(renewal);
				break;
			case R.id.bt_examined_time://驾照年审
				ShowDate(examined);
				break;
			case R.id.bt_replacement_time://驾照换证
				ShowDate(replacement);
				break;				
			case R.id.bt_inspection_address://年检提醒
				ToDealAdress(getString(R.string.inspection_title));
				break;
			case R.id.bt_examined_address://驾照年审
				ToDealAdress(getString(R.string.examined_title));
				break;
			case R.id.bt_replacement_address://驾照换证
				ToDealAdress(getString(R.string.replacement_title));
				break;
			case R.id.bt_renewal_call://车辆续保
				ToCall("phone");
				break;
			case R.id.bt_maintenance_call://车辆保养
				ToCall("phone");
				break;
			}
		}		
	};
	private void hideLinearlayout(){
	    ll_inspection.setVisibility(View.GONE);
	    ll_renewal.setVisibility(View.GONE);
	    ll_maintenance.setVisibility(View.GONE);
	    ll_examined.setVisibility(View.GONE);
	    ll_replacement.setVisibility(View.GONE);
	}
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