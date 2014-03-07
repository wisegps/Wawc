package com.wise.wawc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.CarData;
import com.wise.data.IllegalCity;
import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.SpinerPopWindow;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import com.wise.wawc.MyVehicleActivity.ClickListener;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 添加新车辆
 * @author 王庆文
 */
public class NewVehicleActivity extends Activity{
	
	private ImageView cancleAdd = null;   //取消新车辆的添加
	private TextView saveAdd = null;     //保存添加
	private TableRow choiceBrank = null;    //选择品牌
	public static final int newVehicleBrank = 4;
	public static final int newVehicleSeries = 8;
	public static final int newVehicleType = 9;
	public static final int newVehicleInsurance = 5;
	public static final int newVehicleMaintain = 7;
	private static final int addCar = 6;
	
	private TextView vehicleBrank = null;  //选择车辆品牌
	private TableRow choiceInsurance = null;  
	private TableRow ivMaintain = null;
	private TextView showMaintain = null;
	
	private TextView TvVehicleSeries = null;
	private TextView TvVehicleType = null;
	private TableRow IvVehicleSeries = null;  
	private TableRow IvVehicleType = null;
	private TableRow illegalCityRow = null;
	private TableRow engineNumRow = null;
	private TableRow vehicleFrameNumRow = null;
	private TableRow vehicleRegNumRow = null;
	
	
	private TextView maintainShop = null;
	private TextView illegalCityTv = null;
	private EditText carRegNumber = null;
	private EditText carNumber = null;
	private EditText engineNumber = null;
	private EditText CJNumber = null;
	private TextView insuranceTime = null;
	private TextView lastMaintainTime = null;
	private EditText nextMaintainMileage = null;
	private EditText lastMileage = null;
	private TextView buyTime = null;
	private EditText annualSurveyTime = null;
	
	private DatePickerDialog mDateDialog = null;
	private TextView mBeginDateTv,mEndDateTv;
	private int mThisDatePicker;
	
	private static String carSeriesTitle = "carSeries";
	private static String carTypeTitle = "carType";
	private static final int refreshCarSeries = 2;
	private static final int getCityViolateRegulationsCode = 9;
	private List<String> carSeriesNameList = new ArrayList<String>();
	private List<String> carSeriesIdList = new ArrayList<String>();
	private int width = 0 ;
	
	private ProgressDialog myDialog = null;
	private MyHandler myHandler = null;
	private DBHelper dBhalper = null;
	private DBExcute dBExcute = null;
	
