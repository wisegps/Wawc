package com.wise.wawc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.Article;
import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.SpinerPopWindow;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.MyAdapter;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 车友圈
 * @author Mr.Wang
 */
public class VehicleFriendActivity extends Activity implements IXListViewListener, AbstractSpinerAdapter.IOnItemSelectListener {
    private static final String TAG = "VehicleFriendActivity";
	private boolean isJump = false;//false 加载，true 跳转
	private ImageView menuButton = null;
	private XListView articleList = null;
	private MyAdapter myAdapter = null;
	
	private ImageView newArticle = null;
	private View saySomething;   //发表评论控件
	private Button sendButton = null;
	private TextView commentContent = null;
	
	private MyHandler myHandler = null;
	
	private ImageView qqUserHead = null;
	private TextView qqUserName = null;
	private TextView TVTitle = null;
	
	
	private SpinerPopWindow mSpinerPopWindow;//文章筛选列表
	private List<String> titleList = new ArrayList<String>();
	
	
	private static final int setUserIcon = 4;
	private static final int getArticleList = 10;
	private static final int commentArticle = 18;
	private static final int articleFavorite = 20;
	public static final int newArticleResult = 67;
	private static final int loadMoreCode = 87;
	private static final int refreshCode = 89;
	private static final int loadMoreAction = 21;
	private static final int FriendType = 11;
	private static final int FriendTypeLoadMore = 33;
	private static final int selectFriendType = 22;
	private int articleTypeMinBlogId = 0;
	
	private int totalNum = 0;
	private int screenWidth = 0;
	
	public static int newArticleBlogId = 0;
	private List<Article> articleDataList = new ArrayList<Article>();//  文章列表数据源
	private ProgressDialog myDialog = null;
	public static int blogId = 0;
	private  int[] blogIdArray = null;
	private boolean isLoadMore = false;
	private boolean isChickTypeTile = true;
	
	//操作数据库
	private DBExcute dBExcute = null;
	
	private String commentMsg = null;
	
	public static int minBlogId = 0;
	public static int maxBlogId = 0;
	private String articleType = "";
	
	private int article = 6;  //文章类型
	
