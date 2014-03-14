package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.CarData;
import com.wise.extend.CarAdapter;
import com.wise.extend.OpenDateDialog;
import com.wise.extend.OpenDateDialogListener;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * 车务提醒
 * @author honesty
 */
public class CarRemindActivity extends Activity {
    private static final String TAG = "CarRemindActivity";
    private static final int get_user_date = 1;// 获取用户信息
    private static final int change_user_date = 2;// 修改用户信息
    private static final int get_car_info = 3; // 获取车辆最新信息
    private static final int Update_data = 4; // 更新车辆最新信息
    /**
     * 年检提醒
     */
    private static final int inspection = 1;
    /**
     * 车辆续保
     */
    private static final int renewal = 2;
    /**
     * 驾照年审
     */
    private static final int examined = 3;
    /**
     * 驾照换证
     */
    private static final int replacement = 4;
    LinearLayout ll_inspection, ll_renewal, ll_maintenance, ll_examined,
            ll_replacement;
    RelativeLayout rl_maintenance;
    HorizontalScrollView hsv_cars;
    TextView tv_activity_car_remind_inspection,tv_activity_car_maintenance_inspection,
            tv_activity_car_remind_renewal, tv_change_date,
            tv_annual_inspect_date;
    ImageView iv_inspection,iv_renewal,iv_maintenance,iv_examined,iv_replacement;
    CarAdapter carAdapter;
    CarData carData;// 默认指定第0个

