package com.wise.wawc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.wise.data.CarData;
import com.wise.data.DevicesData;
import com.wise.extend.HScrollLayout;
import com.wise.extend.OnViewChangeListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.JsonParser;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 首页
 * @author honesty
 */
public class HomeActivity extends Activity{
    private static final String TAG = "HomeActivity";
    private static final int Get_FutureWeather = 1; // 获取未来天气
    private static final int Get_RealTimeWeather = 2; // 获取实时天气
    private static final int Get_Fuel = 3; // 获取城市油价
    private static final int Get_Cars = 4; // 获取车辆数据
    private static final int Get_CarsLogo = 5; // 获取车辆图标
    private static final int Get_Devicesdata = 6; // 获取终端信息
    private static final int Get_persion = 7;//获取个人信息
    
    LinearLayout ll_image;
    TextView tv_item_weather_date, tv_item_weather_wd, tv_item_weather,
            tv_item_weather_sky, tv_item_weather_temp1,
            tv_item_weather_index_xc, tv_item_weather_city,tv_item_oil_90, tv_item_oil_93,
            tv_item_oil_97, tv_item_oil_0;
    HScrollLayout ScrollLayout_car;
    private ImageView saySomething = null; // 语音识别
  	private SpeechRecognizer iatRecognizer;   //识别对象
  	private StringBuffer sb = null;
  	ImageView voiceImage ,iv_car_remind;
  	VoiceDialog voiceDialog = null;

    String LocationCityCode = "";// 城市编码
    String LocationCity = "";// 城市
    int DefaultVehicleID;//默认选中车辆id
    List<CarData> carDatas = new ArrayList<CarData>();
    boolean isNeedGetLogoFromUrl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ll_image = (LinearLayout)findViewById(R.id.ll_image);
        ImageView iv_activity_home_menu = (ImageView) findViewById(R.id.iv_activity_home_menu);
        iv_activity_home_menu.setOnClickListener(onClickListener);
        ImageView iv_activity_car_home_search = (ImageView) findViewById(R.id.iv_activity_car_home_search);
        iv_activity_car_home_search.setOnClickListener(onClickListener);
        Button bt_activity_home_help = (Button) findViewById(R.id.bt_activity_home_help);
        bt_activity_home_help.setOnClickListener(onClickListener);
        Button bt_activity_home_risk = (Button) findViewById(R.id.bt_activity_home_risk);
        bt_activity_home_risk.setOnClickListener(onClickListener);
        iv_car_remind = (ImageView)findViewById(R.id.iv_car_remind);
        ScrollLayout_car = (HScrollLayout) findViewById(R.id.ScrollLayout_car);
        ScrollLayout_car.setOnViewChangeListener(new OnViewChangeListener() {            
            @Override
            public void OnViewChange(int view) {
                changeImage(view);
                saveVehicleID(view);
                //notiRemind(view);
            }            
            @Override
            public void OnLastView() {}
        });
        Button bt_activity_home_vehicle_status = (Button) findViewById(R.id.bt_activity_home_vehicle_status);
        bt_activity_home_vehicle_status.setOnClickListener(onClickListener);
        Button bt_activity_home_car_remind = (Button) findViewById(R.id.bt_activity_home_car_remind);
        bt_activity_home_car_remind.setOnClickListener(onClickListener);
        Button bt_activity_home_traffic = (Button) findViewById(R.id.bt_activity_home_traffic);
        bt_activity_home_traffic.setOnClickListener(onClickListener);
        Button bt_activity_home_share = (Button) findViewById(R.id.bt_activity_home_share);
        bt_activity_home_share.setOnClickListener(onClickListener);

        saySomething = (ImageView) findViewById(R.id.iv_home_say_something);
        saySomething.setOnClickListener(onClickListener);
        //用户登录(使用SpeechRecognizer类需要授权)
      	SpeechUser.getUser().login(HomeActivity.this, null, null,"appid=" + Variable.MscKey, listener);
      	iatRecognizer=SpeechRecognizer.createRecognizer(HomeActivity.this);

