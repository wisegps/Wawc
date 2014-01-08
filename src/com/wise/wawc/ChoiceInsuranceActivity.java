package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import com.wise.pubclas.Config;
import com.wise.pubclas.NetThread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
public class ChoiceInsuranceActivity extends Activity {
	private ListView insuranceList = null;
	private ArrayAdapter<String> adapter = null;
	private List<String> dateList = null;
	private int code = 0;
	
	private Intent intetn = null;
	private ProgressDialog progressDialog = null;
	private MyHandler myHandler = null;
	private static final int getInsuranceCode = 1;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_insurance_company);
		
		intetn = getIntent();
		insuranceList = (ListView) findViewById(R.id.insurance_company_list);
		myHandler = new MyHandler();
		
		progressDialog = ProgressDialog.show(ChoiceInsuranceActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		new Thread(new NetThread.GetDataThread(myHandler, Config.BaseUrl + "insurance", getInsuranceCode)).start();
		
		code = intetn.getIntExtra("code", 0);
		
		
	}
	
	List<String> getDate(){
		dateList = new ArrayList<String>();
		for(int i = 1 ; i <= 10 ; i ++){
			dateList.add("保险公司" + i);
		}
		return dateList;
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case getInsuranceCode :
				progressDialog.dismiss();
				adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, getDate());
				insuranceList.setAdapter(adapter);
				insuranceList.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						
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
}
