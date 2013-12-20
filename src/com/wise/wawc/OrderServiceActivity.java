package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
/**
 * 订购服务
 * @author honesty
 */
public class OrderServiceActivity extends Activity{
	private static final String TAG = "OrderServiceActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_service);
		Button bt_activity_order_service_submit = (Button)findViewById(R.id.bt_activity_order_service_submit);
		bt_activity_order_service_submit.setOnClickListener(onClickListener);
		ImageView iv_activity_order_device_back = (ImageView)findViewById(R.id.iv_activity_order_device_back);
		iv_activity_order_device_back.setOnClickListener(onClickListener);
	}
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_activity_order_service_submit:
				OrderServiceActivity.this.startActivity(new Intent(OrderServiceActivity.this, OrderConfirmActivity.class));
				break;

			case R.id.iv_activity_order_device_back:
				finish();
				break;
			}
		}
	};
}