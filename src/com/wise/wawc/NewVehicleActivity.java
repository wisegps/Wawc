package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.wawc.MyVehicleActivity.ClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    
    private static final int Add_car = 1;
	
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
			    addCar();
				//Toast.makeText(getApplicationContext(), "添加新车辆成功", 0).show();
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
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Add_car:
                System.out.println("add Car = " + msg.obj.toString());
                break;

            default:
                break;
            }
        }	    
	};
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
	
	private void addCar(){
	    String url = Constant.BaseUrl + "vehicle?auth_code=" + Variable.auth_code;
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
        params.add(new BasicNameValuePair("obj_name", "粤B12345"));
        params.add(new BasicNameValuePair("car_brand", "大众"));
        params.add(new BasicNameValuePair("car_series", "途安"));
        params.add(new BasicNameValuePair("car_type", "2013款 1.4T 自动 睿智版 5座"));
        params.add(new BasicNameValuePair("engine_no", "109088"));
        params.add(new BasicNameValuePair("frame_no", "123456"));
        params.add(new BasicNameValuePair("insurance_company", "中国人保汽车保险"));
        params.add(new BasicNameValuePair("insurance_date", "2013-07-01"));
        params.add(new BasicNameValuePair("annual_inspect_date", "2014-10-01"));
        params.add(new BasicNameValuePair("maintain_company", "德熙大众4S店"));
        params.add(new BasicNameValuePair("maintain_last_mileage", "12000"));
        params.add(new BasicNameValuePair("maintain_last_date", "2014-10-01"));
        params.add(new BasicNameValuePair("maintain_next_mileage", "22000"));
        params.add(new BasicNameValuePair("buy_date", "2012-09-29"));
        
        new Thread(new NetThread.postDataThread(handler, url, params, Add_car)).start();
	}
}