	private TextView showInsurance = null;
	private String carBrank = "";
	private String carSeries = "";
	private String carType = "";
	private String carBrankId = "";
	private String carSeriesId = "";
	private String carTypeId = "";
	private Bitmap vehicleLogoBitmap = null;
	private IllegalCity illegalCity;
	private String city_code = "";
	private String illegalCityStr = "";
	private int register = 0;
	private int registerNo = 0;
	private int engine = 0;
	private int engineNo = 0;
	private int car = 0;
	private int carNo = 0;
	private String illegalCityCode = "";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_vehicle);
		cancleAdd = (ImageView) findViewById(R.id.new_vechile_cancle_iv);
		saveAdd = (TextView) findViewById(R.id.new_vechile_commit_tv);
		choiceBrank = (TableRow) findViewById(R.id.new_vehicle_brank_tr);
		vehicleBrank = (TextView) findViewById(R.id.new_vehicle_brank_tv );
		choiceInsurance = (TableRow) findViewById(R.id.new_vehicle_insurance_tr);
		showInsurance = (TextView) findViewById(R.id.new_vehicle_insurance_tv);
		ivMaintain = (TableRow) findViewById(R.id.new_vehicle_maintain_tr);
		showMaintain = (TextView) findViewById(R.id.new_vehicle_maintain_tv);
		illegalCityRow = (TableRow) findViewById(R.id.new_vehicle_illegal_city_tr);
		engineNumRow = (TableRow) findViewById(R.id.new_vehicle_engine_num_tr);
		vehicleFrameNumRow = (TableRow) findViewById(R.id.new_vehicle_frame_tr);
		vehicleRegNumRow = (TableRow) findViewById(R.id.new_vehicle_register_num_tr);
		
		TvVehicleSeries = (TextView) findViewById(R.id.new_vehicle_series_tv);
		illegalCityTv = (TextView) findViewById(R.id.new_vehicle_illegal_city_tv);
		TvVehicleType = (TextView) findViewById(R.id.new_vehicle_type_tv);
		IvVehicleSeries = (TableRow) findViewById(R.id.new_vehicle_series_tr);
		IvVehicleType = (TableRow) findViewById(R.id.new_vehicle_type_tr);
		carNumber = (EditText) findViewById(R.id.new_vehicle_number_et);
		carRegNumber = (EditText) findViewById(R.id.new_vehilce_reg_num_et);
		engineNumber = (EditText) findViewById(R.id.new_vehicle_engine_num_et);
		CJNumber = (EditText) findViewById(R.id.new_vehilce_frame_et);
		insuranceTime = (TextView) findViewById(R.id.new_vehicle_insurance_time_tv);
		getDateView(insuranceTime);
		lastMaintainTime = (TextView) findViewById(R.id.new_vehicle_last_maintain_tv);
		getDateView(lastMaintainTime);
		lastMileage = (EditText) findViewById(R.id.new_vehicle_last_maintain_et);
		buyTime = (TextView) findViewById(R.id.new_vehicle_buy_time_tv);
		getDateView(buyTime);
