package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.SpinerPopWindow;

/**
 * 设置中心
 * @author 王庆文
 */
public class SettingCenterActivity extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener {
	private Button setCenterMenu;
	private Button setCenterHome;
	private ImageView shareHaveGift;   // 分享有礼
	private ImageView feedBack;   // 意见反馈
	private ImageView giveUsScore;   // 给我们评分
	private ImageView aboutAppliaction;   // 关于我爱我车
	
	private TextView mTView;
	private ImageButton mBtnDropDown;
	private List<String> nameList = new ArrayList<String>();
	
	//显示自定义ListView
	private SpinerPopWindow mSpinerPopWindow;
	
	private AlertDialog dlg = null;  //显示评分对话框
	private Button gradeCommit = null;  //提交评分
	private Button gradeCancle = null;   //取消评分
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_center);
		setCenterMenu = (Button) findViewById(R.id.setting_center_menu);
		setCenterHome = (Button) findViewById(R.id.setting_center_home);
		shareHaveGift = (ImageView) findViewById(R.id.share_have_gift);
		feedBack = (ImageView) findViewById(R.id.feedback);
		giveUsScore = (ImageView) findViewById(R.id.give_us_score);
		aboutAppliaction = (ImageView) findViewById(R.id.about_appliaction);
		
		setCenterMenu.setOnClickListener(new ClickListener());
		feedBack.setOnClickListener(new ClickListener());
		giveUsScore.setOnClickListener(new ClickListener());
		aboutAppliaction.setOnClickListener(new ClickListener());
		setCenterHome.setOnClickListener(new ClickListener());
		shareHaveGift.setOnClickListener(new ClickListener());
		/**
		 * 自定义Spinner
		 */
    	mTView = (TextView) findViewById(R.id.tv_value);   //显示List点击的内容
		mBtnDropDown = (ImageButton) findViewById(R.id.bt_dropdown);  //点击显示下方ListView
		mBtnDropDown.setOnClickListener(this);   //设置监听
		
		nameList.add("车辆位置");
		nameList.add("手机位置");
		
		mSpinerPopWindow = new SpinerPopWindow(this);
		
		//设置数据
		mSpinerPopWindow.refreshData(nameList, 0);
		//设置监听
		mSpinerPopWindow.setItemListener(this);
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.setting_center_menu:
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
			default:
				return;
			}
		}
	}
	@Override
	public void onItemClick(int pos) {
		if (pos >= 0 && pos <= nameList.size()){
			String value = nameList.get(pos);
			mTView.setText(value);
		}
	}
	
	@Override  //自定义ListView点击监听
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bt_dropdown:
			mSpinerPopWindow.setWidth(mTView.getWidth());
			mSpinerPopWindow.showAsDropDown(mTView);
			break;
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
}
