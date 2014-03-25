package com.wise.wawc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.CarData;
import com.wise.data.CharacterParser;
import com.wise.data.IllegalCity;
import com.wise.data.ProvinceModel;
import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.CarAdapter;
import com.wise.extend.SpinerPopWindow;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
/**
 * 我的爱车
 * @author 王庆文
 */
public class MyVehicleActivity extends Activity{
	private ImageView menu = null;
	private TextView editVehicle = null;
	private TableRow brand = null;
	private TableRow device = null;
	private TableRow insuranceCompany = null;
	private TableRow selectCity = null;
	private TableRow engineNumLayout = null;
	private TableRow vehicleNumLayout = null;
	private TableRow registerNumLayout = null;
	private TextView showInsuranceCompany;   //显示保险公司
	public static final int resultCodeInsurance = 2;   //选择保险公司的识别码
	public static final int resultCodeBrank = 3;       //选择汽车品牌的识别码
	public static final int resultCodeSeries = 12;       //选择汽车品牌的识别码
	public static final int resultCodeType = 14;       //选择汽车品牌的识别码
	
	public static final int resultCodeMaintain = 6;       
	public static final int resultCodeDevice = 7;       
	public static final int showCarData = 8;       //显示汽车数据
	public static final int deleteCarData = 10;       //删除汽车数据
	private static final int setCarLogo = 11;      // 动态设置汽车Logo
	public static final int getCityViolateRegulationsCode = 41;      // 获取违章城市代码
	private static final int getBrankData = 39;
	private static final int getSeriesData = 37;
	private static final int getIllegalforUrlCode = 42;      // 获取违章城市代码
	private static final int bindDeviceId = 13;
	public static final int myVehiclePetrol = 15;
	private static final int addCarResultCode = 16;
	
	HorizontalScrollView horizontalscrollview;
	View v_divider;
	private EditText etDialogMileage = null;   //输入里程
	private TableRow choiceMaintian = null;
	private TableRow petrolGradeTr = null;
	
	private TextView myVehicleBrank = null;
	private TextView tvMaintain = null;
	private EditText vehicleNumber = null;
	private EditText engineNum = null;
	private EditText frameNum = null;
	private EditText lastMaintain = null;
	private EditText vehicleRegNum = null;
	private TextView lastMaintainTime = null;
	private TextView buyTime = null;
	private TextView ivInsuranceDate = null;
	private LinearLayout buttomView = null;
	
	private GridView vehicleGridView = null;
	private CarAdapter carAdapter = null;
	AlertDialog dlg = null;
	boolean isJump = false; //false从菜单页跳转过来返回打开菜单，true从首页跳转返回关闭页面
	private boolean buttomViewIsShow = false;
	
	private int width = 0 ; 
	
	private TableRow ivCarSeries = null; //车型
	private TableRow ivCarType = null;  //车款
	private TextView tvCarSeries = null;
	private TextView tvCarType = null;
	private TextView btSaveVehicleData = null;
	private TextView btDeleteVehicle = null;
	private TextView selectCityTv = null;
	private TextView petrolGradeTv = null;
	private EditText insuranceTel = null;
	private EditText maintainTelEd = null;
	
	private MyHandler myHandler = null;
	private DBHelper dBhalper = null;
	
	private ProgressDialog myDialog = null;
	
	private DBExcute dBExcute = null;
	
	private List<String> carSeriesNameList = new ArrayList<String>();
	private List<String> carSeriesIdList = new ArrayList<String>();
	
	//保险时间段
	String time1 = "";
	String time2 = "";
	private String carBrankId = "";
	private String carSeriesId = "";
	private String carTypeId = "";
	private static String carSeriesTitle = "carSeries";
	private static String carTypeTitle = "carType";
	private String carBrank = "";
	private String carSeries = "";
	private String carType = "";
	private CarData oneCarData = null;  //存储修改的车辆数据
//	private String vehicleId = "";     //存储车牌号   用户更改数据库
	private String vehicleNum = "";
	
	private int chickIndex = 0;
	private CarData newCarImage = null;
	private Bitmap imageBitmap = null;
	
	private SharedPreferences preferences = null;
	private String city_code = null;
	
	//违章城市相关
	private int engine = 0;
	private int car = 0;
	private int register = 0; 
	
	private int engineNo = 0;
	private int carNo = 0;
	private int registerNo = 0;
	private IllegalCity illegalCity;
	
	static CharacterParser characterParser;    //将汉字转为拼音
	static PinyinComparator comparator;         //排序
	static List<ProvinceModel> illegalList;
	private TextView myVehicleDevice = null;
	private String deviceId = null;
	private String deviceName = null;
	public static String illegalCityStr = "";
	public static boolean hasSelectIllegalCity = false;
	private String vehNum = "";   //临时存储车牌号
	
	private String petrolResult = "";
	
	private MyBroadCastReceiver myBroadCastReceiver = null;
	private IntentFilter intentFilter = null;
	
