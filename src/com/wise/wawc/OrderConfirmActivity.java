package com.wise.wawc;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import com.alipay.android.app.sdk.AliPay;
import com.wise.alipay.Keys;
import com.wise.alipay.Rsa;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 订单确认
 * @author honesty
 */
public class OrderConfirmActivity extends Activity{
    private static final String TAG = "OrderConfirmActivity";
    private static final int submit_wap = 1;
    private static final int RQF_PAY = 2;
    TextView tv_client,tv_wap;
    boolean isClient = true;
    double money = 0.01;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_confirm);
		ImageView iv_activity_order_confirm_back = (ImageView)findViewById(R.id.iv_activity_order_confirm_back);
		iv_activity_order_confirm_back.setOnClickListener(onClickListener);
		Button bt_activity_order_confirm_submit = (Button)findViewById(R.id.bt_activity_order_confirm_submit);
		bt_activity_order_confirm_submit.setOnClickListener(onClickListener);
		RelativeLayout rl_client = (RelativeLayout)findViewById(R.id.rl_client);
		rl_client.setOnClickListener(onClickListener);
		RelativeLayout rl_wap = (RelativeLayout)findViewById(R.id.rl_wap);
		rl_wap.setOnClickListener(onClickListener);
		tv_client = (TextView)findViewById(R.id.tv_client);
		tv_wap = (TextView)findViewById(R.id.tv_wap);
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
				pay();
				break;
			case R.id.rl_client:
			    tv_client.setVisibility(View.VISIBLE);
			    tv_wap.setVisibility(View.GONE);
			    isClient = true;
			    break;
			case R.id.rl_wap:
			    tv_client.setVisibility(View.GONE);
			    tv_wap.setVisibility(View.VISIBLE);
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

            default:
                break;
            }
        }	    
	};
	private void pay(){
	    if(isClient){
	        clientPay();
	    }else{
	        wapPay();
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
        sb.append(URLEncoder.encode("http://notify.java.jpxx.org/index.jsp"));
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
	
	private void wapPay(){
	    String url = Constant.BaseUrl + "pay/get_url?auth_code=" + Variable.auth_code + "&product_name=OBD云终端"
	            + "&order_id=3012353453045300000000002&total_price=0.01&cust_id=" + Variable.cust_id;
	    new Thread(new NetThread.GetDataThread(handler, url, submit_wap)).start();
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