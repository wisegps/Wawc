package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 订单确认
 * @author honesty
 */
public class OrderConfirmActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_confirm);
		ImageView iv_activity_order_confirm_back = (ImageView)findViewById(R.id.iv_activity_order_confirm_back);
		iv_activity_order_confirm_back.setOnClickListener(onClickListener);
		Button bt_activity_order_confirm_submit = (Button)findViewById(R.id.bt_activity_order_confirm_submit);
		bt_activity_order_confirm_submit.setOnClickListener(onClickListener);
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
				break;
			}
		}
	};
}
