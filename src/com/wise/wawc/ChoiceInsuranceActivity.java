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
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/insurance", getInsuranceCode)).start();
	}
	
	private void getData() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{"carBrank"});
		 JSONArray jsonArray = null;
		if(cursor.moveToFirst()){
			progressDialog.dismiss();
			try {
				jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("Content")));
				Log.e("数据库获取的诗数据：",jsonArray.length()+"");
//				parseJSON(jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
//			new Thread(new NetThread.GetDataThread(myHandler, Config.BaseUrl + "base/car_brand", GET_BRANK)).start();
		}
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case getInsuranceCode :
				progressDialog.dismiss();
				String insurance = msg.obj.toString();
				int insuranceLength = insurance.length();
				Log.e("服务器的保险公司：",msg.obj.toString());
				
				try {
					JSONArray jsonArray = new JSONArray(insurance);
					for(int i = 0 ; i < insuranceLength ; i ++){
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Log.e("保险公司：",jsonObject.getString("name"));
						dateList.add(jsonObject.getString("name"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, dateList);
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
				break;
			default:
				return;
			}
		}
	}
	public void onRefresh() {
	}
	public void onLoadMore() {
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
}
