package com.wise.wawc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.MaintainAdapter;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 4s店
 * @author Mr'wang
 */
public class MaintainShopActivity extends Activity {
	private ListView maintainList = null;
	private MaintainAdapter maintainAdapter = null;
	private Intent intent = null;
	private int code = 0;
	private String brank = "";
	ProgressDialog progressDialog = null;
	private MyHandler myHandler = null;
	private static final int getMaintainShopCode = 2;
	private SharedPreferences sharedPreferences = null;
	private List<String> MaintainList = new ArrayList<String>();
	private ImageView choiceMaintainCancle = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_maintain);
		maintainList = (ListView) findViewById(R.id.maintain_list);
		choiceMaintainCancle = (ImageView) findViewById(R.id.choice_maintain_cancle);
		choiceMaintainCancle.setOnClickListener(new ClickListener());
		intent = getIntent();
		code = intent.getIntExtra("code", 0);
		brank = (String) intent.getSerializableExtra("brank");
		myHandler = new MyHandler();
		progressDialog = ProgressDialog.show(MaintainShopActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		progressDialog.setCancelable(true);
		sharedPreferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		if("".equals(sharedPreferences.getString(Constant.FourShopParmeter, "")) || "".equals(brank)){
			progressDialog.dismiss();
			Toast.makeText(getApplicationContext(), "城市或者品牌为选择", 0).show();
			MaintainShopActivity.this.finish();
			return;
		}else{
			String URLBrank = "";
			try {
				URLBrank = URLEncoder.encode(brank, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//查询数据库
			DBHelper dBHelper = new DBHelper(MaintainShopActivity.this);
			SQLiteDatabase reader = dBHelper.getReadableDatabase();
			Cursor cursor = reader.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{"maintain/" + sharedPreferences.getString("Constant.FourShopParmeter", "") + "/" + brank});
			if(cursor.moveToFirst()){
				parseJSON(cursor.getString(cursor.getColumnIndex("Content")));
				progressDialog.dismiss();
				Log.e("查询数据库","查询数据库");
			}else{
				Log.e("查询服务器","查询服务器");
				new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/dealer?city_spell=" + sharedPreferences.getString(Constant.FourShopParmeter, "") + "&brand=" + URLBrank, getMaintainShopCode)).start();
			}
		}
		
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			switch(msg.what){
			case getMaintainShopCode:
				progressDialog.dismiss();
				Log.e("4s保养店商家:",msg.obj+"");
				//存在数据库
				if(!"[]".equals(msg.obj.toString())){
					DBExcute dBExcute = new DBExcute();
					ContentValues values = new ContentValues();
					values.put("Cust_id", Variable.cust_id);
					values.put("Title", "maintain/" + sharedPreferences.getString("Constant.FourShopParmeter", "") + "/" + brank);
					values.put("Content", msg.obj.toString());
					dBExcute.InsertDB(MaintainShopActivity.this, values, Constant.TB_Base);
					parseJSON(msg.obj.toString());
				}
				
				break;
			default:
				return;
			}
			super.handleMessage(msg);
		}
	}
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.choice_maintain_cancle:
				MaintainShopActivity.this.finish();
				break;
			case R.id.search_maintain_shop:
				break;
			}
		}
	}
	public void parseJSON(String jsonData){
		try {
			MaintainList.clear();
			JSONArray jsonArray = new JSONArray(jsonData);
			for(int i = 0 ; i < jsonArray.length() ; i ++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				MaintainList.add(jsonObject.getString("name"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		maintainAdapter = new MaintainAdapter(MaintainShopActivity.this,MaintainList);
		maintainList.setAdapter(maintainAdapter);
		maintainList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
//				TextView textView = (TextView)arg0.getChildAt(arg2).findViewById(R.id.vehicle_maintain_name);
				TextView textView = (TextView)maintainList.getChildAt(arg2).findViewById(R.id.vehicle_maintain_name);
				Intent intents = new Intent();
				intents.putExtra("maintain", textView.getText().toString());
				if(code == MyVehicleActivity.resultCodeMaintain){
					MaintainShopActivity.this.setResult(MyVehicleActivity.resultCodeMaintain, intents);
				}else if(code == NewVehicleActivity.newVehicleMaintain){
					Log.e("选择的item",intents.getSerializableExtra("maintain").toString());
					MaintainShopActivity.this.setResult(NewVehicleActivity.newVehicleMaintain, intents);
				}
				MaintainShopActivity.this.finish();
			}
		});
	}
}

