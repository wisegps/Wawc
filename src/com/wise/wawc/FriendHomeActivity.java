package com.wise.wawc;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wise.data.Article;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.FriendArticleAdapter;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 好友主页
 * @author 王庆文
 */
public class FriendHomeActivity extends Activity implements IXListViewListener{
	private XListView friendArticleList;
	private EditText saySomething;
	private ImageView friendHead;    //点击好友头像显示资料
	private MyHandler myHandler = null;
	private Button cancle;
	private TextView friendName = null;

	private Intent lastPageDatas = null;
	private DBExcute dBExcute = null;
	private List<Article> articleDataList = new ArrayList<Article>();
	private ProgressDialog myDialog = null;
	private FriendArticleAdapter myAdapter = null;
	
	private static final int initDatas = 2;
	private static final int getArticleList = 3;
	private String cust_id = "";
	private int friendArticleTotalNum = 0;
	private boolean isLoadMore = false;
	private String MyTag = "FriendHomeActivity";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_home);
		friendArticleList = (XListView) findViewById(R.id.friend_home_article_list);
		saySomething = (EditText) findViewById(R.id.friend_home_say_something);
		myAdapter = new FriendArticleAdapter(FriendHomeActivity.this,saySomething,articleDataList);
		friendArticleList.setAdapter(myAdapter);
		friendHead = (ImageView) findViewById(R.id.friend_home_user_head);
		friendHead.setOnClickListener(new OnClickListener());
		friendName = (TextView) findViewById(R.id.friend_home_name);
		
		cancle = (Button) findViewById(R.id.friend_back);
		cancle.setOnClickListener(new OnClickListener());
		
		lastPageDatas = getIntent();
		dBExcute = new DBExcute();
		cust_id = lastPageDatas.getStringExtra("cust_id");
		Log.e(MyTag,cust_id);
		myHandler = new MyHandler();
		Message msg = new Message();
		msg.what = initDatas;
		myHandler.sendMessage(msg);
	}
	
	class OnClickListener implements android.view.View.OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.friend_home_user_head:
				startActivity(new Intent(FriendHomeActivity.this,
						FriendInformationActivity.class));
				break;
			case R.id.friend_back:
				FriendHomeActivity.this.finish();
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
				friendHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(Constant.UserIcon));
				friendName.setText(Variable.cust_name);
				
				//显示用户数据    查询本地数据库时候存在数据
				getArticleDatas(0);
				break;
			default:
				return;
			}
		}
	}
	
	//获取数据
		public void getArticleDatas(int actionCode){
			DBHelper dBHelper = new DBHelper(FriendHomeActivity.this);
			SQLiteDatabase reader = dBHelper.getReadableDatabase();
			Cursor cursor = reader.rawQuery("select * from " + Constant.TB_VehicleFriend + " where Cust_id = ?", new String[]{cust_id});
			friendArticleTotalNum = cursor.getCount();
			Log.e("好友文章总量:",friendArticleTotalNum+"");
			if(friendArticleTotalNum > 0){
				//查询数据库
				Constant.totalPage1 = friendArticleTotalNum%Constant.pageSize1 > 0 ? friendArticleTotalNum/Constant.pageSize1 + 1 : friendArticleTotalNum/Constant.pageSize1;
				if(Constant.totalPage1 - 1 >= Constant.currentPage1){
					Constant.start1 = Constant.currentPage1*Constant.pageSize1;
					Constant.currentPage1 ++ ;
					articleDataList = dBExcute.getArticlePageDatas(FriendHomeActivity.this, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(Constant.start1),String.valueOf(Constant.pageSize1)}, articleDataList);
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
				Log.e("获取文章url:",Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code);
			}
			Variable.articleList = articleDataList;
			myAdapter.refreshDates(articleDataList);
		}
		
		public void onRefresh() {
		}
		public void onLoadMore() {
			if(isLoadMore){
				Log.e(MyTag,"服务器获取更多");
				//获取成功之后将isLoadMore值变为false
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

		public List<Article> getArticleDataList() {
			return articleDataList;
		}
		public void setArticleDataList(List<Article> articleDataList) {
			this.articleDataList = articleDataList;
		}
}
