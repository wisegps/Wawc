package com.wise.wawc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.service.MaintainAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MaintainShopActivity extends Activity {
	private ListView maintainList = null;
	private MaintainAdapter maintainAdapter = null;
	private Intent intent = null;
	private int code = 0;
	private String brank = "";
	ProgressDialog progressDialog = null;
	private MyHandler myHandler = null;
	private static final int getMaintainShopCode = 2;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_maintain);
		maintainList = (ListView) findViewById(R.id.maintain_list);

		intent = getIntent();
		code = intent.getIntExtra("code", 0);
		brank = (String) intent.getSerializableExtra("brank");
		
		myHandler = new MyHandler();
		progressDialog = ProgressDialog.show(MaintainShopActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		progressDialog.setCancelable(true);
		SharedPreferences sharedPreferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
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
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/dealer?city_spell=" + sharedPreferences.getString(Constant.FourShopParmeter, "") + "&brand=" + URLBrank, getMaintainShopCode)).start();
		}
		
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			switch(msg.what){
			case getMaintainShopCode:
				progressDialog.dismiss();
				Log.e("4s保养店商家:",msg.obj+"");
				
				
				maintainAdapter = new MaintainAdapter(MaintainShopActivity.this);
				maintainList.setAdapter(maintainAdapter);
				maintainList.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
						TextView textView = (TextView)arg0.getChildAt(arg2).findViewById(R.id.vehicle_maintain_name);
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
				break;
			default:
				return;
			}
			super.handleMessage(msg);
		}
	}
}
