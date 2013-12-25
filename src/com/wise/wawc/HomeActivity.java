package com.wise.wawc;

import java.util.ArrayList;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.wise.data.CharacterParser;
import com.wise.extend.HScrollLayout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 首页
 * @author honesty
 */
public class HomeActivity extends Activity implements RecognizerDialogListener{
	private RecognizerDialog recognizerDialog = null;    //语音合成文字
	StringBuffer sb = null;
	private ImageView saySomething = null;  //语音识别
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ImageView iv_activity_home_menu = (ImageView)findViewById(R.id.iv_activity_home_menu);
		iv_activity_home_menu.setOnClickListener(onClickListener);
		ImageView iv_activity_car_home_search = (ImageView)findViewById(R.id.iv_activity_car_home_search);
		iv_activity_car_home_search.setOnClickListener(onClickListener);
		Button bt_activity_home_help = (Button)findViewById(R.id.bt_activity_home_help);
		bt_activity_home_help.setOnClickListener(onClickListener);
		Button bt_activity_home_risk = (Button)findViewById(R.id.bt_activity_home_risk);
		bt_activity_home_risk.setOnClickListener(onClickListener);
		//ScrollLayout_car
		RelativeLayout rl_activity_home_car = (RelativeLayout)findViewById(R.id.rl_activity_home_car);
		rl_activity_home_car.setOnClickListener(onClickListener);
		Button bt_activity_home_vehicle_status = (Button)findViewById(R.id.bt_activity_home_vehicle_status);
		bt_activity_home_vehicle_status.setOnClickListener(onClickListener);
		Button bt_activity_home_car_remind = (Button)findViewById(R.id.bt_activity_home_car_remind);
		bt_activity_home_car_remind.setOnClickListener(onClickListener);
		Button bt_activity_home_traffic = (Button)findViewById(R.id.bt_activity_home_traffic);
		bt_activity_home_traffic.setOnClickListener(onClickListener);
		Button bt_activity_home_share = (Button)findViewById(R.id.bt_activity_home_share);
		bt_activity_home_share.setOnClickListener(onClickListener);
		TextView tv_activity_home_car_adress = (TextView)findViewById(R.id.tv_activity_home_car_adress);
		tv_activity_home_car_adress.setOnClickListener(onClickListener);
		
		
		saySomething = (ImageView) findViewById(R.id.iv_home_say_something);
		saySomething.setOnClickListener(onClickListener);
		sb = new StringBuffer();
		
		HScrollLayout ScrollLayout_other = (HScrollLayout)findViewById(R.id.ScrollLayout_other);
		LayoutInflater mLayoutInflater = LayoutInflater.from(HomeActivity.this);
        View weatherView = mLayoutInflater.inflate(R.layout.item_weather, null);
        View oilView = mLayoutInflater.inflate(R.layout.item_oil, null);
    	ScrollLayout_other.addView(weatherView);
    	ScrollLayout_other.addView(oilView);
        
    	// 注册（将语音转文字）
     	recognizerDialog = new RecognizerDialog(this, "appid=5281eaf4");
     	recognizerDialog.setListener(this);
     	recognizerDialog.setEngine("sms", "", null);
     	recognizerDialog.setSampleRate(RATE.rate16k);
 		sb = new StringBuffer();
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_home_menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.iv_activity_car_home_search:
				ActivityFactory.A.RightMenu();
				break;
			case R.id.bt_activity_home_help://救援
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, ShareLocationActivity.class));
				break;
			case R.id.bt_activity_home_risk://报险
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, ShareLocationActivity.class));
				break;
			case R.id.bt_activity_home_share://位置分享
				Intent intent = new Intent(HomeActivity.this, NewArticleActivity.class);
				intent.putExtra("isSNS", true);
				HomeActivity.this.startActivity(intent);
				break;
			case R.id.bt_activity_home_traffic://车辆违章
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, TrafficActivity.class));
				break;
			case R.id.bt_activity_home_car_remind://车务提醒
				Intent eventIntent = new Intent(HomeActivity.this, CarRemindActivity.class);
				eventIntent.putExtra("isFinish", true);
				HomeActivity.this.startActivity(eventIntent);
				break;
			case R.id.bt_activity_home_vehicle_status://爱车车况
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, VehicleStatusActivity.class));
				break;
			case R.id.tv_activity_home_car_adress: //车辆位置
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, CarLocationActivity.class));
				break;
			case R.id.rl_activity_home_car: //我的爱车
				HomeActivity.this.startActivity(new Intent(HomeActivity.this, MyVehicleActivity.class));
				break;
			case R.id.iv_home_say_something:
				recognizerDialog.show();
				break;
			}
		}
	};
	@Override
	public void onEnd(SpeechError arg0) {
		Toast.makeText(getApplicationContext(), sb.toString(), 0).show();
		sb.delete(0, sb.length());
	}
	public void onResults(ArrayList<RecognizerResult> results, boolean arg1) {
		for (RecognizerResult recognizerResult : results) {
			sb.append(recognizerResult.text);
		}
	}
}