	ChoiceCarInformationActivity choiceCarInformationActivity = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_vehicle);
		horizontalscrollview = (HorizontalScrollView)findViewById(R.id.horizontalscrollview);
		v_divider = (View)findViewById(R.id.v_divider);
		menu = (ImageView)findViewById(R.id.my_vechile_menu);
		editVehicle = (TextView) findViewById(R.id.my_vechile_edit);
		brand = (TableRow) findViewById(R.id.iv_my_vehicle_brank);
		device = (TableRow) findViewById(R.id.vehicle_device_layout);
		selectCity = (TableRow) findViewById(R.id.select_city_layout);
		insuranceCompany = (TableRow)findViewById(R.id.insurance_company_layout);
		showInsuranceCompany = (TextView) findViewById(R.id.show_insurance_company);
		myVehicleBrank = (TextView) findViewById(R.id.tv_my_vehicle_beank);
		choiceMaintian = (TableRow) findViewById(R.id.choice_maintain_image_layout);
		tvMaintain = (TextView) findViewById(R.id.show_maintain);
		vehicleNumber = (EditText)findViewById(R.id.my_vehicle_ed_vehicle_number);
		engineNum = (EditText)findViewById(R.id.my_vehicle_ed_engine_num);
		frameNum = (EditText)findViewById(R.id.my_vehilce_ed_fram_num);
		vehicleRegNum = (EditText) findViewById(R.id.my_vehilce_reg_num);
		lastMaintain = (EditText)findViewById(R.id.my_vehicle_ed_last_maintain);
		lastMaintainTime = (TextView) findViewById(R.id.my_vehicle_last_maintain_time);
		petrolGradeTr = (TableRow) findViewById(R.id.my_vehicle_petrol_grade_tr);
		insuranceTel = (EditText) findViewById(R.id.my_vehicle_insurance_company_tel);
		maintainTelEd = (EditText) findViewById(R.id.my_vehicle_maintain_shop_tel);
		getDateView(lastMaintainTime);
		buyTime = (TextView)findViewById(R.id.my_vehicle_ed_buy_time);
		selectCityTv = (TextView) findViewById(R.id.my_vehicle_select_city);
		engineNumLayout = (TableRow) findViewById(R.id.my_vehicle_engine_num_layout);
		vehicleNumLayout = (TableRow) findViewById(R.id.my_vehicle_num_layout);
		registerNumLayout = (TableRow) findViewById(R.id.my_vehicle_register_num_layout);
		myVehicleDevice = (TextView) findViewById(R.id.my_vehicle_device);
		petrolGradeTv = (TextView) findViewById(R.id.my_vehicle_petrol_grade);
		getDateView(buyTime);
		ivInsuranceDate = (TextView) findViewById(R.id.my_vehicle_tv_insurance);
		getDateView(ivInsuranceDate);
		btSaveVehicleData = (TextView) findViewById(R.id.new_vehilce_tv);
		btDeleteVehicle = (TextView) findViewById(R.id.my_vehilce_delete);
		buttomView = (LinearLayout) findViewById(R.id.my_vehicle_buttom_view);
		vehicleGridView = (GridView) findViewById(R.id.gv_my_vehicle);
		
		
		ivCarSeries = (TableRow) findViewById(R.id.car_series_layout);
		ivCarType = (TableRow) findViewById(R.id.car_type_layout);
		tvCarSeries = (TextView) findViewById(R.id.tv_car_series);
		tvCarType = (TextView) findViewById(R.id.tv_car_type);
		ivCarSeries.setOnClickListener(new ClickListener());
		ivCarType.setOnClickListener(new ClickListener());
		petrolGradeTr.setOnClickListener(new ClickListener());
		selectCity.setOnClickListener(new ClickListener());
		dBExcute = new DBExcute();
		choiceCarInformationActivity = new ChoiceCarInformationActivity();
		
		preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		chickIndex = preferences.getInt(Constant.DefaultVehicleID, 0);
		characterParser = new CharacterParser().getInstance();
		comparator = new PinyinComparator();
		myHandler = new MyHandler();	
		
		String jsonData = dBExcute.selectIllegal(MyVehicleActivity.this);
		if(jsonData == null){
		    myDialog = ProgressDialog.show(MyVehicleActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
	        myDialog.setCancelable(true);
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl+"violation/city?cuth_code=" + Variable.auth_code, getIllegalforUrlCode)).start();
		}else{
			//解析数据  并且更新
			illegalList = parseJson(jsonData);
			Variable.illegalProvinceList = illegalList;
		}

        
        Intent intent = getIntent();
        isJump = intent.getBooleanExtra("isJump", false);
        if(isJump){
            menu.setImageResource(R.drawable.nav_back);
        }else{
            menu.setImageResource(R.drawable.side_left);
        }
        
        //  注册广播
        
        myBroadCastReceiver = new MyBroadCastReceiver();
        intentFilter = new IntentFilter(Constant.updataMyVehicleLogoAction);
        MyVehicleActivity.this.registerReceiver(myBroadCastReceiver, intentFilter);
	}
	protected void onResume() {
		super.onResume();
		dBhalper = new DBHelper(MyVehicleActivity.this);
		dBExcute = new DBExcute();
		if(Variable.carDatas.size() == 1){
		    horizontalscrollview.setVisibility(View.GONE);
		    v_divider.setVisibility(View.GONE);
		}else{
		    horizontalscrollview.setVisibility(View.VISIBLE);
		    v_divider.setVisibility(View.VISIBLE);
		}
		//车辆数据
		int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constant.ImageWidth, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams((Variable.carDatas.size() * (px + 10) + 10),LayoutParams.WRAP_CONTENT);
		//汽车品牌Logo
		carAdapter = new CarAdapter(MyVehicleActivity.this,Variable.carDatas);
		vehicleGridView.setAdapter(carAdapter);
		vehicleGridView.setLayoutParams(params);
		vehicleGridView.setColumnWidth(px);
		vehicleGridView.setHorizontalSpacing(10);
		vehicleGridView.setStretchMode(GridView.NO_STRETCH);
		vehicleGridView.setNumColumns(Variable.carDatas.size());
		vehicleGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
					carBrankId = "";
					Log.d(TAG, "onItemClick carBrankId = " + carBrankId);
					carSeriesId = "";
					carBrank = "";
					carSeries = "";
					carType = "";
					oneCarData = Variable.carDatas.get(arg2);
					Message msg = new Message();
					msg.obj = Variable.carDatas.get(arg2);
					msg.what = showCarData;
					myHandler.sendMessage(msg);
					for(int i = 0 ; i < Variable.carDatas.size() ; i++){
						Variable.carDatas.get(i).setCheck(false);
					}
					Variable.carDatas.get(arg2).setCheck(true);
					carAdapter.notifyDataSetChanged();
					chickIndex = arg2;
					hasSelectIllegalCity = false;
			}
		});
		//设置默认选择第一辆汽车
		for(int i = 0 ; i < Variable.carDatas.size() ; i++){
			Variable.carDatas.get(i).setCheck(false);
		}
		Variable.carDatas.get(chickIndex).setCheck(true);
		carAdapter.notifyDataSetChanged();
		
		btSaveVehicleData.setOnClickListener(new ClickListener());
		btDeleteVehicle.setOnClickListener(new ClickListener());
		choiceMaintian.setOnClickListener(new ClickListener());
		device.setOnClickListener(new ClickListener());
		menu.setOnClickListener(new ClickListener());
		editVehicle.setOnClickListener(new ClickListener());
		brand.setOnClickListener(new ClickListener());
		insuranceCompany.setOnClickListener(new ClickListener());

		width = getWindowManager().getDefaultDisplay().getWidth();
		final Message msg = new Message();
		msg.obj = Variable.carDatas.get(chickIndex);
		oneCarData = Variable.carDatas.get(chickIndex);
		msg.what = showCarData;
		new Thread(new Runnable() {
			public void run() {
				while(illegalList == null){
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				myHandler.sendMessage(msg);
			}
		}).start();
	}
	//点击监听
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.my_vechile_menu:
				if(CheckDatas()){
					commitData();
					
					if(isJump){
							finish();
					}else{
						ActivityFactory.A.LeftMenu();
					}
				}
				break;
			case R.id.iv_my_vehicle_brank:    //选择汽车品牌
				Intent intent = new Intent(MyVehicleActivity.this,ChoiceCarInformationActivity.class);
				intent.putExtra("code", resultCodeBrank);
				startActivityForResult(intent, resultCodeBrank);
				break;
			case R.id.car_series_layout:    //选择车型
			    Log.d(TAG, "car_series_layout carBrankId = " + carBrankId);
				if("".equals(carBrankId)){
					Toast.makeText(MyVehicleActivity.this, "请选择车牌", 0).show();
					return;
				}else{
					Intent intent3 = new Intent(MyVehicleActivity.this,ChoiceCarInformationActivity.class);
					intent3.putExtra("code", resultCodeSeries);
					intent3.putExtra("brankId", carBrankId);
					Log.d(TAG, "carBrankId = " + carBrankId);
					intent3.putExtra("carBrank", carBrank);
					startActivityForResult(intent3, resultCodeSeries);
				}
				break;
			case R.id.car_type_layout:    //选择车款
				if("".equals(carSeriesId)){
					Toast.makeText(MyVehicleActivity.this, "请选择车型", 0).show();
					return;
				}else{
					Intent intent6 = new Intent(MyVehicleActivity.this,ChoiceCarInformationActivity.class);
					intent6.putExtra("code", resultCodeType);
					intent6.putExtra("brankId", carBrankId);
					Log.d(TAG, "carBrankId = " + carBrankId);
					intent6.putExtra("carBrank", carBrank);
					intent6.putExtra("seriesId", carSeriesId);
					intent6.putExtra("series", carSeries);
					startActivityForResult(intent6, resultCodeType);
				}
				break;
			case R.id.vehicle_device_layout:    //我的终端
			    Toast.makeText(MyVehicleActivity.this, R.string.new_version, Toast.LENGTH_SHORT).show();