        HScrollLayout ScrollLayout_other = (HScrollLayout) findViewById(R.id.ScrollLayout_other);
        tv_item_weather_date = (TextView)findViewById(R.id.tv_item_weather_date);
        tv_item_weather_wd = (TextView)findViewById(R.id.tv_item_weather_wd);
        tv_item_weather = (TextView)findViewById(R.id.tv_item_weather);
        tv_item_weather_sky = (TextView)findViewById(R.id.tv_item_weather_sky);
        tv_item_weather_temp1 = (TextView)findViewById(R.id.tv_item_weather_temp1);
        tv_item_weather_index_xc = (TextView)findViewById(R.id.tv_item_weather_index_xc);
        tv_item_weather_city = (TextView)findViewById(R.id.tv_item_weather_city);
        tv_item_weather_city.setOnClickListener(onClickListener);
        
        tv_item_oil_90 = (TextView)findViewById(R.id.tv_item_oil_90);
        tv_item_oil_93 = (TextView)findViewById(R.id.tv_item_oil_93);
        tv_item_oil_97 = (TextView)findViewById(R.id.tv_item_oil_97);
        tv_item_oil_0 = (TextView)findViewById(R.id.tv_item_oil_0);
        
        final ImageView iv_weather = (ImageView)findViewById(R.id.iv_weather);
        final ImageView iv_oil = (ImageView)findViewById(R.id.iv_oil);
        //tv_item_oil_update = (TextView) oilView.findViewById(R.id.tv_item_oil_update);
        ScrollLayout_other.setOnViewChangeListener(new OnViewChangeListener() {            
            @Override
            public void OnViewChange(int view) {
                switch (view) {
                case 0:
                    iv_weather.setImageResource(R.drawable.home_body_cutover);
                    iv_oil.setImageResource(R.drawable.home_body_cutover_press);
                    break;
                case 1:
                    iv_weather.setImageResource(R.drawable.home_body_cutover_press);
                    iv_oil.setImageResource(R.drawable.home_body_cutover);
                    break;
                }
            }            
            @Override
            public void OnLastView() {}
        });
        getSp();
        GetOldWeather();// 获取本地存储的数据
        GetFutureWeather();
        GetRealTimeWeather();
        GetFuel();
        registerBroadcastReceiver();
        GetDBCars();
        GetDevicesDB();
        if(isNeedGetLogoFromUrl){
          new Thread(new getLogoThread()).start();
        }
        showCar();
        GetDBCarRemindData();
    }
    private void changeImage(int index){
        for(int i = 0 ; i < carDatas.size() ; i++){
            ImageView imageView = (ImageView)ll_image.getChildAt(i);
            if(index == i){
                imageView.setImageResource(R.drawable.home_body_cutover_press);
            }else{
                imageView.setImageResource(R.drawable.home_body_cutover);
            }
        }
    }
    private void saveVehicleID(int index){
        SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putInt(Constant.DefaultVehicleID, index);
        editor.commit();
    }
    private TextView[] mTextViews;
    //TODO 显示车辆
    private void showCar(){
        System.out.println("showCar");
        ScrollLayout_car.removeAllViews();
        mTextViews = new TextView[carDatas.size()];
        for(int i = 0 ; i < carDatas.size() ; i++){
            CarData carData = carDatas.get(i);
            View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.item_home_car, null);
            ScrollLayout_car.addView(view);
            TextView tv_car_number = (TextView)view.findViewById(R.id.tv_car_number);
            tv_car_number.setOnClickListener(onClickListener);
            ImageView iv_carLogo = (ImageView)view.findViewById(R.id.iv_carLogo);
            TextView tv_activity_home_car_adress = (TextView)view.findViewById(R.id.tv_activity_home_car_adress);
            tv_activity_home_car_adress.setText(Variable.Adress);
            tv_activity_home_car_adress.setOnClickListener(onClickListener);
            mTextViews[i] = tv_activity_home_car_adress;
            tv_car_number.setText(carData.getObj_name());
            Bitmap bimage = BitmapFactory.decodeFile(carData.getLogoPath());
            if(bimage != null){            
                iv_carLogo.setImageBitmap(bimage);
            }else{
                iv_carLogo.setImageResource(R.drawable.body_nothing_icon);
            }
        }
        ll_image.removeAllViews();
        for(int i = 0 ; i < carDatas.size() ; i++){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.home_body_cutover_press);
            imageView.setPadding(5, 0, 5, 0);
            ll_image.addView(imageView);
        }
        changeImage(DefaultVehicleID);
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
                Intent intent_help = new Intent(HomeActivity.this, ShareLocationActivity.class);
                intent_help.putExtra("reason", "救援 ");
                intent_help.putExtra("index", DefaultVehicleID);
                HomeActivity.this.startActivity(intent_help);
                break;
            case R.id.bt_activity_home_risk:// 报险
                Intent intent_risk = new Intent(HomeActivity.this, ShareLocationActivity.class);
                intent_risk.putExtra("reason", "报险");
                intent_risk.putExtra("index", DefaultVehicleID);
                HomeActivity.this.startActivity(intent_risk);
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
                Intent intent_adress = new Intent(HomeActivity.this,CarLocationActivity.class);
                intent_adress.putExtra("index", DefaultVehicleID);
                HomeActivity.this.startActivity(intent_adress);
                break;
            case R.id.tv_car_number: // 我的爱车
                Intent intent = new Intent(HomeActivity.this,MyVehicleActivity.class);
                intent.putExtra("isJump", true);
                HomeActivity.this.startActivity(intent);
                break;
            case R.id.iv_home_say_something:
            	iatRecognizer=SpeechRecognizer.createRecognizer(HomeActivity.this);
				iatRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
				iatRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
				iatRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
				iatRecognizer.startListening(recognizerListener);
				//显示语音识别Dialog
				voiceDialog = new VoiceDialog(HomeActivity.this);
				voiceDialog.show();
				voiceDialog.setCancelable(true);
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
                showCar();
                GetDevicesData();
                break;
            case Get_CarsLogo:
                showCar();
                break;
            case Get_persion:
                jsonCarRemind(msg.obj.toString());
                break;
            case 999:
                Log.d(TAG, msg.obj.toString());
                break;
            case Get_Devicesdata:
                jsonDevice(msg.obj.toString());
                JudgeDevice(msg.obj.toString());
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
                String reg_no = cursor.getString(cursor.getColumnIndex("reg_no"));
                String vio_location = cursor.getString(cursor.getColumnIndex("vio_location"));
                String device_id = cursor.getString(cursor.getColumnIndex("device_id"));
                String serial = cursor.getString(cursor.getColumnIndex("serial"));
                String maintain_last_date = cursor.getString(cursor.getColumnIndex("maintain_last_date"));
                String city_code = cursor.getString(cursor.getColumnIndex("vio_location"));
                
                CarData carData = new CarData();
                carData.setCheck(false);   
                carData.setObj_id(obj_id);
                carData.setType(0);
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
                carData.setMaintain_last_date(maintain_last_date);
                carData.setBuy_date(buy_date);
                carData.setRegNo(reg_no);
                carData.setVio_location(vio_location);
                carData.setAdress(Variable.Adress);
                carData.setLat(String.valueOf(Variable.Lat));
                carData.setLon(String.valueOf(Variable.Lon));
                carData.setDevice_id(device_id);
                carData.setCity_code(city_code);
                String imagePath = Constant.VehicleLogoPath + car_brand + ".png";//SD卡路径
                if(new File(imagePath).isFile()){//存在
                    carData.setLogoPath(imagePath);
                }else{
                    isNeedGetLogoFromUrl = true;
                    carData.setLogoPath("");
                }
                carDatas.add(carData);
            }
            cursor.close();
            db.close();
            if(carDatas.size() > 0){
                if(DefaultVehicleID >= carDatas.size()){//删除车辆后默认旋转第一个车
                    carDatas.get(0).setCheck(true);
                    ScrollLayout_car.snapToScreen(0);
                }else{
                    carDatas.get(DefaultVehicleID).setCheck(true);
                    ScrollLayout_car.snapToScreen(DefaultVehicleID);
                }
            }
        }
        Log.e("查询数据库完毕",carDatas.size()+"");
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
            String imagePath = Constant.VehicleLogoPath + brand + ".png";//SD卡路径
            if(new File(imagePath).isFile()){//存在
                System.out.println("文件存在");
            }else{//不存在
                for(int j = 0 ; j < brands.size() ; j++){
                   String name =  brands.get(j).getName();
                   if(name.equals(brand)){
                       String imageUrl = Constant.ImageUrl + brands.get(j).getUrl_icon();
                       Log.d(TAG, "imageUrl = " + imageUrl);
                       Bitmap bitmap = GetSystem.getBitmapFromURL(imageUrl);
                        if(bitmap != null){
                            GetSystem.saveImageSD(bitmap,Constant.VehicleLogoPath, brand + ".png");
                            Variable.carDatas.get(i).setLogoPath(imagePath);
                        }
                       break;
                   }
                }
            }
        }
        Message message = new Message();
        message.what = Get_CarsLogo;
        handler.sendMessage(message);
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
    private void jsonCars(String result) {
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
                String reg_no = "";
                String vio_location = "";
                if(jsonObject.opt("reg_no") != null){
                    reg_no = jsonObject.getString("reg_no");
                }
                if(jsonObject.opt("vio_location") != null){
                    vio_location = jsonObject.getString("vio_location");
                }
                String device_id = "";
                if(jsonObject.opt("device_id") != null){
                    device_id = jsonObject.getString("device_id");
                }
                String annual_inspect_date = jsonObject.getString("insurance_date").replace("T", " ").substring(0, 19);
                String insurance_date = jsonObject.getString("insurance_date").replace("T", " ").substring(0, 19);
                String maintain_company = jsonObject.getString("maintain_company");
                String maintain_last_mileage = jsonObject.getString("maintain_last_mileage");
                String maintain_next_mileage = jsonObject.getString("maintain_next_mileage");
                String buy_date = jsonObject.getString("buy_date");
                buy_date = buy_date.substring(0, 10);
                
                CarData carData = new CarData();                
                carData.setCheck(false);
                carData.setObj_id(obj_id);
                carData.setType(0);
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
                carData.setRegNo(reg_no);
                carData.setVio_location(vio_location);
                carData.setDevice_id(device_id);
                String imagePath = Constant.VehicleLogoPath + car_brand + ".png";//SD卡路径
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
                values.put("reg_no", reg_no);
                values.put("vio_location", vio_location);
                values.put("device_id", device_id);
                dbExcute.InsertDB(HomeActivity.this, values, Constant.TB_Vehicle);
            }
            Variable.carDatas = carDatas;
            if(Variable.carDatas.size() > 0){
                Variable.carDatas.get(0).setCheck(true);
            }
            if(isNeedGetLogoFromUrl){
                new Thread(new getLogoThread()).start();
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
        CarData carData = Variable.carDatas.get(DefaultVehicleID);
        String url = "http://api.map.baidu.com/geocoder?location="
                + carData.getLat() + "," + carData.getLon()
                + "&coord_type=bd09ll&output=html";
        StringBuffer sb = new StringBuffer();
        sb.append("【位置】");
        sb.append(carData.getAdress());
        sb.append("," + url);
        GetSystem.share(HomeActivity.this, sb.toString(),
                "", Float.valueOf(carData.getLat()),
                Float.valueOf(carData.getLon()));
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.A_Login);
        intentFilter.addAction(Constant.A_City);
        intentFilter.addAction(Constant.A_LoginOut);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.A_Login)) {
                GetDBCars();
                showCar();
                GetDevicesDB();
                if((Variable.carDatas == null) || (Variable.carDatas.size() == 0)){
                    Log.d(TAG, "获取车辆数据");
                    GetCars();
                }
                GetDBCarRemindData();
            }else if(action.equals(Constant.A_City)){
                for(int i = 0 ; i < mTextViews.length ; i++){
                    mTextViews[i].setText(intent.getStringExtra("AddrStr"));
                }
            }else if(action.equals(Constant.A_LoginOut)){
                //TODO 注销
                Variable.carDatas.clear();
                showCar();
            }
        }
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        //ShowCarInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        Log.d(TAG, "onDestroy");
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
    
	/**
	 * 用户授权回调监听器.
	 */
	private SpeechListener listener = new SpeechListener()
	{
		public void onData(byte[] arg0) {}
		public void onCompleted(SpeechError error) {
			if(error != null) {
				Toast.makeText(HomeActivity.this, "授权失败" + error, Toast.LENGTH_SHORT).show();
			}			
		}
		public void onEvent(int arg0, Bundle arg1) {}		
	};
	//TODO 更改语音识别ui布局
	RecognizerListener recognizerListener=new RecognizerListener(){
		public void onBeginOfSpeech() {	
			sb = new StringBuffer();
			Toast.makeText(getApplicationContext(), "开始说话", 0).show();
		}

		public void onError(SpeechError err) {
			voiceDialog.dismiss();
			Toast.makeText(getApplicationContext(), "识别出错，稍后再试", 0).show();
			Log.e("错误码：",err+"");
		}
		public void onEndOfSpeech() {
		}
		public void onEvent(int eventType, int arg1, int arg2, String msg) {
		}
		public void onResult(RecognizerResult results, boolean isLast) {
			voiceDialog.dismiss();
			String text = JsonParser.parseIatResult(results.getResultString());
			sb.append(text);
			Toast.makeText(getApplicationContext(), sb.toString(), 0).show();
		}
		public void onVolumeChanged(int volume) {
			if(volume == 0){
				voiceImage.clearAnimation();
			}else{
				Animation animation = voiceImage.getAnimation();
				if(animation == null){
					animation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.tip);
					 LinearInterpolator lin = new LinearInterpolator();  
					 animation.setInterpolator(lin); 
					 voiceImage.startAnimation(animation);
				}
			}
		}
	};
	
	class VoiceDialog extends AlertDialog{
		public VoiceDialog(Context context) {
			super(context);
		}
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.voice_dialog);
			voiceImage = (ImageView) findViewById(R.id.do_something_voice_image);
		}
	}
	/**
	 * 车辆提醒
	 * @param index
	 */
	private void notiRemind(int index){
	    CarData carData = carDatas.get(index);
	    System.out.println(carData.getAnnual_inspect_date());
	    System.out.println(carData.getInsurance_date());
	    if(GetSystem.isTimeOut(carData.getAnnual_inspect_date()) ||
	            GetSystem.isTimeOut(carData.getInsurance_date())||
	            GetSystem.isTimeOut(carData.getMaintain_last_date())||
	            GetSystem.isTimeOut(annual_inspect_date)||
	            GetSystem.isTimeOut(change_date)){
	        //TODO 提醒
	        iv_car_remind.setVisibility(View.VISIBLE);
	    }else{
	        iv_car_remind.setVisibility(View.GONE);
	    }
	    //String url = Constant.BaseUrl + "vehicle/72" + "?auth_code=" + Variable.auth_code;
	    //new Thread(new NetThread.GetDataThread(handler, url, 999)).start();
	}
	String annual_inspect_date;
	String change_date;
	private void GetDBCarRemindData(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Account + " where cust_id=?", new String[]{Variable.cust_id});
        if(cursor.getCount() == 0){
            if(Variable.cust_id == null || Variable.cust_id.equals("")){
                
            }else{
                String url = Constant.BaseUrl + "customer/" + Variable.cust_id +"?auth_code=" + Variable.auth_code;
                new Thread(new NetThread.GetDataThread(handler, url, Get_persion)).start();
            }            
        }else{
            if(cursor.moveToFirst()){
                annual_inspect_date = cursor.getString(cursor.getColumnIndex("annual_inspect_date"));
                change_date = cursor.getString(cursor.getColumnIndex("change_date"));
            }                
        }
    }
	private void jsonCarRemind(String result){
	    try {           
	        JSONObject jsonObject = new JSONObject(result);
            DBExcute dbExcute = new DBExcute();
            ContentValues values = new ContentValues();
            if(jsonObject.opt("contacts") != null){
                String contacts = jsonObject.getString("contacts");
                values.put("Consignee", contacts);
            }
            if(jsonObject.opt("address") != null){
                String address = jsonObject.getString("address");
                values.put("Adress", address);
            }
            if(jsonObject.opt("tel") != null){
                String tel = jsonObject.getString("tel");
                values.put("Phone", tel);
            }
            if(jsonObject.opt("annual_inspect_date") != null){
                annual_inspect_date = jsonObject.getString("annual_inspect_date").replace("T", " ").substring(0, 19);                
                values.put("annual_inspect_date", annual_inspect_date);
            }
            if(jsonObject.opt("change_date") != null){
                change_date = jsonObject.getString("change_date").replace("T", " ").substring(0, 19);
                values.put("change_date", change_date);
            }
            values.put("cust_id", Variable.cust_id);
            dbExcute.InsertDB(HomeActivity.this, values, Constant.TB_Account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	private void GetDevicesDB(){
        DBHelper dbHelper = new DBHelper(HomeActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=? and Cust_id=?", new String[] { "Devices",Variable.cust_id });
        if (cursor.moveToFirst()) {
            String Content = cursor.getString(cursor.getColumnIndex("Content"));
            // 解析数据
            jsonDevice(Content);
            isHaveDevices = true;
        }
        cursor.close();
        db.close();
        Log.d(TAG, "GetDevicesDB");
    }
	/**
     * 解析终端数据
     * @param result
     */
    private void jsonDevice(String result){
        try {
            List<DevicesData> devicesDatas = new ArrayList<DevicesData>();
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DevicesData devicesData = new DevicesData();
                devicesData.setDevice_id(jsonObject.getString("device_id"));
                devicesData.setHardware_version(jsonObject.getString("hardware_version"));
                devicesData.setSerial(jsonObject.getString("serial"));
                devicesData.setService_end_date(jsonObject.getString("service_end_date"));
                devicesData.setSim(jsonObject.getString("sim"));
                devicesData.setSoftware_version(jsonObject.getString("software_version"));
                devicesData.setStatus(jsonObject.getString("status"));
                devicesData.setType(0);
                devicesDatas.add(devicesData);
                for(int j = 0 ; j < Variable.carDatas.size() ; j++){
                    CarData carData = Variable.carDatas.get(j);
                    if(carData.getDevice_id().equals(jsonObject.getString("device_id"))){
                        carData.setSerial(jsonObject.getString("serial"));
                        DBExcute dbExcute = new DBExcute();
                        ContentValues values = new ContentValues();
                        values.put("serial", jsonObject.getString("serial"));
                        dbExcute.UpdateDB(this, values, "obj_id=?", new String[]{String.valueOf(carData.getObj_id())}, Constant.TB_Vehicle);
                        break;
                    }
                }
            }
            Variable.devicesDatas = devicesDatas;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 从服务器读取我的终端
     */
    public void GetDevicesData(){
        String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/device?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url, Get_Devicesdata)).start();
    }
    boolean isHaveDevices = false;
    /**
     * 判断更新还是插入
     * @param result
     */
    private void JudgeDevice(String result) {
        if (isHaveDevices) {// 更新
            UpdateDevice(result, "Devices");
        } else {// 插入
            InsertDevice(result, "Devices");
        }
    }
    private void UpdateDevice(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Content", result);
        dbExcute.UpdateDB(HomeActivity.this, values, Title);
    }
    private void InsertDevice(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Cust_id", Variable.cust_id);
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(HomeActivity.this, values, Constant.TB_Base);
    }
}
