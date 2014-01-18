package com.wise.wawc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.CarData;
import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.SpinerPopWindow;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.LogoAdapter;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.KeyEvent;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
/**
 * 我的爱车
 * @author 王庆文
 */
public class MyVehicleActivity extends Activity implements  AbstractSpinerAdapter.IOnItemSelectListener {
	
	private static final int getCarSeries = 1;
	private static final int refreshCarSeries = 2;
	private static final int getCarType = 7;
	private static final int saveVehicleData = 9;
	private Button menu = null;
	private Button home = null;
	private ImageView brand = null;
	private ImageView device = null;
	private ImageView insuranceCompany = null;
	private TextView showInsuranceCompany;   //显示保险公司
	public static final int resultCodeInsurance = 2;   //选择保险公司的识别码
	public static final int resultCodeBrank = 3;       //选择汽车品牌的识别码
	public static final int resultCodeMaintain = 6;       //选择汽车品牌的识别码
	public static final int showCarData = 8;       //显示汽车数据
	public static final int deleteCarData = 10;       //删除汽车数据
	
	
	private EditText etDialogMileage = null;   //输入里程
	
	private Button btSureMileage = null;
	private Button btCancleMileage = null;
	private ImageView insuranceTime;
	private ImageView choiceMaintian = null;
	
	private TextView tvMileage = null;  //显示里程
	private TextView myVehicleBrank = null;
	private TextView tvMaintain = null;
	private EditText vehicleNumber = null;
	private EditText engineNum = null;
	private EditText frameNum = null;
	private EditText lastMaintain = null;
	private EditText buyTime = null;
	private TextView ivInsuranceDate = null;
	
	private GridView vehicleGridView = null;
	private LogoAdapter logoAdapter = null;
	AlertDialog dlg = null;
	boolean isJump = false;//false从菜单页跳转过来返回打开菜单，true从首页跳转返回关闭页面
	
	private int width = 0 ; 
	
	private ImageView ivCarSeries = null; //车型
	private ImageView ivCarType = null;  //车款
	private TextView tvCarSeries = null;
	private TextView tvCarType = null;
	private Button btSaveVehicleData = null;
	private Button btDeleteVehicle = null;
	
	
	private SpinerPopWindow mSpinerPopWindow;
	
	private MyHandler myHandler = null;
	private DBHelper dBhalper = null;
	
	private ProgressDialog myDialog = null;
	
	private DBExcute dBExcute = null;
	
	private List<String> carSeriesNameList = new ArrayList<String>();
	private List<String> carSeriesIdList = new ArrayList<String>();
	
	private String carBrankId = "";
	private String carSeriesId = "";
	private static String carSeriesTitle = "carSeries";
	private static String carTypeTitle = "carType";
	private String brank = ""; 
	private CarData oneCarData = null;  //存储修改的车辆数据
//	private String vehicleId = "";     //存储车牌号   用户更改数据库
	private String vehicleNum = "";
	
