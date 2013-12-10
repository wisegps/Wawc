package com.wise.wawc;

import com.wise.extend.SlidingMenuView;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActivityGroup {
	SlidingMenuView slidingMenuView;	
	ViewGroup tabcontent;
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
	}
	
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_activity_menu_home:
				Intent i = new Intent(MainActivity.this,HomeActivity.class);
		    	View view = getLocalActivityManager().startActivity(HomeActivity.class.getName(), i).getDecorView();
				tabcontent.removeAllViews();
				tabcontent.addView(view);
				slidingMenuView.snapToScreen(1);
				break;
				//车友圈
			case R.id.car_circle:
				Intent intent = new Intent(MainActivity.this,VehicleFriendActivity.class);
		    	View vv = getLocalActivityManager().startActivity(VehicleFriendActivity.class.getName(), intent).getDecorView();
				tabcontent.removeAllViews();
				tabcontent.addView(vv);
				slidingMenuView.snapToScreen(1);
				break;
			default:
				break;
			}
		}
	};
	
	private void ToHome(){
        slidingMenuView.snapToScreen(0);
        Intent i = new Intent(MainActivity.this,HomeActivity.class);
    	View v = getLocalActivityManager().startActivity(HomeActivity.class.getName(), i).getDecorView();
		tabcontent.removeAllViews();
		tabcontent.addView(v);
	}
	public void ShowMenu(){
		slidingMenuView.snapToScreen(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}