package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DevicesAddActivity extends Activity{
    private static final String TAG = "DevicesAddActivity";
    
    private static final int check_serial = 1;
    private static final int add_serial = 2;
    private static final int update_sim = 3;
    private static final int update_user = 4;
    private static final int update_car = 5;
    TextView tv_car;
    EditText et_serial,et_sim;
    String car_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_add);
        et_serial = (EditText)findViewById(R.id.et_serial);
        et_serial.setOnFocusChangeListener(onFocusChangeListener);
        et_sim = (EditText)findViewById(R.id.et_sim);
        et_sim.setOnFocusChangeListener(onFocusChangeListener);
        tv_car = (TextView)findViewById(R.id.tv_car);
        ImageView iv_serial = (ImageView)findViewById(R.id.iv_serial);
        iv_serial.setOnClickListener(onClickListener);
        ImageView iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(onClickListener);
        Button iv_add = (Button)findViewById(R.id.iv_add);
        iv_add.setOnClickListener(onClickListener);
    }
    OnClickListener onClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_serial:
                startActivityForResult(new Intent(DevicesAddActivity.this, BarcodeActivity.class), 0);
                break;
            case R.id.iv_add:
                Add();
                break;
            }
        }        
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case check_serial:
                Log.d(TAG, "返回=" + msg.obj.toString());
                jsonSerial(msg.obj.toString());
                break;

            case add_serial:
                jsonAddSerial(msg.obj.toString());
                break;
            case update_sim:
                Log.d(TAG, "update_sim" + msg.obj.toString());
                try {
                     String status_code = new JSONObject(msg.obj.toString()).getString("status_code");
                     if(status_code.equals("0")){
                         String url_sim = Constant.BaseUrl + "device/" + device_id + "customer?auth_code=" + Variable.auth_code;
                         List<NameValuePair> paramSim = new ArrayList<NameValuePair>();
                         paramSim.add(new BasicNameValuePair("cust_id", Variable.cust_id));
                         new Thread(new NetThread.putDataThread(handler, url_sim, paramSim, update_user)).start();
                     }else{
                         showToast();
                     }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast();
                }
                
                break;
            case update_user:
                Log.d(TAG, "更换用户 = " +msg.obj.toString());
                try {
                    String status_code = new JSONObject(msg.obj.toString()).getString("status_code");
                    if(status_code.equals("0")){
                        if(car_id != null){
                            //绑定车辆
                            String url = Constant.BaseUrl + "vehicle/" + car_id + "/device?auth_code=" + Variable.auth_code;
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("device_id", device_id));
                            new Thread(new NetThread.putDataThread(handler, url, params, update_car)).start();
                        }else{
                            //操作完成
                        }
                        //TODO 发送广播更新
                        //
                    }else{
                        showToast();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast();
                }                
                break;
            case update_car:
                Log.d(TAG, "更换车辆 = " +msg.obj.toString());
                //TODO 更新车辆数据
                break;
            }
        }        
    };
    private void showToast(){
        Toast.makeText(DevicesAddActivity.this, "添加终端失败", Toast.LENGTH_SHORT).show();
    }
    private void Add(){
        String serial = et_serial.getText().toString().trim();
        String sim = et_sim.getText().toString().trim();
        if(serial.equals("")){
            et_serial.setError("序列号不能为空");
        }else if(sim.length() != 11){
            et_sim.setError("sim格式不对");
        }else{
            String url = Constant.BaseUrl + "device/serial/" + serial + "?auth_code=" + Variable.auth_code;
            new Thread(new NetThread.GetDataThread(handler, url, add_serial)).start();
        }
    }
    String device_id;
    private void jsonAddSerial(String result){
        try {
            if(result.equals("")){
                et_serial.setError("序列号不存在");
            }else{
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if(status.equals("0")||status.equals("1")){
                    String sim = et_sim.getText().toString().trim();
                    device_id = jsonObject.getString("device_id");
                    String url = Constant.BaseUrl + "device/" + device_id + "/sim?auth_code=" + Variable.auth_code;                    
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("sim", sim));
                    new Thread(new NetThread.putDataThread(handler, url, params, update_sim)).start();
                }else if(status.equals("2")){
                    et_serial.setError("序列号已经使用");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void checkSerial(){
        String serial = et_serial.getText().toString().trim();
        String url = Constant.BaseUrl + "device/serial/" + serial + "?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url, check_serial)).start();
    }
    private void jsonSerial(String result){
        try {
            if(result.equals("")){
                et_serial.setError("序列号不存在");
            }else{
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if(status.equals("0")||status.equals("1")){
                    
                }else if(status.equals("2")){
                    et_serial.setError("序列号已经使用");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                switch (v.getId()) {
                case R.id.et_serial:
                    checkSerial();
                    break;
                case R.id.et_sim:
                    String sim = et_sim.getText().toString().trim();
                    if(sim.length() != 11){
                        et_sim.setError("sim格式不对");
                    }
                    break;
                }
            }
        }
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0){
            String result = data.getStringExtra("result");
            et_serial.setText(result);
        }        
    };
}
