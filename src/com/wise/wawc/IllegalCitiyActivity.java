package com.wise.wawc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.CharacterParser;
import com.wise.data.IllegalCity;
import com.wise.data.ProvinceModel;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.IllegalProvinceAdapter;
import com.wise.sql.DBExcute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

/**
 *  选择省份
 * @author Mr'Wang
 */
public class IllegalCitiyActivity extends Activity {
	ListView provinceListView;
	ListView cityListView;
	IllegalProvinceAdapter adapter;
	ProgressDialog myDialog;
	DBExcute dbExcute;
	static List<ProvinceModel> illegalList;
	ImageView back;
	private int requestCode = 0;
	public static final String showProvinceAction = "province";
	public static final String showCityAction = "city";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.illegal_province_activity);
		provinceListView = (ListView) findViewById(R.id.illegal_provnice_list);
		cityListView = (ListView) findViewById(R.id.illegal_city_list);
		back = (ImageView) findViewById(R.id.illegal_province_back);
		dbExcute = new DBExcute();
		requestCode = getIntent().getIntExtra("requestCode", 0);
		
		//相关监听
		provinceListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//选择省份之后选择城市
				Variable.illegalList = Variable.illegalProvinceList.get(arg2).getIllegalCityList();
				provinceListView.setVisibility(View.GONE);
				adapter = new IllegalProvinceAdapter(null, IllegalCitiyActivity.this,showCityAction,Variable.illegalList);
				cityListView.setAdapter(adapter);
				cityListView.setVisibility(View.VISIBLE);
			}
		});
		cityListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent = new Intent(IllegalCitiyActivity.this,MyVehicleActivity.class);
				intent.putExtra("IllegalCity", Variable.illegalList.get(arg2));
				IllegalCitiyActivity.this.setResult(requestCode, intent);
				IllegalCitiyActivity.this.finish();
			}
		});
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(cityListView.getVisibility() == View.VISIBLE){
					cityListView.setVisibility(View.GONE);
					provinceListView.setVisibility(View.VISIBLE);
				}else{
					IllegalCitiyActivity.this.finish();
				}
			}
		});
		adapter = new IllegalProvinceAdapter(Variable.illegalProvinceList, IllegalCitiyActivity.this,showProvinceAction,null);
		provinceListView.setAdapter(adapter);
		
	}
}
