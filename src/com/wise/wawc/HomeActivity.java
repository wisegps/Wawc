package com.wise.wawc;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.wise.data.CharacterParser;
import com.wise.extend.HScrollLayout;
import com.wise.pubclas.Config;
import com.wise.pubclas.NetThread;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 首页
 * 
 * @author honesty
 */
public class HomeActivity extends Activity implements RecognizerDialogListener {
    private static final String TAG = "HomeActivity";
    private static final int Get_FutureWeather = 1; // 获取未来天气
    private static final int Get_RealTimeWeather = 2; // 获取实时天气

    TextView tv_item_weather_date, tv_item_weather_wd, tv_item_weather,tv_item_weather_sky;
    private RecognizerDialog recognizerDialog = null; // 语音合成文字
    StringBuffer sb = null;
    private ImageView saySomething = null; // 语音识别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView iv_activity_home_menu = (ImageView) findViewById(R.id.iv_activity_home_menu);
        iv_activity_home_menu.setOnClickListener(onClickListener);
        ImageView iv_activity_car_home_search = (ImageView) findViewById(R.id.iv_activity_car_home_search);
        iv_activity_car_home_search.setOnClickListener(onClickListener);
        Button bt_activity_home_help = (Button) findViewById(R.id.bt_activity_home_help);
        bt_activity_home_help.setOnClickListener(onClickListener);
        Button bt_activity_home_risk = (Button) findViewById(R.id.bt_activity_home_risk);
        bt_activity_home_risk.setOnClickListener(onClickListener);
        // ScrollLayout_car
        RelativeLayout rl_activity_home_car = (RelativeLayout) findViewById(R.id.rl_activity_home_car);
        rl_activity_home_car.setOnClickListener(onClickListener);
        Button bt_activity_home_vehicle_status = (Button) findViewById(R.id.bt_activity_home_vehicle_status);
        bt_activity_home_vehicle_status.setOnClickListener(onClickListener);
        Button bt_activity_home_car_remind = (Button) findViewById(R.id.bt_activity_home_car_remind);
        bt_activity_home_car_remind.setOnClickListener(onClickListener);
        Button bt_activity_home_traffic = (Button) findViewById(R.id.bt_activity_home_traffic);
        bt_activity_home_traffic.setOnClickListener(onClickListener);
        Button bt_activity_home_share = (Button) findViewById(R.id.bt_activity_home_share);
        bt_activity_home_share.setOnClickListener(onClickListener);
        TextView tv_activity_home_car_adress = (TextView) findViewById(R.id.tv_activity_home_car_adress);
        tv_activity_home_car_adress.setOnClickListener(onClickListener);

        saySomething = (ImageView) findViewById(R.id.iv_home_say_something);
        saySomething.setOnClickListener(onClickListener);
        sb = new StringBuffer();

        HScrollLayout ScrollLayout_other = (HScrollLayout) findViewById(R.id.ScrollLayout_other);
        LayoutInflater mLayoutInflater = LayoutInflater.from(HomeActivity.this);
        View weatherView = mLayoutInflater.inflate(R.layout.item_weather, null);
        View oilView = mLayoutInflater.inflate(R.layout.item_oil, null);
        ScrollLayout_other.addView(weatherView);
        ScrollLayout_other.addView(oilView);
        tv_item_weather_date = (TextView) weatherView
                .findViewById(R.id.tv_item_weather_date);
        tv_item_weather_wd = (TextView) weatherView
                .findViewById(R.id.tv_item_weather_wd);
        tv_item_weather = (TextView) weatherView
                .findViewById(R.id.tv_item_weather);
        tv_item_weather_sky = (TextView) weatherView
                .findViewById(R.id.tv_item_weather_sky);

        // 注册（将语音转文字）
        recognizerDialog = new RecognizerDialog(this, "appid=5281eaf4");
        recognizerDialog.setListener(this);
        recognizerDialog.setEngine("sms", "", null);
        recognizerDialog.setSampleRate(RATE.rate16k);
        sb = new StringBuffer();

