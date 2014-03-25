package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 设置中心
 */
public class SettingCenterActivity extends Activity{
	
	TextView tv_value;
	ImageView iv_traffic,iv_status,iv_alert,iv_remind;

    boolean isTraffic,isStatus,isAlert,isRemind;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_center);
		
		tv_value = (TextView)findViewById(R.id.tv_value);
		ImageView iv_setting_menu = (ImageView)findViewById(R.id.iv_setting_menu);
		iv_setting_menu.setOnClickListener(onClickListener);
		RelativeLayout rl_traffic = (RelativeLayout)findViewById(R.id.rl_traffic);
		rl_traffic.setOnClickListener(onClickListener);
		RelativeLayout rl_status = (RelativeLayout)findViewById(R.id.rl_status);
		rl_status.setOnClickListener(onClickListener);
		RelativeLayout rl_alert = (RelativeLayout)findViewById(R.id.rl_alert);
		rl_alert.setOnClickListener(onClickListener);
		RelativeLayout rl_remind = (RelativeLayout)findViewById(R.id.rl_remind);
		rl_remind.setOnClickListener(onClickListener);
		RelativeLayout rl_center = (RelativeLayout)findViewById(R.id.rl_center);
		rl_center.setOnClickListener(onClickListener);
		
		iv_traffic = (ImageView)findViewById(R.id.iv_traffic);
		iv_status = (ImageView)findViewById(R.id.iv_status);
		iv_alert = (ImageView)findViewById(R.id.iv_alert);
		iv_remind = (ImageView)findViewById(R.id.iv_remind);
		
		TextView tv_share_gift = (TextView)findViewById(R.id.tv_share_gift);
		tv_share_gift.setOnClickListener(onClickListener);
		TextView tv_feedback = (TextView)findViewById(R.id.tv_feedback);
		tv_feedback.setOnClickListener(onClickListener);
		TextView tv_about = (TextView)findViewById(R.id.tv_about);
		tv_about.setOnClickListener(onClickListener);		
		getsp();
	}
	
	OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_setting_menu:
                ActivityFactory.A.LeftMenu();
                break;
            case R.id.rl_traffic:
                if(isTraffic){
                    isTraffic = false;
                    iv_traffic.setVisibility(View.GONE);
                }else{
                    isTraffic = true;
                    iv_traffic.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_status:
                if(isStatus){
                    isStatus = false;
                    iv_status.setVisibility(View.GONE);
                }else{
                    isStatus = true;
                    iv_status.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_alert:
                if(isAlert){
                    isAlert = false;
                    iv_alert.setVisibility(View.GONE);
                }else{
                    isAlert = true;
                    iv_alert.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_remind:
                if(isRemind){
                    isRemind = false;
                    iv_remind.setVisibility(View.GONE);
                }else{
                    isRemind = true;
                    iv_remind.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_center:
                startActivityForResult(new Intent(SettingCenterActivity.this, CarSelectActivity.class), 0);
                break;
            case R.id.tv_share_gift:
                Intent intent = new Intent(SettingCenterActivity.this, WapActivity.class);
                intent.putExtra("Title", "推荐有礼");
                intent.putExtra("url", "http://wiwc.api.wisegps.cn/help/share");
                startActivity(intent);
                //startActivity(new Intent(SettingCenterActivity.this, ArticleActivity.class));
                break;
            case R.id.tv_feedback:
                startActivity(new Intent(SettingCenterActivity.this, FeedBackActivity.class));
                break;
            case R.id.tv_about:
                startActivity(new Intent(SettingCenterActivity.this, AboutActivity.class));
                break;
            }
        }
    };
    Handler handler = new Handler(){
        
    };
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("alert", isAlert ? 1 : 0);
        values.put("event", isRemind ? 1 : 0);
        values.put("fault", isStatus ? 1 : 0);
        values.put("vio", isTraffic ? 1 : 0);
        dbExcute.UpdateDB(this, values, "cust_id=?",new String[] { Variable.cust_id }, Constant.TB_Account);
        
        
        String url = Constant.BaseUrl + "customer/" + Variable.cust_id +"/push?auth_code=" + Variable.auth_code;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("if_vio_noti", isTraffic ? "1" : "0"));   
        params.add(new BasicNameValuePair("if_fault_noti", isStatus ? "1" : "0"));   
        params.add(new BasicNameValuePair("if_alert_noti", isAlert ? "1" : "0"));   
        params.add(new BasicNameValuePair("if_event_noti", isRemind ? "1" : "0"));   
        new Thread(new NetThread.putDataThread(handler, url, params, 999)).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void getsp(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Account
                + " where cust_id=?", new String[] { Variable.cust_id });
        
        if (cursor.moveToFirst()) {
            int alert = cursor.getInt(cursor.getColumnIndex("alert"));
            if(alert == 1){
                isAlert = true;
            }else{
                isAlert = false;
            }
            int event = cursor.getInt(cursor.getColumnIndex("event"));
            if(event == 1){
                isRemind = true;
            }else{
                isRemind = false;
            }
            int fault = cursor.getInt(cursor.getColumnIndex("fault"));
            if(fault == 1){
                isStatus = true;
            }else{
                isStatus = false;
            }
            int vio = cursor.getInt(cursor.getColumnIndex("vio"));
            if(vio == 1){//违章
                isTraffic = true;
            }else{
                isTraffic = false;
            }
            System.out.println(vio + "," + fault + "," + event + "," + alert + "," );
        }else{
            isTraffic = false;
            isStatus = false;
            isAlert = false;
            isRemind = false;
        }
        if(isTraffic){
            iv_traffic.setVisibility(View.VISIBLE);
        }
        if(isStatus){
            iv_status.setVisibility(View.VISIBLE);
        }
        if(isAlert){
            iv_alert.setVisibility(View.VISIBLE);
        }
        if(isRemind){
            iv_remind.setVisibility(View.VISIBLE);
        }
        if(Variable.carDatas == null || Variable.carDatas.size() == 0){
            
        }else{
            SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
            int index = preferences.getInt(Constant.DefaultVehicleID, 0);
            tv_value.setText("车辆" + Variable.carDatas.get(index).getObj_name() + "的位置");
        } 
        cursor.close();
        db.close();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            int car_id = data.getIntExtra("Obj_id", 0);
            String Obj_name = data.getStringExtra("Obj_name");
            tv_value.setText("车辆" + Obj_name + "的位置");
            for(int i = 0 ; i < Variable.carDatas.size() ; i++){
                if(Variable.carDatas.get(i).getObj_id() == car_id){                    
                    SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
                    Editor editor = preferences.edit();
                    editor.putInt(Constant.DefaultVehicleID, i);
                    editor.commit();
                    Variable.carDatas.get(i).setCheck(true);
                }else{
                    Variable.carDatas.get(i).setCheck(false);
                }
            }
        }
    }
}