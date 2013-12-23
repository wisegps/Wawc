package com.wise.wawc;

import com.wise.wawc.MyVehicleActivity.ClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 添加新车辆
 * @author 王庆文
 */
public class NewVehicleActivity extends Activity {
	
	private Button cancleAdd = null;   //取消新车辆的添加
	private Button saveAdd = null;     //保存添加
	private ImageView choiceBrank = null;    //选择品牌
	public static final int newVehicleBrank = 4;
	public static final int newVehicleInsurance = 5;
	public static final int newVehicleMaintain = 7;
	
	private TextView vehicleBrank = null;  //选择车辆品牌
	private ImageView choiceInsurance = null;  
	private ImageView ivMaintain = null;
	private TextView showMaintain = null;
	
	
	private TextView showInsurance = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_vehicle);
		cancleAdd = (Button) findViewById(R.id.add_vechile_cancle);
		saveAdd = (Button) findViewById(R.id.add_vechile_save);
		choiceBrank = (ImageView) findViewById(R.id.add_vehicle_choice_brank);
		vehicleBrank = (TextView) findViewById(R.id.new_vehicle_brank );
		choiceInsurance = (ImageView) findViewById(R.id.add_vehicle_choice_insurance);
		showInsurance = (TextView) findViewById(R.id.add_vehicle_show_insurance);
		ivMaintain = (ImageView) findViewById(R.id.add_vehicle_maintain);
		showMaintain = (TextView) findViewById(R.id.add_vehicle_show_maintain);
		
		ivMaintain.setOnClickListener(new CilckListener());
		saveAdd.setOnClickListener(new CilckListener());
		choiceBrank.setOnClickListener(new CilckListener());
		cancleAdd.setOnClickListener(new CilckListener());
		choiceInsurance.setOnClickListener(new CilckListener());
	}
	
	class CilckListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.add_vechile_cancle:
				NewVehicleActivity.this.finish();
				break;
			case R.id.add_vechile_save:
				Toast.makeText(getApplicationContext(), "添加新车辆成功", 0).show();
				break;
			case R.id.add_vehicle_choice_brank:   //选择车辆品牌
				Intent intent = new Intent(NewVehicleActivity.this,CarBrankListActivity.class);
				intent.putExtra("code", newVehicleBrank);
				startActivityForResult(intent, newVehicleBrank);
				break;
			case R.id.add_vehicle_choice_insurance:   //选择保险公司
				Intent intent1 = new Intent(NewVehicleActivity.this,ChoiceInsuranceActivity.class);
				intent1.putExtra("code", newVehicleInsurance);
				startActivityForResult(intent1, newVehicleInsurance);
				break;
			case R.id.add_vehicle_maintain:   //选择保养公司
				Intent intent2 = new Intent(NewVehicleActivity.this,MaintainShopActivity.class);
				intent2.putExtra("code", newVehicleMaintain);
				startActivityForResult(intent2, newVehicleMaintain);
				break;	
				
			default:
				return;
			}
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == newVehicleBrank){  //设置品牌
			String brank = (String)data.getSerializableExtra("brank");
			vehicleBrank.setText(brank); 
		}else if(resultCode == newVehicleInsurance){   //设置保险公司
			String insurance = (String)data.getSerializableExtra("ClickItem");
			showInsurance.setText(insurance);
		}else if(resultCode == newVehicleMaintain){
			String maintain = (String)data.getSerializableExtra("maintain");
			showMaintain.setText(maintain);
		}
	}
}
