package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OrderServiceActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_service);
		Button bt_activity_order_service_submit = (Button)findViewById(R.id.bt_activity_order_service_submit);
		bt_activity_order_service_submit.setOnClickListener(onClickListener);
	}
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_activity_order_service_submit:
				OrderServiceActivity.this.startActivity(new Intent(OrderServiceActivity.this, OrderConfirmActivity.class));
				break;

			default:
				break;
			}
		}
	};
}