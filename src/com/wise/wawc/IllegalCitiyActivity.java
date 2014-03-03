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
	MyHandler myHandler;
	ProgressDialog myDialog;
	DBExcute dbExcute;
	static List<ProvinceModel> illegalList;
	ImageView back;
	static CharacterParser characterParser;    //将汉字转为拼音
	static PinyinComparator comparator;         //排序
	private int requestCode = 0;
	public static final String showProvinceAction = "province";
	public static final String showCityAction = "city";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.illegal_province_activity);
		provinceListView = (ListView) findViewById(R.id.illegal_provnice_list);
		cityListView = (ListView) findViewById(R.id.illegal_city_list);
		back = (ImageView) findViewById(R.id.illegal_province_back);
		myHandler = new MyHandler();
		dbExcute = new DBExcute();
		characterParser = new CharacterParser().getInstance();
		comparator = new PinyinComparator();
		requestCode = getIntent().getIntExtra("requestCode", 0);
		//初始化数据 
		initData();
		//省份点击监听
		provinceListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//选择省份之后选择城市
				Variable.illegalList = IllegalCitiyActivity.this.illegalList.get(arg2).getIllegalCityList();
				provinceListView.setVisibility(View.GONE);
				adapter = new IllegalProvinceAdapter(null, IllegalCitiyActivity.this,showCityAction,Variable.illegalList);
				cityListView.setAdapter(adapter);
				cityListView.setVisibility(View.VISIBLE);
			}
		});
		//城市点击监听
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
	}
	
	
	public void  initData(){
		//判断数据库是否存在数据
		myDialog = ProgressDialog.show(IllegalCitiyActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		myDialog.setCancelable(true);
		String jsonData = dbExcute.selectIllegal(IllegalCitiyActivity.this);
		if(jsonData == null){
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl+"/violation/city?cuth_code=" + Variable.auth_code, 0)).start();
		}else{
			//解析数据  并且更新
			adapter = new IllegalProvinceAdapter(parseJson(jsonData), IllegalCitiyActivity.this,showProvinceAction,null);
			provinceListView.setAdapter(adapter);
			myDialog.dismiss();
		}
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			myDialog.dismiss();
			//存储到数据库
			ContentValues values = new ContentValues();
			values.put("json_data",msg.obj.toString());
			dbExcute.InsertDB(IllegalCitiyActivity.this, values, Constant.TB_IllegalCity);
			initData();
		}
	}
	
	
	//获取省份   TODO
	public static List<ProvinceModel> parseJson(String jsonData){
		illegalList = new ArrayList<ProvinceModel>();
		try {
			JSONObject jsonObj = new JSONObject(jsonData);
			JSONObject result = jsonObj.getJSONObject("result");
			Iterator it = result.keys();
			while(it.hasNext()){
				List<IllegalCity> illegalCityList = new ArrayList<IllegalCity>();
				ProvinceModel provinceModel = new ProvinceModel();
				
				String key = it.next().toString();
				JSONObject jsonObject = result.getJSONObject(key);
				String province = jsonObject.getString("province");  //省份
				
				JSONArray jsonArray = jsonObject.getJSONArray("citys");  //城市
				for(int i = 0 ; i < jsonArray.length() ; i ++){
					IllegalCity illegalCity = new IllegalCity();
					JSONObject jsonObject3 = jsonArray.getJSONObject(i);
					illegalCity.setAbbr(jsonObject3.getString("abbr"));
					illegalCity.setCityCode(jsonObject3.getString("city_code"));
					illegalCity.setCityName(jsonObject3.getString("city_name"));
					illegalCity.setClassa(jsonObject3.getString("classa"));
					illegalCity.setEngine(jsonObject3.getString("engine"));
					illegalCity.setEngineno(jsonObject3.getString("engineno"));
					illegalCity.setRegist(jsonObject3.getString("regist"));
					illegalCity.setRegistno(jsonObject3.getString("registno"));
					illegalCity.setVehiclenum(jsonObject3.getString("class"));
					illegalCity.setVehiclenumno(jsonObject3.getString("classno"));
					illegalCityList.add(illegalCity);
				}
				provinceModel.setIllegalCityList(illegalCityList);
				provinceModel.setProvinceName(province);
				illegalList.add(provinceModel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//排序后返回
		return filledData(illegalList);
	}
	
	//将省份汉字转为拼音
	private static List<ProvinceModel> filledData(List<ProvinceModel> provinceModelList){
		for(int i=0; i<provinceModelList.size(); i++){
			ProvinceModel sortModel = provinceModelList.get(i);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(provinceModelList.get(i).getProvinceName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			sortModel.setProvinceLetter(sortString.toUpperCase());   //设置拼音
		}
		Collections.sort(provinceModelList, comparator);
		return provinceModelList;
	}
	
	
	//根据拼音首字母排序
	class PinyinComparator implements Comparator<ProvinceModel> {
		public int compare(ProvinceModel o1, ProvinceModel o2) {
			if (o1.getProvinceLetter().equals("@")
					|| o2.getProvinceLetter().equals("#")) {
				return -1;
			} else if (o1.getProvinceLetter().equals("#")
					|| o2.getProvinceLetter().equals("@")) {
				return 1;
			} else {
				return o1.getProvinceLetter().compareTo(o2.getProvinceLetter());
			}
		}
	}
}