//		nextMaintainMileage = (EditText) findViewById(R.id.et_next_maintain_mileage);
//		annualSurveyTime = (EditText) findViewById(R.id.annual_survey_time);
//		getDateView(annualSurveyTime);
//		getDateView(annualSurveyTime);
		
		
		myHandler = new MyHandler();
		dBhalper = new DBHelper(NewVehicleActivity.this);
		dBExcute = new DBExcute();
		
		illegalCityRow.setOnClickListener(new CilckListener());
		ivMaintain.setOnClickListener(new CilckListener());
		saveAdd.setOnClickListener(new CilckListener());
		choiceBrank.setOnClickListener(new CilckListener());
		cancleAdd.setOnClickListener(new CilckListener());
		choiceInsurance.setOnClickListener(new CilckListener());
		IvVehicleSeries.setOnClickListener(new CilckListener());
		IvVehicleType.setOnClickListener(new CilckListener());
		IvVehicleSeries.setOnClickListener(new CilckListener());
		IvVehicleType.setOnClickListener(new CilckListener());
		
		
		engineNumRow.setVisibility(View.GONE);
		vehicleFrameNumRow.setVisibility(View.GONE);
		vehicleRegNumRow.setVisibility(View.GONE);
	}
	
	class CilckListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.new_vechile_cancle_iv:
				NewVehicleActivity.this.finish();
				break;
			case R.id.new_vechile_commit_tv:
				//  TODO
				if(getVehicleData()){
					addCar();
				}
				break;
			case R.id.new_vehicle_brank_tr:   //选择车辆品牌
				Intent intent = new Intent(NewVehicleActivity.this,ChoiceCarInformationActivity.class);
				intent.putExtra("code", newVehicleBrank);
				startActivityForResult(intent, newVehicleBrank);
				break;
			case R.id.new_vehicle_series_tr:
				if("".equals(carBrankId)){
					Toast.makeText(NewVehicleActivity.this, "请选择品牌", 0).show();
				}else{
					Intent intent1 = new Intent(NewVehicleActivity.this,ChoiceCarInformationActivity.class);
					intent1.putExtra("code", newVehicleSeries);
					intent1.putExtra("brankId", carBrankId);
					intent1.putExtra("carBrank", carBrank);
					startActivityForResult(intent1, newVehicleSeries);
				}
				break;
			case R.id.new_vehicle_type_tr:
				if("".equals(carBrankId)){
					Toast.makeText(NewVehicleActivity.this, "请选择品牌", 0).show();
				}else{
					Intent intent6 = new Intent(NewVehicleActivity.this,ChoiceCarInformationActivity.class);
					intent6.putExtra("code", newVehicleSeries);
					intent6.putExtra("brankId", carBrankId);
					intent6.putExtra("carBrank", carBrank);
					intent6.putExtra("seriesId", carSeriesId);
					intent6.putExtra("series", carSeries);
					startActivityForResult(intent6, newVehicleType);
				}
				break;
			case R.id.new_vehicle_illegal_city_tr:  //选择违章城市
				Intent intent6 = new Intent(NewVehicleActivity.this,IllegalCitiyActivity.class);
				intent6.putExtra("requestCode", getCityViolateRegulationsCode);
				startActivityForResult(intent6, getCityViolateRegulationsCode);
				break;
			case R.id.new_vehicle_insurance_tr:   //选择保险公司
				Intent intent1 = new Intent(NewVehicleActivity.this,InsuranceActivity.class);
				intent1.putExtra("code", newVehicleInsurance);
				startActivityForResult(intent1, newVehicleInsurance);
				break;
			case R.id.new_vehicle_maintain_tr:   //选择保养公司（4s店）
				SharedPreferences shareFile = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
				if("".equals(shareFile.getString(Constant.LocationCity, ""))){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_choice_city), 0).show();
				}else if("".equals(carBrank)){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_choice_vehicle_brank), 0).show();
				}else{
					Intent intent2 = new Intent(NewVehicleActivity.this,MaintainShopActivity.class);
					intent2.putExtra("code", newVehicleMaintain);
					intent2.putExtra("brank", carBrank);
					intent2.putExtra("city", shareFile.getString(Constant.LocationCity, ""));
					startActivityForResult(intent2, newVehicleMaintain);
				}
				break;	
			default:
				return;
			}
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == newVehicleBrank){  //设置品牌
			
			carBrank = data.getStringExtra("brank");
			carSeries = data.getStringExtra("series");
			carType = data.getStringExtra("type");
			carBrankId = data.getStringExtra("brankId");
			
			vehicleBrank.setText(carBrank);
			TvVehicleSeries.setText(carSeries);
			TvVehicleType.setText(carType);
			
//			carBrankId = data.getStringExtra("carId");
//			Bitmap logo = logoImageIsExist(Constant.VehicleLogoPath,data.getStringExtra("carLogo"));
			
			
		}else if(resultCode == newVehicleSeries){
			carBrank = data.getStringExtra("brank");
			carBrankId = data.getStringExtra("brankId");
			carSeries = data.getStringExtra("series");
			carSeriesId = data.getStringExtra("seriesId");
			carType = data.getStringExtra("type");
			vehicleBrank.setText(carBrank);
			TvVehicleSeries.setText(carSeries);
			TvVehicleType.setText(carType);
		}else if(resultCode == newVehicleInsurance){   //设置保险公司
			String insurance = (String)data.getSerializableExtra("insurance_name");
			String insurance_phone = (String)data.getSerializableExtra("insurance_phone");
			showInsurance.setText(insurance);
		}else if(resultCode == newVehicleMaintain){
			String maintainName = (String)data.getSerializableExtra("maintain_name");
			String maintainTel = (String)data.getSerializableExtra("maintain_name");
			showMaintain.setText(maintainName);
		}else if(resultCode == getCityViolateRegulationsCode){    //设置为违章城市
			illegalCity = (IllegalCity) data.getSerializableExtra("IllegalCity");
			illegalCityCode = illegalCity.getCityCode();
			if(illegalCity != null){
				Log.e("illegalCity.getEngine()",illegalCity.getEngine());
				Log.e("illegalCity.getEngineno()",illegalCity.getEngineno());
				Log.e("illegalCity.getVehiclenum()",illegalCity.getVehiclenum());
				Log.e("illegalCity.getVehiclenumno()",illegalCity.getVehiclenumno());
				Log.e("illegalCity.getRegisternum()",illegalCity.getRegist());
				Log.e("illegalCity.getVehiclenumno()",illegalCity.getRegistno());
				engineNumRow.setVisibility(View.VISIBLE);
				vehicleFrameNumRow.setVisibility(View.VISIBLE);
				vehicleRegNumRow.setVisibility(View.VISIBLE);
				city_code = illegalCity.getCityCode();  //城市代码
				illegalCityStr = illegalCity.getCityName();   //显示需要的城市名字
				illegalCityTv.setText(illegalCity.getCityName());
				if(Integer.valueOf(illegalCity.getEngine()) == 0){  //隐藏发动机
					engineNumRow.setVisibility(View.GONE);
					engine = 0;
				}else if(Integer.valueOf(illegalCity.getEngine()) == 1){
					engine = 1;
					engineNo = Integer.valueOf(illegalCity.getEngineno());
					if(engineNo == 0){
						engineNumber.setHint(this.getResources().getString(R.string.all_engine_num_hint));
					}else{
						engineNumber.setHint(this.getResources().getString(R.string.engine_num_hint) + engineNo + this.getResources().getString(R.string.hint_wei));
						engineNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(engineNo)});
					}
				}
				if(Integer.valueOf(illegalCity.getVehiclenum()) == 0){   //隐藏车架号
					vehicleFrameNumRow.setVisibility(View.GONE);
					car = 0;
				}else if(Integer.valueOf(illegalCity.getVehiclenum()) == 1){
					car = 1;
					carNo = Integer.valueOf(illegalCity.getVehiclenumno());
					if(carNo == 0){
						CJNumber.setHint(this.getResources().getString(R.string.all_vehicle_num_hint));
					}else{
						CJNumber.setHint(this.getResources().getString(R.string.vehicle_num_hint) + carNo + this.getResources().getString(R.string.hint_wei));
						CJNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(carNo)});
					}
				} 
				if(Integer.valueOf(illegalCity.getRegist()) == 0 ){    // 隐藏车辆登记证号
					vehicleRegNumRow.setVisibility(View.GONE);
					register = 0;
				}else if(Integer.valueOf(illegalCity.getRegist()) == 1){
					register = 1;
					registerNo = Integer.valueOf(illegalCity.getRegistno());
					if(registerNo == 0){
						carRegNumber.setHint(this.getResources().getString(R.string.all_register_num_hint));
					}else{
						carRegNumber.setHint(this.getResources().getString(R.string.register_num_hint) + registerNo + this.getResources().getString(R.string.hint_wei));
						carRegNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(registerNo)});
					}
				}
				illegalCity = null;
			}
		}
	}
	private void addCar(){
	    String url = Constant.BaseUrl + "vehicle?auth_code=" + Variable.auth_code;
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
        params.add(new BasicNameValuePair("obj_name", carNumber.getText().toString().trim()));
        params.add(new BasicNameValuePair("car_brand", vehicleBrank.getText().toString()));
        params.add(new BasicNameValuePair("car_series", TvVehicleSeries.getText().toString()));
        params.add(new BasicNameValuePair("car_type", TvVehicleType.getText().toString()));
        params.add(new BasicNameValuePair("vio_location", illegalCityCode));
        if(engine == 1){
        	params.add(new BasicNameValuePair("engine_no", engineNumber.getText().toString().trim()));
        }else{
        	params.add(new BasicNameValuePair("engine_no", ""));
        }
        if(car == 1){
        	params.add(new BasicNameValuePair("frame_no", CJNumber.getText().toString().trim()));
        }else{
        	params.add(new BasicNameValuePair("frame_no", ""));
        }
        if(register == 1){
        	params.add(new BasicNameValuePair("reg_no", carRegNumber.getText().toString().trim()));
        }else{
        	params.add(new BasicNameValuePair("reg_no", ""));
        }
        params.add(new BasicNameValuePair("insurance_company", showInsurance.getText().toString()));
        params.add(new BasicNameValuePair("insurance_date", insuranceTime.getText().toString()));
//        params.add(new BasicNameValuePair("annual_inspect_date", annualSurveyTime.getText().toString()));
        params.add(new BasicNameValuePair("annual_inspect_date", ""));
        params.add(new BasicNameValuePair("maintain_company", showMaintain.getText().toString()));
        params.add(new BasicNameValuePair("maintain_last_mileage", lastMileage.getText().toString().trim()));
        params.add(new BasicNameValuePair("maintain_last_date", lastMaintainTime.getText().toString()));
//        params.add(new BasicNameValuePair("maintain_next_mileage",nextMaintainMileage.getText().toString().trim()));
        params.add(new BasicNameValuePair("maintain_next_mileage",""));
        params.add(new BasicNameValuePair("buy_date", buyTime.getText().toString()));
        
        Log.e("车牌号",carNumber.getText().toString());
        Log.e("车辆品牌",vehicleBrank.getText().toString());
        Log.e("车辆型号",TvVehicleSeries.getText().toString());
        Log.e("车款",TvVehicleType.getText().toString());
        Log.e("城市代码",illegalCityCode);
        Log.e("发动机型号",engineNumber.getText().toString());
        Log.e("车架号",CJNumber.getText().toString());
        Log.e("登记证号",carRegNumber.getText().toString().trim());
        Log.e("保险到公司",showInsurance.getText().toString());
        Log.e("保险到期时间",insuranceTime.getText().toString());
        Log.e("4s店",showMaintain.getText().toString());
//        Log.e("年检时间",annualSurveyTime.getText().toString());
        Log.e("最后保养里程",lastMileage.getText().toString());
        Log.e("最后保养时间",lastMaintainTime.getText().toString());
//        Log.e("下次保养时间",nextMaintainMileage.getText().toString());
        Log.e("购车时间",buyTime.getText().toString());
        myDialog = ProgressDialog.show(NewVehicleActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
        new Thread(new NetThread.postDataThread(myHandler, url, params, addCar)).start();
	}
	class MyHandler extends Handler {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case refreshCarSeries:
				break;
			case addCar:
			    System.out.println(msg.obj.toString());
				myDialog.dismiss();
				Log.e("返回数据--->",msg.obj.toString());
				String  obj_id = "";
				String code = "";
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					obj_id = jsonObject.getString("obj_id");
					code = jsonObject.getString("status_code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if("0".equals(code)){
					//添加到数据库
				    System.out.println("保存到数据库");
					ContentValues value = new ContentValues();
					value.put("Cust_id", Variable.cust_id);
					value.put("obj_id", obj_id);
					value.put("obj_name", carNumber.getText().toString().trim());
					value.put("car_brand", vehicleBrank.getText().toString());
					value.put("car_series", TvVehicleSeries.getText().toString());
					value.put("car_type", TvVehicleType.getText().toString());
					value.put("vio_location", illegalCityCode);
					value.put("engine_no", engineNumber.getText().toString().trim());
					value.put("frame_no", CJNumber.getText().toString().trim());
					value.put("reg_no", carRegNumber.getText().toString().trim());
					value.put("insurance_company", showInsurance.getText().toString());
					value.put("insurance_date", insuranceTime.getText().toString());
//					value.put("annual_inspect_date", annualSurveyTime.getText().toString());
					value.put("annual_inspect_date", "");
					value.put("maintain_company", showMaintain.getText().toString());
					value.put("maintain_last_mileage", lastMileage.getText().toString().trim());
					value.put("maintain_last_date", lastMaintainTime.getText().toString());
					value.put("maintain_next_mileage", "");
					value.put("buy_date", buyTime.getText().toString());
					dBExcute.InsertDB(NewVehicleActivity.this, value, Constant.TB_Vehicle);
					Log.e("添加到数据库","添加到数据库");
					
				    CarData carData = new CarData();
	                carData.setCheck(false);
	                carData.setObj_id(Integer.parseInt(obj_id));
	                carData.setObj_name(carNumber.getText().toString().trim());
	                carData.setCar_brand(vehicleBrank.getText().toString());
	                carData.setCar_series(TvVehicleSeries.getText().toString());
	                carData.setCar_type(TvVehicleType.getText().toString());
	                carData.setVio_location(illegalCityCode);
	                carData.setEngine_no(engineNumber.getText().toString().trim());
	                carData.setFrame_no(CJNumber.getText().toString().trim());
	                carData.setRegNo(carRegNumber.getText().toString().trim());
	                carData.setInsurance_company(showInsurance.getText().toString());
	                carData.setInsurance_date(insuranceTime.getText().toString());
//	                carData.setAnnual_inspect_date(annualSurveyTime.getText().toString());
	                carData.setAnnual_inspect_date("");
	                carData.setMaintain_company(showMaintain.getText().toString());
	                carData.setMaintain_last_mileage(lastMileage.getText().toString().trim());
	                carData.setMaintain_last_date(lastMaintainTime.getText().toString());
	                carData.setMaintain_next_mileage("");
//	                carData.setMaintain_next_mileage(nextMaintainMileage.getText().toString().trim());
	                carData.setBuy_date( buyTime.getText().toString());
	                Variable.carDatas.add(carData);
	                Intent intent = new Intent(Constant.A_UpdateCar);
	                sendBroadcast(intent);
					NewVehicleActivity.this.finish();
				}else{
					Toast.makeText(getApplicationContext(), "添加失败，请重试", 0).show();
					return;
				}
				break;
			default:
				return;
			}
		}
	}
	
	public void getCarDatas(String title,String url,int code,String id){
		Log.e("访问地址--->",Constant.BaseUrl + url + id);
		SQLiteDatabase read = dBhalper.getReadableDatabase();
		//查询数据库
		Cursor cursor = read.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{title + id});
		JSONArray jsonArray = null;
		String carSeries = "";
		if(cursor.moveToFirst()){
			try {
				carSeries = cursor.getString(cursor.getColumnIndex("Content"));
				jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("Content")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			myDialog.dismiss();
		}else{
			//请求服务器
			Log.e("请求服务器","请求服务器");
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + url + id, code)).start();
		}
	}
	
	//获取用户输入的数据
	private boolean getVehicleData() {
		if("".equals(carNumber.getText().toString().trim())){
			carNumber.setError("车牌号不能为空");
			return false;
		}
		if(carNumber.getText().toString().trim().length() != 7){
			carNumber.setError("车牌号不合法");
			return false;
		}
		if("".equals(vehicleBrank.getText().toString().trim())){
			vehicleBrank.setHintTextColor(Color.RED);
			return false;
		}
		if("".equals(illegalCityTv.getText().toString().trim())){
			illegalCityTv.setHintTextColor(Color.RED);
			return false;
		}
		if(engine == 1){
 			if(engineNo == 0){
 				if(engineNumber.getText().toString().trim().length() == engineNo){
 					engineNumber.setError("引擎号不合法");
 					return false;
 				}
 			}else if(engineNumber.getText().toString().trim().length() != engineNo){
 				engineNumber.setError("引擎号不合法");
					return false;
				}
 		}
 		if(car == 1){
 			if(carNo == 0){
 				if(CJNumber.getText().toString().trim().length() == carNo){
 					CJNumber.setError("车架号不合法");
 	 				return false;
 	 			}
 			}else if(CJNumber.getText().toString().trim().length() != carNo){
 				CJNumber.setError("车架号不合法");
 				return false;
 			}
 		}
 		if(register == 1){
 			if(register == 0){
 				if(carRegNumber.getText().toString().trim().length() == carNo){
 					carRegNumber.setError("登记证号不合法");
 	 				return false;
 	 			}
 			}else if(carRegNumber.getText().toString().trim().length() != carNo){
 				carRegNumber.setError("登记证号不合法");
 				return false;
 			}
 		}
		
		if("".equals(showInsurance.getText().toString().trim())){
			showInsurance.setHintTextColor(Color.RED);
			return false;
		}
		if("".equals(insuranceTime.getText().toString().trim())){
			insuranceTime.setHintTextColor(Color.RED);
			return false;
		}
		if("".equals(showMaintain.getText().toString().trim())){
			showMaintain.setHintTextColor(Color.RED);
			return false;
		}
		if("".equals(lastMileage.getText().toString().trim())){
			lastMileage.setHintTextColor(Color.RED);
			return false;
		}
		if("".equals(lastMaintainTime.getText().toString().trim())){
			lastMaintainTime.setHintTextColor(Color.RED);
			return false;
		}
		if("".equals(buyTime.getText().toString().trim())){
			buyTime.setHintTextColor(Color.RED);
			return false;
		}
		if("".equals(lastMileage.getText().toString().trim())){
			lastMileage.setError("最后保养里程不能为空");
			return false;
		}
		if("".equals(buyTime.getText().toString().trim())){
			buyTime.setError("购车时间不能为空");
			return false;
		}
		
		if("".equals(lastMaintainTime.getText().toString().trim())){
			lastMaintainTime.setError("最后保养时间不能为空");
			return false;
		}
		return true;
	}
	
	
	public void getDateView(final TextView textView){
		textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
		        public void onFocusChange(View v, boolean hasFocus) {  
		            if(hasFocus){  
		                Calendar c = Calendar.getInstance();  
		                new DatePickerDialog(NewVehicleActivity.this, new DatePickerDialog.OnDateSetListener() {  
							public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
								String tempData = year + "/"+ (monthOfYear + 1) + "/" + dayOfMonth + " 12:3:2";
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								String data = sdf.format(new Date(tempData));
								textView.setText(data);
							}  
		                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();  
		             
		            }  
		        }  
		    });  
		textView.setOnClickListener(new View.OnClickListener() {  
				public void onClick(View v) {
					Calendar c = Calendar.getInstance();
					new DatePickerDialog(NewVehicleActivity.this,
							new DatePickerDialog.OnDateSetListener() {
								public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
									String tempData = year + "/"+ (monthOfYear + 1) + "/" + dayOfMonth + " 12:3:2";
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									String data = sdf.format(new Date(tempData));
									textView.setText(data);
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
				}
		    });
		}
	/**
	 * @param imagePath  路径
	 * @param name  文件名
	 * @return  图片对象
	 */
	public Bitmap logoImageIsExist(String imagePath,String name){
		Log.e("imagePath：" + imagePath,"name:" + name);
		File filePath = new File(imagePath);
		File imageFile = new File(imagePath + name);
		if(!filePath.exists()){
			filePath.mkdir();
		}
		if(imageFile.exists()){
			//将图片读取出来 
			vehicleLogoBitmap = BitmapFactory.decodeFile(imagePath + name);
			Log.e("本地存在图片","本地存在图片");
		}else{
			//服务器获取logo图片
			final String imageUrl = Constant.ImageUrl + name;
			new Thread(new Runnable() {
				public void run() {
					vehicleLogoBitmap = GetSystem.getBitmapFromURL(imageUrl);
				}
			}).start();
              if(vehicleLogoBitmap != null){
            	  createImage(imagePath + carBrank + ".jpg",vehicleLogoBitmap);
              }
              Log.e("服务器的图片",imageUrl);
		}
		return vehicleLogoBitmap;
	}
	
	//向SD卡中添加图片
	public void createImage(String fileName,Bitmap bitmap){
		FileOutputStream b = null;
		try {  
            b = new FileOutputStream(fileName);  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                b.flush();  
                b.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
	}
}