//			    if(Variable.devicesDatas.size() == 0){
//			        //跳转到购买终端界面
//			        startActivity(new Intent(MyVehicleActivity.this, OrderDeviceActivity.class));
//			    }else{
//			      //跳转到绑定终端界面
//    				Intent intent2 = new Intent(MyVehicleActivity.this,DevicesActivity.class);
//                    intent2.putExtra("isJump", false);
//                    startActivityForResult(intent2, resultCodeDevice);
//			    }
				break;
			case R.id.insurance_company_layout:  //选择保险公司
				Intent intent1 = new Intent(MyVehicleActivity.this,InsuranceActivity.class);
				intent1.putExtra("code", resultCodeInsurance);
				startActivityForResult(intent1, resultCodeInsurance);
				break;
			case R.id.choice_maintain_image_layout:  //选择保养店
				SharedPreferences shareFile = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
				if("".equals(shareFile.getString(Constant.LocationCity, ""))){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_choice_city), 0).show();
				}else if("".equals(carBrank)){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_choice_vehicle_brank), 0).show();
				}else{
					Intent intent3 = new Intent(MyVehicleActivity.this,MaintainShopActivity.class);
					intent3.putExtra("code", resultCodeMaintain);
					intent3.putExtra("brank", carBrank);
					intent3.putExtra("city", shareFile.getString(Constant.LocationCity, ""));
					startActivityForResult(intent3, resultCodeMaintain);
				}
				break;
			case R.id.new_vehilce_tv:
				Intent intents = new Intent(MyVehicleActivity.this,NewVehicleActivity.class);
				intents.putExtra("code", addCarResultCode);
				startActivityForResult(intents, addCarResultCode);
				break;
			case R.id.my_vehilce_delete:
				new AlertDialog.Builder(MyVehicleActivity.this).setTitle(getString(R.string.point)).setMessage(getString(R.string.sure_delete_vehicle) + vehicleNum + "?").setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myDialog = ProgressDialog.show(MyVehicleActivity.this, "提示", "正在删除...");
						myDialog.setCancelable(true);
						new Thread(new NetThread.DeleteThread(myHandler, Constant.BaseUrl + "vehicle/" + Variable.carDatas.get(chickIndex).getObj_id() + "?auth_code=" + Variable.auth_code, deleteCarData)).start();
					}
				}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
				break;
			case R.id.my_vechile_edit:
				if(!buttomViewIsShow){
					buttomView.setVisibility(View.VISIBLE);
					buttomViewIsShow = true;
				}else if(buttomViewIsShow){
					buttomView.setVisibility(View.GONE);
					buttomViewIsShow = false;
				}
				break;
			case R.id.select_city_layout:
				hasSelectIllegalCity = true;
				illegalCityStr = "";
				Intent intent6 = new Intent(MyVehicleActivity.this,IllegalCitiyActivity.class);
				intent6.putExtra("requestCode", getCityViolateRegulationsCode);
				startActivityForResult(intent6, getCityViolateRegulationsCode);
				break;
			case R.id.my_vehicle_petrol_grade_tr:
				Intent intent9 = new Intent(MyVehicleActivity.this,PetrolGradeActivity.class);
				intent9.putExtra("code", myVehiclePetrol);
				startActivityForResult(intent9, myVehiclePetrol);
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
			String insurance = (String) data.getSerializableExtra("insurance_name");
			String insurance_phone = (String) data.getSerializableExtra("insurance_phone");
			showInsuranceCompany.setText(data.getStringExtra("insurance_name"));
			insuranceTel.setText(insurance_phone);
			//更改静态类
			Variable.carDatas.get(chickIndex).setInsurance_tel(insurance_phone);
			Variable.carDatas.get(chickIndex).setInsurance_company(insurance);
		}
		//选择品牌   
		if(resultCode == this.resultCodeBrank){
			carBrank = data.getStringExtra("brank");
			carBrankId = data.getStringExtra("brankId");
			carSeries = data.getStringExtra("series");
			carSeriesId = data.getStringExtra("seriesId");
			carType = data.getStringExtra("type");
			carTypeId = data.getStringExtra("typeId");
			//更改静态类
			Variable.carDatas.get(chickIndex).setCar_brand(carBrank);
			Variable.carDatas.get(chickIndex).setCar_series(carSeries);
			Variable.carDatas.get(chickIndex).setCar_type(carType);
			Variable.carDatas.get(chickIndex).setCar_brand_id(carBrankId);
			Variable.carDatas.get(chickIndex).setCar_series_id(carSeriesId);
			Variable.carDatas.get(chickIndex).setCar_type_id(carTypeId);
		}
		//点击车型返回
		if(resultCode == this.resultCodeSeries){
			carBrank = data.getStringExtra("brank");
			carBrankId = data.getStringExtra("brankId");
			Log.d(TAG, "车型返回 carBrankId = " + carBrankId);
			carSeries = data.getStringExtra("series");
			carSeriesId = data.getStringExtra("seriesId");
			carType = data.getStringExtra("type");
			carTypeId = data.getStringExtra("typeId");
			Variable.carDatas.get(chickIndex).setCar_brand(carBrank);
			Variable.carDatas.get(chickIndex).setCar_series(carSeries);
			Variable.carDatas.get(chickIndex).setCar_type(carType);
			Variable.carDatas.get(chickIndex).setCar_brand_id(carBrankId);
			Variable.carDatas.get(chickIndex).setCar_series_id(carSeriesId);
			Variable.carDatas.get(chickIndex).setCar_type_id(carTypeId);
		}
		
		if(resultCode == this.resultCodeType){
			carBrank = data.getStringExtra("brank");
			carBrankId = data.getStringExtra("brankId");
			Log.d(TAG, "resultCodeType carBrankId = " + carBrankId);
			carSeries = data.getStringExtra("series");
			carSeriesId = data.getStringExtra("seriesId");
			carType = data.getStringExtra("type");
			carTypeId = data.getStringExtra("typeId");
			Variable.carDatas.get(chickIndex).setCar_brand(carBrank);
			Variable.carDatas.get(chickIndex).setCar_series(carSeries);
			Variable.carDatas.get(chickIndex).setCar_type(carType);
			Variable.carDatas.get(chickIndex).setCar_brand_id(carBrankId);
			Variable.carDatas.get(chickIndex).setCar_series_id(carSeriesId);
			Variable.carDatas.get(chickIndex).setCar_type_id(carTypeId);
		}
		//选择保养店
		if(resultCode == this.resultCodeMaintain){
			String maintainName = (String) data.getSerializableExtra("maintain_name");
			String maintainTel = (String) data.getSerializableExtra("maintain_phone");
			tvMaintain.setText(maintainName);
			maintainTelEd.setText(maintainTel);
			//更改静态类
			Variable.carDatas.get(chickIndex).setMaintain_company(maintainName);
			Variable.carDatas.get(chickIndex).setMaintain_tel(maintainTel);
		}
		if(resultCode == this.getCityViolateRegulationsCode){
			
			illegalCity = (IllegalCity) data.getSerializableExtra("IllegalCity");
			if(illegalCity != null){
				
				engine = Integer.valueOf(illegalCity.getEngine());
				engineNo = Integer.valueOf(illegalCity.getEngineno());
				car = Integer.valueOf(illegalCity.getVehiclenum());
				carNo = Integer.valueOf(illegalCity.getVehiclenumno());
				register = Integer.valueOf(illegalCity.getRegist());
				registerNo = Integer.valueOf(illegalCity.getRegistno());
				
				engineNumLayout.setVisibility(View.VISIBLE);
				vehicleNumLayout.setVisibility(View.VISIBLE);
				registerNumLayout.setVisibility(View.VISIBLE);
				city_code = illegalCity.getCityCode();  //城市代码
				illegalCityStr = illegalCity.getCityName();   //显示需要的城市名字
				selectCityTv.setText(illegalCity.getCityName());
				if(Integer.valueOf(illegalCity.getEngine()) == 0){  //隐藏发动机
					engineNumLayout.setVisibility(View.GONE);
					engine = 0;
				}else if(Integer.valueOf(illegalCity.getEngine()) == 1){
					engine = 1;
					engineNo = Integer.valueOf(illegalCity.getEngineno());
					if(engineNo == 0){
						engineNum.setHint(this.getResources().getString(R.string.all_engine_num_hint));
					}else{
						engineNum.setHint(this.getResources().getString(R.string.engine_num_hint) + engineNo + this.getResources().getString(R.string.hint_wei));
						engineNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(engineNo)});
					}
				}
				if(Integer.valueOf(illegalCity.getVehiclenum()) == 0){   //隐藏车架号
					vehicleNumLayout.setVisibility(View.GONE);
					car = 0;
				}else if(Integer.valueOf(illegalCity.getVehiclenum()) == 1){
					car = 1;
					carNo = Integer.valueOf(illegalCity.getVehiclenumno());
					if(carNo == 0){
						frameNum.setHint(this.getResources().getString(R.string.all_vehicle_num_hint));
					}else{
						frameNum.setHint(this.getResources().getString(R.string.vehicle_num_hint) + carNo + this.getResources().getString(R.string.hint_wei));
						frameNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(carNo)});
					}
				} 
				if(Integer.valueOf(illegalCity.getRegist()) == 0 ){    // 隐藏车辆登记证号
					registerNumLayout.setVisibility(View.GONE);
					register = 0;
				}else if(Integer.valueOf(illegalCity.getRegist()) == 1){
					register = 1;
					registerNo = Integer.valueOf(illegalCity.getRegistno());
					if(registerNo == 0){
						vehicleRegNum.setHint(this.getResources().getString(R.string.all_register_num_hint));
					}else{
						vehicleRegNum.setHint(this.getResources().getString(R.string.register_num_hint) + registerNo + this.getResources().getString(R.string.hint_wei));
						vehicleRegNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(registerNo)});
					}
				}
				// TODO
				Variable.carDatas.get(chickIndex).setVio_location(illegalCity.getCityCode());
				illegalCity = null;
			}
		}
		if(resultCode == resultCodeDevice){
		    deviceId = data.getStringExtra("DeviceId");
		    deviceName = data.getStringExtra("Serial");
		    //更新静态类
		    Variable.carDatas.get(chickIndex).setSerial(deviceName);
		    myVehicleDevice.setText(deviceName);
		    List<NameValuePair> parms = new ArrayList<NameValuePair>();
			parms.add(new BasicNameValuePair("device_id", deviceId));
			System.out.println("deviceId = " + deviceId);
			new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "vehicle/" + Variable.carDatas.get(chickIndex).obj_id + "/device?auth_code=" + Variable.auth_code, parms, bindDeviceId)).start();
		}
		if(resultCode == myVehiclePetrol){
			petrolResult = data.getStringExtra("result");
			petrolGradeTv.setText(petrolResult);
			Variable.carDatas.get(chickIndex).setGas_no(petrolResult);
		}
		
		if(addCarResultCode == resultCode){
			carAdapter.refresh(Variable.carDatas);
		}
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case showCarData:
				CarData carData = (CarData) msg.obj;
				
				carBrank = carData.getCar_brand();
				carSeries = carData.getCar_series();
				//  匹配车辆品牌id、车型id、车款id   用于第一次点击二级项（车型）获取相关数据
