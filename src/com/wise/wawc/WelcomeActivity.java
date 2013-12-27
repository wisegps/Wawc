package com.wise.wawc;

import com.wise.pubclas.Config;
import com.wise.pubclas.GetSystem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
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
		TextView tv_activity_welcom_version = (TextView)findViewById(R.id.tv_activity_welcom_version);
		tv_activity_welcom_version.setText("V" + GetSystem.GetVersion(getApplicationContext(), Config.PackageName));
		isOffline();
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
	/**
     * 判断网络连接状况，没网则提示打开网络
     */
    private void isOffline(){
    	if(!GetSystem.checkNetWorkStatus(getApplicationContext())){
			setNetworkMethod();
		}else{
			new Thread(new WaitThread()).start();			
		}
    }
    /*
     * 打开设置网络界面
     * */
    public void setNetworkMethod(){
    	new AlertDialog.Builder(WelcomeActivity.this).setTitle(R.string.system_note)
    	.setMessage(R.string.network_error)
    	.setPositiveButton(R.string.set_network, new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		}).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).show();
    }
	class WaitThread extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(1000);
				Message message = new Message();
				message.what = Wait;
				handler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}