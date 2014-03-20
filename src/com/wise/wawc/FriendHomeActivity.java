package com.wise.wawc;
import java.io.File;
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
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.FriendArticleAdapter;
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
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
/**
 * 好友主页
 * @author 王庆文
 */
public class FriendHomeActivity extends Activity implements IXListViewListener{
	private XListView friendArticleList;
	private LinearLayout saySomething;
	private ImageView friendHead;    //点击好友头像显示资料
	private MyHandler myHandler = null;
	private ImageView cancle;
	private TextView friendName = null;
	private TextView commentContent = null;

	private Intent lastPageDatas = null;
	private DBExcute dBExcute = null;
	private List<Article> articleDataList = new ArrayList<Article>();
	private ProgressDialog myDialog = null;
	private FriendArticleAdapter myAdapter = null;
	
	private static final int initDatas = 2;
	private static final int getArticleList = 3;
	private static final int commentArticle = 4;
	private static final int loadMoreAction = 5;
	private static final int loadMoreCode = 6;
	private static final int refreshCode = 7;
	private String cust_id = "";
	private String user_logo = "";
	private String user_name = "";
	
	private int friendArticleTotalNum = 0;
	private boolean isLoadMore = false;
	private String Tag = "FriendHomeActivity";
	private String commentMsg = null;
	public static int blogId = 0;
	private int minBlogId = 0;
	private int maxBlogId = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_home);
		friendArticleList = (XListView) findViewById(R.id.friend_home_article_list);
		saySomething = (LinearLayout)findViewById(R.id.friend_home_say_something);
		myAdapter = new FriendArticleAdapter(FriendHomeActivity.this,saySomething,articleDataList);
		friendArticleList.setAdapter(myAdapter);
		friendHead = (ImageView) findViewById(R.id.friend_home_user_head);
		friendHead.setOnClickListener(new OnClickListener());
		friendName = (TextView) findViewById(R.id.friend_home_name);
		commentContent = (TextView) findViewById(R.id.et_sendmessage);
		cancle = (ImageView) findViewById(R.id.friend_back);
		cancle.setOnClickListener(new OnClickListener());
		
		lastPageDatas = getIntent();
		dBExcute = new DBExcute();
		cust_id = lastPageDatas.getStringExtra("cust_id");
		user_logo = lastPageDatas.getStringExtra("user_logo");
		user_name = lastPageDatas.getStringExtra("user_name");
		myHandler = new MyHandler();
		
		if(!new File(Constant.userIconPath + cust_id + ".jpg").exists()){
			Message msg = new Message();
			msg.what = initDatas;
			myHandler.sendMessage(msg);
		}else{
			Bitmap userHead = BitmapFactory.decodeFile(Constant.userIconPath + cust_id + ".jpg"); 
			friendHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(userHead));
		}
        friendName.setText(user_name);
        //显示用户数据    查询本地数据库时候存在数据
        getArticleDatas(0);
		
		
		friendArticleList.setXListViewListener(this);
		//不设置上拉加载无效
		friendArticleList.setPullLoadEnable(true);
		friendArticleList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				saySomething.setVisibility(View.GONE);
				myAdapter.isClick = false;
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			}
		});
		friendArticleList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Article article = (Article) friendArticleList.getItemAtPosition(arg2);
				Intent intents = new Intent(FriendHomeActivity.this,ArticleDetailActivity.class);
				intents.putExtra("article", article);
				FriendHomeActivity.this.startActivity(intents);
			}
		});
	}
	class OnClickListener implements android.view.View.OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.friend_home_user_head:
				Intent intent = new Intent(FriendHomeActivity.this,FriendInformationActivity.class);
				intent.putExtra("cust_id", cust_id);
				intent.putExtra("user_logo", user_logo);
				intent.putExtra("user_name", user_name);
				startActivity(intent);
				FriendHomeActivity.this.finish();
				break;
			case R.id.friend_back:
				FriendHomeActivity.this.finish();
				break;
			default:
			    break;
			}
		}
	}
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case initDatas:   // 设置用户信息
				new Thread(new Runnable() {
					public void run() {
						Bitmap userLogo = GetSystem.getBitmapFromURL(user_logo);
						if(userLogo != null){
							GetSystem.saveImageSD(userLogo, Constant.userIconPath, cust_id + ".jpg",100);
						}
						friendHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(userLogo));
					}
				}).start();
				break;
			case getArticleList:
				String commentResult = msg.obj.toString();
				if(!"".equals(commentResult)){
					jsonToList(msg.obj.toString());
					getArticleDatas(0);
				}
				break;
			case loadMoreCode:
				String result = msg.obj.toString();
				if(!"".equals(result)){
					jsonToList(msg.obj.toString());   //存到数据库
					isLoadMore = false;
					getArticleDatas(0);
				}
				onLoad();
				break;
			case refreshCode:
				if(!"[]".equals(msg.obj.toString())){
					//  更新数据库
					JSONArray jsonArray = null;
					try {
						jsonArray = new JSONArray(msg.obj.toString());
						if(jsonArray.length() > 1){
							for(int i = 0 ; i < jsonArray.length() ; i ++){
								if(Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")) != maxBlogId){
									ContentValues values = new ContentValues();
									values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
									values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
									if(jsonArray.getJSONObject(i).opt("logo") != null){
										values.put("UserLogo", jsonArray.getJSONObject(i).getString("logo"));
									}else{
										values.put("UserLogo", "");
									}
									values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
									dBExcute.InsertDB(FriendHomeActivity.this,values,Constant.TB_VehicleFriend);
								}
							}
							//重新分页   TODO
							articleDataList.clear();
							setArticleDataList(articleDataList);
							Constant.totalPage1 = 0;
							Constant.start1 = 0;  // 开始页
							Constant.pageSize1 =10;   //每页数量
							Constant.totalPage1 = 0;   //数据总量
							Constant.currentPage1 = 0;  //当前页
							getArticleDatas(0);
							onLoad();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				onLoad();
				break;
			default:
			    break;
			}
		}
	}
	
	public void onLoadMore() {
		if(!isLoadMore){
			getArticleDatas(loadMoreAction);
		}else{
			DBHelper dBHelper = new DBHelper(FriendHomeActivity.this);
			SQLiteDatabase  sQLiteDatabase = dBHelper.getReadableDatabase();
			Cursor cursor = sQLiteDatabase.rawQuery("select * from " + Constant.TB_VehicleFriend + " where Cust_id = ? order by Blog_id desc", new String[]{cust_id});
			if(cursor.moveToLast()){
				minBlogId = cursor.getInt(cursor.getColumnIndex("Blog_id"));
			}
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + cust_id + "/blog?auth_code=" + Variable.auth_code + "&min_id=" + minBlogId, loadMoreCode)).start();
		}
	}
	
	//获取数据  TODO
		public void getArticleDatas(int actionCode){
			DBHelper dBHelper = new DBHelper(FriendHomeActivity.this);
			SQLiteDatabase reader = dBHelper.getReadableDatabase();
			Cursor cursor = reader.rawQuery("select * from " + Constant.TB_VehicleFriend + " where Cust_id = ?", new String[]{cust_id});
			friendArticleTotalNum = cursor.getCount();
			if(friendArticleTotalNum > 0){
				//查询数据库
				Constant.totalPage1 = friendArticleTotalNum%Constant.pageSize1 > 0 ? friendArticleTotalNum/Constant.pageSize1 + 1 : friendArticleTotalNum/Constant.pageSize1;
				
//				if(Constant.totalPage1 - 1>= Constant.currentPage1){
//					Constant.start1 = Constant.currentPage1*Constant.pageSize1;
//					if(Constant.totalPage1 >= Constant.pageSize1){
//						Constant.currentPage1 ++ ;
//					}
//					articleDataList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " where Cust_id = ? order by Blog_id desc limit ?,?", new String[]{cust_id,String.valueOf(Constant.start1),String.valueOf(Constant.pageSize1)}, articleDataList);
//					setArticleDataList(articleDataList);
//				}
//				if(Constant.totalPage1 == Constant.currentPage1){
//					isLoadMore = true;
//				}
				
				//分页查询数据库   当前  总数据量（1  -  9）小于每页数据量（10）
				if(Constant.totalPage1 < Constant.pageSize1){
					articleDataList.clear();
					articleDataList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " where Cust_id = ? order by Blog_id desc limit ?,?", new String[]{cust_id,String.valueOf(Constant.start1),String.valueOf(Constant.pageSize1)}, articleDataList);
					myAdapter.refreshDates(articleDataList);
					isLoadMore = true;
				}else if(Constant.totalPage1 == Constant.pageSize1){
					articleDataList.clear();
					Constant.start1 = Constant.currentPage1*Constant.pageSize1;
					Constant.currentPage1 ++ ;
					articleDataList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " where Cust_id = ? order by Blog_id desc limit ?,?", new String[]{cust_id,String.valueOf(Constant.start1),String.valueOf(Constant.pageSize1)}, articleDataList);
					myAdapter.refreshDates(articleDataList);
				}else{
					Constant.start1 = Constant.currentPage1*Constant.pageSize1;
					Constant.currentPage1 ++ ;
					articleDataList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " where Cust_id = ? order by Blog_id desc limit ?,?", new String[]{cust_id,String.valueOf(Constant.start1),String.valueOf(Constant.pageSize1)}, articleDataList);
					myAdapter.refreshDates(articleDataList);
				}
				if(Constant.totalPage1 == Constant.currentPage1){
					isLoadMore = true;
				}
			}else{
				myDialog = ProgressDialog.show(FriendHomeActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
				myDialog.setCancelable(true);
				//  TODO  获取文章列表
				new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + cust_id + "/blog?auth_code=" + Variable.auth_code, getArticleList)).start();
			}
			myAdapter.refreshDates(articleDataList);
			
			if(loadMoreAction == actionCode){
				onLoad();
			}
		}
		
		//解析数据
		public void jsonToList(String JSON){
			try {
			JSONArray jsonArray = new JSONArray(JSON);
//			int[] blog_idAry = new int[jsonArray.length()];
			for(int i = 0 ; i < jsonArray.length() ; i ++){
					//存储到数据库
					ContentValues values = new ContentValues();
					values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
					values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
					values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
					dBExcute.InsertDB(FriendHomeActivity.this,values,Constant.TB_VehicleFriend);
//					blog_idAry[i] = Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id"));
			}
			
//			for(int n = 0 ; n < blog_idAry.length ; n ++){
//				for(int m = 0 ; m < n ; m ++){
//					if(blog_idAry[m] < blog_idAry[n]){
//						int temp = blog_idAry[m];
//						blog_idAry[m] = blog_idAry[n];
//						blog_idAry[n] = temp;
//					}
//				}
//			}
			DBHelper dBHelper = new DBHelper(getApplicationContext());
			SQLiteDatabase db = dBHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from " + Constant.TB_VehicleFriend + " where Cust_id = ?", new String[]{cust_id});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public void onRefresh() {
			List<Article> articleTempList = new ArrayList<Article>();
			articleTempList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " where Cust_id = ?", new String[]{cust_id}, articleTempList);
			int[] blogIdList = new int[articleTempList.size()];
			for (int i = 0; i < articleTempList.size(); i++) {
				blogIdList[i] = articleTempList.get(i).getBlog_id();
			}
			maxBlogId = VehicleFriendActivity.paiXu(blogIdList)[0];
			String url = Constant.BaseUrl + "customer/" + cust_id + "/blog?auth_code=" + Variable.auth_code + "&max_id=" + maxBlogId;
			new Thread(new NetThread.GetDataThread(myHandler, url, refreshCode)).start();
			
		}
		private void onLoad() {
			//获取当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			String temp = sdf.format(new Date());
			String date = temp.substring(5, 16);
			friendArticleList.stopRefresh();
			friendArticleList.stopLoadMore();
			friendArticleList.setRefreshTime(date);
		}
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				FriendHomeActivity.this.finish();
			}
			return super.onKeyDown(keyCode, event);
		}

		public List<Article> getArticleDataList() {
			return articleDataList;
		}
		public void setArticleDataList(List<Article> articleDataList) {
			this.articleDataList = articleDataList;
		}
		protected void onDestroy() {
			Constant.start1 = 0;  // 开始页
			Constant.pageSize1 = 10;   //每页数量
			Constant.totalPage1 = 0;   //数据总量
			Constant.currentPage1 = 0;  //当前页
			super.onDestroy();
		}
}
