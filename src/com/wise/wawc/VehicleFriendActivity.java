package com.wise.wawc;

import com.wise.service.MyAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
/**
 * 车友圈
 * @author 王庆文
 */
public class VehicleFriendActivity extends Activity {
	private Button menuButton = null;
	private Button homeButton = null;
	private ListView articleList = null;
	private MyAdapter myAdapter = null;
	
	private ImageView newArticle = null;
	private EditText saySomething;
	private ImageButton userInformation;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.vehicle_friend);
		//设置无标题
		menuButton = (Button) findViewById(R.id.menu);
		menuButton.setOnClickListener(new ClickListener());
		homeButton = (Button) findViewById(R.id.home);
		homeButton.setOnClickListener(new ClickListener());
		newArticle = (ImageView) findViewById(R.id.publish_article);
		newArticle.setOnClickListener(new ClickListener());
		saySomething = (EditText) findViewById(R.id.say_something);
		userInformation = (ImageButton) findViewById(R.id.user_head);
		userInformation.setOnClickListener(new ClickListener());
		
		articleList = (ListView) findViewById(R.id.article_list);
		myAdapter = new MyAdapter(this,saySomething);
		articleList.setAdapter(myAdapter);
		
		articleList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				saySomething.setVisibility(View.GONE);
				myAdapter.isClick = false;
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			}
		});
		
		bindDate();
		
	}
	//TODO
	private void bindDate() {
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.publish_article:  //发表新文章
				startActivity(new Intent(VehicleFriendActivity.this,NewArticleActivity.class));
				break;
			case R.id.home:
				ActivityFactory.A.ToHome();
				break;
				
			case R.id.user_head:    //用户资料
				startActivity(new Intent(VehicleFriendActivity.this,AccountActivity.class));
				break;
			default:
				return;
			}
		}
	}
	
}
