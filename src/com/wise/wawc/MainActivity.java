package com.wise.wawc;

import javax.xml.validation.ValidatorHandler;

import com.wise.extend.SlidingMenuView;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 菜单界面
 * @author honesty
 */
public class MainActivity extends ActivityGroup {
	SlidingMenuView slidingMenuView;	
	ViewGroup tabcontent;
	int Screen = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActivityFactory.A = this;
		slidingMenuView = (SlidingMenuView) findViewById(R.id.sliding_menu_view);        
        tabcontent = (ViewGroup) slidingMenuView.findViewById(R.id.sliding_body);
        //获取屏幕宽度
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int width = (int) (wm.getDefaultDisplay().getWidth()*0.8);//屏幕宽度
        Button bt_activity_menu_home = (Button)findViewById(R.id.bt_activity_menu_home);
        bt_activity_menu_home.setWidth(width);
        bt_activity_menu_home.setOnClickListener(onClickListener);
        ToHome();
        //车友圈
        TextView vehiclefriend = (TextView)findViewById(R.id.car_circle);
        vehiclefriend.setOnClickListener(onClickListener);
        //我的收藏
        TextView myCollection = (TextView)findViewById(R.id.menu_my_collection);
        myCollection.setOnClickListener(onClickListener);
      //设置中心
        TextView settingCenter = (TextView)findViewById(R.id.settting_center);
        settingCenter.setOnClickListener(onClickListener);
        TextView tv_activity_main_right = (TextView)findViewById(R.id.tv_activity_main_right);
        tv_activity_main_right.setWidth(width);//设置菜单宽度
        
        LinearLayout ll_activity_main_car_remind = (LinearLayout)findViewById(R.id.ll_activity_main_car_remind);
        ll_activity_main_car_remind.setOnClickListener(onClickListener);
        LinearLayout ll_activity_main_mycar = (LinearLayout)findViewById(R.id.ll_activity_main_mycar);
        ll_activity_main_mycar.setOnClickListener(onClickListener);
        
        Intent i = new Intent(MainActivity.this,VehicleStatusActivity.class);
    	View v = getLocalActivityManager().startActivity(HomeActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
	}
	
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_activity_menu_home:
				ToHome();
				break;
				//车友圈
			case R.id.car_circle:
				ToVehicleFriends();
				break;
			case R.id.ll_activity_main_car_remind:
				ToCarRemind();
				break;
			case R.id.ll_activity_main_mycar:
				ToMyCar();
				break;
				//我的收藏
			case R.id.menu_my_collection:
				ToMyCollection();
				break;
				//设置中心
			case R.id.settting_center:
				ToSettingCenter();
				break;
			default:
				return;
			}
		}
	};
	
	
	/**
	 * 设置中心
	 */
	public void ToSettingCenter(){
		Intent intent = new Intent(MainActivity.this,SettingCenterActivity.class);
    	View vv = getLocalActivityManager().startActivity(SettingCenterActivity.class.getName(), intent).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(vv);
		slidingMenuView.snapToScreen(1);
	}

	/**
	 * 我的收藏
	 */
	private void ToMyCollection() {
		Intent intent = new Intent(MainActivity.this,MyCollectionActivity.class);
    	View vv = getLocalActivityManager().startActivity(MyCollectionActivity.class.getName(), intent).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(vv);
		slidingMenuView.snapToScreen(1);
	}
	
	public void ToHome(){
		Screen = 1;
        slidingMenuView.snapToScreen(1);
        Intent i = new Intent(MainActivity.this,HomeActivity.class);
    	View v = getLocalActivityManager().startActivity(HomeActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
	}
	/**
	 * 车友圈
	 */
	public void ToVehicleFriends(){
		Intent intent = new Intent(MainActivity.this,VehicleFriendActivity.class);
    	View vv = getLocalActivityManager().startActivity(VehicleFriendActivity.class.getName(), intent).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(vv);
		slidingMenuView.snapToScreen(1);
	}
	/**
	 * 车务提醒
	 */
	public void ToCarRemind(){
		Screen = 1;
		slidingMenuView.snapToScreen(1);
		Intent i = new Intent(MainActivity.this,CarRemindActivity.class);
    	View view = getLocalActivityManager().startActivity(CarRemindActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(view);
	}
	/**
	 * 我的爱车
	 */
	public void ToMyCar(){
		Screen = 1;
		slidingMenuView.snapToScreen(1);
		Intent i = new Intent(MainActivity.this,MyVehicleActivity.class);
    	View view = getLocalActivityManager().startActivity(MyVehicleActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(view);
	}
	
	public void LeftMenu(){
		if(Screen == 0){
			Screen = 1;
		}else if(Screen == 1){
			Screen = 0;
		}
		slidingMenuView.snapToScreen(Screen);
	}
	
	public void RightMenu(){
		if(Screen == 2){
			Screen = 1;
		}else if(Screen == 1){
			Screen = 2;
		}
		slidingMenuView.snapToScreen(Screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