//				getVehiclebrandData(ChoiceCarInformationActivity.carBrankTitle,Constant.TB_Base,Constant.BaseUrl + "base/car_brand",getBrankData);
				carBrankId = carData.getCar_brand_id();
				Log.d(TAG, "MyHandler carBrankId = " + carBrankId);
				carSeriesId = carData.getCar_series_id();
				carTypeId = carData.getCar_type_id();
				
				
				IllegalCity illegalCitys = null;
				String location = null;
				//哪些要显示  哪些不用
				if(carData.getVio_location() != null){
					if(!"".equals(carData.getVio_location())){
						for(int i = 0; i < illegalList.size() ; i++ ){
							for(int j = 0 ; j < illegalList.get(i).getIllegalCityList().size() ; j ++){
								if(carData.getVio_location().equals(illegalList.get(i).getIllegalCityList().get(j).getCityCode())){
									location = illegalList.get(i).getIllegalCityList().get(j).getCityName();   //用户显示违章城市栏
									illegalCitys = illegalList.get(i).getIllegalCityList().get(j);            //违章城市的相关属性
								}
							}
						}
					}
				}
				if(illegalCitys != null){
					engine = Integer.valueOf(illegalCitys.getEngine());
					engineNo = Integer.valueOf(illegalCitys.getEngineno());
					
					car = Integer.valueOf(illegalCitys.getVehiclenum());
					carNo = Integer.valueOf(illegalCitys.getVehiclenumno());
					
					register = Integer.valueOf(illegalCitys.getRegist());
					registerNo = Integer.valueOf(illegalCitys.getRegistno());
				}
				//判断终端是否绑定
				if(carData.getSerial() == null){
					myVehicleDevice.setText("未绑定终端");
				}else if("" == carData.getSerial()){
					myVehicleDevice.setText("未绑定终端");
				}else{
					myVehicleDevice.setText(carData.getSerial());
				}
				vehicleNum = carData.getObj_name();
				if(!"".equals(vehNum)){
					vehicleNumber.setText(vehNum);
					vehNum = "";
				}else{
					vehicleNumber.setText(carData.getObj_name());
				}
				tvCarSeries.setText(carData.getCar_series());
				tvCarType.setText(carData.getCar_type());
				myVehicleBrank.setText(carData.getCar_brand());
				carBrank = carData.getCar_brand();  //保存默认品牌  用户获取4s店数据
				engineNum.setText(carData.getEngine_no());
				frameNum.setText(carData.getFrame_no());
				showInsuranceCompany.setText(carData.getInsurance_company());
				System.out.println("carData.getInsurance_date() = " + carData.getInsurance_date());
				ivInsuranceDate.setText(carData.getInsurance_date().substring(0, 10));
				tvMaintain.setText(carData.getMaintain_company());
				lastMaintain.setText(carData.getMaintain_last_mileage());
				buyTime.setText(carData.getBuy_date());
				vehicleRegNum.setText(carData.getRegNo());
				lastMaintainTime.setText(carData.getMaintain_last_date().substring(0, 10));
				petrolGradeTv.setText(carData.getGas_no());
				petrolResult = carData.getGas_no();
				insuranceTel.setText(carData.getInsurance_tel());
				maintainTelEd.setText(carData.getMaintain_tel());
				//点击了选择违章城市
