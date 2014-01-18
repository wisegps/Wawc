package com.wise.wawc;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private static final int getArticleList = 10;
	private List<Article> articleDataList = new ArrayList<Article>();
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
		myAdapter = new MyAdapter(this,saySomething,articleDataList);
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
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, getArticleList)).start();
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
				break;
			case getArticleList:
				myDialog.dismiss();
				String temp1 = (msg.obj.toString()).replaceAll("\\\\", "");
//				jsonToList(temp1);
				Log.e("文章列表",temp1);
//				try {
//					JSONArray jsonArray = new JSONArray(msg.obj.toString());
					
//					for(int i = 0 ; i < jsonArray.length() ; i ++){
//						String picuter = jsonArray.getJSONObject(i).getString("pics");
//						String temp = picuter.substring(2,(picuter.length() - 2));
//						String temp1 = temp.replaceAll("\\\\", "");
//						JSONArray json = new JSONArray(temp1);
//						for(int j = 0 ; j < json.length() ; j ++){
//							JSONObject jsonObject = json.getJSONObject(j);
//							Log.e("小图", jsonObject.getString("small_pic"));
//							Log.e("大图", jsonObject.getString("big_pic"));
//						}
//						Log.e("pics:",temp1);
//					}
					
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	
	public List<Article> jsonToList(String JSON){
		try {
		JSONArray jsonArray = new JSONArray(JSON);
		for(int i = 0 ; i < jsonArray.length() ; i ++){
			Article article = new Article();
			String oneArticle = (jsonArray.getJSONObject(i).toString()).replaceAll("\\\\", "");
			article.setJSONDatas(oneArticle);
			article.set_id(Integer.valueOf(jsonArray.getJSONObject(i).getString("_id")));
			article.set_v(Integer.valueOf(jsonArray.getJSONObject(i).getString("＿v")));
			article.setBlog_id(Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
			article.setCity(jsonArray.getJSONObject(i).getString("city"));
			List<String> commentList = new ArrayList<String>();
			if(!"[]".equals(jsonArray.getJSONObject(i).getString("comments"))){
				
			}
			article.setCommentList(commentList);
			article.setContent(jsonArray.getJSONObject(i).getString("content"));
			article.setCreate_time(jsonArray.getJSONObject(i).getString("create_time"));
			article.setCust_id(Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
			
			Map<String,String> imageListTemp = new HashMap<String,String>();
			List<Map<String,String>> imageList = null;
			if(!"[]".equals(jsonArray.getJSONObject(i).getString("pics"))){
				String picuter = jsonArray.getJSONObject(i).getString("pics");
				String temp = picuter.substring(2,(picuter.length() - 2));
				String temp1 = temp.replaceAll("\\\\", "");
				JSONArray json = new JSONArray(temp1);
//				for(int j = 0 ; j < json.length() ; j ++){
//					JSONObject jsonObject = json.getJSONObject(j);
//					imageList = new 
//					imageListTemp.put("small_pic", jsonObject.getString("small_pic"));
//					imageListTemp.put("small_pic", jsonObject.getString("small_pic"));
//					Log.e("小图", jsonObject.getString("small_pic"));
//					Log.e("大图", jsonObject.getString("big_pic"));
//				}
			}
			article.setImageList(imageList);
		}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return articleDataList;
	}
}
