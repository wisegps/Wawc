package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChoiceInsuranceActivity extends Activity {
	private ListView insuranceList = null;
	private ArrayAdapter<String> adapter = null;
	private List<String> dateList = null;
	
	private Intent intetn = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_insurance_company);
		
		intetn = getIntent();
		insuranceList = (ListView) findViewById(R.id.insurance_company_list);
		adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, getDate());
		insuranceList.setAdapter(adapter);
		insuranceList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int code = intetn.getIntExtra("code", 0);
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
	
	List<String> getDate(){
		dateList = new ArrayList<String>();
		for(int i = 1 ; i <= 10 ; i ++){
			dateList.add("保险公司" + i);
		}
		return dateList;
	}
}
