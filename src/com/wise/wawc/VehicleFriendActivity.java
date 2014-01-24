package com.wise.wawc;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.AdressData;
import com.wise.data.Article;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.MyAdapter;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import com.wise.sql.DBOperation;
import com.wise.wawc.MainActivity.GetBitMapFromUrlThread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
	private boolean isJump = false;//false 加载，true 跳转
	private Button menuButton = null;
	private Button homeButton = null;
	private XListView articleList = null;
	private MyAdapter myAdapter = null;
	
	private ImageView newArticle = null;
	private View saySomething;   //发表评论控件
	private Button sendButton = null;
	private TextView commentContent = null;
	
	private MyHandler myHandler = null;
	private EditText searchView = null;
	
	private ImageView qqUserHead = null;
	private TextView qqUserName = null;
	
	
	private static final int setUserIcon = 4;
	private static final int getArticleList = 10;
	private static final int commentArticle = 18;
	private static final int articleFavorite = 20;
	public static final int newArticleResult = 67;
	
	private static int start = 0;  // 开始页
	private static int pageSize = 2;   //每页数量
	private static int totalPage = 0;   //数据总量
	public static int currentPage = 0;  //当前页
	private List<Article> articleDataList = new ArrayList<Article>();
	private ProgressDialog myDialog = null;
	public static int blogId = 0;
	private static int[] blogIdArray = null;
	
	//操作数据库
	private DBExcute dBExcute = null;
	
	private String commentMsg = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.vehicle_friend);
		Intent intent = getIntent();
		isJump = intent.getBooleanExtra("isJump", false);
		//设置无标题
		menuButton = (Button) findViewById(R.id.menu);
		menuButton.setOnClickListener(new ClickListener());
		homeButton = (Button) findViewById(R.id.home);
		homeButton.setOnClickListener(new ClickListener());
		newArticle = (ImageView) findViewById(R.id.publish_article);
		newArticle.setOnClickListener(new ClickListener());
		saySomething = (View) findViewById(R.id.say_something);
		sendButton = (Button) findViewById(R.id.btn_send);
		sendButton.setOnClickListener(new ClickListener());
		commentContent = (TextView) findViewById(R.id.et_sendmessage);
		searchView = (EditText) findViewById(R.id.search);
		qqUserHead = (ImageView) findViewById(R.id.user_head);
		qqUserHead.setOnClickListener(new ClickListener());
		qqUserName = (TextView) findViewById(R.id.tv_qq_user_name);
		
		articleList = (XListView) findViewById(R.id.article_list);
		articleList.setXListViewListener(this);
		//不设置上拉加载无效
		articleList.setPullLoadEnable(true);
		
		dBExcute = new DBExcute();
		myHandler = new MyHandler();
		Message msg = new Message();
		msg.what = setUserIcon;
		myHandler.sendMessage(msg);
		myAdapter = new MyAdapter(VehicleFriendActivity.this,saySomething,articleDataList);
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
		
		getArticleDatas();
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.menu:
				ActivityFactory.A.LeftMenu();
				break;
			case R.id.publish_article:  //发表新文章
				Intent newArticle  = new Intent(VehicleFriendActivity.this,NewArticleActivity.class);
				startActivityForResult(newArticle,newArticleResult);
				break;
			case R.id.home:
				ActivityFactory.A.ToHome();
				break;
				
			case R.id.user_head:    //用户资料
				startActivity(new Intent(VehicleFriendActivity.this,AccountActivity.class));
				break;
				
			case R.id.btn_send:
				commentMsg = commentContent.getText().toString().trim();
				//发布到服务器/刷新文章内容显示/评论成功后清空编辑框/隐藏编辑框
				
				if("".equals(commentMsg)){
					Toast.makeText(getApplicationContext(), "评论类容不能为空", 0).show();
					return;
				}else{
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
					params.add(new BasicNameValuePair("name", Variable.cust_name));
					params.add(new BasicNameValuePair("content", commentMsg));
					myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
					myDialog.setCancelable(true);
					new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + blogId + "/comment?auth_code=" + Variable.auth_code, params, commentArticle)).start();
					Log.e("评论的url :",Constant.BaseUrl + "blog/" + blogId + "/comment?auth_code=" + Variable.auth_code);
				}
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
		
//	for(int i = 0 ; i < blogIdArray.length ; i ++){
//		for(int j = 0 ; j < i ; j ++){
//			if(blogIdArray[i] < blogIdArray[j]){
//				int temp = blogIdArray[j];
//				blogIdArray[j] = blogIdArray[i];
//				blogIdArray[i] = temp;
//			}
//		}
//	}
	//最大id 刷新  /最小id加载
