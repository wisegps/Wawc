package com.wise.wawc;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wise.data.Article;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.MyAdapter;
import com.wise.sql.DBOperation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 车友圈
 * @author 王庆文
 */
public class VehicleFriendActivity extends Activity implements IXListViewListener{
	private Button menuButton = null;
	private Button homeButton = null;
	private XListView articleList = null;
	private MyAdapter myAdapter = null;
	
	private ImageView newArticle = null;
	private View saySomething;
	
	private MyHandler myHandler = null;
	private EditText searchView = null;
	
	private ImageView qqUserHead = null;
	private TextView qqUserName = null;
	
	private DBOperation dBOperation = null;
	private List<Object[]> objList = new ArrayList<Object[]>();
	
	
	private static final int setUserIcon = 4;
	private List<Article> artList = null;
	private ProgressDialog myDialog = null;
	
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
		saySomething = (View) findViewById(R.id.say_something);
		searchView = (EditText) findViewById(R.id.search);
		qqUserHead = (ImageView) findViewById(R.id.user_head);
		qqUserHead.setOnClickListener(new ClickListener());
		qqUserName = (TextView) findViewById(R.id.tv_qq_user_name);
		
		articleList = (XListView) findViewById(R.id.article_list);
		articleList.setXListViewListener(this);
		//不设置上拉加载无效
		articleList.setPullLoadEnable(true);
		
		myHandler = new MyHandler();
		Message msg = new Message();
		msg.what = setUserIcon;
		myHandler.sendMessage(msg);
		myAdapter = new MyAdapter(this,saySomething,artList);
		articleList.setAdapter(myAdapter);
	
		
		articleList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				saySomething.setVisibility(View.GONE);
				searchView.setVisibility(View.GONE);
				myAdapter.isClick = false;
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			}
		});
		
		bindDate();
		
	}
	//TODO
	private void bindDate() {
		myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		myDialog.setCancelable(true);
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, 10)).start();
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override  //下拉刷新
	public void onRefresh() {
		searchView.setVisibility(View.VISIBLE);
		Log.e("下拉刷新","下拉刷新");
		myHandler.postDelayed(new Runnable() {
			public void run() {
				onLoad();
			}
		}, 2000);
	}
	@Override   //上拉加载
	public void onLoadMore() {
		Log.e("上拉加载","上拉加载");
		myHandler.postDelayed(new Runnable() {
			public void run() {
				onLoad();
			}
		}, 2000);
	}
	
	private void onLoad() {
		//获取当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String temp = sdf.format(new Date());
		String date = temp.substring(5, 16);
		articleList.stopRefresh();
		articleList.stopLoadMore();
		articleList.setRefreshTime(date);
	}
	public void PullUp() {
		searchView.setVisibility(View.GONE);
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			switch(msg.what){
			case setUserIcon:
				if(Constant.UserIcon != null){
					qqUserHead.setImageBitmap(Constant.UserIcon);
				}else{
					qqUserHead.setBackgroundResource(R.drawable.ic_launcher);
				}
				Log.e("用户昵称",Constant.qqUserName);
				if(!"".equals(Constant.qqUserName)){
					qqUserName.setText(Constant.qqUserName);
				}else{
					qqUserName.setText("未登录");
				}
				
				/**
				 * 测试用
				 */
				List<Date> dates = new ArrayList<Date>();
				Date date1 = new Date("2013/05/22 13:08:34");
				//用这个日期测试
				Date date2 = new Date("2014/06/21 18:28:14");
				Date date3 = new Date("2014/06/20 13:08:34");
				Date date4 = new Date("2014/06/19 13:08:34");
				Date date5 = new Date("2014/03/22 13:08:34");
				Date date6 = new Date("2013/04/24 13:08:34");
				Date date7 = new Date("2013/03/22 13:08:34");
				Date date8 = new Date("2013/02/22 13:08:34");
				Date date9 = new Date("2013/01/22 13:08:34");
				Date date10 = new Date("2013/06/22 13:08:34");
				dates.add(date10);
				dates.add(date9);
				dates.add(date8);
				dates.add(date7);
				dates.add(date6);
				dates.add(date5);
				dates.add(date4);
				dates.add(date3);
				dates.add(date2);
				dates.add(date1);
				
//				模拟说说列表
				
//				if(dBOperation.selectArticle(0, 10).size() == 0){
//					for(int i = 0 ; i < 10 ; i ++){
//						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						sdf.format(dates.get(i));
//						dBOperation.newArticle(new Object[]{"张三",sdf.format(dates.get(i)),"宝马",0});
//					}
//				}
				break;
			case 10:
				myDialog.dismiss();
				Log.e("文章列表",msg.obj.toString());
				break;
			}
			super.handleMessage(msg);
		}
	}
}
