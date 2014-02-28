package com.wise.wawc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.IllegalCity;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.IllegalAdapter;
import com.wise.sql.DBExcute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

/**
 *  不同城市的违章查询所需信息
 * @author Mr'Wang
 */
public class IllegalCitiyActivity extends Activity {
	ListView listView;
	IllegalAdapter adapter;
	MyHandler myHandler;
	ProgressDialog myDialog;
	DBExcute dbExcute;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.illegal_city);
		listView = (ListView) findViewById(R.id.illegal_list);
		myHandler = new MyHandler();
		dbExcute = new DBExcute();
		
		//初始化数据 
		initData();
		adapter = new IllegalAdapter();
	}
	
	
	public void  initData(){
		//判断数据库是否存在数据
		myDialog = ProgressDialog.show(IllegalCitiyActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		myDialog.setCancelable(true);
		List<IllegalCity> illegalCity = dbExcute.selectIllegal(IllegalCitiyActivity.this);
		if(illegalCity == null){
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl+"/violation/city?cuth_code=" + Variable.auth_code, 0)).start();
		}else{
			
		}
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			myDialog.dismiss();
			//存储到数据库
			List<IllegalCity> illegalList = parseJson(msg.obj.toString());
			
		}
	}
	
	public static List<IllegalCity> parseJson(String jsonData){
		List<IllegalCity> cityList = new ArrayList<IllegalCity>();
		try {
			JSONObject jsonObj = new JSONObject(jsonData);
			JSONObject result = jsonObj.getJSONObject("result");
			Iterator it = result.keys();
			while(it.hasNext()){
				String key = it.next().toString();
				JSONObject jsonObject = result.getJSONObject(key);
				JSONArray jsonArray = jsonObject.getJSONArray("citys");
				IllegalCity illegalCity = new IllegalCity();
				for(int i = 0 ; i < jsonArray.length() ; i ++){
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
					Log.e("city_code:",jsonObject3.getString("city_code"));
				}
				cityList.add(illegalCity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return cityList;
	}
}
