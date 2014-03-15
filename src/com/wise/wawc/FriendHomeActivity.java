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
	private Button sendButton = null;
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
	private String cust_id = "";
	private int friendArticleTotalNum = 0;
	private boolean isLoadMore = false;
	private String Tag = "FriendHomeActivity";
	private String commentMsg = null;
	public static int blogId = 0;
	private int minBlogId = 0;
	private Article article = null;
	
	
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
		sendButton = (Button) findViewById(R.id.btn_send);
		sendButton.setOnClickListener(new OnClickListener());
		cancle = (ImageView) findViewById(R.id.friend_back);
		cancle.setOnClickListener(new OnClickListener());
		
		lastPageDatas = getIntent();
		dBExcute = new DBExcute();
		cust_id = lastPageDatas.getStringExtra("cust_id");
		myHandler = new MyHandler();
		
		article = (Article) lastPageDatas.getSerializableExtra("article");
		if(!new File(Constant.userIconPath + cust_id + ".jpg").exists()){
			Message msg = new Message();
			msg.what = initDatas;
			myHandler.sendMessage(msg);
		}else{
			Bitmap userHead = BitmapFactory.decodeFile(Constant.userIconPath + cust_id + ".jpg"); 
			friendHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(userHead));
		}
        friendName.setText(article.getName());
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
	}
	class OnClickListener implements android.view.View.OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.friend_home_user_head:
				Intent intent = new Intent(FriendHomeActivity.this,FriendInformationActivity.class);
				intent.putExtra("cust_id", cust_id);
				intent.putExtra("article", article);
				startActivity(intent);
				FriendHomeActivity.this.finish();
				break;
			case R.id.friend_back:
				FriendHomeActivity.this.finish();
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
					
					myDialog = ProgressDialog.show(FriendHomeActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
					myDialog.setCancelable(true);
					new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + blogId + "/comment?auth_code=" + Variable.auth_code, params, commentArticle)).start();
				}
				break;
			default:
				return;
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
						Bitmap userLogo = GetSystem.getBitmapFromURL(article.getUserLogo());
						if(userLogo != null){
							GetSystem.saveImageSD(userLogo, Constant.userIconPath, cust_id + ".jpg");
						}
						friendHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(userLogo));
					}
				}).start();
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
						FriendArticleAdapter.isClick = false;
						
						//更新数据库
						dBExcute.updateArticleComments(FriendHomeActivity.this, Constant.TB_VehicleFriend, blogId, commentMsg, Variable.cust_name, Integer.valueOf(Variable.cust_id));
						//刷新列表
						articleDataList.clear();
						articleDataList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize)}, articleDataList);
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
			default:
				return;
			}
		}
	}
	
	//获取数据  TODO
		public void getArticleDatas(int actionCode){
			DBHelper dBHelper = new DBHelper(FriendHomeActivity.this);
			SQLiteDatabase reader = dBHelper.getReadableDatabase();
			Cursor cursor = reader.rawQuery("select * from " + Constant.TB_VehicleFriend + " where Cust_id = ?", new String[]{cust_id});
			Log.e(Tag,"查询数据库时user_id = " + cust_id);
			friendArticleTotalNum = cursor.getCount();
			if(friendArticleTotalNum > 0){
				//查询数据库
				Constant.totalPage1 = friendArticleTotalNum%Constant.pageSize1 > 0 ? friendArticleTotalNum/Constant.pageSize1 + 1 : friendArticleTotalNum/Constant.pageSize1;
				if(Constant.totalPage1 - 1 >= Constant.currentPage1){
					Constant.start1 = Constant.currentPage1*Constant.pageSize1;
					Constant.currentPage1 ++ ;
					articleDataList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " where Cust_id = ? order by Blog_id desc limit ?,?", new String[]{cust_id,String.valueOf(Constant.start1),String.valueOf(Constant.pageSize1)}, articleDataList);
					setArticleDataList(articleDataList);
				}
				Log.e("currentPage:" + Constant.currentPage1 , "totalPage:" + Constant.totalPage1);
				if(Constant.totalPage1 == Constant.currentPage1){
					isLoadMore = true;
				}
			}else{
				myDialog = ProgressDialog.show(FriendHomeActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
				myDialog.setCancelable(true);
				//  TODO  获取文章列表
				new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, getArticleList)).start();
				Log.e("获取文章url:",Constant.BaseUrl + "customer/" + cust_id + "/blog?auth_code=" + Variable.auth_code);
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
			for(int i = 0 ; i < jsonArray.length() ; i ++){
					//存储到数据库
					ContentValues values = new ContentValues();
					values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
					values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
					values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
					dBExcute.InsertDB(FriendHomeActivity.this,values,Constant.TB_VehicleFriend);
					if(i == (jsonArray.length()-1)){
						minBlogId = Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id"));
					}
					Log.e("解析数据:minBlogId",jsonArray.getJSONObject(i).getString("blog_id"));
			}
			DBHelper dBHelper = new DBHelper(getApplicationContext());
			SQLiteDatabase db = dBHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from " + Constant.TB_VehicleFriend, new String[]{});
			Log.e("服务器获取的数据总量：",cursor.getCount() + "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public void onRefresh() {
		}
		public void onLoadMore() {
			if(!isLoadMore){
				getArticleDatas(loadMoreAction);
			}else{
				DBHelper dBHelper = new DBHelper(FriendHomeActivity.this);
				SQLiteDatabase  sQLiteDatabase = dBHelper.getReadableDatabase();
				Cursor cursor = sQLiteDatabase.rawQuery("select * from " + Constant.TB_VehicleFriend, new String[]{});
				if(cursor.moveToLast()){
					minBlogId = cursor.getInt(cursor.getColumnIndex("Blog_id"));
				}
				
				new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code + "&min_id=" + minBlogId, loadMoreCode)).start();
			}
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
