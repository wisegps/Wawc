package com.wise.wawc;

import java.util.HashMap;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 欢迎界面
 * 
 * @author honesty
 */
public class WelcomeActivity extends Activity implements PlatformActionListener {
    private static final String TAG = "WelcomeActivity";
    
    private final static int Wait = 1;
    private final static int Get_city = 2;
    private final static int Get_host_city = 3;
    private final static int login = 4;
    LinearLayout ll_login;
    Button bt_sina , bt_qq;
    Platform platformQQ;
    Platform platformSina;
    Platform platformWhat;
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ShareSDK.initSDK(this);
        ll_login = (LinearLayout)findViewById(R.id.ll_login);
        bt_sina = (Button)findViewById(R.id.bt_sina);
        bt_sina.setOnClickListener(onClickListener);
        bt_qq = (Button)findViewById(R.id.bt_qq);
        bt_qq.setOnClickListener(onClickListener);
        if (isOffline()) {
            // 没有网络
            setNetworkMethod();
        }else{
            //跳转到登录界面
            isLogin();
            if(isLogin){
                new Thread(new WaitThread()).start();
            }else{
                System.out.println("show");
                Animation operatingAnim = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.in_from_bottom);  
                ll_login.setAnimation(operatingAnim);
                ll_login.setVisibility(View.VISIBLE);
            }
            isNeedGetCityFromUrl();
            
        }
    }
    OnClickListener onClickListener = new OnClickListener() {        
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.bt_sina:
                platformSina.setPlatformActionListener(WelcomeActivity.this);
                platformSina.SSOSetting(true);
                platformSina.showUser(null);
                break;

            case R.id.bt_qq:
                platformQQ.setPlatformActionListener(WelcomeActivity.this);
                platformQQ.showUser(null);
                break;
            }
        }
    };
    
    private void isLogin() {
        platformQQ = ShareSDK.getPlatform(WelcomeActivity.this, QZone.NAME);
        platformSina = ShareSDK.getPlatform(WelcomeActivity.this, SinaWeibo.NAME);
        if (platformQQ.getDb().isValid()) {
            System.out.println("qq登录");
            isLogin = true;
            bt_sina.setVisibility(View.INVISIBLE);
            bt_qq.setVisibility(View.INVISIBLE);
         } else if (platformSina.getDb().isValid()){
            isLogin = true;
            System.out.println("sina登录");
            bt_sina.setVisibility(View.INVISIBLE);
            bt_qq.setVisibility(View.INVISIBLE);
       } else {
            isLogin = false;
            System.out.println("没有登录");
            bt_sina.setVisibility(View.VISIBLE);
            bt_qq.setVisibility(View.VISIBLE);
        }
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
            case login:
                isLogin = true;
                TurnActivity();
                break;
            }
        }
    };
    

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
    private void isNeedGetCityFromUrl(){
        DBHelper dbHelper = new DBHelper(WelcomeActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=?", new String[] { "City" });
        if(cursor.getCount() == 0){
            String url = Constant.BaseUrl + "base/city?is_hot=0";
            new Thread(new NetThread.GetDataThread(handler, url, Get_city)).start();
        }else{
            isCity = true;
        }
        cursor.close();
        Cursor c = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=?", new String[] { "hotCity" });
        if(c.getCount() == 0){
            String url_hot = Constant.BaseUrl + "base/city?is_hot=1";
            new Thread(new NetThread.GetDataThread(handler, url_hot, Get_host_city)).start();
        }else{
            isHotCity = true;
        }
        c.close();
        db.close();
    }
    
    private void InsertCity(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(WelcomeActivity.this, values, Constant.TB_Base);
    }
    private void TurnActivity(){
        if(isCity && isHotCity && isLogin){
            if(isFristLoad()){
                Intent intent = new Intent(WelcomeActivity.this, SelectCityActivity.class);
                intent.putExtra("Welcome", true);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }                          
        }
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
        Message message = new Message();
        message.what = login;
        handler.sendMessage(message);
    }
    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        // TODO Auto-generated method stub
        
    }
}