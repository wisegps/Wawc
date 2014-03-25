package com.wise.wawc;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.alipay.android.app.sdk.AliPay;
import com.wise.alipay.Keys;
import com.wise.alipay.Rsa;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
/**
 * 订单确认
 * @author honesty
 */
public class OrderConfirmActivity extends Activity{
    private static final String TAG = "OrderConfirmActivity";
    private static final int submit_wap = 1;
    private static final int RQF_PAY = 2;
    private static final int submit_order = 3;
    EditText et_consignee,et_adress,et_phone;
    ImageView iv_client,iv_wap;
    boolean isClient = true;
    double money = 0.01;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WawcApplication.getActivityInstance().addActivity(this);
		setContentView(R.layout.activity_order_confirm);
		et_consignee = (EditText)findViewById(R.id.et_consignee);
		et_adress = (EditText)findViewById(R.id.et_adress);
		et_phone = (EditText)findViewById(R.id.et_phone);
		ImageView iv_activity_order_confirm_back = (ImageView)findViewById(R.id.iv_activity_order_confirm_back);
		iv_activity_order_confirm_back.setOnClickListener(onClickListener);
		Button bt_activity_order_confirm_submit = (Button)findViewById(R.id.bt_activity_order_confirm_submit);
		bt_activity_order_confirm_submit.setOnClickListener(onClickListener);
		RelativeLayout rl_client = (RelativeLayout)findViewById(R.id.rl_client);
		rl_client.setOnClickListener(onClickListener);
		RelativeLayout rl_wap = (RelativeLayout)findViewById(R.id.rl_wap);
		rl_wap.setOnClickListener(onClickListener);
		iv_client = (ImageView)findViewById(R.id.iv_client);
		iv_wap = (ImageView)findViewById(R.id.iv_wap);
		GetDBData();
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_order_confirm_back:
				finish();
				break;
			case R.id.bt_activity_order_confirm_submit:
				Toast.makeText(OrderConfirmActivity.this, "提交订单",Toast.LENGTH_SHORT).show();
				submitOrder();
				break;
			case R.id.rl_client:
			    iv_client.setVisibility(View.VISIBLE);
			    iv_wap.setVisibility(View.GONE);
			    isClient = true;
			    break;
			case R.id.rl_wap:
			    iv_client.setVisibility(View.GONE);
			    iv_wap.setVisibility(View.VISIBLE);
			    isClient = false;
                break;
			}
		}
	};
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case submit_wap:
                System.out.println(msg.obj.toString());
                jsonWapData(msg.obj.toString());
                break;

            case submit_order:
                System.out.println(msg.obj.toString());
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    String order_id = jsonObject.getString("order_id");
                    pay(order_id);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                break;
            case RQF_PAY:
                Intent intent = new Intent(Constant.A_Order);
                sendBroadcast(intent);
                WawcApplication.getActivityInstance().exit();
                ActivityFactory.A.Toorders();
                break;
            }
        }	    
	};
	private void GetDBData(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Account + " where cust_id=?", new String[]{Variable.cust_id});
        if(cursor.getCount() == 0){
        }else{
            if(cursor.moveToFirst()){
                String Consignee = cursor.getString(cursor.getColumnIndex("Consignee"));
                String Adress = cursor.getString(cursor.getColumnIndex("Adress"));
                String Phone = cursor.getString(cursor.getColumnIndex("Phone"));
                et_consignee.setText(Consignee);
                et_adress.setText(Adress);
                et_phone.setText(Phone); 
            }                
        }
        cursor.close();
        db.close();
    }
	private void submitOrder(){
	    String Consignee = et_consignee.getText().toString().trim();
	    String Adress = et_adress.getText().toString().trim();
	    String Phone = et_phone.getText().toString().trim();
	    if(Consignee.equals("")||Adress.equals("")||Phone.equals("")){
	        Toast.makeText(OrderConfirmActivity.this, "地址等信息不能为空",Toast.LENGTH_SHORT).show();
	    }else{
	        Toast.makeText(OrderConfirmActivity.this, "提交订单",Toast.LENGTH_SHORT).show();
	        String url = Constant.BaseUrl + "order?auth_code=" + Variable.auth_code;
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
	        params.add(new BasicNameValuePair("order_type", "1"));
	        params.add(new BasicNameValuePair("product_name", "OBD云终端"));
	        params.add(new BasicNameValuePair("remark", "OBD云终端"));
	        params.add(new BasicNameValuePair("unit_price", "0.01"));
	        params.add(new BasicNameValuePair("quantity", "1"));
	        params.add(new BasicNameValuePair("total_price", "0.01"));
	        new Thread(new NetThread.postDataThread(handler, url, params, submit_order)).start();
	    }        
    }
	private void pay(String order_id){
	    if(isClient){
	        clientPay();
	    }else{
	        wapPay(order_id);
	    }
	}
	private void clientPay(){
	    try {
            Log.i("ExternalPartner", "onItemClick");
            String info = getNewOrderInfo(1);
            String sign = Rsa.sign(info, Keys.PRIVATE);
            sign = URLEncoder.encode(sign);
            info += "&sign=\"" + sign + "\"&" + getSignType();
            Log.i("ExternalPartner", "start pay");
            // start the pay.
            Log.i(TAG, "info = " + info);
            
            final String orderInfo = info;
            new Thread() {
                public void run() {
                    AliPay alipay = new AliPay(OrderConfirmActivity.this, handler);
                    
                    //设置为沙箱模式，不设置默认为线上环境
                    //alipay.setSandBox(true);

                    String result = alipay.pay(orderInfo);

                    Log.i(TAG, "result = " + result);
                    Message msg = new Message();
                    msg.what = RQF_PAY;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(OrderConfirmActivity.this, "支付出错",Toast.LENGTH_SHORT).show();
        }
	}
	private String getNewOrderInfo(int position) {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Keys.DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append(getOutTradeNo());
        sb.append("\"&subject=\"");
        sb.append("subject");
        sb.append("\"&body=\"");
        sb.append("body");
        sb.append("\"&total_fee=\"");
        sb.append("0.01");
        sb.append("\"&notify_url=\"");

        // 网址需要做URL编码
        //sb.append(URLEncoder.encode("http://notify.java.jpxx.org/index.jsp"));
        sb.append(URLEncoder.encode("http://wiwc.api.wisegps.cn/pay/app_notify"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Keys.DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"1m");
        sb.append("\"");

        return new String(sb);
    }
	private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
        Date date = new Date();
        String key = format.format(date);

        java.util.Random r = new java.util.Random();
        key += r.nextInt();
        key = key.substring(0, 15);
        Log.d(TAG, "outTradeNo: " + key);
        return key;
    }
	private String getSignType() {
        return "sign_type=\"RSA\"";
    }
	
	private void wapPay(String order_id){
	    try {
	        String url = Constant.BaseUrl + "pay/get_url?auth_code=" + Variable.auth_code + "&product_name=" + URLEncoder.encode("OBD云终端", "UTF-8")
	                + "&order_id=" + order_id + "&total_price=0.01&cust_id=" + Variable.cust_id;
	        new Thread(new NetThread.GetDataThread(handler, url, submit_wap)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }	    
	}
	private void jsonWapData(String result){
	    try {
            JSONObject jsonObject = new JSONObject(result);
            String status_code = jsonObject.getString("status_code");
            String redirect = jsonObject.getString("redirect");
            if(status_code.equals("0")){
                Intent intent = new Intent(OrderConfirmActivity.this, WapZfbActivity.class);
                intent.putExtra("redirect", redirect);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }	    
	}
}