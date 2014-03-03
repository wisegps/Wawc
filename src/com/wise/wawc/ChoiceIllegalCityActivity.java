package com.wise.wawc;

import com.wise.pubclas.Variable;
import com.wise.service.IllegalCityAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
/**
 * 选择城市
 * @author Mr.Wang
 */
public class ChoiceIllegalCityActivity extends Activity {
	ListView listView;
	IllegalCityAdapter adapter;
	int index = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_illegal_city);
		index = getIntent().getIntExtra("index", 0);
		listView = (ListView) findViewById(R.id.choice_illegal_city_list);
		adapter = new IllegalCityAdapter(Variable.illegalList, this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(ChoiceIllegalCityActivity.this,MyVehicleActivity.class);
				intent.putExtra("IllegalCity", Variable.illegalList.get(arg2));
				ChoiceIllegalCityActivity.this.startActivity(intent);
				ChoiceIllegalCityActivity.this.finish();
				Log.e("选择的城市：",Variable.illegalList.get(arg2).getCityName());
			}
		});
	}
}