	private int chickIndex = 0;
	private CarData newCarImage = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_vehicle);
		menu = (Button) findViewById(R.id.my_vechile_menu);
		home = (Button) findViewById(R.id.my_vechile_home);
		brand = (ImageView) findViewById(R.id.my_vehicle_brank);
		device = (ImageView) findViewById(R.id.	my_vehicle_device);
		insuranceCompany = (ImageView)findViewById(R.id.insurance_company);
		showInsuranceCompany = (TextView) findViewById(R.id.show_insurance_company);
		insuranceTime = (ImageView) findViewById(R.id.insurance_mileage);
		tvMileage = (TextView) findViewById(R.id.my_vehicle_mileage);
		myVehicleBrank = (TextView) findViewById(R.id.my_vehicle_beank);
		choiceMaintian = (ImageView) findViewById(R.id.choice_maintain_image);
		tvMaintain = (TextView) findViewById(R.id.show_maintain);
		vehicleNumber = (EditText)findViewById(R.id.my_vehicle_ed_vehicle_number);
		engineNum = (EditText)findViewById(R.id.my_vehicle_ed_engine_num);
		frameNum = (EditText)findViewById(R.id.my_vehilce_ed_fram_num);
		lastMaintain = (EditText)findViewById(R.id.my_vehicle_ed_last_maintain);
		buyTime = (EditText)findViewById(R.id.my_vehicle_ed_buy_time);
		ivInsuranceDate = (TextView) findViewById(R.id.my_vehicle_tv_insurance);
		btSaveVehicleData = (Button) findViewById(R.id.my_vehilce_save);
		btDeleteVehicle = (Button) findViewById(R.id.my_vehilce_delete);
		
		
		
		ivCarSeries = (ImageView) findViewById(R.id.iv_car_series);
		ivCarType = (ImageView) findViewById(R.id.iv_car_type);
		tvCarSeries = (TextView) findViewById(R.id.tv_car_series);
		tvCarType = (TextView) findViewById(R.id.tv_car_type);
		
	}
	protected void onResume() {
		super.onResume();

		ivCarSeries.setOnClickListener(new ClickListener());
		ivCarType.setOnClickListener(new ClickListener());
		mSpinerPopWindow = new SpinerPopWindow(MyVehicleActivity.this);
		mSpinerPopWindow.setItemListener(this);
		
		myHandler = new MyHandler();
		dBhalper = new DBHelper(MyVehicleActivity.this);
		dBExcute = new DBExcute();
		
		Log.e("MyVehicleActivity---num:",Variable.carDatas.size() +  "");
		
		//车辆数据
		newCarImage = new CarData();
		newCarImage.setCarLogo(R.drawable.new_vehicle);
		Variable.carDatas.add(newCarImage);
		int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(Variable.carDatas.size() * (px + 10),LayoutParams.WRAP_CONTENT);
		vehicleGridView = (GridView) findViewById(R.id.gv_my_vehicle);
		//汽车品牌Logo
		logoAdapter = new LogoAdapter(MyVehicleActivity.this,Variable.carDatas);
		vehicleGridView.setAdapter(logoAdapter);
		vehicleGridView.setLayoutParams(params);
		vehicleGridView.setColumnWidth(px);
		vehicleGridView.setHorizontalSpacing(6);
		vehicleGridView.setStretchMode(GridView.NO_STRETCH);
		vehicleGridView.setNumColumns(Variable.carDatas.size());
		vehicleGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if((Variable.carDatas.size() - 1) == arg2){
					Variable.carDatas.remove(newCarImage);
					startActivity(new Intent(MyVehicleActivity.this,NewVehicleActivity.class));
				}else{
					oneCarData = Variable.carDatas.get(arg2);
					Message msg = new Message();
					msg.obj = Variable.carDatas.get(arg2);
					msg.what = showCarData;
					myHandler.sendMessage(msg);
					for(int i = 0 ; i < Variable.carDatas.size() ; i++){
						Variable.carDatas.get(i).setCheck(false);
					}
					Variable.carDatas.get(arg2).setCheck(true);
					logoAdapter.notifyDataSetChanged();
					chickIndex = arg2;
				}
			}
		});
		
		btSaveVehicleData.setOnClickListener(new ClickListener());
		btDeleteVehicle.setOnClickListener(new ClickListener());
		choiceMaintian.setOnClickListener(new ClickListener());
		device.setOnClickListener(new ClickListener());
		menu.setOnClickListener(new ClickListener());
		home.setOnClickListener(new ClickListener());
		brand.setOnClickListener(new ClickListener());
		insuranceCompany.setOnClickListener(new ClickListener());
		insuranceTime.setOnClickListener(new ClickListener());
		
		Intent intent = getIntent();
		isJump = intent.getBooleanExtra("isJump", false);

		width = getWindowManager().getDefaultDisplay().getWidth();
		
		Message msg = new Message();
		msg.obj = Variable.carDatas.get(chickIndex);
		oneCarData = Variable.carDatas.get(chickIndex);
		msg.what = showCarData;
		myHandler.sendMessage(msg);
		
		Log.e("MyVehicleActivity---num:",Variable.carDatas.size() +  "");
	}


	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.my_vechile_menu:
				if(isJump){
					finish();
				}else{
					ActivityFactory.A.LeftMenu();
				}
				break;
			case R.id.my_vechile_home:
				if(isJump){
					finish();
				}else{
					ActivityFactory.A.ToHome();
				}
				break;
			case R.id.my_vehicle_brank:    //选择汽车品牌
				Variable.carDatas.remove(newCarImage);
				Intent intent = new Intent(MyVehicleActivity.this,CarBrankListActivity.class);
				intent.putExtra("code", resultCodeBrank);
				startActivityForResult(intent, resultCodeBrank);
				Log.e("选择品牌：" ,Variable.carDatas.size() + "");
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
				intent3.putExtra("brank", brank);
				startActivityForResult(intent3, resultCodeMaintain);
				break;
			case R.id.iv_car_series:  //选择车型    根据品牌id获取
				myDialog = ProgressDialog.show(MyVehicleActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
				myDialog.setCancelable(true);
				if("".equals(carBrankId)){
					Toast.makeText(getApplicationContext(), "请选择品牌", 0).show();
					myDialog.dismiss();
					return;
				}
				getCarDatas(carSeriesTitle,"base/car_series?pid=",getCarSeries,carBrankId);
				break;
			case R.id.iv_car_type:  //选择车款
				if(carSeriesNameList.size() > 0){
					mSpinerPopWindow.setWidth(width);
					mSpinerPopWindow.setHeight(300);
					mSpinerPopWindow.showAsDropDown(tvCarType);
				}else{
					Toast.makeText(getApplicationContext(), "请选择车型", 0).show();
				}
				break;
				
			case R.id.my_vehilce_save:
				myDialog = ProgressDialog.show(MyVehicleActivity.this, "提示", "正在保存...");
				myDialog.setCancelable(true);
				// TODO
				List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(new BasicNameValuePair("obj_name", vehicleNumber.getText().toString().trim()));
		        params.add(new BasicNameValuePair("car_brand", myVehicleBrank.getText().toString()));
		        params.add(new BasicNameValuePair("car_series", tvCarSeries.getText().toString()));
		        params.add(new BasicNameValuePair("car_type", tvCarType.getText().toString()));
		        params.add(new BasicNameValuePair("engine_no", engineNum.getText().toString().trim()));
		        Log.e("更改服务器时：",engineNum.getText().toString().trim() + "");
		        params.add(new BasicNameValuePair("frame_no", frameNum.getText().toString().trim()));
		        params.add(new BasicNameValuePair("insurance_company", showInsuranceCompany.getText().toString()));
		        params.add(new BasicNameValuePair("insurance_date", ivInsuranceDate.getText().toString()));
		        params.add(new BasicNameValuePair("maintain_company", tvMaintain.getText().toString()));
		        params.add(new BasicNameValuePair("maintain_last_mileage", lastMaintain.getText().toString().trim()));
		        //  ui布局无此参数
		        params.add(new BasicNameValuePair("maintain_last_date", "2015"));
		        params.add(new BasicNameValuePair("annual_inspect_data", oneCarData.getAnnual_inspect_date()));
		        params.add(new BasicNameValuePair("maintain_next_mileage", "2013"));
		        params.add(new BasicNameValuePair("buy_time", buyTime.getText().toString().trim()));
				new Thread(new NetThread.postDataThread(myHandler, Constant.BaseUrl + "vehicle/" + Variable.carDatas.get(chickIndex).getObj_id() + "?auth_code=" + Variable.auth_code, params, saveVehicleData)).start();
//				Log.e("需要更改的车辆id",carDataList.get(chickIndex).getObj_id() + "");
				break;
			case R.id.my_vehilce_delete:
				myDialog = ProgressDialog.show(MyVehicleActivity.this, "提示", "正在删除...");
				myDialog.setCancelable(true);
				new Thread(new NetThread.DeleteThread(myHandler, Constant.BaseUrl + "vehicle/" + Variable.carDatas.get(chickIndex).getObj_id() + "?auth_code=" + Variable.auth_code, deleteCarData)).start();
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
			brank = (String) data.getSerializableExtra("brank");
			carBrankId = (String) data.getSerializableExtra("carId");
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
	
	class MyHandler extends Handler{
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
				dBExcute.InsertDB(MyVehicleActivity.this, values, Constant.TB_Base);
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
				dBExcute.InsertDB(MyVehicleActivity.this, carTypeValues, Constant.TB_Base);
				JSONArray typeJsonArray = null;
				try {
					typeJsonArray = new JSONArray(carType);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				parseJSONToList(typeJsonArray,msg.what);
				break;
			case showCarData:
				CarData carData = (CarData) msg.obj;
				//设置汽车数据    TODO
				vehicleNum = carData.getObj_name();
				myVehicleBrank.setText(carData.getCar_brand());
				vehicleNumber.setText(carData.getObj_name());
				tvCarSeries.setText(carData.getCar_series());
				tvCarType.setText(carData.getCar_type());
				engineNum.setText(carData.getEngine_no());
				Log.e("显示数据时",carData.getObj_id()+"");
				frameNum.setText(carData.getFrame_no());
				showInsuranceCompany.setText(carData.getInsurance_company());
				ivInsuranceDate.setText(carData.getInsurance_date());
				tvMaintain.setText(carData.getMaintain_company());
				lastMaintain.setText(carData.getMaintain_last_mileage());
				buyTime.setText(carData.getBuy_date());
				break;
				
			case saveVehicleData:
				myDialog.dismiss();
				//TODO
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					if(Integer.parseInt(jsonObject.getString("status_code")) == 0){
								Variable.carDatas.get(chickIndex).setObj_name(vehicleNumber.getText().toString().trim());
								Variable.carDatas.get(chickIndex).setCar_brand(myVehicleBrank.getText().toString());
								Variable.carDatas.get(chickIndex).setCar_series(tvCarSeries.getText().toString());
								Variable.carDatas.get(chickIndex).setCar_type(tvCarType.getText().toString());
								Variable.carDatas.get(chickIndex).setEngine_no(engineNum.getText().toString().trim());
								Log.e("更改的值：" , engineNum.getText().toString().trim());
								Variable.carDatas.get(chickIndex).setFrame_no(frameNum.getText().toString().trim());
								//布局无此参数
								Variable.carDatas.get(chickIndex).setAnnual_inspect_date("2015年");
								Variable.carDatas.get(chickIndex).setInsurance_date(ivInsuranceDate.getText().toString());
								Variable.carDatas.get(chickIndex).setMaintain_company(tvMaintain.getText().toString());
								Variable.carDatas.get(chickIndex).setMaintain_last_mileage(lastMaintain.getText().toString().trim());
								//布局无此参数
								Variable.carDatas.get(chickIndex).setMaintain_last_date("2013");
								Variable.carDatas.get(chickIndex).setMaintain_next_mileage("47000Km");
								Variable.carDatas.get(chickIndex).setBuy_date("2014年1月14");
						
						//更改数据库
						ContentValues carDatas = new ContentValues();
						carDatas.put("obj_name", vehicleNumber.getText().toString().trim());
						carDatas.put("car_brand", myVehicleBrank.getText().toString());
						carDatas.put("car_series", tvCarSeries.getText().toString());
						carDatas.put("car_type", tvCarType.getText().toString());
						carDatas.put("engine_no", engineNum.getText().toString().trim());
						Log.e("更改数据库时：",engineNum.getText().toString().trim());
						carDatas.put("frame_no", frameNum.getText().toString().trim());
						carDatas.put("insurance_company", showInsuranceCompany.getText().toString());
						carDatas.put("insurance_date", ivInsuranceDate.getText().toString());
						//布局无此参数
						carDatas.put("annual_inspect_date", "2013");
						carDatas.put("maintain_company", tvMaintain.getText().toString());
						carDatas.put("maintain_last_mileage", lastMaintain.getText().toString().trim());
						//布局无此参数
						carDatas.put("maintain_next_mileage", "2014");
						carDatas.put("buy_date", buyTime.getText().toString().trim());
						dBExcute.updataVehilce(getApplicationContext(), Constant.TB_Vehicle, carDatas, "obj_id = ?", new String[]{String.valueOf(Variable.carDatas.get(chickIndex).getObj_id())});
						
						Toast.makeText(getApplicationContext(), "保存成功", 0).show();
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case deleteCarData:
				JSONObject jsonObject = null;
				myDialog.dismiss();
				try {
					jsonObject = new JSONObject(msg.obj.toString());
					if(Integer.parseInt(jsonObject.getString("status_code")) == 0){
						dBExcute.DeleteDB(getApplicationContext(), Constant.TB_Vehicle, "obj_id = ?", new String[]{String.valueOf(Variable.carDatas.get(chickIndex).getObj_id())});
						Log.e("删除前" + Variable.carDatas.size() ,Variable.carDatas.size() + "");
						Variable.carDatas.remove(Variable.carDatas.get(chickIndex));
						Log.e("删除后" + Variable.carDatas.size() ,Variable.carDatas.size() + "");
						logoAdapter.updataDatas(Variable.carDatas);
						if(Variable.carDatas.size() > 1){
							vehicleNum = Variable.carDatas.get(0).getObj_name();
							myVehicleBrank.setText(Variable.carDatas.get(0).getCar_brand());
							vehicleNumber.setText(Variable.carDatas.get(0).getObj_name());
							tvCarSeries.setText(Variable.carDatas.get(0).getCar_series());
							tvCarType.setText(Variable.carDatas.get(0).getCar_type());
							engineNum.setText(Variable.carDatas.get(0).getEngine_no());
							Log.e("显示数据时",Variable.carDatas.get(0).getObj_id()+"");
							frameNum.setText(Variable.carDatas.get(0).getFrame_no());
							showInsuranceCompany.setText(Variable.carDatas.get(0).getInsurance_company());
							ivInsuranceDate.setText(Variable.carDatas.get(0).getInsurance_date());
							tvMaintain.setText(Variable.carDatas.get(0).getMaintain_company());
							lastMaintain.setText(Variable.carDatas.get(0).getMaintain_last_mileage());
							buyTime.setText(Variable.carDatas.get(0).getBuy_date());
						}else{
							Variable.carDatas.remove(newCarImage);
							startActivity(new Intent(MyVehicleActivity.this,NewVehicleActivity.class));
						}
						
						Toast.makeText(getApplicationContext(), "删除成功", 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				Log.e("删除车辆执行结果--->",msg.obj.toString());
				break;
			default:
				return;
			}
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
			mSpinerPopWindow.showAsDropDown(tvCarSeries);
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
			mSpinerPopWindow.showAsDropDown(tvCarType);
		}
		
	}
	public void getCarDatas(String title,String url,int code,String id){
		Log.e("访问地址--->",Constant.BaseUrl + url + id);
		SQLiteDatabase read = dBhalper.getReadableDatabase();
		//查询数据库
		Cursor cursor = read.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{title + id});
		JSONArray jsonArray = null;
//		String carSeries = "";
		if(cursor.moveToFirst()){
			try {
//				carSeries = cursor.getString(cursor.getColumnIndex("Content"));
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
	public void onItemClick(int pos, int type) {
		if(type == getCarSeries){
			String value = "";
			if (pos >= 0 && pos <= carSeriesNameList.size()){
				value = carSeriesNameList.get(pos);
				tvCarSeries.setText(value);
			}
			
			for(int i = 0 ; i < carSeriesNameList.size() ; i ++){
				if(carSeriesNameList.get(i).equals(value)){
					carSeriesId = carSeriesIdList.get(i);
				}
			}
			myDialog = ProgressDialog.show(MyVehicleActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
			myDialog.setCancelable(true);
			getCarDatas(carTypeTitle,"base/car_type?pid=",getCarType,carSeriesId);
			Log.e("点击车型所得","点击车型所得");
		}else if(type == getCarType){
			Log.e("点击车款所得","点击车款所得");
			String value = "";
			if (pos >= 0 && pos <= carSeriesNameList.size()){
				value = carSeriesNameList.get(pos);
				tvCarType.setText(value);
			}
		}
	}
	public void showUserCarData(CarData carData){
		
		Log.e("obj_id=",carData.getObj_id() + "");
		Log.e("car_brand=",carData.getCar_brand());
		Log.e("car_series=",carData.getCar_series());
		Log.e("car_type=",carData.getCar_type());
		Log.e("engine_no=",carData.getEngine_no());
		Log.e("frame_no=",carData.getFrame_no());
		Log.e("insurance_company=",carData.getInsurance_company());
		Log.e("insurance_date=",carData.getInsurance_date());
		Log.e("annual_inspect_date=",carData.getAnnual_inspect_date());
		Log.e("maintain_company=",carData.getMaintain_company());
		Log.e("maintain_last_mileage=",carData.getMaintain_last_mileage());
		Log.e("maintain_next_mileage=",carData.getMaintain_next_mileage());
		Log.e("buy_date=",carData.getBuy_date());
		Log.e("obj_name=",carData.getObj_name());
		
	}
}