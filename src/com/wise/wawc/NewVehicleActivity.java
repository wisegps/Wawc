package com.wise.wawc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.CarData;
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
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
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
import android.widget.TextView;
import android.widget.Toast;
/**
 * 添加新车辆
 * @author 王庆文
 */
public class NewVehicleActivity extends Activity implements  AbstractSpinerAdapter.IOnItemSelectListener {
	
	private Button cancleAdd = null;   //取消新车辆的添加
	private Button saveAdd = null;     //保存添加
	private ImageView choiceBrank = null;    //选择品牌
	public static final int newVehicleBrank = 4;
	public static final int newVehicleInsurance = 5;
	public static final int newVehicleMaintain = 7;
	private static final int addCar = 6;
	
	private TextView vehicleBrank = null;  //选择车辆品牌
	private ImageView choiceInsurance = null;  
	private ImageView ivMaintain = null;
	private TextView showMaintain = null;
	
	private TextView TvVehicleSeries = null;
	private TextView TvVehicleType = null;
	private ImageView IvVehicleSeries = null;  
	private ImageView IvVehicleType = null;
	
	
	private TextView maintainShop = null;
	private EditText carNumber = null;
	private EditText engineNumber = null;
	private EditText CJNumber = null;
	private EditText insuranceTime = null;
	private EditText lastMaintainTime = null;
	private EditText nextMaintainMileage = null;
	private EditText lastMileage = null;
	private EditText buyTime = null;
	private EditText annualSurveyTime = null;
	
	private DatePickerDialog mDateDialog = null;
	private TextView mBeginDateTv,mEndDateTv;
	private int mThisDatePicker;
	
	
	private String carBrankId = "";
	private String carSeriesId = "";
	private static String carSeriesTitle = "carSeries";
	private static String carTypeTitle = "carType";
	private static final int getCarSeries = 1;
	private static final int refreshCarSeries = 2;
	private static final int getCarType = 7;
	private List<String> carSeriesNameList = new ArrayList<String>();
	private List<String> carSeriesIdList = new ArrayList<String>();
	private SpinerPopWindow mSpinerPopWindow;
	private int width = 0 ;
	
	private ProgressDialog myDialog = null;
	private MyHandler myHandler = null;
	private DBHelper dBhalper = null;
	private DBExcute dBExcute = null;
	
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
		
		TvVehicleSeries = (TextView) findViewById(R.id.tv_new_vericher_series);
		TvVehicleType = (TextView) findViewById(R.id.tv_new_vericher_type);
		IvVehicleSeries = (ImageView) findViewById(R.id.iv_new_vericher_series);
		IvVehicleType = (ImageView) findViewById(R.id.iv_new_vericher_type);
		
		carNumber = (EditText) findViewById(R.id.et_new_vercher_number);
		engineNumber = (EditText) findViewById(R.id.et_new_vehicle_engine_number);
		CJNumber = (EditText) findViewById(R.id.et_new_car_number);
		insuranceTime = (EditText) findViewById(R.id.et_insurance_over_time);
		getDateView(insuranceTime);
		lastMaintainTime = (EditText) findViewById(R.id.et_last_maintain_time);
		getDateView(lastMaintainTime);
		lastMileage = (EditText) findViewById(R.id.et_last_insurance_mileage);
		buyTime = (EditText) findViewById(R.id.et_new_vehicle_buy_car_time);
		nextMaintainMileage = (EditText) findViewById(R.id.et_next_maintain_mileage);
		annualSurveyTime = (EditText) findViewById(R.id.annual_survey_time);
		getDateView(annualSurveyTime);
		getDateView(annualSurveyTime);
		
		
		myHandler = new MyHandler();
		dBhalper = new DBHelper(NewVehicleActivity.this);
		dBExcute = new DBExcute();
		mSpinerPopWindow = new SpinerPopWindow(NewVehicleActivity.this);
		mSpinerPopWindow.setItemListener(this);
		width = getWindowManager().getDefaultDisplay().getWidth();
		