//	List<NameValuePair> parmas = new ArrayList<NameValuePair>();
//	parmas.add(new BasicNameValuePair("max_id", String.valueOf(blogIdArray[blogIdArray.length - 1])));
//	new Thread(new NetThread.postDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, parmas, getArticleList)).start();
	}
	@Override   //上拉加载
	public void onLoadMore() {
		Log.e("上拉加载","上拉加载");
//		getArticleDatas(start, pageSize, selectDatabasesCode, articleDataList);
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
				if(!"".equals(Variable.cust_name)){
					qqUserName.setText(Variable.cust_name);
				}else{
					qqUserName.setText("未登录");
				}
				break;
			case getArticleList:
				myDialog.dismiss();
				if(!"[]".equals(msg.obj.toString())){
				String temp1 = (msg.obj.toString()).replaceAll("\\\\", "");
				jsonToList(temp1);
				getArticleDatas();
				}
				break;
				
			case commentArticle:
				String commentResult = msg.obj.toString();
				Log.e("评论结果：",msg.obj.toString());
				try {
					JSONObject jsonObject = new JSONObject(commentResult);
					if(Integer.valueOf(jsonObject.getString("status_code")) == 0){
						commentContent.setText("");
						//隐藏键盘
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						saySomething.setVisibility(View.GONE);
						searchView.setVisibility(View.GONE);
						myAdapter.isClick = false;
						
						//更新数据库
						dBExcute.updateArticleComments(VehicleFriendActivity.this, Constant.TB_VehicleFriend, blogId, commentMsg, Variable.cust_name, Integer.valueOf(Variable.cust_id));
						currentPage -= 1;
						articleDataList.clear();
						getArticleDatas();
						myDialog.dismiss();
						Toast.makeText(getApplicationContext(), "评论成功", 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case articleFavorite:
				String result = msg.obj.toString();
				try {
					JSONObject jsonArray = new JSONObject(result);
					if(Integer.valueOf(jsonArray.getString("status_code")) == 0){
						//刷新成功
						myDialog.dismiss();
						Log.e("赞成功","赞成功");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	
	
	//解析数据
	public void jsonToList(String JSON){
		try {
		JSONArray jsonArray = new JSONArray(JSON);
//		blogIdArray = new int[jsonArray.length()];
		for(int i = 0 ; i < jsonArray.length() ; i ++){
//			blogIdArray[i] = jsonArray.getJSONObject(i).getInt("blog_id");
//			Article article = new Article();
//			article.setJSONDatas(JSON);
//			article.set_id(jsonArray.getJSONObject(i).getString("_id"));
//			article.setBlog_id(Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
//			article.setCity(jsonArray.getJSONObject(i).getString("city"));
//			article.setName(jsonArray.getJSONObject(i).getString("name"));
//			List<String[]> comments = new ArrayList<String[]>();
//			if(!"[]".equals(jsonArray.getJSONObject(i).getString("comments"))){
//				String commentjson = jsonArray.getJSONObject(i).getString("comments");
//				JSONArray  jsonArrayComment = new JSONArray(commentjson);
//				for(int m = 0; m < jsonArrayComment.length() ; m ++){
//					String[] commentList = new String[2];
//					JSONObject jsonObjectComment = jsonArrayComment.getJSONObject(m);
//					commentList[0] =  jsonObjectComment.getString("name");
//					commentList[1] =  jsonObjectComment.getString("content");
//					comments.add(commentList);
//				}
//			}
//			article.setCommentList(comments);
//			article.setContent(jsonArray.getJSONObject(i).getString("content"));
//			article.setCreate_time(jsonArray.getJSONObject(i).getString("create_time"));
//			article.setCust_id(Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
//			
//			//用户发表的图片
//			Map<String,String> imageListTemp = null;
//			List<Map<String,String>> imageList = new ArrayList<Map<String,String>>();
//			if(!"[]".equals(jsonArray.getJSONObject(i).getString("pics"))){
//				JSONArray json = new JSONArray(jsonArray.getJSONObject(i).getString("pics"));
//				for(int j = 0 ; j < json.length() ; j ++){
//					JSONObject jsonObject = json.getJSONObject(j);
//					imageListTemp = new HashMap<String, String>();
//					imageListTemp.put("small_pic", jsonObject.getString("small_pic"));
//					imageListTemp.put("big_pic", jsonObject.getString("big_pic"));
//					imageList.add(imageListTemp);
//				}
//			}
//			if(!"[]".equals(jsonArray.getJSONObject(i).getString("praises"))){
//				JSONArray json = new JSONArray(jsonArray.getJSONObject(i).getString("praises"));
//				List<String> parisesList = new ArrayList<String>();
//				for(int k = 0 ; k < json.length(); k ++){
//					parisesList.add(json.getJSONObject(k).getString("name"));
//				}
//				article.setPraisesList(parisesList);
//			}
//			article.setImageList(imageList);
//			articleDataList.add(article);
			//存储到数据库
//			Cust_id text,FriendID int,Blog_id int,Content text)";
			ContentValues values = new ContentValues();
			values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
			values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
			values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
			dBExcute.InsertDB(VehicleFriendActivity.this,values,Constant.TB_VehicleFriend);
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//获取数据
	public void getArticleDatas(){
		int totalNum = dBExcute.getTotalCount(Constant.TB_VehicleFriend, VehicleFriendActivity.this);
		Log.e("数据总量",totalNum+"");
		if(totalNum > 0){
			//查询数据库
			totalPage = totalNum%pageSize > 0 ? totalNum/pageSize + 1 : totalNum/pageSize;
			if(totalPage - 1 >= currentPage){
				start = currentPage*pageSize;
				currentPage ++ ;
				articleDataList = dBExcute.getArticlePageDatas(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(start),String.valueOf(pageSize)}, articleDataList);
			}
			if(totalPage - 1 == currentPage){
				Log.e("请求服务器","请求服务器");
			}
		}else{
			myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
			myDialog.setCancelable(true);
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, getArticleList)).start();
		}
		Variable.articleList = articleDataList;
		myAdapter.refreshDates(articleDataList);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		myDialog.setCancelable(true);
		//文章发表成功后刷新数据库  列表  TODO
		if(requestCode == newArticleResult){
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, getArticleList)).start();
		}
	}
}
