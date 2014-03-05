package com.wise.wawc;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.wise.customView.EnergyCurveView;
import com.wise.customView.EnergyGroup;
import com.wise.data.EnergyItem;
import com.wise.data.TimeData;
import com.wise.extend.CarAdapter;
import com.wise.extend.OnViewChangeListener;
import com.wise.extend.OnViewTouchListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的车况,爱车车况
 * @author honesty
 */
public class VehicleStatusActivity extends Activity {
    private static final String TAG = "VehicleStatusActivity";
    private static final int Hide_rl = 1;
    private static final int get_data = 2;
    private static final int get_day = 3;
    private static final int get_DeviceStatus = 4;
    private DisplayMetrics dm = new DisplayMetrics();

    LinearLayout ll_activity_vehicle_status_oil;
    RelativeLayout rl_status;
    TextView tv_activity_vehicle_status_oil, tv_month_hk_fuel,tv_fault,tv_alarm,
            tv_month_distance, tv_month_fuel,tv_vehicle_status_date;
    EnergyGroup hScrollLayout;
    CarAdapter carAdapter;
    // List<CarData> carDatas;
    boolean isWait = true;
    String device_id = "3";
    int index_car = 0;
    int index ; //选择哪一天
    TimeData timeData;

    DBExcute dbExcute = new DBExcute();
    ArrayList<EnergyItem> Edistance = new ArrayList<EnergyItem>();
    ArrayList<EnergyItem> Efuel = new ArrayList<EnergyItem>();
    ArrayList<EnergyItem> Ehk_fuel = new ArrayList<EnergyItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_status);
        rl_status = (RelativeLayout)findViewById(R.id.rl_status);
        ll_activity_vehicle_status_oil = (LinearLayout) findViewById(R.id.ll_activity_vehicle_status_oil);
        ll_activity_vehicle_status_oil.setOnClickListener(onClickListener);
        ImageView iv_activity_vehicle_status_data_next = (ImageView) findViewById(R.id.iv_activity_vehicle_status_data_next);
        iv_activity_vehicle_status_data_next.setOnClickListener(onClickListener);
        ImageView iv_activity_vehicle_status_data_previous = (ImageView) findViewById(R.id.iv_activity_vehicle_status_data_previous);
        iv_activity_vehicle_status_data_previous.setOnClickListener(onClickListener);
        ImageView iv_activity_vehicle_status_back = (ImageView) findViewById(R.id.iv_activity_vehicle_status_back);
        iv_activity_vehicle_status_back.setOnClickListener(onClickListener);
        tv_vehicle_status_date = (TextView)findViewById(R.id.tv_vehicle_status_date);
        tv_activity_vehicle_status_oil = (TextView) findViewById(R.id.tv_activity_vehicle_status_oil);
        tv_month_hk_fuel = (TextView) findViewById(R.id.tv_month_hk_fuel);
        tv_month_hk_fuel.setOnClickListener(onClickListener);
        tv_month_distance = (TextView) findViewById(R.id.tv_month_distance);
        tv_month_distance.setOnClickListener(onClickListener);
        tv_month_fuel = (TextView) findViewById(R.id.tv_month_fuel);
        tv_month_fuel.setOnClickListener(onClickListener);
        tv_fault = (TextView) findViewById(R.id.tv_fault);
        tv_fault.setOnClickListener(onClickListener);
        TextView tv_fault_title = (TextView) findViewById(R.id.tv_fault_title);
        tv_fault_title.setOnClickListener(onClickListener);
        tv_alarm = (TextView) findViewById(R.id.tv_alarm);
        tv_alarm.setOnClickListener(onClickListener);

        hScrollLayout = (EnergyGroup) findViewById(R.id.hscrollLayout);
        hScrollLayout.setOnViewChangeListener(onViewChangeListener);
        GridView gv_activity_vehicle_status = (GridView) findViewById(R.id.gv_activity_vehicle_status);
        carAdapter = new CarAdapter(VehicleStatusActivity.this,Variable.carDatas);
        gv_activity_vehicle_status.setAdapter(carAdapter);

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                120, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams(Variable.carDatas.size()
                * (px + 10), LayoutParams.WRAP_CONTENT);
        gv_activity_vehicle_status.setLayoutParams(params);
        gv_activity_vehicle_status.setColumnWidth(px);
        gv_activity_vehicle_status.setHorizontalSpacing(10);
        gv_activity_vehicle_status.setStretchMode(GridView.NO_STRETCH);
        gv_activity_vehicle_status.setNumColumns(Variable.carDatas.size());
        gv_activity_vehicle_status.setOnItemClickListener(onItemClickListener);

        new Thread(new waitThread()).start();
        timeData = GetSystem.GetNowMonth();
        tv_vehicle_status_date.setText(timeData.getDate());      
        initView();
        
        SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);        
        index_car = preferences.getInt(Constant.DefaultVehicleID, 0);
        device_id = Variable.carDatas.get(index_car).getDevice_id();
        Variable.carDatas.get(index_car).setCheck(true);
        carAdapter.notifyDataSetChanged();
        getCarLocationDB();
        getDeviceStatus();
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_vehicle_status_back:
                finish();
                break;
            case R.id.tv_month_hk_fuel:
                hScrollLayout.snapToScreen(0);
                break;
            case R.id.tv_month_distance:
                hScrollLayout.snapToScreen(1);
                break;
            case R.id.tv_month_fuel:
                hScrollLayout.snapToScreen(2);
                break;
            case R.id.tv_fault_title:
                startActivity(new Intent(VehicleStatusActivity.this,CarFaultActivity.class));
             break;
            case R.id.tv_fault:
                startActivity(new Intent(VehicleStatusActivity.this,CarFaultActivity.class));
                break;
            case R.id.ll_activity_vehicle_status_oil:
                Intent intent = new Intent(VehicleStatusActivity.this, TravelActivity.class);
                intent.putExtra("Date", timeData.getDate() +"-" + Edistance.get(index).date);
                startActivity(intent);
                break;
            case R.id.iv_activity_vehicle_status_data_next:
                Toast.makeText(VehicleStatusActivity.this, "NEXT", Toast.LENGTH_SHORT).show();
                timeData = GetSystem.GetNextMonth(timeData.getDate(), 1);
                tv_vehicle_status_date.setText(timeData.getDate());  
                GetTotalDB();
                GetTripListDB();
                break;
            case R.id.iv_activity_vehicle_status_data_previous:
                Toast.makeText(VehicleStatusActivity.this, "LAST", Toast.LENGTH_SHORT).show();
                timeData = GetSystem.GetNextMonth(timeData.getDate(), -1);
                tv_vehicle_status_date.setText(timeData.getDate());  
                GetTotalDB();
                GetTripListDB();
                break;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Hide_rl:
                ll_activity_vehicle_status_oil.setVisibility(View.GONE);
                break;

            case get_data:
                ContentValues values = new ContentValues();
                values.put("device_id", device_id);
                values.put("tDate", timeData.getDate());
                values.put("Content", msg.obj.toString());
                dbExcute.InsertDB(VehicleStatusActivity.this, values, Constant.TB_TripTotal);
                showTotal(msg.obj.toString());
                break;
            case get_day:
                ContentValues value = new ContentValues();
                value.put("device_id", device_id);
                value.put("tDate", timeData.getDate());
                value.put("Content", msg.obj.toString());
                dbExcute.InsertDB(VehicleStatusActivity.this, value, Constant.TB_TripList);
                jsonDays(msg.obj.toString());
                break;
            case get_DeviceStatus:
                Log.d(TAG, msg.obj.toString());
                break;
            }
        }
    };
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            Variable.carDatas.get(index_car).setCheck(false);
            Variable.carDatas.get(arg2).setCheck(true);
            carAdapter.notifyDataSetChanged();
            index_car = arg2;
            device_id = Variable.carDatas.get(arg2).getDevice_id();
            getCarLocationDB();
        }
    };
    private void getCarLocationDB(){
        if(device_id == null || device_id.equals("")){
            Toast.makeText(VehicleStatusActivity.this, "该车辆没有绑定终端，无法获取数据", Toast.LENGTH_SHORT).show();
        }else{
            GetTotalDB();
            GetTripListDB();
        }
    }
    /**
     * 显示提示框
     * @param value
     */
    private void ViewTouch(String value) {
        ll_activity_vehicle_status_oil.setVisibility(View.VISIBLE);
        tv_activity_vehicle_status_oil.setText(value + "L");
        wait = 4;
    }

    boolean isFrist = true;
    int wait = 0;

    class waitThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isWait) {
                try {
                    if (wait > 0) {
                        wait--;
                    } else {
                        Message message = new Message();
                        message.what = Hide_rl;
                        handler.sendMessage(message);
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 获取本地统计月统计数目信息并判断
     */
    private void GetTotalDB() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_TripTotal
                + " where device_id=? and tDate =? ", new String[] {device_id,timeData.getDate()});
        if(cursor.moveToFirst()){
            showTotal(cursor.getString(cursor.getColumnIndex("Content")));
        }else{
            GetDataFromUrl(3, timeData.getYear(), timeData.getMonth());
        }
        cursor.close();
        db.close();
    }
    /**
     * 获取每天的数据,画图
     */
    private void GetTripListDB(){
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_TripList
                + " where device_id=? and tDate =? ", new String[] {device_id,timeData.getDate()});
        if(cursor.moveToFirst()){
            jsonDays(cursor.getString(cursor.getColumnIndex("Content")));
        }else{
            GetDayFromUrl(3, timeData.getYear(), timeData.getMonth());
        }
        cursor.close();
        db.close();
    }

    /**
     * 获取指定月的统计数据
     * 
     * @param device_id
     * @param year
     * @param month
     */
    private void GetDataFromUrl(int device_id, String year, String month) {
        String url = Constant.BaseUrl + "device/" + device_id
                + "/trip_stat/month?auth_code=" + Variable.auth_code + "&year="
                + year + "&month=" + month;
        new Thread(new NetThread.GetDataThread(handler, url, get_data)).start();
    }

    /**
     * 显示统计信息
     * 
     * @param result
     */
    private void showTotal(String result) {
        try {            
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.opt("month_distance") != null) {
                String html = String.format(
                        getResources().getString(R.string.month_distance),
                        jsonObject.getString("month_distance"));
                tv_month_distance.setText(html);
            }
            if (jsonObject.opt("month_fuel") != null) {
                String html = String.format(
                        getResources().getString(R.string.month_fuel),
                        jsonObject.getString("month_fuel"));
                tv_month_fuel.setText(html);
            }
            if (jsonObject.opt("month_hk_fuel") != null) {
                String month_hk_fuel = jsonObject.getString("month_hk_fuel");
                month_hk_fuel = month_hk_fuel.substring(0,
                        (month_hk_fuel.indexOf(".") + 2));
                String html = String.format(
                        getResources().getString(R.string.month_hk_fuel),
                        month_hk_fuel);
                tv_month_hk_fuel.setText(html);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetDayFromUrl(int device_id, String year, String month) {
        String url = Constant.BaseUrl + "device/" + device_id
                + "/trip_stat/day?auth_code=" + Variable.auth_code
                + "&year=" + year + "&month=" + month;
        new Thread(new NetThread.GetDataThread(handler, url, get_day)).start();
    }

    private void jsonDays(String result) {
        try {
            Edistance.clear();
            Efuel.clear();
            Ehk_fuel.clear();
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String _id = jsonObject.getString("_id");
                float day_distance = Float.valueOf(jsonObject.getString("day_distance"));
                float day_fuel = Float.valueOf(jsonObject.getString("day_fuel"));
                float day_hk_fuel = Float.valueOf(jsonObject.getString("day_hk_fuel"));
                Edistance.add(new EnergyItem(_id, day_distance, "a"));
                Efuel.add(new EnergyItem(_id, day_fuel, "a"));
                Ehk_fuel.add(new EnergyItem(_id, day_hk_fuel, "a"));
            }
            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getDeviceStatus(){
        String url = Constant.BaseUrl + "device/3/fault?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url, get_DeviceStatus)).start();
        String url1 = Constant.BaseUrl + "device/3/alert?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url1, get_DeviceStatus)).start();
    }
    
    EnergyCurveView View_hk_fuel,View_distance,View_ful;
    private void initView() {
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        View_hk_fuel = new EnergyCurveView(getApplicationContext(), null);
        View_hk_fuel.setWindowsWH(dm);
        View_hk_fuel.initPoints(Ehk_fuel);
        View_hk_fuel.setOnViewTouchListener(onViewTouchListener);
        hScrollLayout.addView(View_hk_fuel);
        
        View_distance = new EnergyCurveView(getApplicationContext(), null);
        View_distance.setWindowsWH(dm);
        View_distance.initPoints(Edistance);
        View_distance.setOnViewTouchListener(onViewTouchListener);
        hScrollLayout.addView(View_distance);

        View_ful = new EnergyCurveView(getApplicationContext(),null);
        View_ful.setWindowsWH(dm);
        View_ful.initPoints(Efuel);
        View_ful.setOnViewTouchListener(onViewTouchListener);
        hScrollLayout.addView(View_ful);
    }
    private void refreshView(){
        View_hk_fuel.initPoints(Ehk_fuel);
        View_hk_fuel.RefreshView();
        View_distance.initPoints(Edistance);
        View_distance.RefreshView();
        View_ful.initPoints(Efuel);
        View_ful.RefreshView();
    }

    private void setBackground() {
        tv_month_hk_fuel.setBackgroundResource(R.color.white);
        tv_month_hk_fuel.setTextColor(getResources().getColor(R.color.common));
        tv_month_distance.setBackgroundResource(R.color.white);
        tv_month_distance.setTextColor(getResources().getColor(R.color.common));
        tv_month_fuel.setBackgroundResource(R.color.white);
        tv_month_fuel.setTextColor(getResources().getColor(R.color.common));
    }
    
    /**
     * view长按后触发的touch事件
     */
    OnViewTouchListener onViewTouchListener = new OnViewTouchListener() {
        @Override
        public void OnViewTouch(String value, int index) {
            ViewTouch(value);
        }
    };

    OnViewChangeListener onViewChangeListener = new OnViewChangeListener() {

        @Override
        public void OnViewChange(int view) {
            switch (view) {
            case 0:
                setBackground();
                tv_month_hk_fuel.setBackgroundResource(R.color.blue);
                tv_month_hk_fuel.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                setBackground();
                tv_month_distance.setBackgroundResource(R.color.blue);
                tv_month_distance.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                setBackground();
                tv_month_fuel.setBackgroundResource(R.color.blue);
                tv_month_fuel.setTextColor(getResources().getColor(R.color.white));
                break;
            }
        }

        @Override
        public void OnLastView() {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isWait = false;
    }
}