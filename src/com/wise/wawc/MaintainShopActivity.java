package com.wise.wawc;

import com.wise.service.MaintainAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MaintainShopActivity extends Activity {
	private ListView maintainList = null;
	private MaintainAdapter maintainAdapter = null;
	private Intent intent = null;
	private int code = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_maintain);
		maintainList = (ListView) findViewById(R.id.maintain_list);
		maintainAdapter = new MaintainAdapter(MaintainShopActivity.this);
		maintainList.setAdapter(maintainAdapter);

		intent = getIntent();
		code = intent.getIntExtra("code", 0);
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
	}
}