	private LinearLayout titleLayout = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.vehicle_friend);
		Intent intent = getIntent();
		isJump = intent.getBooleanExtra("isJump", false);
		//设置无标题
		menuButton = (ImageView) findViewById(R.id.menu);
		menuButton.setOnClickListener(new ClickListener());
		newArticle = (ImageView) findViewById(R.id.publish_article);
		newArticle.setOnClickListener(new ClickListener());
		saySomething = (View) findViewById(R.id.say_something);
		sendButton = (Button) findViewById(R.id.btn_send);
		sendButton.setOnClickListener(new ClickListener());
		commentContent = (TextView) findViewById(R.id.et_sendmessage);
		qqUserHead = (ImageView) findViewById(R.id.user_head);
		qqUserHead.setOnClickListener(new ClickListener());
		qqUserName = (TextView) findViewById(R.id.tv_qq_user_name);
		articleList = (XListView) findViewById(R.id.article_list);
		articleList.setXListViewListener(this);
		screenWidth = (int) (getWindowManager().getDefaultDisplay().getWidth()*0.5);

		titleList.add("车友圈");
		titleList.add("同城车友");
		titleList.add("同车型车友");
		titleList.add("附近车友");
		titleList.add("我的收藏");
		
		TVTitle = (TextView) findViewById(R.id.tv_vehicle_friend_title);
		titleLayout = (LinearLayout) findViewById(R.id.vehicle_friend_title);
		titleLayout.setOnClickListener(new ClickListener());
		//不设置上拉加载无效
		articleList.setPullLoadEnable(true);
		
		dBExcute = new DBExcute();
		myHandler = new MyHandler();
		Message msg = new Message();
		msg.what = setUserIcon;
		myHandler.sendMessage(msg);
		myAdapter = new MyAdapter(VehicleFriendActivity.this,saySomething,articleDataList,articleList);
		articleList.setAdapter(myAdapter);
		
		articleList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				saySomething.setVisibility(View.GONE);
				myAdapter.isClick = false;
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			}
		});
		
		mSpinerPopWindow = new SpinerPopWindow(VehicleFriendActivity.this);
		mSpinerPopWindow.setItemListener(this);
		getArticleDatas(0);
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
				}
				break;
			case R.id.vehicle_friend_title:
				mSpinerPopWindow.refreshData(titleList, 0);
				mSpinerPopWindow.setWidth(screenWidth);
				mSpinerPopWindow.setHeight(300);
				mSpinerPopWindow.showAsDropDown(TVTitle);
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
		
		DBHelper dBHelper = new DBHelper(VehicleFriendActivity.this);
		SQLiteDatabase  sQLiteDatabase = dBHelper.getReadableDatabase();
		Cursor cursor = sQLiteDatabase.rawQuery("select * from " + Constant.TB_VehicleFriend, new String[]{});
		if(cursor.moveToFirst()){
			maxBlogId = cursor.getInt(cursor.getColumnIndex("Blog_id"));
		}
		
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code + "&max_id=" + maxBlogId, refreshCode)).start();
	}
	@Override
	public void onLoadMore() {
		if(isChickTypeTile){
			if(!isLoadMore){
				getArticleDatas(loadMoreAction);
			}else{
				DBHelper dBHelper = new DBHelper(VehicleFriendActivity.this);
				SQLiteDatabase  sQLiteDatabase = dBHelper.getReadableDatabase();
				Cursor cursor = sQLiteDatabase.rawQuery("select * from " + Constant.TB_VehicleFriend, new String[]{});
				if(cursor.moveToLast()){
					minBlogId = cursor.getInt(cursor.getColumnIndex("Blog_id"));
				}
				new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code + "&min_id=" + minBlogId, loadMoreCode)).start();
			}
		}else{
			//加载文章分类接口
			if(!isLoadMore){
				articleSort(article,3);
			}else{
				//上次分类最小blog_id
				Log.e("MinBlog_id:",articleTypeMinBlogId+"");
				new Thread(new NetThread.GetDataThread(myHandler, articleType + "&min_id=?" + articleTypeMinBlogId, FriendType)).start();
			}
			
		}
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
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			switch(msg.what){
			case setUserIcon:
			    Bitmap bimage = BitmapFactory.decodeFile(Constant.userIconPath + Variable.cust_id + ".jpg");
				if(bimage != null){
					qqUserHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
				}else{
					qqUserHead.setBackgroundResource(R.drawable.body_nothing_icon);
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
				getArticleDatas(0);
				}
				break;
			case newArticleResult:
				myDialog.dismiss();
				if(!"[]".equals(msg.obj.toString())){
				String temp1 = (msg.obj.toString()).replaceAll("\\\\", "");
				try {
					JSONArray jsonArray = new JSONArray(temp1);
					for(int i = 0 ; i < jsonArray.length() ; i ++){
						if(Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")) == newArticleBlogId){
							ContentValues values = new ContentValues();
							values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
							values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
							values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
							dBExcute.InsertDB(VehicleFriendActivity.this,values,Constant.TB_VehicleFriend);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				articleDataList.clear();
				articleDataList = dBExcute.getArticlePageDatas(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize + 1)}, articleDataList);
				Variable.articleList = articleDataList;
				setArticleDataList(articleDataList);
				myAdapter.refreshDates(articleDataList);
				}
				newArticleBlogId = 0;
				break;
			case commentArticle:
				String commentResult = msg.obj.toString();
				try {
					JSONObject jsonObject = new JSONObject(commentResult);
					if(Integer.valueOf(jsonObject.getString("status_code")) == 0){
						commentContent.setText("");
						//隐藏键盘
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						saySomething.setVisibility(View.GONE);
						myAdapter.isClick = false;
						
						//更新数据库
						dBExcute.updateArticleComments(VehicleFriendActivity.this, Constant.TB_VehicleFriend, blogId, commentMsg, Variable.cust_name, Integer.valueOf(Variable.cust_id));
						//刷新列表
						articleDataList.clear();
						articleDataList = dBExcute.getArticlePageDatas(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize)}, articleDataList);
						Variable.articleList = articleDataList;
						setArticleDataList(articleDataList);
						myAdapter.refreshDates(articleDataList);
						
						myDialog.dismiss();
						Toast.makeText(getApplicationContext(), "评论成功", 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
				//加载更多
			case loadMoreCode:
				String result = msg.obj.toString();
				Log.e("加载更多结果：",msg.obj.toString());
				if(!"".equals(result)){
					jsonToList(msg.obj.toString());
					isLoadMore = false;
					getArticleDatas(0);
				}
				onLoad();	
				break;
			case FriendTypeLoadMore:
				String FriendTypeLoadMoreResult = msg.obj.toString();
				Log.e("分类加载更多结果：",msg.obj.toString());
				if(!"".equals(FriendTypeLoadMoreResult)){
					jsonToList(msg.obj.toString());
					isLoadMore = false;
					getArticleDatas(0);
				}
				onLoad();	
				break;
			case refreshCode:
				Log.e("刷新数据结果：",msg.obj.toString());
				if(!"[]".equals(msg.obj.toString())){
					
				}
				onLoad();
				break;
			case FriendType:   // TODO
				Log.e("同城车友：",msg.obj.toString());
				String results = msg.obj.toString();
				try {
					if(!"[]".equals(msg.obj.toString())){
						DBHelper dBHelper = new DBHelper(VehicleFriendActivity.this);
						SQLiteDatabase reader = dBHelper.getReadableDatabase();
						SQLiteDatabase write = dBHelper.getWritableDatabase();
						JSONArray jsonArray = new JSONArray(results);
						int[] blogIdList = new int[jsonArray.length()];
						for(int i = 0 ; i < jsonArray.length() ; i ++){
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							//判断车友圈文章表里面是否存在此blog_id   存在   操作类型表    不存在   将这条数据添加到车友圈文章表  TODO
							int blog_id = Integer.valueOf(jsonObject.getString("blog_id"));
							blogIdList[i] = blog_id;
							Cursor cursor = reader.rawQuery("select * from " + Constant.TB_VehicleFriend + " where blog_id=?", new String[]{""+blog_id});
							if(cursor.moveToNext()){ //存在   操作类型表 
								ContentValues values = new ContentValues();
								values.put("Type_id", article);
								values.put("Blog_id", Integer.valueOf(blog_id));
								dBExcute.InsertDB(VehicleFriendActivity.this,values,Constant.TB_VehicleFriendType);
								Log.e("操作类型表","操作类型表");
							}else{     //不存在   将这条数据添加到车友圈文章表   然后添加到类型表
								ContentValues values = new ContentValues();
								values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
								values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
								values.put("UserLogo", jsonArray.getJSONObject(i).getString("logo"));
								values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
								dBExcute.InsertDB(VehicleFriendActivity.this,values,Constant.TB_VehicleFriend);
								
								ContentValues valuess = new ContentValues();
								values.put("Type_id", article);
								values.put("Blog_id", Integer.valueOf(blog_id));
								write.execSQL("insert into " + Constant.TB_VehicleFriendType + "(Type_id,Blog_id) values(?,?)", new String[]{article+"",""+blog_id});
								Log.e("操作文章表","操作文章表" + jsonArray.getJSONObject(i).getString("blog_id"));
							}
						}
						//查找最小blog_id
						for(int m = 0 ; m < blogIdList.length ; m ++){
							for(int n = 0 ; n < m ; n ++){
								int temp = 0;
								if(blogIdList[m] < blogIdList[n]){
									temp = blogIdList[m];
									blogIdList[m] = blogIdList[n];
									blogIdList[n] = temp;
								}
							}
						}
						articleTypeMinBlogId = blogIdList[0];
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				articleSort(article,0);
				myDialog.cancel();
				onLoad();
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	
	
	//解析数据
	public void jsonToList(String JSON){
		try {
		JSONArray jsonArray = new JSONArray(JSON);
			for(int i = 0 ; i < jsonArray.length() ; i ++){
				//存储到数据库
//				Cust_id text,FriendID int,Blog_id int,Content text)";
					ContentValues values = new ContentValues();
					values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
					values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
					if(jsonArray.getJSONObject(i).opt("logo") == null){
						values.put("UserLogo", jsonArray.getJSONObject(i).getString("logo"));
					}else{
						values.put("UserLogo", "");
					}
					values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
					dBExcute.InsertDB(VehicleFriendActivity.this,values,Constant.TB_VehicleFriend);
					if(i == (jsonArray.length()-1)){
						minBlogId = Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id"));
					}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//获取数据
	public void getArticleDatas(int actionCode){
		totalNum = dBExcute.getTotalCount(Constant.TB_VehicleFriend, VehicleFriendActivity.this);
		if(totalNum > 0){
			//查询数据库
			Constant.totalPage = totalNum%Constant.pageSize > 0 ? totalNum/Constant.pageSize + 1 : totalNum/Constant.pageSize;
			if(Constant.totalPage - 1 >= Constant.currentPage){
				Constant.start = Constant.currentPage*Constant.pageSize;
				Constant.currentPage ++ ;
				articleDataList = dBExcute.getArticlePageDatas(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
				setArticleDataList(articleDataList);
			}
			if(Constant.totalPage == Constant.currentPage){
				isLoadMore = true;
			}
			Log.e("数据库文章","数据库文章");
		}else{
			Log.e("服务器   文章","服务器    文章");
			myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
			myDialog.setCancelable(true);
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, getArticleList)).start();
		}
		
		
		Variable.articleList = articleDataList;
		myAdapter.refreshDates(articleDataList);
		
		if(loadMoreAction == actionCode){
			onLoad();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//文章发表成功后刷新数据库  
		if(requestCode == newArticleResult){
			if(newArticleBlogId != 0){
			myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
			myDialog.setCancelable(true);
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, newArticleResult)).start();
			}
		}
	}
	public List<Article> getArticleDataList() {
		return articleDataList;
	}
	public void setArticleDataList(List<Article> articleDataList) {
		this.articleDataList = articleDataList;
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
	public void onItemClick(int pos, int type) {
		TVTitle.setText(titleList.get(pos));
		Constant.start = 0;
		Constant.start = 0;  // 开始页
		Constant.pageSize = 10;   //每页数量
		Constant.totalPage = 0;   //数据总量
		Constant.currentPage = 0;  //当前页
		isLoadMore = false;
		
		if(Variable.articleList != null){
			Variable.articleList.clear();
		}
//		if(pos == 0){    //车友圈
//			getArticleDatas(0);
//			isChickTypeTile = true;
//		}else 
			if(pos == 1){   //同城车友
			articleSort(1,0);
			article = 1;
			isChickTypeTile = false;
		}
//		else if(pos == 2){   // 同车型的车友
//			articleSort(0,0);
//			article = 0;
//			isChickTypeTile = false;
//		}else if(pos == 3){    //附近车友
//			articleSort(2,0);
//			article = 2;
//			isChickTypeTile = false;
//		}else if(pos == 4){   //我的收藏
//			articleSort(3,0);
//			article = 3;
//			isChickTypeTile = false;
//		}
	}

	
	//文章分类
	public void articleSort(int type,int action){
		if(type == 2){
			articleType = Constant.BaseUrl + "blog?auth_code=" + Variable.auth_code + "&type=" + type + "&cust_id=" + Variable.cust_id + "&lon=" + Variable.Lon + "&lat=" + Variable.Lat + "&distance=" + Variable.distance;
		}else{
			articleType = Constant.BaseUrl + "blog?auth_code=" + Variable.auth_code + "&type=" + type + "&cust_id=" + Variable.cust_id; 
		}
		Log.e("url--->:",articleType);
		totalNum = dBExcute.getTotalCount( VehicleFriendActivity.this,"select * from " + Constant.TB_VehicleFriendType + " where Type_id=?",new String[]{String.valueOf(type)});
		if(totalNum > 0){
			Constant.totalPage = totalNum%Constant.pageSize > 0 ? totalNum/Constant.pageSize + 1 : totalNum/Constant.pageSize;
			if(Constant.totalPage - 1 >= Constant.currentPage){
				Constant.start = Constant.currentPage*Constant.pageSize;
				Constant.currentPage ++ ;
				Log.e("分类文章数据库","分类文章数据库" + Constant.currentPage);
				articleDataList = dBExcute.getArticleTypeList(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriendType + " where Type_id = ? limit ?,?", new String[]{String.valueOf(type),String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, Variable.articleList);
//				articleDataList = dBExcute.getArticleTypeList(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriendType + " where Type_id = ? limit ?,?", new String[]{String.valueOf(type),String.valueOf(0),String.valueOf(Constant.start)}, articleDataList);
				setArticleDataList(articleDataList);
			}
			if(Constant.totalPage == Constant.currentPage){
				isLoadMore = true;
			}
		}else{
			Log.e("分类文章服务器","分类文章服务器");
			myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
			myDialog.setCancelable(true);
			new Thread(new NetThread.GetDataThread(myHandler, articleType, FriendType)).start();
		}
		Variable.articleList = articleDataList;
		myAdapter.refreshDates(articleDataList);
		if(3 == action){
			onLoad();
		}
	}
}
