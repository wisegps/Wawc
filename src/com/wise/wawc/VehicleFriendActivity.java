package com.wise.wawc;

import com.wise.service.MyAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
/**
 * 车友圈
 * @author 王庆文
 */
public class VehicleFriendActivity extends Activity {
	private Button menuButton = null;
	private ListView articleList = null;
	private MyAdapter myAdapter = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.vehicle_friend);
		//设置无标题
		menuButton = (Button) findViewById(R.id.menu);
		menuButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ActivityFactory.A.LeftMenu();
			}
		});
		articleList = (ListView) findViewById(R.id.article_list);
		myAdapter = new MyAdapter(this);
		articleList.setAdapter(myAdapter);
		bindDate();
		
	}
	//TODO
	private void bindDate() {
	}
}
