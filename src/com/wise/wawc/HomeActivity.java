package com.wise.wawc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.wise.data.CarData;
import com.wise.extend.HScrollLayout;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.LayoutInflater;
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
    private static final int Get_Fuel = 3; // 获取城市油价
    private static final int Get_Cars = 4; // 获取车辆数据

    TextView tv_item_weather_date, tv_item_weather_wd, tv_item_weather,
            tv_item_weather_sky, tv_item_weather_temp1,
            tv_item_weather_index_xc, tv_item_weather_city,tv_item_oil_90, tv_item_oil_93,
            tv_item_oil_97, tv_item_oil_0,tv_car_number,tv_activity_home_car_adress;
    ImageView iv_carLogo;
    private RecognizerDialog recognizerDialog = null; // 语音合成文字
    StringBuffer sb = null;
    private ImageView saySomething = null; // 语音识别

    String LocationCityCode = "";// 城市编码
    String LocationCity = "";// 城市
    int DefaultVehicleID;//默认选中车辆id
    List<CarData> carDatas = new ArrayList<CarData>();
    boolean isNeedGetLogoFromUrl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
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
        tv_activity_home_car_adress = (TextView) findViewById(R.id.tv_activity_home_car_adress);
        tv_activity_home_car_adress.setOnClickListener(onClickListener);
        tv_car_number = (TextView)findViewById(R.id.tv_car_number);
        iv_carLogo = (ImageView)findViewById(R.id.iv_carLogo);

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
        tv_item_weather_temp1 = (TextView) weatherView
                .findViewById(R.id.tv_item_weather_temp1);
        tv_item_weather_index_xc = (TextView) weatherView.findViewById(R.id.tv_item_weather_index_xc);
        tv_item_weather_city = (TextView) weatherView.findViewById(R.id.tv_item_weather_city);
        tv_item_weather_city.setOnClickListener(onClickListener);
        
        tv_item_oil_90 = (TextView) oilView.findViewById(R.id.tv_item_oil_90);
        tv_item_oil_93 = (TextView) oilView.findViewById(R.id.tv_item_oil_93);
        tv_item_oil_97 = (TextView) oilView.findViewById(R.id.tv_item_oil_97);
        tv_item_oil_0 = (TextView) oilView.findViewById(R.id.tv_item_oil_0);
        //tv_item_oil_update = (TextView) oilView.findViewById(R.id.tv_item_oil_update);

        // 注册（将语音转文字）
        recognizerDialog = new RecognizerDialog(this, "appid=5281eaf4");
        recognizerDialog.setListener(this);
        recognizerDialog.setEngine("sms", "", null);
        recognizerDialog.setSampleRate(RATE.rate16k);
        sb = new StringBuffer();
        
        getSp();
        GetOldWeather();// 获取本地存储的数据
        GetFutureWeather();
        GetRealTimeWeather();
        GetFuel();
        registerBroadcastReceiver();
        GetDBCars();
        ShowCarInfo();
        if(isNeedGetLogoFromUrl){
          new Thread(new getLogoThread()).start();
        }
        if(Variable.Adress != null){
            tv_activity_home_car_adress.setText(Variable.Adress);
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_home_menu:
                ActivityFactory.A.LeftMenu();
                break;
            case R.id.iv_activity_car_home_search:
                ActivityFactory.A.RightMenu();
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
                Intent intent = new Intent(HomeActivity.this,MyVehicleActivity.class);
                intent.putExtra("isJump", true);
                HomeActivity.this.startActivity(intent);
                break;
            case R.id.iv_home_say_something:
                recognizerDialog.show();
                break;
            case R.id.tv_item_weather_city:
                startActivityForResult(new Intent(HomeActivity.this, SelectCityActivity.class), 0);
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
                JudgeFutureWeather(msg.obj.toString());
                jsonFutureWeather(msg.obj.toString());
                break;
            case Get_RealTimeWeather:
                JudgeRealTimeWeather(msg.obj.toString());
                jsonRealTimeWeather(msg.obj.toString());
                break;
            case Get_Fuel:
                SaveAndJsonFuel(msg.obj.toString());
                break;
            case Get_Cars:
                jsonCars(msg.obj.toString());
                ShowCarInfo();
                break;
            }
        }
    };
    
    private void getSp(){
        SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        LocationCityCode = preferences.getString(Constant.LocationCityCode,"101280601");
        LocationCity = preferences.getString(Constant.LocationCity, "深圳");
        String LocationCityFuel = preferences.getString(Constant.LocationCityFuel, "");
        //默认显示车的object_id
        DefaultVehicleID = preferences.getInt(Constant.DefaultVehicleID, 0);
        System.out.println("DefaultVehicleID = " + DefaultVehicleID);
        tv_item_weather_city.setText(LocationCity);
        jsonFuel(LocationCityFuel);
    }

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
        Cursor c = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=?", new String[] { "FutureWeather" });
        if (c.moveToFirst()) {
            String Content = c.getString(c.getColumnIndex("Content"));
            isHaveOldFutureWeather = true;
            // 解析数据
            Log.d(TAG, "解析本地未来天气");
            jsonFutureWeather(Content);
        }
        c.close();
        // 实时天气
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=?", new String[] { "RealTimeWeather" });
        if (cursor.moveToFirst()) {
            String Content = cursor.getString(cursor.getColumnIndex("Content"));
            isHaveOldRealTimeWeather = true;
            // 解析数据
            jsonRealTimeWeather(Content);
        }
        cursor.close();
        db.close();
    }
    /**
     * 获取本地车辆信息
     */
    private void GetDBCars(){
    	carDatas.clear();
        if(Variable.cust_id != null){
            DBHelper dbHelper = new DBHelper(HomeActivity.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();            
            Cursor cursor = db.rawQuery("select * from " + Constant.TB_Vehicle + " where Cust_id=?", new String[] {Variable.cust_id });
            boolean isHaveDefaultVehicleID = false;
            while (cursor.moveToNext()) {
                int obj_id = cursor.getInt(cursor.getColumnIndex("obj_id"));
                String obj_name =  cursor.getString(cursor.getColumnIndex("obj_name"));
                String car_brand =  cursor.getString(cursor.getColumnIndex("car_brand"));
                String car_series =  cursor.getString(cursor.getColumnIndex("car_series"));
                String car_type =  cursor.getString(cursor.getColumnIndex("car_type"));
                String engine_no =  cursor.getString(cursor.getColumnIndex("engine_no"));
                String frame_no =  cursor.getString(cursor.getColumnIndex("frame_no"));
                String insurance_company =  cursor.getString(cursor.getColumnIndex("insurance_company"));
                String insurance_date =  cursor.getString(cursor.getColumnIndex("insurance_date"));
                String annual_inspect_date =  cursor.getString(cursor.getColumnIndex("annual_inspect_date"));
                String maintain_company =  cursor.getString(cursor.getColumnIndex("maintain_company"));
                String maintain_last_mileage =  cursor.getString(cursor.getColumnIndex("maintain_last_mileage"));
                String maintain_next_mileage =  cursor.getString(cursor.getColumnIndex("maintain_next_mileage"));
                String buy_date =  cursor.getString(cursor.getColumnIndex("buy_date"));
                
                CarData carData = new CarData();
                
                if(DefaultVehicleID == obj_id){
                    carData.setCheck(true);
                    isHaveDefaultVehicleID = true;
                }else{
                    carData.setCheck(false);
                }    
                carData.setObj_id(obj_id);
                carData.setObj_name(obj_name);
                carData.setCar_brand(car_brand);
                carData.setCar_series(car_series);
                carData.setCar_type(car_type);
                carData.setEngine_no(engine_no);
                carData.setFrame_no(frame_no);
                carData.setInsurance_company(insurance_company);
                carData.setInsurance_date(insurance_date);
                carData.setAnnual_inspect_date(annual_inspect_date);
                carData.setMaintain_company(maintain_company);
                carData.setMaintain_last_mileage(maintain_last_mileage);
                carData.setMaintain_next_mileage(maintain_next_mileage);
                carData.setBuy_date(buy_date);
                String imagePath = Constant.VehicleLogoPath + car_brand + ".jpg";//SD卡路径
                if(new File(imagePath).isFile()){//存在
                    carData.setLogoPath(imagePath);
                }else{
                    isNeedGetLogoFromUrl = true;
                    carData.setLogoPath("");
                }
                carDatas.add(carData);
                System.out.println(carData.toString());
            }
            cursor.close();
            db.close();
            if(carDatas.size() > 0 && (!isHaveDefaultVehicleID)){
                carDatas.get(0).setCheck(true);
            }            
        }
        Log.e("查询数据库完毕","查询数据库完毕");
        Variable.carDatas = carDatas;
    }
    class getLogoThread extends Thread{
        @Override
        public void run() {
            super.run();
            getVehicleLogo();
        }
    }
    /**
     * 获取图片
     */
    private void getVehicleLogo(){
        List<Brand> brands = new ArrayList<Brand>();
        //读取车的品牌
        String url = Constant.BaseUrl + "base/car_brand";
        String result = GetSystem.getStringFromURL(url);
        if(!result.equals("")){
            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Brand brand = new Brand();
                    brand.setName(jsonObject.getString("name"));
                    if(jsonObject.opt("url_icon") != null){
                        brand.setUrl_icon(jsonObject.getString("url_icon"));
                    }                    
                    brands.add(brand);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(int i = 0 ; i < Variable.carDatas.size() ; i++){
            String brand = Variable.carDatas.get(i).getCar_brand();            
            String imagePath = Constant.VehicleLogoPath + brand + ".jpg";//SD卡路径
            if(new File(imagePath).isFile()){//存在
                System.out.println("文件存在");
            }else{//不存在
                for(int j = 0 ; j < brands.size() ; j++){
                   String name =  brands.get(j).getName();
                   if(name.equals(brand)){
                       String imageUrl = Constant.ImageUrl + brands.get(j).getUrl_icon();
                       Bitmap bitmap = GetSystem.getBitmapFromURL(imageUrl);
                        if(bitmap != null){
                            GetSystem.saveImageSD(bitmap,Constant.VehicleLogoPath, brand + ".jpg");
                            Variable.carDatas.get(i).setLogoPath(imagePath);
                        }
                       break;
                   }
                }
            }
        }
    }
    private class Brand{
        String name;
        String url_icon;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getUrl_icon() {
            return url_icon;
        }
        public void setUrl_icon(String url_icon) {
            this.url_icon = url_icon;
        }
    }
    /**
     * 判断未来天气是插入数据库or更新数据库
     * 
     * @param result
     */
    private void JudgeFutureWeather(String result) {
        if (isHaveOldFutureWeather) {// 更新
            UpdateWeather(result, "FutureWeather");
        } else {// 插入
            InsertWeather(result, "FutureWeather");
        }
    }

    /**
     * 判断实时天气是插入数据库or更新数据库
     * 
     * @param result
     */
    private void JudgeRealTimeWeather(String result) {
        if (isHaveOldRealTimeWeather) {// 更新
            Log.d(TAG, "更新数据库");
            UpdateWeather(result, "RealTimeWeather");
        } else {// 插入
            Log.d(TAG, "插入数据库");
            InsertWeather(result, "RealTimeWeather");
        }
    }

    /**
     * 更新天气
     * 
     * @param result
     * @param Title
     */
    private void UpdateWeather(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Content", result);
        dbExcute.UpdateDB(HomeActivity.this, values, Title);
    }

    /**
     * 插入天气
     * 
     * @param result
     * @param Title
     */
    private void InsertWeather(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(HomeActivity.this, values, Constant.TB_Base);
    }

    /**
     * 解析未来天气
     * 
     * @param result
     */
    private void jsonFutureWeather(String result) {
        try {
            String Weather = "";
            JSONObject jsonObject = new JSONObject(result).getJSONObject("weatherinfo");
            if (jsonObject.opt("date_y") != null) {
                String date_y = jsonObject.getString("date_y");
                //tv_item_oil_update.setText(date_y + "更新");
                Weather += date_y;
            }
            if (jsonObject.opt("week") != null) {
                String week = jsonObject.getString("week");
                Weather += "    " + week;
            }
            if (jsonObject.opt("weather1") != null) {
                tv_item_weather_sky.setText(jsonObject.getString("weather1"));
            }
            if (jsonObject.opt("temp1") != null) {
                tv_item_weather_temp1.setText(jsonObject.getString("temp1"));
            }
            if (jsonObject.opt("index_xc") != null) {
                tv_item_weather_index_xc.setText(jsonObject.getString("index_xc"));
            }
            tv_item_weather_date.setText(Weather);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetSystem.displayBriefMemory(HomeActivity.this);
    }

    /**
     * 解析实时数据
     * 
     * @param result
     */
    private void jsonRealTimeWeather(String result) {
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
     * 解析油价
     * 
     * @param result
     */
    private void jsonFuel(String result) {
        if(!result.equals("")){
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (!result.equals("")) {
                    tv_item_oil_90.setText(jsonObject.getString("fuel90"));
                    tv_item_oil_93.setText(jsonObject.getString("fuel93"));
                    tv_item_oil_97.setText(jsonObject.getString("fuel97"));
                    tv_item_oil_0.setText(jsonObject.getString("fuel0"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }        
    }

    /**
     * 保存并解析油价
     * 
     * @param result
     */
    private void SaveAndJsonFuel(String result) {
        try {
            JSONObject jsonObject = new JSONArray(result).getJSONObject(0);
            String fuel_price = jsonObject.getString("fuel_price");
            // 存储
            SharedPreferences preferences = getSharedPreferences(
                    Constant.sharedPreferencesName, Context.MODE_PRIVATE);
            Editor editor = preferences.edit();
            editor.putString(Constant.LocationCityFuel, fuel_price);
            editor.commit();
            // 解析
            jsonFuel(fuel_price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析车辆数据
     * @param result
     */
    private void jsonCars(String result) {// TODO Cars
    	carDatas.clear();
        try {
            JSONArray jsonArray = new JSONArray(result);
            Log.e("用户车辆：" ,jsonArray.length() + "");
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int obj_id = jsonObject.getInt("obj_id");
                String Cust_id = jsonObject.getString("cust_id");
                String obj_name = jsonObject.getString("obj_name");
                String car_brand = jsonObject.getString("car_brand");
                String car_series = jsonObject.getString("car_series");
                String car_type = jsonObject.getString("car_type");
                String engine_no = jsonObject.getString("engine_no");
                String frame_no = jsonObject.getString("frame_no");
                String insurance_company = jsonObject.getString("insurance_company");
                String insurance_date = jsonObject.getString("insurance_date");
                insurance_date = insurance_date.substring(0, 10);
                String annual_inspect_date = jsonObject.getString("annual_inspect_date");
                annual_inspect_date = annual_inspect_date.substring(0, 10);
                String maintain_company = jsonObject.getString("maintain_company");
                String maintain_last_mileage = jsonObject.getString("maintain_last_mileage");
                String maintain_next_mileage = jsonObject.getString("maintain_next_mileage");
                String buy_date = jsonObject.getString("buy_date");
                buy_date = buy_date.substring(0, 10);
                
                CarData carData = new CarData();
                
                carData.setCheck(false);
                carData.setObj_id(obj_id);
                carData.setObj_name(obj_name);
                carData.setCar_brand(car_brand);
                carData.setCar_series(car_series);
                carData.setCar_type(car_type);
                carData.setEngine_no(engine_no);
                carData.setFrame_no(frame_no);
                carData.setInsurance_company(insurance_company);
                carData.setInsurance_date(insurance_date);
                carData.setAnnual_inspect_date(annual_inspect_date);
                carData.setMaintain_company(maintain_company);
                carData.setMaintain_last_mileage(maintain_last_mileage);
                carData.setMaintain_next_mileage(maintain_next_mileage);
                carData.setBuy_date(buy_date);
                String imagePath = Constant.VehicleLogoPath + car_brand + ".jpg";//SD卡路径
                if(new File(imagePath).isFile()){//存在
                    carData.setLogoPath(imagePath);
                }else{
                    isNeedGetLogoFromUrl = true;
                    carData.setLogoPath("");
                }
                carDatas.add(carData);
                //存储在数据库
                DBExcute dbExcute = new DBExcute();
                ContentValues values = new ContentValues();
                values.put("obj_id", obj_id);
                values.put("Cust_id", Cust_id);
                values.put("obj_name", obj_name);
                values.put("car_brand", car_brand);
                values.put("car_series", car_series);
                values.put("car_type", car_type);
                values.put("engine_no", engine_no);
                values.put("frame_no", frame_no);
                values.put("insurance_company", insurance_company);
                values.put("insurance_date", insurance_date);
                values.put("annual_inspect_date", annual_inspect_date);
                values.put("maintain_company", maintain_company);
                values.put("maintain_last_mileage", maintain_last_mileage);
                values.put("maintain_next_mileage", maintain_next_mileage);
                values.put("buy_date", buy_date);
                dbExcute.InsertDB(HomeActivity.this, values, Constant.TB_Vehicle);
            }
            Variable.carDatas = carDatas;
            if(Variable.carDatas.size() > 0){
                Variable.carDatas.get(0).setCheck(true);
            }
            Log.d(TAG, "Variable.carDatas = " + Variable.carDatas.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取未来天气
     */
    private void GetFutureWeather() {
        String url = Constant.BaseUrl + "base/weather?city_code="
                + LocationCityCode + "&is_real=0";
        new Thread(new NetThread.GetDataThread(handler, url, Get_FutureWeather))
                .start();

    }

    /**
     * 获取实时天气
     */
    private void GetRealTimeWeather() {
        String url = Constant.BaseUrl + "base/weather?city_code="
                + LocationCityCode + "&is_real=1";
        new Thread(new NetThread.GetDataThread(handler, url,
                Get_RealTimeWeather)).start();

    }

    /**
     * 获取城市油价
     */
    private void GetFuel() {
        try {
            String url = Constant.BaseUrl + "base/city/"
                    + URLEncoder.encode(LocationCity, "UTF-8");
            new Thread(new NetThread.GetDataThread(handler, url, Get_Fuel))
                    .start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户下车辆数据
     */
    private void GetCars() {
        String url = Constant.BaseUrl + "customer/" + Variable.cust_id
                + "/vehicle?auth_code=" + Variable.auth_code;
        Log.e("查询用户车辆的连接",url);
        new Thread(new NetThread.GetDataThread(handler, url, Get_Cars)).start();
    }

    private void ToShare() {
        Intent intent = new Intent(HomeActivity.this, ShareLocationActivity.class);
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

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.A_Login);
        intentFilter.addAction(Constant.A_City);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.A_Login)) {
                if((Variable.carDatas == null) || (Variable.carDatas.size() == 0)){
                    Log.d(TAG, "获取车辆数据");
                    GetCars();
                }
            }else if(action.equals(Constant.A_City)){
                tv_activity_home_car_adress.setText(intent.getStringExtra("AddrStr"));
            }
        }
    };
    /**
     * 显示车辆信息
     */
    private void ShowCarInfo(){
        if(Variable.carDatas != null || Variable.carDatas.size() > 0){
            for(CarData carData : Variable.carDatas){
                if(carData.isCheck()){
                    //TODO 显示车辆信息
                    tv_car_number.setText(carData.getObj_name());
                    Bitmap bimage = BitmapFactory.decodeFile(carData.getLogoPath());
                    if(bimage != null){            
                        iv_carLogo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
                    }else{
                        iv_carLogo.setImageResource(R.drawable.ic_launcher);
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode = " + requestCode + " , resultCode = " + resultCode);
        if(resultCode == 1){
            Log.d(TAG, "城市设置完毕");
            getSp();
            GetFutureWeather();
            GetRealTimeWeather();
            GetFuel();
        }
    }
}
