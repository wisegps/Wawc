package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import org.json.JSONArray;
import org.json.JSONException;

import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.SpinerPopWindow;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import com.wise.wawc.MyVehicleActivity.ClickListener;

import android.app.Activity;
import android.app.AlertDialog;
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
	
	
	private EditText carNumber = null;
	private EditText engineNumber = null;
	private EditText CJNumber = null;
	private TextView insuranceTime = null;
	private TextView maintainShop = null;
	private TextView currentMileage = null;
	private EditText lastMileage = null;
	private EditText buyTime = null;
	
	
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
		insuranceTime = (TextView) findViewById(R.id.et_new_vercher_insurance_time);
		currentMileage = (TextView) findViewById(R.id.add_vehicle_mileage);
		lastMileage = (EditText) findViewById(R.id.new_vercher_last_insurance_mileage);
		buyTime = (EditText) findViewById(R.id.et_new_vehicle_buy_car_time);
		
		
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
			    addCar();
				//Toast.makeText(getApplicationContext(), "添加新车辆成功", 0).show();
				Toast.makeText(getApplicationContext(), "添加新车辆成功", 0).show();
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
        params.add(new BasicNameValuePair("obj_name", "粤B54321"));
        params.add(new BasicNameValuePair("car_brand", "奥迪"));
        params.add(new BasicNameValuePair("car_series", "A8"));
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
			lastMileage.setError("保养里程不能为空");
			return;
		}
		if("".equals(buyTime.getText().toString().trim())){
			buyTime.setError("购车时间不能为空");
			return;
		}
		
		//添加车辆
//		NameValuePair nameValuePair = new NameValuePair();
//		new Thread(new NetThread.postDataThread(myHandler, Constant.BaseUrl + "", params, addCar)).start();
	}
}
