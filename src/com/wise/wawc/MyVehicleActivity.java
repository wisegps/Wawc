package com.wise.wawc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private Button menu = null;
	private Button home = null;
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
	
	private GridView vehicleGridView = null;
	private LogoAdapter logoAdapter = null;
	
	
	private int[] images = new int[]{R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image,
			R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.new_vehicle};
	
	AlertDialog dlg = null;
	boolean isJump = false;//false从菜单页跳转过来返回打开菜单，true从首页跳转返回关闭页面
	
	private int width = 0 ; 
	
	private ImageView ivCarSeries = null; //车型
	private ImageView ivCarType = null;  //车款
	private TextView tvCarSeries = null;
	private TextView tvCarType = null;
	
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
		
		
		ivCarSeries = (ImageView) findViewById(R.id.iv_car_series);
		ivCarType = (ImageView) findViewById(R.id.iv_car_type);
		tvCarSeries = (TextView) findViewById(R.id.tv_car_series);
		tvCarType = (TextView) findViewById(R.id.tv_car_type);
		ivCarSeries.setOnClickListener(new ClickListener());
		ivCarType.setOnClickListener(new ClickListener());
		mSpinerPopWindow = new SpinerPopWindow(MyVehicleActivity.this);
		mSpinerPopWindow.setItemListener(this);
		
		myHandler = new MyHandler();
		dBhalper = new DBHelper(MyVehicleActivity.this);
		dBExcute = new DBExcute();
		
		
		int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(images.length * (px + 10),LayoutParams.WRAP_CONTENT);
		vehicleGridView = (GridView) findViewById(R.id.gv_my_vehicle);
		//汽车品牌Logo
		logoAdapter = new LogoAdapter(MyVehicleActivity.this,images);
		vehicleGridView.setAdapter(logoAdapter);
		vehicleGridView.setLayoutParams(params);
		vehicleGridView.setColumnWidth(px);
		vehicleGridView.setHorizontalSpacing(6);
		vehicleGridView.setStretchMode(GridView.NO_STRETCH);
		vehicleGridView.setNumColumns(images.length);
		vehicleGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if((images.length - 1) == arg2){
					startActivity(new Intent(MyVehicleActivity.this,NewVehicleActivity.class));
				}
			}
		});
		

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
}
