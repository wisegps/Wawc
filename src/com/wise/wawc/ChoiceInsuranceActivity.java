package com.wise.wawc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.NetThread;
import com.wise.service.InsuranceAdapter;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 保险公司选择
 * @author Mr.Wang
 */
public class ChoiceInsuranceActivity extends Activity implements IXListViewListener{
	private XListView insuranceList = null;
	private InsuranceAdapter adapter = null;
	private List<String[]> dateList = null;
	private int code = 0;
	private ImageView choiceInsuranceCancle = null;
	
	private Intent intetn = null;
	private ProgressDialog progressDialog = null;
	private MyHandler myHandler = null;
	private static final int getInsuranceCode = 1;
	private static final int refreshInsurance = 2;
	
	private DBExcute dBExcute = null;
	private DBHelper dbHelper = null;
	private static final String insurance_title_name = "insurance";
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_insurance_company);
		
		intetn = getIntent();
		insuranceList = (XListView) findViewById(R.id.insurance_company_list);
		choiceInsuranceCancle = (ImageView) findViewById(R.id.choice_insurance_cancle);
		choiceInsuranceCancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ChoiceInsuranceActivity.this.finish();
			}
		});
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
		 Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{insurance_title_name});
		 JSONArray jsonArray = null;
		if(cursor.moveToFirst()){
			progressDialog.dismiss();
			try {
				jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("Content")));
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
				JSONArray jsonArray = null;
				Log.e("服务器的保险公司：",msg.obj.toString());
				//存储到数据库
				if(!"".equals(insurance)){
					ContentValues contentValues = new ContentValues();
					contentValues.put("Cust_id", Variable.cust_id);
					contentValues.put("Title", insurance_title_name);
					contentValues.put("Content", insurance);
					dBExcute.InsertDB(ChoiceInsuranceActivity.this, contentValues, Constant.TB_Base);
					try {
						jsonArray = new JSONArray(insurance);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败，稍后再试", 0).show();
				}
				setListData(jsonArray);
				break;
				
			case refreshInsurance:
				onLoad();
				String refInsurance = msg.obj.toString();
				if(!"".equals(refInsurance)){
					try {
						JSONArray newDatas = new JSONArray(refInsurance);
						//更新数据库
						ContentValues values = new ContentValues();
						values.put("Cust_id", Variable.cust_id);
						values.put("Ttitle", insurance_title_name);
						values.put("Content", refInsurance);
						dBExcute.UpdateDB(ChoiceInsuranceActivity.this, values, "Title=?", new String[]{insurance_title_name}, Constant.TB_Base);
						setListData(newDatas);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					Toast.makeText(getApplicationContext(), "刷新失败", 0).show();
				}
				break;
			default:
				return;
			}
		}
	}
	
	private void setListData(JSONArray obj) {
		dateList = new ArrayList<String[]>();
		try {
			for(int i = 0 ; i < obj.length() ; i ++){
				String[] str = new String[2];
				str[0] = obj.getJSONObject(i).getString("name");
				str[1] = obj.getJSONObject(i).getString("service_phone");
				dateList.add(str);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		adapter = new InsuranceAdapter(getApplicationContext(), dateList);
		insuranceList.setAdapter(adapter);
		insuranceList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				String name = dateList.get(arg2)[0];
				Log.e("保险公司名字：",name);
				String phone = dateList.get(arg2)[0];
				Log.e("保险公司电话：",phone);
				Intent intent = new Intent();
				intent.putExtra("insurance_name", name);
				intent.putExtra("insurance_phone", phone);
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
