package com.wise.wawc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wise.pubclas.Constant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.NetThread;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 保险公司选择
 * @author Mr.Wang
 */
public class ChoiceInsuranceActivity extends Activity implements IXListViewListener{
	private XListView insuranceList = null;
	private ArrayAdapter<String> adapter = null;
	private List<String> dateList = null;
	private int code = 0;
	
	private Intent intetn = null;
	private ProgressDialog progressDialog = null;
	private MyHandler myHandler = null;
	private static final int getInsuranceCode = 1;
	private static final int refreshInsurance = 2;
	
	private DBExcute dBExcute = null;
	private DBHelper dbHelper = null;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_insurance_company);
		
		intetn = getIntent();
		insuranceList = (XListView) findViewById(R.id.insurance_company_list);
		dateList = new ArrayList<String>();
		myHandler = new MyHandler();
		insuranceList.setXListViewListener(this);
		//不设置上拉加载无效
		insuranceList.setPullLoadEnable(true);
		
		dbHelper = new DBHelper(this);
		dBExcute = new DBExcute();
		
		progressDialog = ProgressDialog.show(ChoiceInsuranceActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		code = intetn.getIntExtra("code", 0);
		
		getData();
		
	}
	
	private void getData() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{"insurance"});
		 JSONArray jsonArray = null;
		if(cursor.moveToFirst()){
			progressDialog.dismiss();
			try {
				jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("Content")));
				Log.e("数据库获取的诗数据：",jsonArray.length()+"");
				setListData(jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/insurance", getInsuranceCode)).start();
		}
	}
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case getInsuranceCode :
				progressDialog.dismiss();
				String insurance = msg.obj.toString();
				Log.e("服务器的保险公司：",msg.obj.toString());
				
				if(!"".equals(insurance)){
					ContentValues contentValues = new ContentValues();
					contentValues.put("Title", "insurance");
					contentValues.put("Content", insurance);
					dBExcute.InsertDB(ChoiceInsuranceActivity.this, contentValues, Constant.TB_Base);
					try {
						JSONArray jsonArray = new JSONArray(insurance);
						int insuranceLength = jsonArray.length();
						for(int i = 0 ; i < insuranceLength ; i ++){
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							Log.e("保险公司：",jsonObject.getString("name"));
							dateList.add(jsonObject.getString("name"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败，稍后再试", 0).show();
				}
				setListData(dateList);
				break;
				
			case refreshInsurance:
				onLoad();
				String refInsurance = msg.obj.toString();
				Log.e("刷新得到服务器的保险公司：",msg.obj.toString());
				
				if(!"".equals(refInsurance)){
					ContentValues contentValues = new ContentValues();
					contentValues.put("Title", "insurance");
					contentValues.put("Content", refInsurance);
					//TODO 更新数据库
//					dBExcute.InsertDB(ChoiceInsuranceActivity.this, contentValues, Constant.TB_Base);
					try {
						JSONArray jsonArray = new JSONArray(refInsurance);
						int insuranceLength = jsonArray.length();
						for(int i = 0 ; i < insuranceLength ; i ++){
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							Log.e("保险公司：",jsonObject.getString("name"));
							dateList.add(jsonObject.getString("name"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败，稍后再试", 0).show();
				}
				setListData(dateList);
				break;
			default:
				return;
			}
		}
	}
	
	private void setListData(Object obj) {
		List<String> data = null;
		if(obj instanceof List){
			data = (List<String>) obj;
		}else if(obj instanceof JSONArray){
			try {
				data = new ArrayList<String>();
				JSONArray jsonArray = (JSONArray) obj;
				int length = jsonArray.length();
				for(int i = 0 ; i < length ; i ++){
					data.add(jsonArray.getJSONObject(i).getString("name"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, data);
		insuranceList.setAdapter(adapter);
		insuranceList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				String text = (String) arg0.getItemAtPosition(arg2);
				Intent intent = new Intent();
				intent.putExtra("ClickItem", text);
				if(code == NewVehicleActivity.newVehicleInsurance){
					ChoiceInsuranceActivity.this.setResult(NewVehicleActivity.newVehicleInsurance, intent);
				}else if(code == MyVehicleActivity.resultCodeInsurance){
					ChoiceInsuranceActivity.this.setResult(MyVehicleActivity.resultCodeInsurance, intent);
				}
				ChoiceInsuranceActivity.this.finish();
				}
			});
		
	}
	
	public void onRefresh() {
//		new Handler(){
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				onLoad();
//			}
//		}.sendMessageDelayed(new Message(), 1000);
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/insurance", refreshInsurance)).start();
	}
	public void onLoadMore() {
		new Handler(){
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				onLoad();
			}
		}.sendMessageDelayed(new Message(), 1000);
	}
	public void PullUp() {
	}
	
	private void onLoad() {
		//获取当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String temp = sdf.format(new Date());
		String date = temp.substring(5, 16);
		insuranceList.stopRefresh();
		insuranceList.stopLoadMore();
		insuranceList.setRefreshTime(date);
	}
	
	protected void onDestroy() {
		Constant.isHideFooter = false;
		super.onDestroy();
	}
	protected void onPause() {
		Constant.isHideFooter = false;
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Constant.isHideFooter = true;
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Constant.isHideFooter = true;
		super.onResume();
	}
}
