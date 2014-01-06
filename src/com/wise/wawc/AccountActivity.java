package com.wise.wawc;
import com.wise.pubclas.Config;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
/**
 * 我的账户
 * @author honesty
 */
public class AccountActivity extends Activity{
	private View view = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		view = findViewById(R.id.account_to_my_vehicle);
		view.setOnClickListener(onClickListener);
		ImageView iv_activity_account_menu = (ImageView)findViewById(R.id.iv_activity_account_menu);
		iv_activity_account_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_account_home = (ImageView)findViewById(R.id.iv_activity_account_home);
		iv_activity_account_home.setOnClickListener(onClickListener);
		Button bt_activity_account_logout = (Button)findViewById(R.id.bt_activity_account_logout);
		bt_activity_account_logout.setOnClickListener(onClickListener);
		ShareSDK.initSDK(this);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.iv_activity_account_menu:				
				ActivityFactory.A.LeftMenu();
				finish();
				break;
			case R.id.iv_activity_account_home:
				ActivityFactory.A.ToHome();
				finish();
				break;
			case R.id.account_to_my_vehicle:
				startActivity(new Intent(AccountActivity.this,MyVehicleActivity.class));
				break;
			case R.id.bt_activity_account_logout:
				Platform platformQQ = ShareSDK.getPlatform(AccountActivity.this,QZone.NAME);
				Platform platformSina = ShareSDK.getPlatform(AccountActivity.this,SinaWeibo.NAME);
				platformQQ.removeAccount();
				platformSina.removeAccount();
				Config.qqUserName = "";
				finish();
				break;
			}
		}
	};
}
