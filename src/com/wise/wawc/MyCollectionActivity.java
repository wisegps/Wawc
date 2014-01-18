package com.wise.wawc;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wise.data.AdressData;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;
import com.wise.service.CollectionAdapter;
import com.wise.sql.DBExcute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
/**
 * 我的收藏
 * @author 王庆文
 */
public class MyCollectionActivity extends Activity implements IXListViewListener{
	private XListView collectionList;
	private CollectionAdapter collectionAdapter;
	private Button menuBt;
	private Button homeBt;
	
	ProgressDialog myDialog = null;
	private MyHandler myHandler = null;
	private DBExcute dBExcute = null;
	
	private int start = 0;
	private int pageSize = 5;   //每页内容
	private int totalPage = 0;  //总页数
	private int currentPage = 0;  //当前页
	private List<AdressData> adressData = new ArrayList<AdressData>();
	private static int excuteCode = 2;   //2：获取数据  1: 刷新数据
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_collection);
		collectionList = (XListView) findViewById(R.id.my_collection_list);
		menuBt = (Button) findViewById(R.id.my_vechile_menu);
		homeBt = (Button) findViewById(R.id.my_vechile_home);
		myHandler = new MyHandler();
		dBExcute = new DBExcute();
		//不设置上拉加载无效
		collectionList.setPullLoadEnable(true);
		collectionList.setXListViewListener(this);
		homeBt.setOnClickListener(new ClickListener());
		menuBt.setOnClickListener(new ClickListener());
		
		
		//获取数据
		adressData = getCollectionDatas(start,pageSize,excuteCode,adressData);
		collectionAdapter = new CollectionAdapter(this,adressData);
		collectionList.setAdapter(collectionAdapter);
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}
	

	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.my_vechile_home:
				ActivityFactory.A.ToHome();
				break;
			case R.id.my_vechile_menu:
				ActivityFactory.A.LeftMenu();
				break;
			default :
				return;
			}
		}
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ActivityFactory.A.HideMenu();
		return false;
	}
	@Override
	public void onRefresh() {
		excuteCode = 1;
		Log.e("下拉刷新","下拉刷新");
		myHandler.postDelayed(new Runnable() {
			public void run() {
				onLoad();
			}
		}, 2000);
	}
	@Override
	public void onLoadMore() {
		Log.e("上拉加载","上拉加载");
		myHandler.postDelayed(new Runnable() {
			public void run() {
				onLoad();
			}
		}, 2000);
		int totalNum = dBExcute.getTotalCount(Constant.TB_Collection, MyCollectionActivity.this);
		Log.e("总数据：",totalNum + "");
		Log.e("当前页：",currentPage + "");
		totalPage = totalNum%pageSize > 0 ? totalNum/pageSize + 1 : totalNum/pageSize;
		if(totalPage - 1 > currentPage){
			start = ((currentPage * pageSize) + pageSize);
			currentPage ++ ;
			excuteCode = 2;
			adressData = getCollectionDatas(start,pageSize,excuteCode,adressData);
			collectionAdapter.refish(adressData);
		}
	}
	public void PullUp() {
	}
	
	private void onLoad() {
		//获取当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String temp = sdf.format(new Date());
		String date = temp.substring(5, 16);
		collectionList.stopRefresh();
		collectionList.stopLoadMore();
		collectionList.setRefreshTime(date);
	}
	
	private List<AdressData> getCollectionDatas(int start,int pageSize,int excuteCode,List<AdressData> adressData) {
		return dBExcute.getPageDatas(MyCollectionActivity.this, "select * from " + Constant.TB_Collection + " where Cust_id=? order by favorite_id desc limit ?,?", new String[]{Variable.cust_id,String.valueOf(start),String.valueOf(pageSize)},excuteCode,adressData);
	}
}
