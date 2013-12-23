package com.wise.wawc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 我的爱车
 * @author 王庆文
 */
public class MyVehicleActivity extends Activity {
	private Button menu = null;
	private Button home = null;
	private ImageView addCar = null;
	private ImageView brand = null;
	private ImageView device = null;
	private ImageView insuranceCompany = null;
	private TextView showInsuranceCompany;   //显示保险公司
	public static final int resultCodeInsurance = 2;   //选择保险公司的识别码
	public static final int resultCodeBrank = 3;       //选择汽车品牌的识别码
	public static final int resultCodeMaintain = 6;       //选择汽车品牌的识别码
	
	
	private EditText etDialogMileage = null;   //输入里程
	
	private Button btSureMileage = null;
	private Button btCancleMileage = null;
	private ImageView insuranceTime;
	private ImageView choiceMaintian = null;
	
	private TextView tvMileage = null;  //显示里程
	private TextView myVehicleBrank = null;
	private TextView tvMaintain = null;
	
	AlertDialog dlg = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_vehicle);
		menu = (Button) findViewById(R.id.my_vechile_menu);
		home = (Button) findViewById(R.id.my_vechile_home);
		addCar = (ImageView) findViewById(R.id.add_car);
		brand = (ImageView) findViewById(R.id.my_vehicle_brank);
		device = (ImageView) findViewById(R.id.	my_vehicle_device);
		insuranceCompany = (ImageView)findViewById(R.id.insurance_company);
		showInsuranceCompany = (TextView) findViewById(R.id.show_insurance_company);
		insuranceTime = (ImageView) findViewById(R.id.insurance_mileage);
		tvMileage = (TextView) findViewById(R.id.my_vehicle_mileage);
		myVehicleBrank = (TextView) findViewById(R.id.my_vehicle_beank);
		choiceMaintian = (ImageView) findViewById(R.id.choice_maintain_image);
		tvMaintain = (TextView) findViewById(R.id.show_maintain);

		choiceMaintian.setOnClickListener(new ClickListener());
		device.setOnClickListener(new ClickListener());
		menu.setOnClickListener(new ClickListener());
		home.setOnClickListener(new ClickListener());
		addCar.setOnClickListener(new ClickListener());
		brand.setOnClickListener(new ClickListener());
		insuranceCompany.setOnClickListener(new ClickListener());
		insuranceTime.setOnClickListener(new ClickListener());
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
			case R.id.add_car:    //添加车辆
				startActivity(new Intent(MyVehicleActivity.this,NewVehicleActivity.class));
				break;
			case R.id.my_vehicle_brank:    //选择汽车品牌
				Intent intent = new Intent(MyVehicleActivity.this,CarBrankListActivity.class);
				intent.putExtra("code", resultCodeBrank);
				startActivityForResult(intent, resultCodeBrank);
				break;
			case R.id.my_vehicle_device:    //我的终端
				startActivity(new Intent(MyVehicleActivity.this,MyDevicesActivity.class));
				break;
			case R.id.insurance_company:  //选择保险公司
				Intent intent1 = new Intent(MyVehicleActivity.this,ChoiceInsuranceActivity.class);
				intent1.putExtra("code", resultCodeInsurance);
				startActivityForResult(intent1, resultCodeInsurance);
				break;
			case R.id.insurance_mileage:  //同步里程
				showDialog();
				break;
			case R.id.dialog_mileage_sure:  //确定同步里程
				String mileageValue = etDialogMileage.getText().toString();
				if("".equals(mileageValue.trim())){
					Toast.makeText(MyVehicleActivity.this, "请输入正确的里程", 0).show();
				}else{
					tvMileage.setText(mileageValue.trim() + "Km");
					dlg.cancel();
				}
				break;
			case R.id.dialog_mileage_cancle:  //取消同步里程
				dlg.cancel();
				break;
				
			case R.id.choice_maintain_image:  //选择保养店
				Intent intent3 = new Intent(MyVehicleActivity.this,MaintainShopActivity.class);
				intent3.putExtra("code", resultCodeMaintain);
				startActivityForResult(intent3, resultCodeMaintain);
				break;
			default:
				return;
			}
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == this.resultCodeInsurance){
			//设置选择的保险公司
			String insurance = (String) data.getSerializableExtra("ClickItem");
			showInsuranceCompany.setText(insurance);
		}
		//选择品牌
		if(resultCode == this.resultCodeBrank){
			String brank = (String) data.getSerializableExtra("brank");
			myVehicleBrank.setText(brank);
		}
		//选择保养店
		if(resultCode == this.resultCodeMaintain){
			String maintain = (String) data.getSerializableExtra("maintain");
			tvMaintain.setText(maintain);
		}
	}
	
	
	void showDialog(){
		LayoutInflater layoutInflater = LayoutInflater.from(MyVehicleActivity.this);
		View view = layoutInflater.inflate(R.layout.mileage_dialog, null);
		etDialogMileage = (EditText) view.findViewById(R.id.mileage);
		btSureMileage = (Button) view.findViewById(R.id.dialog_mileage_sure);
		btCancleMileage = (Button) view.findViewById(R.id.dialog_mileage_cancle);
		btSureMileage.setOnClickListener(new ClickListener());
		btCancleMileage.setOnClickListener(new ClickListener());
		dlg = new AlertDialog.Builder(MyVehicleActivity.this).setView(view).setCancelable(true).create();
		dlg.show();
	}
}

