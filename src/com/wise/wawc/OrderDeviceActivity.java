package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 终端购买界面
 * @author honesty
 */
public class OrderDeviceActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_device);
		Button bt_activity_order_device_submit = (Button)findViewById(R.id.bt_activity_order_device_submit);
		bt_activity_order_device_submit.setOnClickListener(onClickListener);
		ImageView iv_activity_order_device_back = (ImageView)findViewById(R.id.iv_activity_order_device_back);
		iv_activity_order_device_back.setOnClickListener(onClickListener);
			
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_activity_order_device_submit:
			    submitOrder();
				OrderDeviceActivity.this.startActivity(new Intent(OrderDeviceActivity.this, OrderConfirmActivity.class));
				break;
			case R.id.iv_activity_order_device_back:
				finish();
				break;
			}
		}
	};
	
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 999:
                System.out.println(msg.obj.toString());
                break;

            default:
                break;
            }
        }	    
	};
	//TODO 模拟数据
	private void submitOrder(){
	    String url = Constant.BaseUrl + "order?auth_code=" + Variable.auth_code;
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
        params.add(new BasicNameValuePair("order_type", "1"));
        params.add(new BasicNameValuePair("product_name", "OBD云终端"));
        params.add(new BasicNameValuePair("remark", "OBD云终端"));
        params.add(new BasicNameValuePair("unit_price", "888"));
        params.add(new BasicNameValuePair("quantity", "1"));
        params.add(new BasicNameValuePair("total_price", "888"));
	    new Thread(new NetThread.postDataThread(handler, url, params, 999)).start();
	}
}