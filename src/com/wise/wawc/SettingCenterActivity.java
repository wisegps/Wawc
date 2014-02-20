package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.SpinerPopWindow;
import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;
import com.wise.service.SaveSettingData;

/**
 * 设置中心
 * @author 王庆文
 */
public class SettingCenterActivity extends Activity{
	private Button setCenterMenu;
	private Button setCenterHome1;
	private TableRow shareHaveGift;   // 分享有礼
	private TableRow feedBack;   // 意见反馈
	private TableRow giveUsScore;   // 给我们评分
	private TableRow aboutAppliaction;   // 关于我爱我车
	
	private TextView mTView;
	private TableRow mBtnDropDown;
	private String[] nameList = new String[Variable.carDatas.size() + 1];
	
	private AlertDialog dlg = null;  //显示评分对话框
	private Button gradeCommit = null;  //提交评分
	private Button gradeCancle = null;   //取消评分
	//推送设置
	private CheckBox againstPush;
	private CheckBox faultPush;
	private CheckBox remainPush;
	TableRow againstPushLayout;
	TableRow faultPushLayout;
	TableRow remainPushLayout;
	
	private static int index = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_center);
		setCenterMenu = (Button) findViewById(R.id.setting_center_menu);
//		setCenterHome = (Button) findViewById(R.id.setting_center_home);
		shareHaveGift = (TableRow) findViewById(R.id.share_have_gift);
		feedBack = (TableRow) findViewById(R.id.feedback);
		giveUsScore = (TableRow) findViewById(R.id.give_us_score);
		aboutAppliaction = (TableRow) findViewById(R.id.about_appliaction);
		//推送设置
		againstPush = (CheckBox) findViewById(R.id.against_push);
		faultPush = (CheckBox) findViewById(R.id.fault_push);
		remainPush = (CheckBox) findViewById(R.id.remaind_push);
		againstPushLayout = (TableRow) findViewById(R.id.against_push_row);
		faultPushLayout = (TableRow) findViewById(R.id.bug_push_row);
		faultPushLayout.setOnClickListener(new ClickListener());
		againstPushLayout.setOnClickListener(new ClickListener());
		remainPushLayout = (TableRow) findViewById(R.id.remaind_push_row);
		remainPushLayout.setOnClickListener(new ClickListener());
//		remainPush.setOnCheckedChangeListener(new CleckBoxListener());
//		faultPush.setOnCheckedChangeListener(new CleckBoxListener());
//		againstPushLayout.setOnCheckedChangeListener(new CleckBoxListener());
		
		setCenterMenu.setOnClickListener(new ClickListener());
		feedBack.setOnClickListener(new ClickListener());
		giveUsScore.setOnClickListener(new ClickListener());
		aboutAppliaction.setOnClickListener(new ClickListener());
//		setCenterHome.setOnClickListener(new ClickListener());
		shareHaveGift.setOnClickListener(new ClickListener());
		
     	mTView = (TextView) findViewById(R.id.tv_value);   //显示List点击的内容
		mBtnDropDown = (TableRow) findViewById(R.id.default_center_layout);  //点击显示下方ListView
		mBtnDropDown.setOnClickListener(new ClickListener());   //设置监听
		
		//初始化用户数据
		againstPush.setChecked(Variable.againstPush);
		faultPush.setChecked(Variable.faultPush);
		remainPush.setChecked(Variable.remaindPush);
		Log.e("默认选择的车辆:",Variable.defaultCenter);
	}
	@Override
	protected void onResume() {
		nameList[0] = "手机位置";
		if(Variable.carDatas != null){
			for(int i = 0 ; i < Variable.carDatas.size() ; i ++){
				nameList[i+1] = Variable.carDatas.get(i).getObj_name();
			}
		}
		mTView.setText(Variable.defaultCenter);
		index = 0;
		super.onResume();
	}
	class CleckBoxListener implements OnCheckedChangeListener{
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
			switch(((Button)buttonView).getId()){
			case R.id.against_push:  //违章推送
			    Variable.againstPush = isChecked;
				break;
			case R.id.fault_push:    //故障推送
			    Variable.faultPush = isChecked;
				break;
			case R.id.remaind_push:   //车务提醒
			    Variable.remaindPush = isChecked;
				break;
			default :
				return;
			}
		}
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.setting_center_menu:
				saveData();
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.setting_center_home:
				ActivityFactory.A.ToHome();
				break;
			case R.id.share_have_gift: // 分享有礼
				startActivity(new Intent(SettingCenterActivity.this,ShareActivity.class));
				break;
			case R.id.feedback: // 意见反馈
				startActivity(new Intent(SettingCenterActivity.this,FeedBackActivity.class));
				break;
			case R.id.give_us_score: // 给我们评分
				showDialog();
				break;
			case R.id.about_appliaction: // 关于我爱我车

				break;
			case R.id.grade_commit: // 提交评分
				Toast.makeText(getApplicationContext(), "评分成功", 0).show();
				dlg.dismiss();
				break;
			case R.id.grade_cancle: // 取消评分
				dlg.dismiss();
				break;
			case R.id.default_center_layout:
				index += 1;
				if(index == Variable.carDatas.size() + 1){
					index = 0;
				}
				mTView.setText(nameList[index]);
//				if(index == 0){
//					mTView.setText(nameList[1]);
//					index = 1;
//				}else if(index == 1){
//					mTView.setText(nameList[0]);
//					index = 0;
//				}
				Variable.defaultCenter = mTView.getText().toString();
				break;
			case R.id.against_push_row:
				if(againstPush.isChecked()){
					againstPush.setChecked(false);
				}else if(!againstPush.isChecked()){
					againstPush.setChecked(true);
				}
				Variable.againstPush = againstPush.isChecked();
				Log.e("Variable.againstPush:" + Variable.againstPush,"againstPush.isChecked():" + againstPush.isChecked());
				break;
			case R.id.bug_push_row:
				if(faultPush.isChecked()){
					faultPush.setChecked(false);
				}else if(!faultPush.isChecked()){
					faultPush.setChecked(true);
				}
				Variable.faultPush = faultPush.isChecked();
				break;
			case R.id.remaind_push_row:
				if(remainPush.isChecked()){
					remainPush.setChecked(false);
				}else if(!remainPush.isChecked()){
					remainPush.setChecked(true);
				}
				Variable.remaindPush = remainPush.isChecked();
				break;
			default:
				return;
			}
		}
	}
	
	//点击评分弹出对话框
	public void showDialog(){
		LayoutInflater layoutInflater = LayoutInflater.from(SettingCenterActivity.this);
		View view = layoutInflater.inflate(R.layout.grand_dialog, null);
		gradeCommit = (Button) view.findViewById(R.id.grade_commit);
		gradeCancle = (Button) view.findViewById(R.id.grade_cancle);
		gradeCommit.setOnClickListener(new ClickListener());
		gradeCancle.setOnClickListener(new ClickListener());
		dlg = new AlertDialog.Builder(SettingCenterActivity.this).setView(view).setCancelable(true).create();
		dlg.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveData();
	}
	public void saveData(){
			for(int i = 0 ; i < nameList.length ; i ++){
				nameList[i] = null;
			}
		new SaveSettingData(this).saveData(Variable.defaultCenter, Variable.againstPush, Variable.faultPush,Variable.remaindPush);
	}
}
