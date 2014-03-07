package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 保险公司
 */
public class InsuranceActivity extends Activity implements
        IXListViewListener {
    private static final int getData = 1;
    
    XListView lv_insurance;
    List<InsuranceData> insuranceDatas = new ArrayList<InsuranceData>();
    InSuranceAdapter inSuranceAdapter;
    boolean isNeedPhone = false;
    int code;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);
        Intent intent = getIntent();
        isNeedPhone = intent.getBooleanExtra("isNeedPhone", false);
        code = intent.getIntExtra("code", 0);
        lv_insurance = (XListView) findViewById(R.id.lv_insurance);
        lv_insurance.setXListViewListener(this);
        lv_insurance.setPullLoadEnable(false);
        inSuranceAdapter = new InSuranceAdapter();
        lv_insurance.setAdapter(inSuranceAdapter);
        lv_insurance.setOnItemClickListener(onItemClickListener);
        getDBData();
        ImageView iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case getData:
                jsonData(msg.obj.toString());
                //插入到服务器
                JudgeInsurance(msg.obj.toString());
                onLoad();
                break;

            default:
                break;
            }
        }
    };
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            if(!isNeedPhone){
                Intent intent = new Intent();
                InsuranceData insuranceData = (InsuranceData) lv_insurance.getItemAtPosition(arg2);
                intent.putExtra("insurance_name", insuranceData.getName());
                intent.putExtra("insurance_phone", insuranceData.getService_phone());
                if(code == NewVehicleActivity.newVehicleInsurance){
                    InsuranceActivity.this.setResult(NewVehicleActivity.newVehicleInsurance, intent);
                }else if(code == MyVehicleActivity.resultCodeInsurance){
                    InsuranceActivity.this.setResult(MyVehicleActivity.resultCodeInsurance, intent);
                }
                InsuranceActivity.this.finish();
            }
        }
    };

    boolean isHaveInsurance = false;
    private void getDBData() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=?", new String[] { "insurance" });
        if (cursor.getCount() == 0) {
            String url = Constant.BaseUrl + "base/insurance";
            new Thread(new NetThread.GetDataThread(handler, url, getData))
                    .start();
        } else {
            if (cursor.moveToFirst()) {
                isHaveInsurance = true;
                String Content = cursor.getString(cursor.getColumnIndex("Content"));
                jsonData(Content);
            }
        }
    }
    private void jsonData(String result){
        try {
            insuranceDatas.clear();
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                InsuranceData insuranceData = new InsuranceData();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                insuranceData.setName(jsonObject.getString("name"));
                insuranceData.setService_phone(jsonObject.getString("service_phone"));
                insuranceDatas.add(insuranceData);
            }
            inSuranceAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void JudgeInsurance(String result) {
        if (isHaveInsurance) {// 更新
            UpdateInsurance(result, "insurance");
        } else {// 插入
            InsertInsurance(result, "insurance");
        }
    }
    private void UpdateInsurance(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Content", result);
        dbExcute.UpdateDB(InsuranceActivity.this, values, Title);
    }
    private void InsertInsurance(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(InsuranceActivity.this, values, Constant.TB_Base);
    }

    @Override
    public void onRefresh() {
        String url = Constant.BaseUrl + "base/insurance";
        new Thread(new NetThread.GetDataThread(handler, url, getData))
                .start();
    }
    private void onLoad() {
        lv_insurance.stopRefresh();
        lv_insurance.stopLoadMore();
    }

    @Override
    public void onLoadMore() {}

    private class InSuranceAdapter extends BaseAdapter {
        LayoutInflater mInflater = LayoutInflater.from(InsuranceActivity.this);
        @Override
        public int getCount() {
            return insuranceDatas.size();
        }
        @Override
        public Object getItem(int position) {
            return insuranceDatas.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_insurance, null);
                holder = new ViewHolder();
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_phone = (ImageView) convertView.findViewById(R.id.iv_phone);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            final InsuranceData insuranceData = insuranceDatas.get(position);
            holder.tv_phone.setText(insuranceData.getService_phone());
            holder.tv_name.setText(insuranceData.getName());
            if(isNeedPhone){
                holder.iv_phone.setVisibility(View.VISIBLE);
                holder.iv_phone.setOnClickListener(new OnClickListener() {                    
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ insuranceData.getService_phone()));  
                        startActivity(intent);
                    }
                });
            }else{
                holder.iv_phone.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
        private class ViewHolder {
            TextView tv_phone,tv_name;
            ImageView iv_phone;
        }
    }

    private class InsuranceData {
        String name;
        String service_phone;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getService_phone() {
            return service_phone;
        }

        public void setService_phone(String service_phone) {
            this.service_phone = service_phone;
        }
    }
}