//				if(hasSelectIllegalCity){
				if(!"".equals(illegalCityStr)){
					if(!illegalCityStr.equals(carData.getVio_city_name())){
						engineNum.setText("");
						frameNum.setText("");
						vehicleRegNum.setText("");
						selectCityTv.setText(illegalCityStr);
					}else{
						//没有点击 按照用户之前的数据要求来显示
						selectCityTv.setText(location);
						if(illegalCitys != null){
							engineNumLayout.setVisibility(View.VISIBLE);
							vehicleNumLayout.setVisibility(View.VISIBLE);
							registerNumLayout.setVisibility(View.VISIBLE);
							
							if(Integer.valueOf(illegalCitys.getEngine()) == 0){  //隐藏发动机
								engineNumLayout.setVisibility(View.GONE);
								engine = 0;
							}else if(Integer.valueOf(illegalCitys.getEngine()) == 1){
								engine = 1;
								engineNo = Integer.valueOf(illegalCitys.getEngineno());
								if(engineNo == 0){
									engineNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.all_engine_num_hint));
								}else{
									engineNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.engine_num_hint) + engineNo + MyVehicleActivity.this.getResources().getString(R.string.hint_wei));
									engineNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(engineNo)});
								}
							}
							if(Integer.valueOf(illegalCitys.getVehiclenum()) == 0){   //隐藏车架号
								vehicleNumLayout.setVisibility(View.GONE);
								car = 0;
							}else if(Integer.valueOf(illegalCitys.getVehiclenum()) == 1){
								car = 1;
								carNo = Integer.valueOf(illegalCitys.getVehiclenumno());
								if(carNo == 0){
									frameNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.all_vehicle_num_hint));
								}else{
									frameNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.vehicle_num_hint) + carNo + MyVehicleActivity.this.getResources().getString(R.string.hint_wei));
									frameNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(carNo)});
								}
							} 
							if(Integer.valueOf(illegalCitys.getRegist()) == 0 ){    // 隐藏车辆登记证号
								registerNumLayout.setVisibility(View.GONE);
								register = 0;
							}else if(Integer.valueOf(illegalCitys.getRegist()) == 1){
								register = 1;
								registerNo = Integer.valueOf(illegalCitys.getRegistno());
								if(registerNo == 0){
									vehicleRegNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.all_register_num_hint));
								}else{
									vehicleRegNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.register_num_hint) + registerNo + MyVehicleActivity.this.getResources().getString(R.string.hint_wei));
									vehicleRegNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(registerNo)});
								}
							}
						}
					}
				}else{
					//没有点击 按照用户之前的数据要求来显示
					selectCityTv.setText(location);
					if(illegalCitys != null){
						engineNumLayout.setVisibility(View.VISIBLE);
						vehicleNumLayout.setVisibility(View.VISIBLE);
						registerNumLayout.setVisibility(View.VISIBLE);
						
						if(Integer.valueOf(illegalCitys.getEngine()) == 0){  //隐藏发动机
							engineNumLayout.setVisibility(View.GONE);
							engine = 0;
						}else if(Integer.valueOf(illegalCitys.getEngine()) == 1){
							engine = 1;
							engineNo = Integer.valueOf(illegalCitys.getEngineno());
							if(engineNo == 0){
								engineNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.all_engine_num_hint));
							}else{
								engineNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.engine_num_hint) + engineNo + MyVehicleActivity.this.getResources().getString(R.string.hint_wei));
								engineNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(engineNo)});
							}
						}
						if(Integer.valueOf(illegalCitys.getVehiclenum()) == 0){   //隐藏车架号
							vehicleNumLayout.setVisibility(View.GONE);
							car = 0;
						}else if(Integer.valueOf(illegalCitys.getVehiclenum()) == 1){
							car = 1;
							carNo = Integer.valueOf(illegalCitys.getVehiclenumno());
							if(carNo == 0){
								frameNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.all_vehicle_num_hint));
							}else{
								frameNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.vehicle_num_hint) + carNo + MyVehicleActivity.this.getResources().getString(R.string.hint_wei));
								frameNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(carNo)});
							}
						} 
						if(Integer.valueOf(illegalCitys.getRegist()) == 0 ){    // 隐藏车辆登记证号
							registerNumLayout.setVisibility(View.GONE);
							register = 0;
						}else if(Integer.valueOf(illegalCitys.getRegist()) == 1){
							register = 1;
							registerNo = Integer.valueOf(illegalCitys.getRegistno());
							if(registerNo == 0){
								vehicleRegNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.all_register_num_hint));
							}else{
								vehicleRegNum.setHint(MyVehicleActivity.this.getResources().getString(R.string.register_num_hint) + registerNo + MyVehicleActivity.this.getResources().getString(R.string.hint_wei));
								vehicleRegNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(registerNo)});
							}
						}
					}
				}
				hasSelectIllegalCity = false;
				city_code = carData.getVio_location();
				break;
			case deleteCarData:
				JSONObject jsonObject = null;
				myDialog.dismiss();
				try {
					jsonObject = new JSONObject(msg.obj.toString());
					if(Integer.parseInt(jsonObject.getString("status_code")) == 0){
						dBExcute.DeleteDB(getApplicationContext(), Constant.TB_Vehicle, "obj_id = ?", new String[]{String.valueOf(Variable.carDatas.get(chickIndex).getObj_id())});
						Variable.carDatas.remove(Variable.carDatas.get(chickIndex));
						carAdapter.notifyDataSetChanged();
						if(Variable.carDatas.size() > 0){
							chickIndex = 0;
							for(int i = 0 ; i < Variable.carDatas.size() ; i++){
								Variable.carDatas.get(i).setCheck(false);
							}
							Variable.carDatas.get(chickIndex).setCheck(true);
							carAdapter.notifyDataSetChanged();
							Message message = new Message();
							message.obj = Variable.carDatas.get(chickIndex);
							oneCarData = Variable.carDatas.get(chickIndex);
							message.what = showCarData;
							myHandler.sendMessage(message);
						}else{
							startActivity(new Intent(MyVehicleActivity.this,NewVehicleActivity.class));
						}
						if(Variable.carDatas.size() == 1){
							vehicleGridView.setVisibility(View.GONE);
						}else{
							vehicleGridView.setVisibility(View.VISIBLE);
						}
						buttomView.setVisibility(View.GONE);
				 		buttomViewIsShow = false;
						showToast("删除成功");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
				
				case setCarLogo:
					carAdapter.notifyDataSetChanged();
					break;
				case getIllegalforUrlCode: //  获取违章城市数据
					if(!"".equals(msg.obj.toString())){
						illegalList = parseJson(msg.obj.toString());
						Variable.illegalProvinceList = illegalList;
						//插入数据库
						ContentValues values = new ContentValues();
						values.put("json_data", msg.obj.toString());
						dBExcute.InsertDB(MyVehicleActivity.this, values, Constant.TB_IllegalCity);
					}
					myDialog.dismiss();
					break;
				case bindDeviceId:
					//绑定终端成功  更新数据库
					ContentValues values1 = new ContentValues();
					values1.put("device_id", deviceId);
					values1.put("serial", deviceName);
					dBExcute.updataVehilce(MyVehicleActivity.this, Constant.TB_Vehicle, values1, "obj_id=?", new String[]{String.valueOf(Variable.carDatas.get(chickIndex).getObj_id())});
					break;
				case getBrankData:
					if(!"[]".equals(msg.obj.toString())){
						//存到数据库
						parseVehicleBrandData(msg.obj.toString(),msg.what);
						ChoiceCarInformationActivity.insertDatabases(ChoiceCarInformationActivity.carBrankTitle, msg.obj.toString(), MyVehicleActivity.this);
					}
					break;
				case getSeriesData:
					if(!"[]".equals(msg.obj.toString())){
						parseVehicleBrandData(msg.obj.toString(),msg.what);
					}
					break;
			default:
				return;
			}
		}
	}
	public Bitmap logoImageIsExist1(final String imagePath,final String name){
		File filePath = new File(imagePath);
		File imageFile = new File(imagePath + name);
		if(!filePath.exists()){
			filePath.mkdir();
		}
		if(imageFile.exists()){
			//将图片读取出来 
			imageBitmap = BitmapFactory.decodeFile(imagePath + name);
		}else{
			  new Thread(new Runnable() {
				public void run() {
					//服务器获取logo图片
					String imageUrl = Constant.ImageUrl + name;
					imageBitmap = GetSystem.getBitmapFromURL(imageUrl);
		              //存储到SD卡
		              if(imageBitmap != null){
		            	  createImage(imagePath + carBrank + ".jpg",imageBitmap);
		            	  Message msg = new Message();
		            	  msg.what = setCarLogo;
		            	  myHandler.sendMessage(msg);
		              }
				}
			}).start();
		}
		return imageBitmap;
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
	
	 long waitTime = 2000;
	 long touchTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!isJump){
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void getDateView(final TextView textView){
//    	switch(textView.getId()){
//    	case R.id.my_vehicle_ed_buy_time:  //购车时间
    		textView.setOnClickListener(new View.OnClickListener() {  
    			public void onClick(View v) {
    				Calendar c = Calendar.getInstance();
    				new DatePickerDialog(MyVehicleActivity.this,
    						new DatePickerDialog.OnDateSetListener() {
    							public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
    								String tempData = year + "/"+ (monthOfYear + 1) + "/" + dayOfMonth + " 12:3:2";
    								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    								String data = sdf.format(new Date(tempData));
    								textView.setText(data);
    								buyTime.setHintTextColor(Color.BLACK);
    								ivInsuranceDate.setHintTextColor(Color.BLACK);
    								lastMaintainTime.setHintTextColor(Color.BLACK);
    							}
    						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    			}
    	    });
//    		
//    		break;
//    	case R.id.my_vehicle_tv_insurance:   //保险时间段
//    		textView.setOnClickListener(new View.OnClickListener() {  
//    			public void onClick(View v) {
//    				
//    				Calendar c = Calendar.getInstance();
//    				DatePickerDialog datePickerDialog = new DatePickerDialog(MyVehicleActivity.this, new DateDialogListener("start"), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//    				datePickerDialog.setTitle("开始时间");
//    				datePickerDialog.show();
//    			}
//    	    });
//    		break;
//    	default:
//    		return;
//    	}
	}
    
//    class DateDialogListener implements OnDateSetListener{
//    	String DataTypes = "";
//    	DateDialogListener(String DataTypes){
//    		this.DataTypes = DataTypes;
//    	}
//		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
//			if("start".equals(this.DataTypes)){
//				time1 = year + "/"+ (monthOfYear + 1) + "/" + dayOfMonth;
//				Calendar c = Calendar.getInstance();
//				DatePickerDialog datePickerDialog = new DatePickerDialog(MyVehicleActivity.this, new DateDialogListener("end"), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//				datePickerDialog.setTitle("结束时间");
//				datePickerDialog.show();
//			}else if("end".equals(this.DataTypes)){
//				time2 = year + "/"+ (monthOfYear + 1) + "/" + dayOfMonth;
//				if(time1.equals(time2)){
//					showToast("非法的时间段");
//				}
//				if(Integer.valueOf(time1.substring(0, 4)) > Integer.valueOf(time2.substring(0, 4))){
//					showToast("非法的时间段");
//					return;
//				}else{
//					if(Integer.valueOf(time1.substring(5, 6)) > Integer.valueOf(time2.substring(5, 6))){
//						showToast("非法的时间段");
//						return;
//					}else{
//						if(Integer.valueOf(time1.substring(7)) > Integer.valueOf(time2.substring(7))){
//							showToast("非法的时间段");
//							return;
//						}else{
//							ivInsuranceDate.setText(time1+"-"+time2);
//						}
//					}
//				}
//			}
//		}
//    }
    private static final String TAG = "MyVehicleActivity";
    //  TODO  向服务器提交数据
    public void commitData(){
    	Editor editor = preferences.edit();
		editor.putInt(Constant.DefaultVehicleID, chickIndex);
		editor.putString(Constant.defaultCenter_key, Variable.carDatas.get(chickIndex).getObj_name());
		String str = Variable.carDatas.get(chickIndex).getObj_name();
		String str2 = str==null?"null":str;
		editor.commit();
		
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("obj_name", vehicleNumber.getText().toString().trim()));
        Log.d(TAG, "obj_name = " + vehicleNumber.getText().toString().trim());
        params.add(new BasicNameValuePair("car_brand", myVehicleBrank.getText().toString()));
        Log.d(TAG, "car_brand = " + myVehicleBrank.getText().toString().trim());
        params.add(new BasicNameValuePair("car_series", tvCarSeries.getText().toString()));
        Log.d(TAG, "car_series = " + tvCarSeries.getText().toString().trim());
        params.add(new BasicNameValuePair("car_type", tvCarType.getText().toString()));
        Log.d(TAG, "car_type = " + tvCarType.getText().toString().trim());
        params.add(new BasicNameValuePair("vio_location", city_code));
        Log.d(TAG, "vio_location = " + city_code);
        
        
        params.add(new BasicNameValuePair("gas_no", petrolResult));
        Log.d(TAG, "gas_no = " + petrolResult);
        
        
        params.add(new BasicNameValuePair("car_brand_id", carBrankId));
        Log.d(TAG, "carBrankId = " + carBrankId);
        Log.d(TAG, "car_brand_id = " + carBrankId);
        params.add(new BasicNameValuePair("car_series_id", carSeriesId));
        Log.d(TAG, "car_series_id = " + carSeriesId);
        params.add(new BasicNameValuePair("car_type_id", carTypeId));
        Log.d(TAG, "car_type_id = " + carTypeId);
        params.add(new BasicNameValuePair("vio_city_name", selectCityTv.getText().toString()));
        Log.d(TAG, "vio_city_name = " + selectCityTv.getText().toString());
        params.add(new BasicNameValuePair("insurance_tel", insuranceTel.getText().toString().trim()));
        Log.d(TAG, "insurance_tel = " + insuranceTel.getText().toString().trim());
        params.add(new BasicNameValuePair("maintain_tel", maintainTelEd.getText().toString().trim()));
        Log.d(TAG, "maintain_tel = " + maintainTelEd.getText().toString().trim());
        //违章查询城市代码  
        if(engine == 0){
        	params.add(new BasicNameValuePair("engine_no", ""));
        	Log.d(TAG, "engine_no = " + "");
        }else if(engine == 1){
        	params.add(new BasicNameValuePair("engine_no", engineNum.getText().toString().trim()));
        	Log.d(TAG, "engine_no = " + engineNum.getText().toString().trim());
        }
        if(car == 0){
        	params.add(new BasicNameValuePair("frame_no", ""));
        	Log.d(TAG, "frame_no = " + "");
        }else if(car == 1){
        	params.add(new BasicNameValuePair("frame_no", frameNum.getText().toString().trim()));
        	Log.d(TAG, "frame_no = " + frameNum.getText().toString().trim());
        }
        if(register == 0){
        	 params.add(new BasicNameValuePair("reg_no", ""));
        	 Log.d(TAG, "reg_no = " + "");
        }else if(register == 1){
        	params.add(new BasicNameValuePair("reg_no", vehicleRegNum.getText().toString().trim()));
        	Log.d(TAG, "reg_no = " + vehicleRegNum.getText().toString().trim());
        }
        
        params.add(new BasicNameValuePair("insurance_company", showInsuranceCompany.getText().toString()));
        Log.d(TAG, "insurance_company = " + showInsuranceCompany.getText().toString().trim());
        params.add(new BasicNameValuePair("insurance_date", ivInsuranceDate.getText().toString()));
        Log.d(TAG, "insurance_date = " + ivInsuranceDate.getText().toString().trim());
        params.add(new BasicNameValuePair("annual_inspect_data", ""));    //暂时去掉
        params.add(new BasicNameValuePair("maintain_company", tvMaintain.getText().toString()));
        Log.d(TAG, "maintain_company = " + tvMaintain.getText().toString().trim());
        params.add(new BasicNameValuePair("maintain_last_mileage", lastMaintain.getText().toString().trim()));
        Log.d(TAG, "maintain_last_mileage = " + lastMaintain.getText().toString().trim());
        params.add(new BasicNameValuePair("maintain_last_date", lastMaintainTime.getText().toString()));
        Log.d(TAG, "maintain_last_date = " + lastMaintainTime.getText().toString().trim());
        params.add(new BasicNameValuePair("maintain_next_mileage", "2013"));   //暂时去掉
        params.add(new BasicNameValuePair("buy_date", buyTime.getText().toString().trim()));
        Log.d(TAG, "buy_time = " + buyTime.getText().toString().trim());
        
        new Thread(new Runnable() {
			public void run() {
				try {
					 	BasicHttpParams httpParams = new BasicHttpParams();  
		                HttpConnectionParams.setConnectionTimeout(httpParams, 10000);  
		                HttpConnectionParams.setSoTimeout(httpParams, 10000); 
		                HttpClient client = new DefaultHttpClient(httpParams);
		                HttpPut httpPut = new HttpPut(Constant.BaseUrl + "vehicle/" + Variable.carDatas.get(chickIndex).getObj_id() + "?auth_code=" + Variable.auth_code); 
		                if(params != null){
		                    httpPut.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
		                }
		                HttpResponse response = client.execute(httpPut); 
					 
					 if(response.getStatusLine().getStatusCode() == 200){
					     HttpEntity entity = response.getEntity();
		                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		                    StringBuilder sb = new StringBuilder();
		                    String line = "";
		                    while ((line = reader.readLine()) != null) {
		                        sb.append(line);
		                    }
						 String strResult = sb.toString();
						 
						 //更新数据库
						 ContentValues values = new ContentValues();
						 values.put("obj_name", vehicleNumber.getText().toString().trim());
						 values.put("car_brand", myVehicleBrank.getText().toString());
						 values.put("car_series", tvCarSeries.getText().toString());
						 values.put("car_type", tvCarType.getText().toString());
						 values.put("vio_location",city_code);
						 values.put("engine_no", engineNum.getText().toString().trim());
						 values.put("frame_no", frameNum.getText().toString().trim());
						 values.put("reg_no", vehicleRegNum.getText().toString().trim());
						 values.put("insurance_company",showInsuranceCompany.getText().toString());
						 values.put("insurance_date", ivInsuranceDate.getText().toString());
//							value.put("annual_inspect_date", annualSurveyTime.getText().toString());
						 values.put("maintain_company", tvMaintain.getText().toString());
						 values.put("maintain_last_mileage", lastMaintain.getText().toString().trim());
						 values.put("maintain_last_date", lastMaintainTime.getText().toString());
//							value.put("maintain_next_mileage", nextMaintainMileage.getText().toString().trim());
						 values.put("maintain_next_mileage", "");
						 values.put("car_brand_id", carBrankId);
						 Log.d(TAG, "carBrankId = " + carBrankId);
						 values.put("car_series_id", carSeriesId);
						 values.put("car_type_id", carTypeId);
						 values.put("vio_city_name", selectCityTv.getText().toString());
						 values.put("insurance_tel", insuranceTel.getText().toString().trim());
						 values.put("maintain_tel", maintainTelEd.getText().toString().trim());
						 values.put("buy_date", buyTime.getText().toString());
						 values.put("gas_no", petrolResult);
						 
						 
						 dBExcute.updataVehilce(MyVehicleActivity.this, Constant.TB_Vehicle, values, "obj_id=?", new String[]{String.valueOf(Variable.carDatas.get(chickIndex).getObj_id())});
						 
						 //更改静态类
						 Variable.carDatas.get(chickIndex).setObj_name(vehicleNumber.getText().toString().trim());
						 Variable.carDatas.get(chickIndex).setCar_brand(myVehicleBrank.getText().toString());
						 Variable.carDatas.get(chickIndex).setCar_series(tvCarSeries.getText().toString());
						 Variable.carDatas.get(chickIndex).setCar_type(tvCarType.getText().toString());
						 Variable.carDatas.get(chickIndex).setVio_location(city_code);
						 Variable.carDatas.get(chickIndex).setEngine_no(engineNum.getText().toString().trim());
						 Variable.carDatas.get(chickIndex).setFrame_no(frameNum.getText().toString().trim());
						 Variable.carDatas.get(chickIndex).setRegNo(vehicleRegNum.getText().toString().trim());
						 
						 Variable.carDatas.get(chickIndex).setInsurance_company(showInsuranceCompany.getText().toString());
						 Variable.carDatas.get(chickIndex).setInsurance_date(ivInsuranceDate.getText().toString());
						 Variable.carDatas.get(chickIndex).setMaintain_company(tvMaintain.getText().toString());
						 Variable.carDatas.get(chickIndex).setMaintain_last_mileage(lastMaintain.getText().toString().trim());
						 Variable.carDatas.get(chickIndex).setMaintain_last_date(lastMaintainTime.getText().toString());
						 Variable.carDatas.get(chickIndex).setBuy_date( buyTime.getText().toString());
						 Variable.carDatas.get(chickIndex).setCar_brand_id(carBrankId);
						 Log.d(TAG, "carBrankId = " + carBrankId);
						 Variable.carDatas.get(chickIndex).setCar_series_id(carSeriesId);
						 Variable.carDatas.get(chickIndex).setCar_type_id(carTypeId);
						 Variable.carDatas.get(chickIndex).setVio_city_name(selectCityTv.getText().toString());
						 Variable.carDatas.get(chickIndex).setInsurance_tel(insuranceTel.getText().toString().trim());
						 Variable.carDatas.get(chickIndex).setMaintain_tel(maintainTelEd.getText().toString().trim());
						 Variable.carDatas.get(chickIndex).setGas_no(petrolResult);
						 
						 Intent intent = new Intent(Constant.A_UpdateCar);
		                 sendBroadcast(intent);
					 }else{
					 }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
    }
   public void showToast(String showContent){
	   Toast.makeText(getApplicationContext(), showContent, 0).show();
    }
   
 //获取省份   TODO
 	public static List<ProvinceModel> parseJson(String jsonData){
 		illegalList = new ArrayList<ProvinceModel>();
 		try {
 			JSONObject jsonObj = new JSONObject(jsonData);
 			JSONObject result = jsonObj.getJSONObject("result");
 			Iterator it = result.keys();
 			while(it.hasNext()){
 				List<IllegalCity> illegalCityList = new ArrayList<IllegalCity>();
 				ProvinceModel provinceModel = new ProvinceModel();
 				
 				String key = it.next().toString();
 				JSONObject jsonObject = result.getJSONObject(key);
 				String province = jsonObject.getString("province");  //省份
 				
 				JSONArray jsonArray = jsonObject.getJSONArray("citys");  //城市
 				for(int i = 0 ; i < jsonArray.length() ; i ++){
 					IllegalCity illegalCity = new IllegalCity();
 					JSONObject jsonObject3 = jsonArray.getJSONObject(i);
 					illegalCity.setAbbr(jsonObject3.getString("abbr"));
 					illegalCity.setCityCode(jsonObject3.getString("city_code"));
 					illegalCity.setCityName(jsonObject3.getString("city_name"));
 					illegalCity.setClassa(jsonObject3.getString("classa"));
 					illegalCity.setEngine(jsonObject3.getString("engine"));
 					illegalCity.setEngineno(jsonObject3.getString("engineno"));
 					illegalCity.setRegist(jsonObject3.getString("regist"));
 					illegalCity.setRegistno(jsonObject3.getString("registno"));
 					illegalCity.setVehiclenum(jsonObject3.getString("class"));
 					illegalCity.setVehiclenumno(jsonObject3.getString("classno"));
 					illegalCityList.add(illegalCity);
 				}
 				provinceModel.setIllegalCityList(illegalCityList);
 				provinceModel.setProvinceName(province);
 				illegalList.add(provinceModel);
 			}
 		} catch (JSONException e) {
 			e.printStackTrace();
 		}
 		return filledData(illegalList);
 	}
 	
 	//将省份汉字转为拼音
 	private static List<ProvinceModel> filledData(List<ProvinceModel> provinceModelList){
 		for(int i=0; i<provinceModelList.size(); i++){
 			ProvinceModel sortModel = provinceModelList.get(i);
 			//汉字转换成拼音
 			String pinyin = characterParser.getSelling(provinceModelList.get(i).getProvinceName());
 			String sortString = pinyin.substring(0, 1).toUpperCase();
 			sortModel.setProvinceLetter(sortString.toUpperCase());   //设置拼音
 		}
 		Collections.sort(provinceModelList, comparator);
 		return provinceModelList;
 	}
 	
 	
 	//根据拼音首字母排序
 	class PinyinComparator implements Comparator<ProvinceModel> {
 		public int compare(ProvinceModel o1, ProvinceModel o2) {
 			if (o1.getProvinceLetter().equals("@")
 					|| o2.getProvinceLetter().equals("#")) {
 				return -1;
 			} else if (o1.getProvinceLetter().equals("#")
 					|| o2.getProvinceLetter().equals("@")) {
 				return 1;
 			} else {
 				return o1.getProvinceLetter().compareTo(o2.getProvinceLetter());
 			}
 		}
 	}
 	//校验输入内容的合法性  TODO
 	public boolean CheckDatas(){
 		if(engine == 1){
 			if(engineNo == 0){
 				if(engineNum.getText().toString().trim().length() == engineNo){
 					engineNum.setError("引擎号不合法");
 					return false;
 				}
 			}else if(engineNum.getText().toString().trim().length() != engineNo){
					engineNum.setError("引擎号不合法");
					return false;
				}
 		}
 		if(car == 1){
 			if(carNo == 0){
 				if(frameNum.getText().toString().trim().length() == carNo){
 	 				frameNum.setError("车架号不合法");
 	 				return false;
 	 			}
 			}else if(frameNum.getText().toString().trim().length() != carNo){
 				frameNum.setError("车架号不合法");
 				return false;
 			}
 		}
 		if(register == 1){
 			if(registerNo == 0){
 				if(frameNum.getText().toString().trim().length() == registerNo){
 	 				frameNum.setError("登记证号不合法");
 	 				return false;
 	 			}
 			}else if(frameNum.getText().toString().trim().length() != registerNo){
 				frameNum.setError("登记证号不合法");
 				return false;
 			}
 		}
 		if("".equals(vehicleNumber.getText().toString().trim())){
 			vehicleNumber.setError("车牌号不合法");
 			return false;
 		}else if(equals(vehicleNumber.getText().toString().trim().length() != 7)){
 			vehicleNumber.setError("车牌号不合法");
 			return false;
 		}
 		if("".equals(lastMaintain.getText().toString().trim())){
 			lastMaintain.setError("最后保养里程不合法");
 			return false;
 		}
 		if("".equals(lastMaintainTime.getText().toString())){
 			lastMaintainTime.setHintTextColor(Color.RED);
 			return false;
 		}
 		if("".equals(ivInsuranceDate.getText().toString())){
 			ivInsuranceDate.setHintTextColor(Color.RED);
 			return false;
 		}
 		if("".equals(buyTime.getText().toString())){
 			buyTime.setHintTextColor(Color.RED);
 			return false;
 		}
 		return true;
 	}
 	protected void onPause() {
 		buttomView.setVisibility(View.GONE);
 		buttomViewIsShow = false;
 		vehNum = vehicleNumber.getText().toString();
 		super.onPause();
 	}
 	
 	@Override
 	protected void onDestroy() {
 		MyVehicleActivity.this.unregisterReceiver(myBroadCastReceiver);
 		super.onDestroy();
 	}
 	
 	
 	public void getVehiclebrandData(String whereValue,String table,String url,int what){
 		String result = null;
 		DBHelper helper  = new DBHelper(MyVehicleActivity.this);
 		SQLiteDatabase reader = helper.getReadableDatabase();
 		Cursor cursor = reader.rawQuery("select * from " + table + " where Title = ?", new String[]{whereValue});
 		if(cursor.moveToFirst()){
 			parseVehicleBrandData(cursor.getString(cursor.getColumnIndex("Content")),what);
 		}else{
 			new Thread(new NetThread.GetDataThread(myHandler, url, what)).start();
 		}
 		cursor.close();
 		reader.close();
 	}
 	
 	public void parseVehicleBrandData(String str,int what){
 		JSONArray jsonary = null;
 		try {
			if(what == getBrankData){
				jsonary = new JSONArray(str.substring(0,str.length()));
				for(int i = 0 ; i < jsonary.length() ; i ++){
					if(carBrank.equals(jsonary.getJSONObject(i).get("name"))){
						carBrankId = jsonary.getJSONObject(i).get("id") + "";
						Log.d(TAG, "getBrankData carBrankId = " + carBrankId);
						getVehiclebrandData(ChoiceCarInformationActivity.carSeriesTitle,Constant.TB_Base,Constant.BaseUrl + "base/car_series?pid=" + carBrankId , getSeriesData);
					}
				}
			}
			if(getSeriesData == what){
				jsonary = new JSONArray(str.substring(0,str.length()));
				for(int i = 0 ; i < jsonary.length() ; i ++){
					if(carSeries.equals(jsonary.getJSONObject(i).get("show_name"))){
						carSeriesId = jsonary.getJSONObject(i).get("id")+"";
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
 	}
 	class MyBroadCastReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			if(carAdapter != null){
				carAdapter.notifyDataSetChanged();
			}
		}
 	}
}