    String annual_inspect_date = "";// 驾照年审
    String change_date = "";// 驾照换证

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_remind);
        hsv_cars = (HorizontalScrollView)findViewById(R.id.hsv_cars);
        ImageView iv_activity_car_remind_menu = (ImageView) findViewById(R.id.iv_activity_car_remind_menu);
        iv_activity_car_remind_menu.setOnClickListener(onClickListener);

        // 年检
        ll_inspection = (LinearLayout) findViewById(R.id.ll_inspection);
        ImageView iv_inspection_help = (ImageView)findViewById(R.id.iv_inspection_help);
        iv_inspection_help.setOnClickListener(onClickListener);
        RelativeLayout rl_inspection = (RelativeLayout) findViewById(R.id.rl_inspection);
        rl_inspection.setOnClickListener(onClickListener);
        iv_inspection = (ImageView) findViewById(R.id.iv_inspection);
        iv_inspection.setOnClickListener(onClickListener);
        Button bt_inspection_time = (Button) findViewById(R.id.bt_inspection_time);
        bt_inspection_time.setOnClickListener(onClickListener);
        Button bt_inspection_address = (Button) findViewById(R.id.bt_inspection_address);
        bt_inspection_address.setOnClickListener(onClickListener);
        // 续保
        ll_renewal = (LinearLayout) findViewById(R.id.ll_renewal);
        ImageView iv_renewal_help = (ImageView)findViewById(R.id.iv_renewal_help);
        iv_renewal_help.setOnClickListener(onClickListener);
        RelativeLayout rl_renewal = (RelativeLayout) findViewById(R.id.rl_renewal);
        rl_renewal.setOnClickListener(onClickListener);
        iv_renewal = (ImageView) findViewById(R.id.iv_renewal);
        iv_renewal.setOnClickListener(onClickListener);
        Button bt_renewal_time = (Button) findViewById(R.id.bt_renewal_time);
        bt_renewal_time.setOnClickListener(onClickListener);
        Button bt_renewal_call = (Button) findViewById(R.id.bt_renewal_call);
        bt_renewal_call.setOnClickListener(onClickListener);
        // 保养
        rl_maintenance = (RelativeLayout)findViewById(R.id.rl_maintenance);
        ll_maintenance = (LinearLayout) findViewById(R.id.ll_maintenance);
        ImageView iv_maintenance_help = (ImageView)findViewById(R.id.iv_maintenance_help);
        iv_maintenance_help.setOnClickListener(onClickListener);
        RelativeLayout rl_maintenance = (RelativeLayout) findViewById(R.id.rl_maintenance);
        rl_maintenance.setOnClickListener(onClickListener);
        iv_maintenance = (ImageView) findViewById(R.id.iv_maintenance);
        iv_maintenance.setOnClickListener(onClickListener);
        Button bt_maintenance = (Button) findViewById(R.id.bt_maintenance);
        bt_maintenance.setOnClickListener(onClickListener);
        Button bt_maintenance_call = (Button) findViewById(R.id.bt_maintenance_call);
        bt_maintenance_call.setOnClickListener(onClickListener);
        tv_activity_car_maintenance_inspection = (TextView)findViewById(R.id.tv_activity_car_maintenance_inspection);
        // 年审
        ll_examined = (LinearLayout) findViewById(R.id.ll_examined);
        ImageView iv_examined_help = (ImageView)findViewById(R.id.iv_examined_help);
        iv_examined_help.setOnClickListener(onClickListener);
        RelativeLayout rl_examined = (RelativeLayout) findViewById(R.id.rl_examined);
        rl_examined.setOnClickListener(onClickListener);
        iv_examined = (ImageView) findViewById(R.id.iv_examined);
        iv_examined.setOnClickListener(onClickListener);
        Button bt_examined_time = (Button) findViewById(R.id.bt_examined_time);
        bt_examined_time.setOnClickListener(onClickListener);
        Button bt_examined_address = (Button) findViewById(R.id.bt_examined_address);
        bt_examined_address.setOnClickListener(onClickListener);
        tv_annual_inspect_date = (TextView) findViewById(R.id.tv_annual_inspect_date);
        // 驾照
        ll_replacement = (LinearLayout) findViewById(R.id.ll_replacement);
        ImageView iv_replacement_help = (ImageView)findViewById(R.id.iv_replacement_help);
        iv_replacement_help.setOnClickListener(onClickListener);
        RelativeLayout rl_replacement = (RelativeLayout) findViewById(R.id.rl_replacement);
        rl_replacement.setOnClickListener(onClickListener);
        iv_replacement = (ImageView) findViewById(R.id.iv_replacement);
        iv_replacement.setOnClickListener(onClickListener);
        Button bt_replacement_time = (Button) findViewById(R.id.bt_replacement_time);
        bt_replacement_time.setOnClickListener(onClickListener);
        Button bt_replacement_address = (Button) findViewById(R.id.bt_replacement_address);
        bt_replacement_address.setOnClickListener(onClickListener);
        tv_change_date = (TextView) findViewById(R.id.tv_change_date);

        tv_activity_car_remind_inspection = (TextView) findViewById(R.id.tv_activity_car_remind_inspection);
        tv_activity_car_remind_renewal = (TextView) findViewById(R.id.tv_activity_car_remind_renewal);

        GridView gv_activity_car_remind = (GridView) findViewById(R.id.gv_activity_car_remind);
        carAdapter = new CarAdapter(CarRemindActivity.this, Variable.carDatas);
        gv_activity_car_remind.setAdapter(carAdapter);

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                Constant.ImageWidth, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams((Variable.carDatas.size()
                * (px + 10) + 10), LayoutParams.WRAP_CONTENT);
        gv_activity_car_remind.setLayoutParams(params);
        gv_activity_car_remind.setColumnWidth(px);
        gv_activity_car_remind.setHorizontalSpacing(10);
        gv_activity_car_remind.setStretchMode(GridView.NO_STRETCH);
        gv_activity_car_remind.setNumColumns(Variable.carDatas.size());
        gv_activity_car_remind.setOnItemClickListener(onItemClickListener);

        if (Variable.carDatas != null && Variable.carDatas.size() > 0) {
            SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
            int DefaultVehicleID = preferences.getInt(Constant.DefaultVehicleID, 0);
            carData = Variable.carDatas.get(DefaultVehicleID);
            ShowText(carData);
            getCarRemindFromUrl();
            if(Variable.carDatas.size() == 1){
                hsv_cars.setVisibility(View.GONE);
            }else{
                hsv_cars.setVisibility(View.VISIBLE);
            }
        }
        GetDBData();

        OpenDateDialog.SetCustomDateListener(new OpenDateDialogListener() {
            @Override
            public void OnDateChange(String Date, int index) {
                System.out.println(Date);
                switch (index) {
                case inspection:
                    System.out.println("更新车辆年检时间");
                    car_remind_inspection(Date);
                    carData.setAnnual_inspect_date(Date + " 00:00:00");
                    changeCarInfo();
                    break;
                case renewal:
                    System.out.println("车辆续保时间");
                    car_renewal(Date);
                    carData.setInsurance_date(Date + " 00:00:00");
                    changeCarInfo();
                    break;
                case examined:
                    System.out.println("驾照年审");
                    annual_inspect_date = Date;
                    tv_annual_inspect_date.setText(String.format(getResources()
                            .getString(R.string.examined_content),
                            annual_inspect_date));
                    ChangeUserDate();
                    break;
                case replacement:
                    System.out.println("驾照换证");
                    change_date = Date;
                    tv_change_date.setText(String.format(getResources()
                            .getString(R.string.replacement_content),
                            change_date));
                    ChangeUserDate();
                    break;
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case change_user_date:
                jsonChangeUserInfo(msg.obj.toString());
                GetDBData();
                break;
            case get_user_date:
                jsonUserInfo(msg.obj.toString());
                GetDBData();
                break;
            case get_car_info:
                jsonCarInfo(msg.obj.toString());
                break;
            case Update_data:
                jsonChangeCarInfo(msg.obj.toString());
                break;
            }
        }
    };
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_car_remind_menu:
                finish();
                break;
            case R.id.rl_inspection:
                hideLinearlayout();
                iv_inspection.setImageResource(R.drawable.body_icon_packup);
                ll_inspection.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_renewal:
                hideLinearlayout();
                iv_renewal.setImageResource(R.drawable.body_icon_packup);
                ll_renewal.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_maintenance:
                hideLinearlayout();
                iv_maintenance.setImageResource(R.drawable.body_icon_packup);
                ll_maintenance.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_examined:
                hideLinearlayout();
                iv_examined.setImageResource(R.drawable.body_icon_packup);
                ll_examined.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_replacement:
                hideLinearlayout();
                iv_replacement.setImageResource(R.drawable.body_icon_packup);
                ll_replacement.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_maintenance:// 车辆保养
                setMileage();
                break;
            case R.id.bt_inspection_time:// 年检提醒
                ShowDate(inspection);
                break;
            case R.id.iv_inspection_help:
                turnActivity("车辆年检","http://wiwc.api.wisegps.cn/help/clnj");
                break;
            case R.id.bt_renewal_time:// 车辆续保
                ShowDate(renewal);
                break;
            case R.id.iv_renewal_help:
                turnActivity("车辆续保","http://wiwc.api.wisegps.cn/help/clxb");
                break;
            case R.id.bt_examined_time:// 驾照年审
                ShowDate(examined);
                break;
            case R.id.bt_replacement_time:// 驾照换证
                ShowDate(replacement);
                break;
            case R.id.bt_inspection_address:// 年检提醒
                ToDealAdress(getString(R.string.inspection_title), 1);
                break;
            case R.id.bt_examined_address:// 驾照年审
                ToDealAdress(getString(R.string.examined_title), 2);
                break;
            case R.id.iv_examined_help:
                turnActivity("驾照年审及换证","http://wiwc.api.wisegps.cn/help/clby");
                break;
            case R.id.bt_replacement_address:// 驾照换证
                ToDealAdress(getString(R.string.replacement_title), 2);
                break;
            case R.id.iv_replacement_help:
                turnActivity("驾照年审及换证","http://wiwc.api.wisegps.cn/help/clby");
                break;
            case R.id.bt_renewal_call:// 车辆续保
                ToCall(carData.getInsurance_tel());
                break;
            case R.id.bt_maintenance_call:// 车辆保养
                ToCall(carData.getMaintain_tel());
                break;
            case R.id.iv_maintenance_help:
                turnActivity("车辆保养","http://wiwc.api.wisegps.cn/help/clby");
                break;
            }
        }
    };
    
    private void setMileage(){
        View view_mileage = LayoutInflater.from(CarRemindActivity.this).inflate(R.layout.set_mileage, null);
        final EditText et_mileage = (EditText)view_mileage.findViewById(R.id.et_mileage);
        AlertDialog.Builder builder = new AlertDialog.Builder(CarRemindActivity.this);
        builder.setTitle("设置");
        builder.setView(view_mileage);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {                    
            public void onClick(DialogInterface dialog, int which) {
                String next_mileage = et_mileage.getText().toString();
                if(next_mileage.equals("")){
                    Toast.makeText(CarRemindActivity.this, "里程不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    carData.setMaintain_next_mileage(next_mileage);
                    carMaintenanceDate(mileage);
                    changeCarInfo();
                }
            }
        });
        builder.setNegativeButton(R.string.cancle, null);
        builder.show();
    }
    
    private void turnActivity(String Title , String url){
        Intent intent = new Intent(CarRemindActivity.this, WapActivity.class);
        intent.putExtra("Title", Title);
        intent.putExtra("url", url);
        startActivity(intent);
    }
    private void ChangeUserDate() {
        String url = Constant.BaseUrl + "customer/" + Variable.cust_id
                + "/inspect_date?auth_code=" + Variable.auth_code;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("annual_inspect_date",
                annual_inspect_date));
        params.add(new BasicNameValuePair("change_date", change_date));
        new Thread(new NetThread.putDataThread(handler, url, params,
                change_user_date)).start();
    }

    private void hideLinearlayout() {
        ll_inspection.setVisibility(View.GONE);
        ll_renewal.setVisibility(View.GONE);
        ll_maintenance.setVisibility(View.GONE);
        ll_examined.setVisibility(View.GONE);
        ll_replacement.setVisibility(View.GONE);
        
        iv_inspection.setImageResource(R.drawable.body_icon_unfold);
        iv_renewal.setImageResource(R.drawable.body_icon_unfold);
        iv_maintenance.setImageResource(R.drawable.body_icon_unfold);
        iv_examined.setImageResource(R.drawable.body_icon_unfold);
        iv_replacement.setImageResource(R.drawable.body_icon_unfold);
    }

    private void ToDealAdress(String Title, int Type) {
        Intent intent = new Intent(CarRemindActivity.this,
                DealAddressActivity.class);
        intent.putExtra("Title", Title);
        intent.putExtra("Type", Type);
        startActivity(intent);
    }

    private void ToCall(String phone) {
        Log.d(TAG, "tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void ShowDate(int index) {
        OpenDateDialog.ShowDate(CarRemindActivity.this, index);
    }

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            for (int i = 0; i < Variable.carDatas.size(); i++) {
                Variable.carDatas.get(i).setCheck(false);
            }
            Variable.carDatas.get(arg2).setCheck(true);
            carAdapter.notifyDataSetChanged();
            carData = Variable.carDatas.get(arg2);
            ShowText(carData);
            getCarRemindFromUrl();
        }
    };
    private void car_remind_inspection(String Date){
        String Annual_inspect_date = String.format(
                getResources().getString(R.string.inspection_content), Date);
        tv_activity_car_remind_inspection.setText(Annual_inspect_date);
        if (GetSystem.isTimeOut(Date + " 00:00:00")) {
            tv_activity_car_remind_inspection.setTextColor(getResources()
                    .getColor(R.color.red));
        } else {
            tv_activity_car_remind_inspection.setTextColor(getResources()
                    .getColor(R.color.common_inactive));
        }
    }
    private void car_renewal(String Date){
        String Insurance_date = String.format(
                getResources().getString(R.string.renewal_content), Date);
        tv_activity_car_remind_renewal.setText(Insurance_date);
        if (GetSystem.isTimeOut(Date + " 00:00:00")) {
            tv_activity_car_remind_renewal.setTextColor(getResources()
                    .getColor(R.color.red));
        } else {
            tv_activity_car_remind_renewal.setTextColor(getResources()
                    .getColor(R.color.common_inactive));
        }
    }

    /**
     * 显示车辆信息
     */
    private void ShowText(CarData carData) {
        Log.d(TAG, carData.toString());
        if(carData.getAnnual_inspect_date() == null || carData.getAnnual_inspect_date().equals("")){
            car_remind_inspection("");
        }else{
            car_remind_inspection(carData.getAnnual_inspect_date().substring(0, 10));
        }
        car_renewal(carData.getInsurance_date().substring(0, 10));
        
        tv_activity_car_maintenance_inspection.setText(String.format(
                getResources().getString(R.string.maintenance_content),carData.getMaintain_next_mileage()));
        tv_activity_car_maintenance_inspection.setTextColor(getResources().getColor(
                R.color.common_inactive));
    }
    /**
     * 获取用户信息    
     */
    private void GetDBData() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Account
                + " where cust_id=?", new String[] { Variable.cust_id });
        if (cursor.getCount() == 0) {
            String url = Constant.BaseUrl + "customer/" + Variable.cust_id
                    + "?auth_code=" + Variable.auth_code;
            new Thread(new NetThread.GetDataThread(handler, url, get_user_date))
                    .start();
        } else {
            if (cursor.moveToFirst()) {
                annual_inspect_date = cursor.getString(cursor
                        .getColumnIndex("annual_inspect_date"));
                change_date = cursor.getString(cursor
                        .getColumnIndex("change_date"));
                userInspectDate(annual_inspect_date);
                userChangeDate(change_date);
            }
        }
    }
    /**
     * 判断证件年检时间
     * @param annual_inspect_date
     */
    private void userInspectDate(String annual_inspect_date) {
        if(annual_inspect_date == null){
            tv_annual_inspect_date.setText("未设置年检时间");
            tv_annual_inspect_date.setTextColor(getResources().getColor(
                    R.color.common_inactive));
        }else{
            tv_annual_inspect_date.setText(String.format(
                    getResources().getString(R.string.examined_content),
                    annual_inspect_date.substring(0, 10)));
            if (GetSystem.isTimeOut(annual_inspect_date)) {
                tv_annual_inspect_date.setTextColor(getResources().getColor(
                        R.color.red));
            } else {
                tv_annual_inspect_date.setTextColor(getResources().getColor(
                        R.color.common_inactive));
            }
        }        
    }
    /**
     * 判断换证时间
     * @param change_date
     */
    private void userChangeDate(String change_date) {
        if(change_date == null){
            tv_change_date.setText("未设置换证时间");
            tv_change_date.setTextColor(getResources().getColor(
                    R.color.common_inactive));
        }else{
            tv_change_date.setText(String.format(
                    getResources().getString(R.string.replacement_content),
                    change_date.substring(0, 10)));
            if (GetSystem.isTimeOut(change_date)) {
                tv_change_date.setTextColor(getResources().getColor(R.color.red));
            } else {
                tv_change_date.setTextColor(getResources().getColor(
                        R.color.common_inactive));
            } 
        }        
    }
    /**
     * 解析用户信息
     * @param result
     */
    private void jsonUserInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            DBExcute dbExcute = new DBExcute();
            ContentValues values = new ContentValues();
            if (jsonObject.opt("contacts") != null) {
                String contacts = jsonObject.getString("contacts");
                values.put("Consignee", contacts);
            }
            if (jsonObject.opt("address") != null) {
                String address = jsonObject.getString("address");
                values.put("Adress", address);
            }
            if (jsonObject.opt("tel") != null) {
                String tel = jsonObject.getString("tel");
                values.put("Phone", tel);
            }
            if (jsonObject.opt("annual_inspect_date") != null) {
                annual_inspect_date = jsonObject
                        .getString("annual_inspect_date").replace("T", " ")
                        .substring(0, 19);
                userInspectDate(annual_inspect_date);
                values.put("annual_inspect_date", annual_inspect_date);
            }
            if (jsonObject.opt("change_date") != null) {
                change_date = jsonObject.getString("change_date")
                        .replace("T", " ").substring(0, 19);
                userChangeDate(change_date);
                values.put("change_date", change_date);
            }
            values.put("cust_id", Variable.cust_id);
            dbExcute.InsertDB(CarRemindActivity.this, values,
                    Constant.TB_Account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 更新用户信息后保存
     * @param result
     */
    private void jsonChangeUserInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("status_code").equals("0")) {
                // 更新DB
                DBExcute dbExcute = new DBExcute();
                ContentValues values = new ContentValues();
                values.put("annual_inspect_date", annual_inspect_date + " 00:00:00");
                values.put("change_date", change_date + " 00:00:00");
                dbExcute.UpdateDB(this, values, "cust_id=?",
                        new String[] { Variable.cust_id }, Constant.TB_Account);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 从url获取车辆信息
     */
    private void getCarRemindFromUrl() {
        if(carData.getDevice_id() == null || carData.getDevice_id().equals("")){
            //TODO TIAOSHI
            rl_maintenance.setVisibility(View.GONE);
        }else{
            rl_maintenance.setVisibility(View.VISIBLE);
            String url = Constant.BaseUrl + "device/" + carData.getDevice_id()
                    + "/active_gps_data?auth_code=" + Variable.auth_code;
            new Thread(new NetThread.GetDataThread(handler, url, get_car_info))
                    .start();
        }        
    }
    int mileage = 0;
    /**
     * 解析车辆里程
     * @param result
     */
    private void jsonCarInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("device_id")
                    .equals(carData.getDevice_id())) {
                mileage = jsonObject.getJSONObject("active_gps_data").getInt("mileage");
                carMaintenanceDate(mileage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 计算车辆里程
     * @param mileage
     */
    private void carMaintenanceDate(int mileage) {
        int next_mileage = Integer.valueOf(carData.getMaintain_next_mileage());
        if(next_mileage > mileage){
            String l = "" + (next_mileage - mileage);
            tv_activity_car_maintenance_inspection.setTextColor(getResources().getColor(
                    R.color.common_inactive));
            tv_activity_car_maintenance_inspection.setText(String.format(
                    getResources().getString(R.string.maintenance_content),l));
        }else{
            tv_activity_car_maintenance_inspection.setText(String.format(
                    getResources().getString(R.string.maintenance_content),"0"));
            tv_activity_car_maintenance_inspection.setTextColor(getResources().getColor(
                    R.color.red));
        }
    }
    
    private void changeCarInfo(){
        String url = Constant.BaseUrl + "vehicle/" + carData.getObj_id() +"inspect_date?auth_code=" + Variable.auth_code;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("insurance_date", carData.getAnnual_inspect_date()));
        params.add(new BasicNameValuePair("annual_inspect_date", carData.getInsurance_date()));
        params.add(new BasicNameValuePair("maintain_next_mileage", carData.getMaintain_next_mileage())); 
        System.out.println(carData.toString());
        new Thread(new NetThread.putDataThread(handler, url, params, Update_data)).start();
    }
    private void jsonChangeCarInfo(String result){
        Log.d(TAG, result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}