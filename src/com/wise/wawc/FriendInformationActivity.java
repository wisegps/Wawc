package com.wise.wawc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.Article;
import com.wise.data.BrankModel;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.BrankAdapter;
import com.wise.service.SeriesAdapter;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import com.wise.wawc.ChoiceCarInformationActivity.MyThread;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.GetChars;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 好友资料
 * @author 王庆文
 */
public class FriendInformationActivity extends Activity{
	ImageView back;
	ImageView userHead = null;
	TextView userName = null;
	ImageView carBrandIv = null;
	TextView carBrand = null;  //品牌
	TextView carPlate = null;   //  牌照
	TextView friendCar = null;
	RelativeLayout carInfo = null;
	
	String[] carInfor = null;
	String cust_id = "";
	String user_logo = "";
	String user_name = "";
	List<String[]> carInforList = new ArrayList<String[]>();
	DBHelper dbHelper = null;
	static final int getBarBrand = 2;
	MyHandler myHandler;
	ProgressDialog progressDialog;
	List<BrankModel> brankList;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_information);
		back = (ImageView) findViewById(R.id.friend_information_back);
		userHead = (ImageView) findViewById(R.id.friend_information_head);
		userName = (TextView) findViewById(R.id.friend_information_name);
		carBrandIv = (ImageView) findViewById(R.id.friend_information_car_brand_iv);
		carBrand = (TextView) findViewById(R.id.friend_information_car_brand_tv);
		carPlate = (TextView) findViewById(R.id.friend_information_car_plate_tv);
		carInfo = (RelativeLayout) findViewById(R.id.friend_car_info);
		friendCar = (TextView) findViewById(R.id.friend_car);
		
		
		myHandler = new MyHandler();
		cust_id = getIntent().getStringExtra("cust_id");
		user_logo = getIntent().getStringExtra("user_logo");
		user_name = getIntent().getStringExtra("user_name");
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FriendInformationActivity.this.finish();
			}
		});
		
		initDates();
	}
	private void initDates() {
		carInfo.setVisibility(View.VISIBLE);
		friendCar.setVisibility(View.VISIBLE);
		//  设置用户资料
		if(new File(Constant.userIconPath + cust_id + ".jpg").exists()){
			Bitmap userHeadBitmap = BitmapFactory.decodeFile(Constant.userIconPath + cust_id + ".jpg");
			userHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(userHeadBitmap));
		}else{
			new Thread(new Runnable() {
				public void run() {
					if(!"".equals(user_logo)){
						Bitmap tempBitmap = GetSystem.getBitmapFromURL(user_logo);
						if(tempBitmap != null){
							 GetSystem.saveImageSD(tempBitmap, Constant.userIconPath, cust_id + ".jpg");
							 userHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(tempBitmap));
						}
					}
				}
			}).start();
		}
		userName.setText(user_name);
		//设置用户车辆
		DBHelper dbHelper = new DBHelper(FriendInformationActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Vehicle + " where Cust_id=?", new String[] {cust_id });
        while(cursor.moveToNext()){
        	String[] str = new String[2];
        	str[0] = cursor.getString(cursor.getColumnIndex("car_brand"));
        	str[1] = cursor.getString(cursor.getColumnIndex("obj_name"));
        	carInforList.add(str);
        }
        //隐藏用户车辆信息  TODO
        if(carInforList.size() == 0){
        	carInfo.setVisibility(View.GONE);
        	friendCar.setVisibility(View.GONE);
        }else{
        	carBrand.setText(carInforList.get(0)[0]);
        	carPlate.setText(carInforList.get(0)[1]);
        	setCarLogo();
        }
	}
	
	
	public void setCarLogo(){
		Bitmap bitmap = null;
		if(new File(Constant.VehicleLogoPath + carInforList.get(0)[0] + ".png").exists()){
    		bitmap = BitmapFactory.decodeFile(Constant.VehicleLogoPath + carInforList.get(0)[0] + ".png");
    	}else{
    		bitmap = BitmapFactory.decodeResource(FriendInformationActivity.this.getResources(), R.drawable.body_nothing_icon);
    		getDate("carBrank",Constant.BaseUrl + "base/car_brand",getBarBrand);
    		if(brankList != null){
    			if(brankList.size() != 0){
    				for(int i =  0 ; i < brankList.size() ; i ++){
    					if(carInforList.get(0)[0].equals(brankList.get(i).getVehicleBrank())){
    						final String logoUrl = brankList.get(i).getLogoUrl();
    						final String carBrandName = brankList.get(i).getVehicleBrank();
    						new Thread(new Runnable() {
								public void run() {
									Bitmap tempBitmap = GetSystem.getBitmapFromURL(logoUrl);
									if(tempBitmap != null){
										GetSystem.saveImageSD(tempBitmap, Constant.VehicleLogoPath, carBrandName + ".png");
										setCarLogo();
									}
								}
							}).start();
    					}
    				}
    			}
    		}
    	}
    	carBrandIv.setImageBitmap(bitmap);
	}
	
	private void getDate(String whereValues,String url,int handlerWhat) {
		Log.e("title:",whereValues);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{whereValues});
		JSONArray jsonArray = null;
		if(cursor.moveToFirst()){
			Log.e("数据库数据","数据库数据");
			try {
				jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("Content")));
				parseJSON(jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			progressDialog = ProgressDialog.show(FriendInformationActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
			progressDialog.setCancelable(true);
			new Thread(new NetThread.GetDataThread(myHandler, url, handlerWhat)).start();
		}
	}
	public void parseJSON(JSONArray jsonArray){
		progressDialog.dismiss();
			try {
				int arrayLength = jsonArray.length();
				brankList = new ArrayList<BrankModel>();
				for(int i = 0 ; i < arrayLength ; i ++){
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					BrankModel brankModel = new BrankModel();
					brankModel.setVehicleBrank(jsonObj.getString("name"));
					brankModel.setBrankId(jsonObj.getString("id"));
					if(jsonObj.opt("url_icon") != null){
						brankModel.setLogoUrl(jsonObj.getString("url_icon"));
					}else{
						brankModel.setLogoUrl("");
					}
					brankList.add(brankModel);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			for(int i = 0 ; i < carInforList.size(); i ++){
				
			}
			
			for(int i = 0 ; i < brankList.size() ; i ++){
			}
		}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case getBarBrand:
				if(!"[]".equals(msg.obj.toString())){
					ChoiceCarInformationActivity.insertDatabases("carBrank", msg.obj.toString(), FriendInformationActivity.this);
					try {
						JSONArray jsonArray = new JSONArray(msg.obj.toString());
						parseJSON(jsonArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
	}
}
