package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
/**
 * 欢迎界面
 * @author honesty
 */
public class WelcomeActivity extends Activity{
	private final static int Wait = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		new Thread(new WaitThread()).start();
	}
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Wait:
				startActivity(new Intent(WelcomeActivity.this, SelectCityActivity.class));
				finish();
				break;

			default:
				break;
			}
		}		
	};
	class WaitThread extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(2000);
				Message message = new Message();
				message.what = Wait;
				handler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}