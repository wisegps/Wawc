package com.wise.wawc;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 我的账户
 * @author honesty
 */
public class AccountActivity extends Activity{
    private static final String TAG = "AccountActivity";
    private static final int Get_data = 1;
    private static final int Update_data = 2;
    
	private View view = null;
	EditText et_activity_account_consignee,et_activity_account_adress,et_activity_account_phone;
	TextView tv_activity_account_name,tv_activity_city;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		view = findViewById(R.id.account_to_my_vehicle);
		view.setOnClickListener(onClickListener);
		tv_activity_account_name = (TextView)findViewById(R.id.tv_activity_account_name);
		tv_activity_city = (TextView)findViewById(R.id.tv_activity_city);
		ImageView iv_activity_account_menu = (ImageView)findViewById(R.id.iv_activity_account_menu);
		iv_activity_account_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_account_home = (ImageView)findViewById(R.id.iv_activity_account_home);
		iv_activity_account_home.setOnClickListener(onClickListener);
		Button bt_activity_account_logout = (Button)findViewById(R.id.bt_activity_account_logout);
		bt_activity_account_logout.setOnClickListener(onClickListener);
		Button bt_activity_account_save = (Button)findViewById(R.id.bt_activity_account_save);
		bt_activity_account_save.setOnClickListener(onClickListener);
		et_activity_account_consignee = (EditText)findViewById(R.id.et_activity_account_consignee);
		et_activity_account_adress = (EditText)findViewById(R.id.et_activity_account_adress);
		et_activity_account_phone = (EditText)findViewById(R.id.et_activity_account_phone);
		ShareSDK.initSDK(this);
		GetSfData();
	}
	OnClickListener onClickListener = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.iv_activity_account_menu:				
				ActivityFactory.A.LeftMenu();
				finish();
				break;
			case R.id.iv_activity_account_home:
				ActivityFactory.A.ToHome();
				finish();
				break;
			case R.id.account_to_my_vehicle:
				startActivity(new Intent(AccountActivity.this,MyVehicleActivity.class));
				break;
			case R.id.bt_activity_account_logout:
				Platform platformQQ = ShareSDK.getPlatform(AccountActivity.this,QZone.NAME);
				Platform platformSina = ShareSDK.getPlatform(AccountActivity.this,SinaWeibo.NAME);
				platformQQ.removeAccount();
				platformSina.removeAccount();
				Constant.qqUserName = "";
				finish();
				break;
			case R.id.bt_activity_account_save:
			    saveData();
			    break;
			}
		}
	};
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_data:
                jsonAccount(msg.obj.toString());
                break;

            case Update_data:
                Log.d(TAG, msg.obj.toString());
                break;
            }
        }	    
	};
	/**
	 * 解析数据，显示并存储
	 * @param result
	 */
	private void jsonAccount(String result){
	    Log.d(TAG, result);
	    try {
	        SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
            Editor editor = preferences.edit();
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.opt("contacts") != null){
                String contacts = jsonObject.getString("contacts");
                et_activity_account_consignee.setText(contacts);                
                editor.putString(Constant.Consignee, contacts);
            }
            if(jsonObject.opt("address") != null){
                String address = jsonObject.getString("address");
                et_activity_account_adress.setText(address);
                editor.putString(Constant.Adress, address);
            }
            if(jsonObject.opt("tel") != null){
                String tel = jsonObject.getString("tel");
                et_activity_account_phone.setText(tel);
                editor.putString(Constant.Phone, tel);
            }
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	/**
	 * 获取本地数据
	 */
	private void GetSfData(){
	    SharedPreferences preferences = getSharedPreferences(
                Constant.sharedPreferencesName, Context.MODE_PRIVATE);
	    et_activity_account_consignee.setText(preferences.getString(Constant.Consignee, ""));
	    et_activity_account_adress.setText(preferences.getString(Constant.Adress, ""));
	    et_activity_account_phone.setText(preferences.getString(Constant.Phone, ""));	    
	    tv_activity_account_name.setText(Variable.cust_name);
	    
	    String LocationProvince = preferences.getString(Constant.LocationProvince, "");
	    String LocationCity = preferences.getString(Constant.LocationCity, "");
	    if(LocationProvince.equals(LocationCity)){
	        tv_activity_city.setText(LocationCity);
	    }else{
	        tv_activity_city.setText(LocationProvince + "   " + LocationCity);
	    }
	    
	    String url = Constant.BaseUrl + "customer/" + Variable.cust_id +"?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url, Get_data)).start();
	}
	/**
	 * 保存数据
	 */
	private void saveData(){
	    String consignee = et_activity_account_consignee.getText().toString().trim();
	    String adress = et_activity_account_adress.getText().toString().trim();
	    String phone = et_activity_account_phone.getText().toString().trim();
	    String url = Constant.BaseUrl + "customer/" + Variable.cust_id +"?auth_code=" + Variable.auth_code;
        Log.d(TAG, url);
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
        params.add(new BasicNameValuePair("id_card_type", "B"));
        params.add(new BasicNameValuePair("contacts", consignee));
        params.add(new BasicNameValuePair("address", adress));
        params.add(new BasicNameValuePair("tel", phone));
        
        new Thread(new NetThread.putDataThread(handler, url, params, Update_data)).start();
	}
}
