package com.wise.wawc;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
/**
 * 我的订单
 * @author honesty
 */
public class OrderMeActivity extends Activity{
    private static final String TAG = "OrderMeActivity";
    private static final int Get_order = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_me);
		ImageView iv_activity_order_me_menu = (ImageView)findViewById(R.id.iv_activity_order_me_menu);
		iv_activity_order_me_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_order_me_home = (ImageView)findViewById(R.id.iv_activity_order_me_home);
		iv_activity_order_me_home.setOnClickListener(onClickListener);
		GetOrder();
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_order_me_menu:
				ActivityFactory.A.LeftMenu();
				break;

			case R.id.iv_activity_order_me_home:
				ActivityFactory.A.ToHome();
				//OrderMeActivity.this.startActivity(new Intent(OrderMeActivity.this, SelectCityActivity.class));
				break;
			}
		}
	};
	
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_order:
                Log.d(TAG, msg.obj.toString());
                break;

            default:
                break;
            }
        }	    
	};
	
	private void GetOrder(){
	    String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/order?auth_code="+Variable.auth_code;
	    new Thread(new NetThread.GetDataThread(handler, url, Get_order)).start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}	
}