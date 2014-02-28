package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
/**
 * 我的终端
 * @author honesty
 */
public class MyDevicesActivity extends Activity{
	private static final String TAG = "MyDevicesActivity";
	private static final int Get_data = 1;
	
	GridView gv_activity_devices;
	TextView tv_activity_devices_serial,tv_activity_devices_sim,tv_activity_devices_status,
	    tv_activity_devices_service_end_date,tv_activity_devices_hardware_version,
	    tv_activity_devices_software_version;
	List<DevicesData> devicesDatas = new ArrayList<DevicesData>();
	DevicesAdapter devicesAdapter;
	boolean isJump = false;//false 加载，true 跳转
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devices);
		ImageView iv_activity_devices_menu = (ImageView)findViewById(R.id.iv_activity_devices_menu);
		iv_activity_devices_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_devices_home = (ImageView)findViewById(R.id.iv_activity_devices_home);
		iv_activity_devices_home.setOnClickListener(onClickListener);
		TextView tv_activity_devices_renewals = (TextView)findViewById(R.id.tv_activity_devices_renewals);
		tv_activity_devices_renewals.setOnClickListener(onClickListener);
		gv_activity_devices = (GridView)findViewById(R.id.gv_activity_devices);
		tv_activity_devices_serial = (TextView)findViewById(R.id.tv_activity_devices_serial);
		tv_activity_devices_sim = (TextView)findViewById(R.id.tv_activity_devices_sim);
		tv_activity_devices_status = (TextView)findViewById(R.id.tv_activity_devices_status);
		tv_activity_devices_service_end_date = (TextView)findViewById(R.id.tv_activity_devices_service_end_date);
		tv_activity_devices_hardware_version = (TextView)findViewById(R.id.tv_activity_devices_hardware_version);
		tv_activity_devices_software_version = (TextView)findViewById(R.id.tv_activity_devices_software_version);
		GetDevicesDB();
		GetDevicesData();	
		
		Intent intent = getIntent();
		isJump = intent.getBooleanExtra("isJump", false);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_devices_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.iv_activity_devices_home:
				ActivityFactory.A.ToHome();
				break;
			case R.id.tv_activity_devices_renewals:
				MyDevicesActivity.this.startActivity(new Intent(MyDevicesActivity.this, OrderServiceActivity.class));
				break;
			}
		}
	};
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_data:
                jsonDevice(msg.obj.toString());
                showGridView();
                JudgeDevice(msg.obj.toString());
                break;

            default:
                break;
            }
        }	    
	};
	private void showGridView(){
	    if(devicesDatas.size() > 0){
	        showDevice(devicesDatas.get(0));
	    }
	    DevicesData devicesData = new DevicesData();
	    devicesData.setSerial("");
	    devicesData.setCheck(false);
	    devicesDatas.add(devicesData);
	    
	    devicesAdapter = new DevicesAdapter();
	    gv_activity_devices.setAdapter(devicesAdapter);
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams(devicesDatas.size() * (px + 10),LayoutParams.WRAP_CONTENT);
        
        gv_activity_devices.setLayoutParams(params);
        gv_activity_devices.setColumnWidth(px);
        gv_activity_devices.setHorizontalSpacing(10);
        gv_activity_devices.setStretchMode(GridView.NO_STRETCH);
        gv_activity_devices.setNumColumns(devicesDatas.size());
        gv_activity_devices.setOnItemClickListener(onItemClickListener);
	}
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg2 == (devicesDatas.size() - 1)){
				MyDevicesActivity.this.startActivity(new Intent(MyDevicesActivity.this, OrderDeviceActivity.class));
			}else{
				for(int i = 0 ; i < devicesDatas.size() ; i++){
				    devicesDatas.get(i).setCheck(false);
				}
				showDevice(devicesDatas.get(arg2));
				devicesDatas.get(arg2).setCheck(true);
				devicesAdapter.notifyDataSetChanged();
			}			
		}
	};
	
	private void showDevice(DevicesData devicesData){
	    tv_activity_devices_serial.setText(devicesData.getSerial());
	    tv_activity_devices_sim.setText(devicesData.getSim());
	    tv_activity_devices_status.setText(devicesData.getStatus());
        tv_activity_devices_service_end_date.setText(devicesData.getService_end_date().substring(0, 10));
        tv_activity_devices_hardware_version.setText(devicesData.getHardware_version());
        tv_activity_devices_software_version.setText(devicesData.getSoftware_version());
        
	}
	
	boolean isHaveDevices = false;
    private void GetDevicesDB(){
        DBHelper dbHelper = new DBHelper(MyDevicesActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=? and Cust_id=?", new String[] { "Devices",Variable.cust_id });
        if (cursor.moveToFirst()) {
            String Content = cursor.getString(cursor.getColumnIndex("Content"));
            isHaveDevices = true;
            // 解析数据
            jsonDevice(Content);
        }
        cursor.close();
        db.close();
        showGridView();
        Log.d(TAG, "GetDevicesDB");
    }
	public void GetDevicesData(){
		//TODO 我的终端
		String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/device?auth_code=" + Variable.auth_code;
		new Thread(new NetThread.GetDataThread(handler, url, Get_data)).start();
	}
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
        dbExcute.UpdateDB(MyDevicesActivity.this, values, Title);
    }
    private void InsertDevice(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Cust_id", Variable.cust_id);
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(MyDevicesActivity.this, values, Constant.TB_Base);
    }
	/**
	 * 解析终端数据
	 * @param result
	 */
	private void jsonDevice(String result){
	    Log.d(TAG, result);
	    try {
	        devicesDatas.clear();
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
                devicesDatas.add(devicesData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	
	private class DevicesAdapter extends BaseAdapter{
	    LayoutInflater mInflater = LayoutInflater.from(MyDevicesActivity.this);
        @Override
        public int getCount() {
            return devicesDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return devicesDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_cars, null);
                holder = new ViewHolder();
                holder.tv_item_carnumber = (TextView) convertView.findViewById(R.id.tv_item_carnumber);
                holder.iv_item_cars = (ImageView)convertView.findViewById(R.id.iv_item_cars);
                holder.ll_item_cars = (LinearLayout)convertView.findViewById(R.id.ll_item_cars);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DevicesData devicesData = devicesDatas.get(position);
            holder.tv_item_carnumber.setText(devicesData.getSerial());
            holder.iv_item_cars.setImageResource(R.drawable.body_icon_add);
            if(devicesData.isCheck()){
                holder.ll_item_cars.setBackgroundResource(R.color.gray);
            }else{
                holder.ll_item_cars.setBackgroundDrawable(null);
            }
            return convertView;
        }	
        private class ViewHolder {
            TextView tv_item_carnumber;
            ImageView iv_item_cars;
            LinearLayout ll_item_cars;
        }
	}
	
	private class DevicesData{
	    String Device_id;
	    String Hardware_version;
	    String Serial;
	    String Service_end_date;
	    String Sim;
	    String Software_version;
	    String Status;
	    boolean isCheck;
        public String getDevice_id() {
            return Device_id;
        }
        public void setDevice_id(String device_id) {
            Device_id = device_id;
        }
        public String getHardware_version() {
            return Hardware_version;
        }
        public void setHardware_version(String hardware_version) {
            Hardware_version = hardware_version;
        }
        public String getSerial() {
            return Serial;
        }
        public void setSerial(String serial) {
            Serial = serial;
        }
        public String getService_end_date() {
            return Service_end_date;
        }
        public void setService_end_date(String service_end_date) {
            Service_end_date = service_end_date;
        }
        public String getSim() {
            return Sim;
        }
        public void setSim(String sim) {
            Sim = sim;
        }
        public String getSoftware_version() {
            return Software_version;
        }
        public void setSoftware_version(String software_version) {
            Software_version = software_version;
        }
        public String getStatus() {
            return Status;
        }
        public void setStatus(String status) {
            Status = status;
        }        
        public boolean isCheck() {
            return isCheck;
        }
        public void setCheck(boolean isCheck) {
            this.isCheck = isCheck;
        }
        @Override
        public String toString() {
            return "devicesData [Device_id=" + Device_id
                    + ", Hardware_version=" + Hardware_version + ", Serial="
                    + Serial + ", Service_end_date=" + Service_end_date
                    + ", Sim=" + Sim + ", Software_version=" + Software_version
                    + ", Status=" + Status + "]";
        }	    
	}
		
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if(isJump){
//				finish();
//			}
//			return false;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	
}
