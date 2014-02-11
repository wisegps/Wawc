package com.wise.wawc;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.sql.DBExcute;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 欢迎界面
 * 
 * @author honesty
 */
public class WelcomeActivity extends Activity {
    private static final String TAG = "WelcomeActivity";
    
    private final static int Wait = 1;
    private final static int Get_city = 2;
    private final static int Get_host_city = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TextView tv_activity_welcom_version = (TextView) findViewById(R.id.tv_activity_welcom_version);
        tv_activity_welcom_version.setText("V"
                + GetSystem.GetVersion(getApplicationContext(),
                        Constant.PackageName));
        if (isOffline()) {
            // 没有网络
            setNetworkMethod();
        } else if (isFristLoad()) {
            // 第一次登录，选着城市
            GetCityList();
        }else{
            //跳转到登录界面
            //test();
            new Thread(new WaitThread()).start();
            //String url = "http://wiwc.api.wisegps.cn/violation/city?auth_code=bba2204bcd4c1f87a19";
            //new Thread(new NetThread.GetDataThread(handler, url, 999)).start();
        }
    }
    private void test(){
        String time1 = "2014-02-11T06:17:08.751Z";
        time1 = time1.substring(0, time1.length() - 8).replace("T", " ");
        System.out.println(formatDateTime(time1)[0] + "," + formatDateTime(time1)[1]);
        
        String time2 = "2014-02-10T06:17:08.751Z";
        time2 = time2.substring(0, time2.length() - 8).replace("T", " ");
        System.out.println(formatDateTime(time2)[0] + "," + formatDateTime(time2)[1]);
        
        String time3 = "2014-01-25T06:17:08.751Z";
        time3 = time3.substring(0, time3.length() - 8).replace("T", " ");
        System.out.println(formatDateTime(time3)[0] + "," + formatDateTime(time3)[1]);
        
    }
    
    private static String[] formatDateTime(String time) {  
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");   
        if(time==null ||"".equals(time)){  
            return null;  
        }  
        Date date = null;  
        try {  
            date = format.parse(time);  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
          
        Calendar current = Calendar.getInstance();  
          
        Calendar today = Calendar.getInstance();    //今天  
          
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));  
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));  
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));  
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数  
        today.set( Calendar.HOUR_OF_DAY, 0);  
        today.set( Calendar.MINUTE, 0);  
        today.set(Calendar.SECOND, 0);  
          
        Calendar yesterday = Calendar.getInstance();    //昨天  
          
        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));  
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));  
        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);  
        yesterday.set( Calendar.HOUR_OF_DAY, 0);  
        yesterday.set( Calendar.MINUTE, 0);  
        yesterday.set(Calendar.SECOND, 0);  
          
        current.setTime(date);  
        String[] myDate = new String[2];
        if(current.after(today)){  
            myDate[0] = "今天";
            myDate[1] = time.split(" ")[1];
        }else if(current.before(today) && current.after(yesterday)){  
            myDate[0] = "昨天";
            myDate[1] = time.split(" ")[1];
        }else{ 
            myDate[0] = time.substring(0, 10);
            myDate[1] = time.split(" ")[1];
            int index = time.indexOf("-")+1;  
        }  
        return myDate;
    }
    
    String citys = "";
    String hot_citys = "";
    boolean isCity = false;
    boolean isHotCity = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Wait:
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                finish();
                break;
            case Get_city:
                isCity = true;
                citys = msg.obj.toString();
                InsertCity(citys, "City");
                TurnActivity();
                break;
            case Get_host_city:
                isHotCity = true;
                hot_citys = msg.obj.toString();
                InsertCity(hot_citys, "hotCity");
                TurnActivity();
                break;
            case 999:
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString()).getJSONObject("result");
                    Iterator it = jsonObject.keys();
                    while(it.hasNext()){
                        String key = (String) it.next();
                        System.out.println("key = " + key);
                        JSONObject jsonObject2 = jsonObject.getJSONObject(key);
                        System.out.println(jsonObject2.getString("province"));
                        JSONArray jsonArray = jsonObject2.getJSONArray("citys");
                        for(int i = 0 ; i < jsonArray.length(); i++){
                            JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                            System.out.println(jsonObject3.getString("city_name"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            break;
            }
        }
    };
    
    private void InsertCity(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(WelcomeActivity.this, values, Constant.TB_Base);
    }

    /**
     * 判断网络连接状况，true,没有网络
     */
    private Boolean isOffline() {
        if (!GetSystem.checkNetWorkStatus(getApplicationContext())) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 打开设置网络界面
     */
    public void setNetworkMethod() {
        new AlertDialog.Builder(WelcomeActivity.this)
                .setTitle(R.string.system_note)
                .setMessage(R.string.network_error)
                .setPositiveButton(R.string.set_network,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                Intent intent = new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.cancle,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                finish();
                            }
                        }).show();
    }

    class WaitThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {                
                Thread.sleep(1000);
                Message message = new Message();
                message.what = Wait;
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否是第一次登录
     * 
     * @return
     */
    private boolean isFristLoad() {
        SharedPreferences preferences = getSharedPreferences(
                Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        String LocationCity = preferences.getString(Constant.LocationCity, "");
        if (LocationCity.equals("")) {
            return true;
        }
        return false;
    }
    private void GetCityList(){
        String url = Constant.BaseUrl + "base/city?is_hot=0";
        new Thread(new NetThread.GetDataThread(handler, url, Get_city)).start();
        String url_hot = Constant.BaseUrl + "base/city?is_hot=1";
        new Thread(new NetThread.GetDataThread(handler, url_hot, Get_host_city)).start();
    }
    private void TurnActivity(){
        if(isCity && isHotCity){
            if(citys.equals("")||hot_citys.equals("")){
                Toast.makeText(WelcomeActivity.this, "读取数据失败！", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Intent intent = new Intent(WelcomeActivity.this, SelectCityActivity.class);
                intent.putExtra("Welcome", true);
                startActivity(intent);
                finish();
            }            
        }
    }
}