        GetOldWeather();// 获取本地存储的数据
        GetFutureWeather();
        GetRealTimeWeather();
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_home_menu:
                ActivityFactory.A.LeftMenu();
                break;
            case R.id.iv_activity_car_home_search:
                HomeActivity.this.startActivity(new Intent(HomeActivity.this,
                        ActivitySearch.class));
                break;
            case R.id.bt_activity_home_help:// 救援
                ToShare();
                break;
            case R.id.bt_activity_home_risk:// 报险
                ToShare();
                break;
            case R.id.bt_activity_home_share:// 位置分享
                ToShare();
                break;
            case R.id.bt_activity_home_traffic:// 车辆违章
                HomeActivity.this.startActivity(new Intent(HomeActivity.this,
                        TrafficActivity.class));
                break;
            case R.id.bt_activity_home_car_remind:// 车务提醒
                Intent eventIntent = new Intent(HomeActivity.this,
                        CarRemindActivity.class);
                eventIntent.putExtra("isJump", true);
                HomeActivity.this.startActivity(eventIntent);
                break;
            case R.id.bt_activity_home_vehicle_status:// 爱车车况
                HomeActivity.this.startActivity(new Intent(HomeActivity.this,
                        VehicleStatusActivity.class));
                break;
            case R.id.tv_activity_home_car_adress: // 车辆位置
                HomeActivity.this.startActivity(new Intent(HomeActivity.this,
                        CarLocationActivity.class));
                break;
            case R.id.rl_activity_home_car: // 我的爱车
                HomeActivity.this.startActivity(new Intent(HomeActivity.this,
                        MyVehicleActivity.class));
                break;
            case R.id.iv_home_say_something:
                recognizerDialog.show();
                break;
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_FutureWeather:
                // Log.d(TAG, "天气=" + msg.obj.toString());
                JudgeFutureWeather(msg.obj.toString());
                jsonFutureWeather(msg.obj.toString());
                break;
            case Get_RealTimeWeather:
                JudgeRealTimeWeather(msg.obj.toString());
                jsonRealTimeWeather(msg.obj.toString());
                break;
            }
        }
    };

    boolean isHaveOldFutureWeather = false;
    boolean isHaveOldRealTimeWeather = false;

    /**
     * 获取本地存储的天气
     */
    private void GetOldWeather() {
        // 查询
        DBHelper dbHelper = new DBHelper(HomeActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 未来天气
        Cursor c = db.rawQuery("select * from " + Config.TB_Base
                + " where Title=?", new String[] { "FutureWeather" });
        if (c.moveToFirst()) {
            String Content = c.getString(c.getColumnIndex("Content"));
            isHaveOldFutureWeather = true;
            // 解析数据
            jsonFutureWeather(Content);
        }
        c.close();
        // 实时天气
        Cursor cursor = db.rawQuery("select * from " + Config.TB_Base
                + " where Title=?", new String[] { "RealTimeWeather" });
        if (c.moveToFirst()) {
            String Content = cursor.getString(c.getColumnIndex("Content"));
            isHaveOldRealTimeWeather = true;
            // 解析数据
            jsonRealTimeWeather(Content);
        }
        cursor.close();

        db.close();
    }

    /**
     * 判断未来天气是插入数据库or更新数据库
     * 
     * @param result
     */
    private void JudgeFutureWeather(String result) {
        if (isHaveOldFutureWeather) {// 更新
            UpdateWeather(result,"FutureWeather");
        } else {// 插入
            InsertWeather(result,"FutureWeather");
        }
    }
    /**
     * 判断实时天气是插入数据库or更新数据库
     * @param result
     */
    private void JudgeRealTimeWeather(String result) {
        if (isHaveOldRealTimeWeather) {// 更新
            UpdateWeather(result,"RealTimeWeather");
        } else {// 插入
            InsertWeather(result,"RealTimeWeather");
        }
    }

    private void UpdateWeather(String result,String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Content", result);
        dbExcute.UpdateDB(HomeActivity.this, values, Title);
    }
    private void InsertWeather(String result,String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(HomeActivity.this, values, Config.TB_Base);
    }

    /**
     * 解析未来天气
     * 
     * @param result
     */
    private void jsonFutureWeather(String result) {
        Log.d(TAG, result);
        try {
            String Weather = "";
            JSONObject jsonObject = new JSONObject(result)
                    .getJSONObject("weatherinfo");
            if (jsonObject.opt("date_y") != null) {
                String date_y = jsonObject.getString("date_y");
                Weather += date_y;
            }
            if (jsonObject.opt("week") != null) {
                String week = jsonObject.getString("week");
                Weather += "    " + week;
            }
            if (jsonObject.opt("weather1") != null) {
                tv_item_weather_sky.setText(jsonObject.getString("weather1"));
            }
            tv_item_weather_date.setText(Weather);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析实时数据
     * 
     * @param result
     */
    private void jsonRealTimeWeather(String result) {
        Log.d(TAG, result);
        try {
            String weather = "";
            JSONObject jsonObject = new JSONObject(result)
                    .getJSONObject("weatherinfo");
            if (jsonObject.opt("temp") != null) {
                tv_item_weather_wd.setText(jsonObject.getString("temp") + "°");
            }
            if (jsonObject.opt("WD") != null) {
                weather += jsonObject.getString("WD");
            }
            if (jsonObject.opt("WS") != null) {
                weather += jsonObject.getString("WS");
            }
            if (jsonObject.opt("SD") != null) {
                weather += "   湿度" + jsonObject.getString("SD");
            }
            tv_item_weather.setText(weather);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取未来天气
     */
    private void GetFutureWeather() {
        SharedPreferences preferences = getSharedPreferences(
                Config.sharedPreferencesName, Context.MODE_PRIVATE);
        String LocationCityCode = preferences.getString(
                Config.LocationCityCode, "");
        String url = "http://wiwc.api.wisegps.cn/base/weather?city_code="
                + LocationCityCode + "&is_real=0";
        new Thread(new NetThread.GetDataThread(handler, url, Get_FutureWeather))
                .start();

    }

    /**
     * 获取实时天气
     */
    private void GetRealTimeWeather() {
        SharedPreferences preferences = getSharedPreferences(
                Config.sharedPreferencesName, Context.MODE_PRIVATE);
        String LocationCityCode = preferences.getString(
                Config.LocationCityCode, "");
        String url = "http://wiwc.api.wisegps.cn/base/weather?city_code="
                + LocationCityCode + "&is_real=1";
        new Thread(new NetThread.GetDataThread(handler, url,
                Get_RealTimeWeather)).start();

    }

    private void ToShare() {
        Intent intent = new Intent(HomeActivity.this, NewArticleActivity.class);
        intent.putExtra("isSNS", true);
        HomeActivity.this.startActivity(intent);
    }

    @Override
    public void onEnd(SpeechError arg0) {
        Toast.makeText(getApplicationContext(), sb.toString(), 0).show();
        sb.delete(0, sb.length());
    }

    public void onResults(ArrayList<RecognizerResult> results, boolean arg1) {
        for (RecognizerResult recognizerResult : results) {
            sb.append(recognizerResult.text);
        }
    }
}