		ivMaintain.setOnClickListener(new CilckListener());
		saveAdd.setOnClickListener(new CilckListener());
		choiceBrank.setOnClickListener(new CilckListener());
		cancleAdd.setOnClickListener(new CilckListener());
		choiceInsurance.setOnClickListener(new CilckListener());
		IvVehicleSeries.setOnClickListener(new CilckListener());
		IvVehicleType.setOnClickListener(new CilckListener());
		
	}
	
	class CilckListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.add_vechile_cancle:
				NewVehicleActivity.this.finish();
				break;
			case R.id.add_vechile_save:
				getVehicleData();
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
				
			case R.id.iv_new_vericher_series: //选择车型  
				myDialog = ProgressDialog.show(NewVehicleActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
				myDialog.setCancelable(true);
				if("".equals(carBrankId)){
					Toast.makeText(getApplicationContext(), "请选择品牌", 0).show();
					myDialog.dismiss();
					return;
				}
				getCarDatas(carSeriesTitle,"base/car_series?pid=",getCarSeries,carBrankId);
				break;
				
			case R.id.iv_new_vericher_type: //选择车款  
				if(carSeriesNameList.size() > 0){
					mSpinerPopWindow.setWidth(width);
					mSpinerPopWindow.setHeight(300);
					mSpinerPopWindow.showAsDropDown(TvVehicleType);
				}else{
					Toast.makeText(getApplicationContext(), "请选择车型", 0).show();
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
			String brank = (String)data.getSerializableExtra("brank");
			vehicleBrank.setText(brank); 
			carBrankId = (String) data.getSerializableExtra("carId");
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
        params.add(new BasicNameValuePair("obj_name", carNumber.getText().toString().trim()));
        params.add(new BasicNameValuePair("car_brand", vehicleBrank.getText().toString()));
        params.add(new BasicNameValuePair("car_series", TvVehicleSeries.getText().toString()));
        params.add(new BasicNameValuePair("car_type", TvVehicleType.getText().toString()));
        params.add(new BasicNameValuePair("engine_no", engineNumber.getText().toString().trim()));
        params.add(new BasicNameValuePair("frame_no", CJNumber.getText().toString().trim()));
        params.add(new BasicNameValuePair("insurance_company", showInsurance.getText().toString()));
        params.add(new BasicNameValuePair("insurance_date", insuranceTime.getText().toString()));
        params.add(new BasicNameValuePair("annual_inspect_date", annualSurveyTime.getText().toString()));
        params.add(new BasicNameValuePair("maintain_company", showMaintain.getText().toString()));
        params.add(new BasicNameValuePair("maintain_last_mileage", lastMileage.getText().toString().trim()));
        params.add(new BasicNameValuePair("maintain_last_date", lastMaintainTime.getText().toString()));
        params.add(new BasicNameValuePair("maintain_next_mileage",nextMaintainMileage.getText().toString().trim()));
        params.add(new BasicNameValuePair("buy_date", buyTime.getText().toString()));
        
        
//        Log.e("车牌号",carNumber.getText().toString());
//		vehicleBrank  品牌
//		TvVehicleSeries.getText();  车型
//		TvVehicleType.getText();   车款
//        Log.e("发动机型号",engineNumber.getText().toString());
//        Log.e("车架号",CJNumber.getText().toString());
//		showInsurance  保险公司
//		showMaintain   保养店
//        Log.e("保险到期时间",insuranceTime.getText().toString());
//        Log.e("年检时间",annualSurveyTime.getText().toString());
//        Log.e("最后保养里程",lastMileage.getText().toString());
//        Log.e("最后保养时间",lastMaintainTime.getText().toString());
//        Log.e("下次保养时间",nextMaintainMileage.getText().toString());
//        Log.e("购车时间",buyTime.getText().toString());
        Log.e("车牌号",carNumber.getText().toString());
        Log.e("发动机型号",engineNumber.getText().toString());
        Log.e("车架号",CJNumber.getText().toString());
        Log.e("保险到期时间",insuranceTime.getText().toString());
        Log.e("年检时间",annualSurveyTime.getText().toString());
        Log.e("最后保养里程",lastMileage.getText().toString());
        Log.e("最后保养时间",lastMaintainTime.getText().toString());
        Log.e("下次保养时间",nextMaintainMileage.getText().toString());
        Log.e("购车时间",buyTime.getText().toString());
        myDialog = ProgressDialog.show(NewVehicleActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
        new Thread(new NetThread.postDataThread(myHandler, url, params, addCar)).start();
	}
	class MyHandler extends Handler {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			
			case getCarSeries:
				myDialog.dismiss();
				String carSeries = msg.obj.toString();
				//存到数据库 
				ContentValues values = new ContentValues();
				values.put("Title", carSeriesTitle + carBrankId);
				values.put("Content", carSeries);
				dBExcute.InsertDB(NewVehicleActivity.this, values, Constant.TB_Base);
				JSONArray jsonArray = null;
				try {
					jsonArray = new JSONArray(carSeries);
					parseJSONToList(jsonArray,msg.what);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case refreshCarSeries:
				break;
				//获取车款
			case getCarType:
				myDialog.dismiss();
				String carType = msg.obj.toString();
				ContentValues carTypeValues = new ContentValues();
				carTypeValues.put("Title", carTypeTitle + carSeriesId);
				carTypeValues.put("Content", carType);
				dBExcute.InsertDB(NewVehicleActivity.this, carTypeValues, Constant.TB_Base);
				JSONArray typeJsonArray = null;
				try {
					typeJsonArray = new JSONArray(carType);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				parseJSONToList(typeJsonArray,msg.what);
				break;
				
			case addCar:
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
//					  Log.e("车牌号",carNumber.getText().toString());
//						vehicleBrank  品牌
//						TvVehicleSeries.getText();  车型
//						TvVehicleType.getText();   车款
//				        Log.e("发动机型号",engineNumber.getText().toString());
//				        Log.e("车架号",CJNumber.getText().toString());
//						showInsurance  保险公司
//						showMaintain   保养店
//				        Log.e("保险到期时间",insuranceTime.getText().toString());
//				        Log.e("年检时间",annualSurveyTime.getText().toString());
//				        Log.e("最后保养里程",lastMileage.getText().toString());
//				        Log.e("最后保养时间",lastMaintainTime.getText().toString());
//				        Log.e("下次保养时间",nextMaintainMileage.getText().toString());
//				        Log.e("购车时间",buyTime.getText().toString());
					//添加到数据库
					ContentValues value = new ContentValues();
					value.put("obj_id", obj_id);
					value.put("obj_name", carNumber.getText().toString().trim());
					value.put("car_brand", vehicleBrank.getText().toString());
					value.put("car_series", TvVehicleSeries.getText().toString());
					value.put("car_type", TvVehicleType.getText().toString());
					value.put("engine_no", engineNumber.getText().toString().trim());
					value.put("frame_no", CJNumber.getText().toString().trim());
					value.put("insurance_company", showInsurance.getText().toString());
					value.put("insurance_date", insuranceTime.getText().toString());
					value.put("annual_inspect_date", annualSurveyTime.getText().toString());
					value.put("maintain_company", showMaintain.getText().toString());
					value.put("maintain_last_mileage", lastMileage.getText().toString().trim());
					value.put("maintain_next_mileage", nextMaintainMileage.getText().toString().trim());
					value.put("buy_date", buyTime.getText().toString());
					dBExcute.InsertDB(NewVehicleActivity.this, value, Constant.TB_Vehicle);
					
				    CarData carData = new CarData();
	                carData.setCarLogo(1);
	                carData.setCheck(false);
	                carData.setObj_id(Integer.parseInt(obj_id));
	                carData.setObj_name(carNumber.getText().toString().trim());
	                carData.setCar_brand(vehicleBrank.getText().toString());
	                carData.setCar_series(TvVehicleSeries.getText().toString());
//	                carData.setCar_type(car_type);
//	                carData.setEngine_no(engine_no);
//	                carData.setFrame_no(frame_no);
//	                carData.setInsurance_company(insurance_company);
//	                carData.setInsurance_date(insurance_date);
//	                carData.setAnnual_inspect_date(annual_inspect_date);
//	                carData.setMaintain_company(maintain_company);
//	                carData.setMaintain_last_mileage(maintain_last_mileage);
//	                carData.setMaintain_next_mileage(maintain_next_mileage);
//	                carData.setBuy_date(buy_date);
					startActivity(new Intent(NewVehicleActivity.this,MyVehicleActivity.class));
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
			parseJSONToList(jsonArray,code);
		}else{
			//请求服务器
			Log.e("请求服务器","请求服务器");
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + url + id, code)).start();
		}
	}
	private void parseJSONToList(JSONArray jsonArray,int codeType) {
		if(codeType == getCarSeries){
			Log.e("获取车型----","获取车型");
			int jsonLength = jsonArray.length();
			if(carSeriesNameList.size() > 0){
				carSeriesNameList.clear();
			}
			if(carSeriesIdList.size() > 0){
				carSeriesIdList.clear();
			}
			for(int i = 0 ; i < jsonLength ; i ++){
				try {
					carSeriesNameList.add(jsonArray.getJSONObject(i).getString("show_name"));
					carSeriesIdList.add(jsonArray.getJSONObject(i).getString("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			mSpinerPopWindow.refreshData(carSeriesNameList, 0);
			mSpinerPopWindow.setWidth(width);
			mSpinerPopWindow.setHeight(300);
			mSpinerPopWindow.showAsDropDown(TvVehicleSeries);
			mSpinerPopWindow.setType(codeType);
		}else if(codeType == getCarType){
			Log.e("获取车款----","获取车款");
			int jsonLength = jsonArray.length();
			//解析车款数据
			if(carSeriesNameList.size() > 0){
				carSeriesNameList.clear();
			}
			for(int i = 0 ; i < jsonLength ; i ++){
				try {
					carSeriesNameList.add(jsonArray.getJSONObject(i).getString("name"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			mSpinerPopWindow.refreshData(carSeriesNameList, 0);
			mSpinerPopWindow.setWidth(width);
			mSpinerPopWindow.setHeight(300);
			mSpinerPopWindow.setType(codeType);
			mSpinerPopWindow.showAsDropDown(TvVehicleType);
		}
		
	}
	@Override
	public void onItemClick(int pos, int type) {
		if(type == getCarSeries){
			String value = "";
			if (pos >= 0 && pos <= carSeriesNameList.size()){
				value = carSeriesNameList.get(pos);
				TvVehicleSeries.setText(value);
			}
			
			for(int i = 0 ; i < carSeriesNameList.size() ; i ++){
				if(carSeriesNameList.get(i).equals(value)){
					carSeriesId = carSeriesIdList.get(i);
				}
			}
			myDialog = ProgressDialog.show(NewVehicleActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
			
			getCarDatas(carTypeTitle,"base/car_type?pid=",getCarType,carSeriesId);
			Log.e("点击车型所得","点击车型所得");
		}else if(type == getCarType){
			Log.e("点击车款所得","点击车款所得");
			String value = "";
			if (pos >= 0 && pos <= carSeriesNameList.size()){
				value = carSeriesNameList.get(pos);
				TvVehicleType.setText(value);
			}
		}
	}
	
	//获取用户输入的数据
	private void getVehicleData() {
		if("".equals(carNumber.getText().toString().trim())){
			carNumber.setError("车牌号不能为空");
			return;
		}
		if("".equals(engineNumber.getText().toString().trim())){
			engineNumber.setError("引擎号不能为空");
			return;
		}
		if("".equals(CJNumber.getText().toString().trim())){
			CJNumber.setError("车架号不能为空");
			return;
		}
		if("".equals(lastMileage.getText().toString().trim())){
			lastMileage.setError("最后保养里程不能为空");
			return;
		}
		if("".equals(buyTime.getText().toString().trim())){
			buyTime.setError("购车时间不能为空");
			return;
		}
		
		if("".equals(lastMaintainTime.getText().toString().trim())){
			lastMaintainTime.setError("最后保养时间不能为空");
			return;
		}
		if("".equals(nextMaintainMileage.getText().toString().trim())){
			nextMaintainMileage.setError("下次保养不能为空");
			return;
		}
		if("".equals(annualSurveyTime.getText().toString().trim())){
			annualSurveyTime.setError("年检时间不能为空");
			return;
		}
		if("".equals(annualSurveyTime.getText().toString().trim())){
			annualSurveyTime.setError("年检时间不能为空");
			return;
		}
		if("".equals(insuranceTime.getText().toString().trim())){
			insuranceTime.setError("保险到期时间不能为空");
			return;
		}
		addCar();
	}
	
	
	public void getDateView(final EditText editText){
			editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
		        public void onFocusChange(View v, boolean hasFocus) {  
		            if(hasFocus){  
		                Calendar c = Calendar.getInstance();  
		                new DatePickerDialog(NewVehicleActivity.this, new DatePickerDialog.OnDateSetListener() {  
							public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
								editText.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
							}  
		                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();  
		             
		            }  
		        }  
		    });  
			editText.setOnClickListener(new View.OnClickListener() {  
				public void onClick(View v) {
					Calendar c = Calendar.getInstance();
					new DatePickerDialog(NewVehicleActivity.this,
							new DatePickerDialog.OnDateSetListener() {
								public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
									editText.setText(year + "/"+ (monthOfYear + 1) + "/" + dayOfMonth);
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
				}
		    });
		}
}
