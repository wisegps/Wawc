package com.wise.wawc;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.CarData;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
	TextView tv_activity_account_name,tv_activity_city,tv_carBrand,tv_carNumber;
	ImageView iv_activity_account_pic,iv_user_car_logo;
	boolean isJump = true;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_account);
		view = findViewById(R.id.account_to_my_vehicle);
		view.setOnClickListener(onClickListener);
		iv_activity_account_pic = (ImageView)findViewById(R.id.iv_activity_account_pic);
		iv_user_car_logo = (ImageView)findViewById(R.id.iv_user_car_logo);
		tv_activity_account_name = (TextView)findViewById(R.id.tv_activity_account_name);
		tv_activity_city = (TextView)findViewById(R.id.tv_activity_city);
		tv_carBrand = (TextView)findViewById(R.id.tv_carBrand);
		tv_carNumber = (TextView)findViewById(R.id.tv_carNumber);
		ImageView iv_activity_account_menu = (ImageView)findViewById(R.id.iv_activity_account_menu);
		iv_activity_account_menu.setOnClickListener(onClickListener);
		Button bt_activity_account_logout = (Button)findViewById(R.id.bt_activity_account_logout);
		bt_activity_account_logout.setOnClickListener(onClickListener);
		et_activity_account_consignee = (EditText)findViewById(R.id.et_activity_account_consignee);
		et_activity_account_adress = (EditText)findViewById(R.id.et_activity_account_adress);
		et_activity_account_phone = (EditText)findViewById(R.id.et_activity_account_phone);
		ShareSDK.initSDK(this);
		GetDBData();
        GetCarData();
        
        Intent intent = getIntent();
        isJump = intent.getBooleanExtra("isJump", false);
        System.out.println("isJump = " + isJump);
        if (isJump) {
            iv_activity_account_menu.setImageResource(R.drawable.nav_back);
        } else {
            iv_activity_account_menu.setImageResource(R.drawable.side_left);
        }
	}
	OnClickListener onClickListener = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.iv_activity_account_menu:	
			    saveData();
			    if (isJump) {
                    finish();
                } else {
                    ActivityFactory.A.LeftMenu();
                }
				break;
			case R.id.account_to_my_vehicle:
				startActivityForResult(new Intent(AccountActivity.this, CarSelectActivity.class), 0);
				break;
			case R.id.bt_activity_account_logout:
				Platform platformQQ = ShareSDK.getPlatform(AccountActivity.this,QZone.NAME);
				Platform platformSina = ShareSDK.getPlatform(AccountActivity.this,SinaWeibo.NAME);
				platformQQ.removeAccount();
				platformSina.removeAccount();
				removeData();
				finish();
				startActivity(new Intent(AccountActivity.this, WelcomeActivity.class));
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
            JSONObject jsonObject = new JSONObject(result);
            DBExcute dbExcute = new DBExcute();
            ContentValues values = new ContentValues();
            if(jsonObject.opt("contacts") != null){
                String contacts = jsonObject.getString("contacts");
                et_activity_account_consignee.setText(contacts);
                values.put("Consignee", contacts);
            }
            if(jsonObject.opt("address") != null){
                String address = jsonObject.getString("address");
                et_activity_account_adress.setText(address);
                values.put("Adress", address);
            }
            if(jsonObject.opt("tel") != null){
                String tel = jsonObject.getString("tel");
                et_activity_account_phone.setText(tel);
                values.put("Phone", tel);
            }
            if(jsonObject.opt("annual_inspect_date") != null){
                String annual_inspect_date = jsonObject.getString("annual_inspect_date");
                values.put("annual_inspect_date", GetSystem.ChangeTimeZone(annual_inspect_date.replace("T", " ").substring(0, 19)));
            }
            if(jsonObject.opt("change_date") != null){
                String change_date = jsonObject.getString("change_date");
                values.put("change_date", GetSystem.ChangeTimeZone(change_date.replace("T", " ").substring(0, 19)));
            }
            values.put("cust_id", Variable.cust_id);
            dbExcute.InsertDB(AccountActivity.this, values, Constant.TB_Account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	/**
	 * 获取本地数据
	 */
	private void GetDBData(){
	    DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Account + " where cust_id=?", new String[]{Variable.cust_id});
        if(cursor.getCount() == 0){
            String url = Constant.BaseUrl + "customer/" + Variable.cust_id +"?auth_code=" + Variable.auth_code;
            new Thread(new NetThread.GetDataThread(handler, url, Get_data)).start();
        }else{
            if(cursor.moveToFirst()){
                String Consignee = cursor.getString(cursor.getColumnIndex("Consignee"));
                String Adress = cursor.getString(cursor.getColumnIndex("Adress"));
                String Phone = cursor.getString(cursor.getColumnIndex("Phone"));
                et_activity_account_consignee.setText(Consignee);
                et_activity_account_adress.setText(Adress);
                et_activity_account_phone.setText(Phone); 
            }                
        }
        cursor.close();
        db.close();
	}
	/**
	 * 获取本地数据
	 */
	private void GetSfData(){
	    Bitmap bimage = BitmapFactory.decodeFile(Constant.userIconPath + Constant.UserImage);
        if(bimage != null){            
            iv_activity_account_pic.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
        }
	    SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
	    tv_activity_account_name.setText(Variable.cust_name);
	    
	    String LocationProvince = preferences.getString(Constant.LocationProvince, "");
	    String LocationCity = preferences.getString(Constant.LocationCity, "");
	    if(LocationProvince.equals(LocationCity)){
	        tv_activity_city.setText(LocationCity);
	    }else{
	        tv_activity_city.setText(LocationProvince + "，" + LocationCity);
	    }
	}
	/**
	 * 获取车辆数据
	 */
	private void GetCarData(){
	    for(CarData carData : Variable.carDatas){
	        if(carData.isCheck){
	            //显示我的爱车
	            Bitmap bimage = BitmapFactory.decodeFile(carData.getLogoPath());
	            if(bimage != null){            
	                iv_user_car_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
	            }
	            tv_carBrand.setText(carData.getCar_brand() + carData.getCar_series());
	            tv_carNumber.setText(carData.getObj_name());
	            break;
	        }
	    }
	}
	/**
	 * 保存数据
	 */
	private void saveData(){
	    String consignee = et_activity_account_consignee.getText().toString().trim();
	    String adress = et_activity_account_adress.getText().toString().trim();
	    String phone = et_activity_account_phone.getText().toString().trim();
	    //更新服务器信息
	    String url = Constant.BaseUrl + "customer/" + Variable.cust_id +"?auth_code=" + Variable.auth_code;
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
        params.add(new BasicNameValuePair("id_card_type", "B"));
        params.add(new BasicNameValuePair("contacts", consignee));
        params.add(new BasicNameValuePair("address", adress));
        params.add(new BasicNameValuePair("tel", phone));        
        new Thread(new NetThread.putDataThread(handler, url, params, Update_data)).start();
        //更新DB
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Consignee", consignee);
        values.put("adress", adress);
        values.put("phone", phone);
        dbExcute.UpdateDB(this, values, "cust_id=?", new String[]{Variable.cust_id}, Constant.TB_Account);
	}
	/**
	 * 删除数据
	 */
	private void removeData(){
	    SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();               
        editor.putString(Constant.Consignee, "");
        editor.putString(Constant.Adress, "");
        editor.putString(Constant.Phone, "");
        editor.putString(Constant.sp_cust_id, "");
        editor.putString(Constant.platform, "");
        editor.commit();
	}
	@Override
	protected void onResume() {
	    super.onResume();
        GetSfData();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        saveData();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(resultCode == 1){
            int car_id = data.getIntExtra("Obj_id", 0);
            String Obj_name = data.getStringExtra("Obj_name");
            for(int i = 0 ; i < Variable.carDatas.size() ; i++){
                if(Variable.carDatas.get(i).getObj_id() == car_id){                    
                    SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
                    Editor editor = preferences.edit();
                    editor.putInt(Constant.DefaultVehicleID, i);
                    editor.commit();
                    Variable.carDatas.get(i).setCheck(true);
                    
                    //显示我的爱车
                    Bitmap bimage = BitmapFactory.decodeFile(Variable.carDatas.get(i).getLogoPath());
                    if(bimage != null){            
                        iv_user_car_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
                    }
                    tv_carBrand.setText(Variable.carDatas.get(i).getCar_brand() + Variable.carDatas.get(i).getCar_series());
                    tv_carNumber.setText(Variable.carDatas.get(i).getObj_name());
                    
                }else{
                    Variable.carDatas.get(i).setCheck(false);
                }
            }
        }
	}
}
