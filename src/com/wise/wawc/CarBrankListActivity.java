package com.wise.wawc;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.BrankModel;
import com.wise.data.CharacterParser;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Config;
import com.wise.pubclas.NetThread;
import com.wise.service.BrankAdapter;
import com.wise.service.ClearEditText;
import com.wise.service.PinyinComparator;
import com.wise.service.SideBar;
import com.wise.service.SideBar.OnTouchingLetterChangedListener;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.wise.wawc.R;

/**
 * 车辆型号
 * @author 王庆文
 */
public class CarBrankListActivity extends Activity implements IXListViewListener{
	private ClearEditText mClearEditText;   //自定义搜索栏
	private XListView vehicleBrankList = null;   //显示车的品牌
	private TextView letterIndex = null;    //字母索引选中提示框
	private SideBar sideBar = null;         //右侧字母索引栏
	
	private CharacterParser characterParser;   //将汉字转成拼音
	private List<BrankModel> brankModelList = new ArrayList<BrankModel>();    //车辆品牌集合
	
	private List<String> brankLogo = null;
	private String[] brankTemp;
	
	private PinyinComparator comparator;      //根据拼音排序
	
	private BrankAdapter adapter = null;
	
	//组件
	private Button choiceBrankBack = null;
	private Intent parentIntent = null;
	private int code = 0;
	
	private MyHandler myHandler = null;
	private ProgressDialog progressDialog;
	private static final int GET_BRANK = 1;
	
	private DBExcute dBExcute = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brank_list);
		parentIntent = getIntent();
		code = parentIntent.getIntExtra("code", 0);
		
		myHandler = new MyHandler();
		brankLogo = new ArrayList<String>();
		dBExcute = new DBExcute();
		//初始化控件
		initViews();
		
		
		vehicleBrankList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Log.e("click Index",arg2+"");
				String beank = ((BrankModel)adapter.getItem(arg2 - 1)).getVehicleBrank();
				finishCurrentActivity(beank);
			}
			
			private void finishCurrentActivity(String brank) {
				Intent intent = new Intent();
				intent.putExtra("brank", brank);
				if(code == MyVehicleActivity.resultCodeBrank){
					CarBrankListActivity.this.setResult(MyVehicleActivity.resultCodeBrank, intent);
				}else if(code == NewVehicleActivity.newVehicleBrank){
					CarBrankListActivity.this.setResult(NewVehicleActivity.newVehicleBrank, intent);
				}
				CarBrankListActivity.this.finish();
			}
		});
	}

	private void initViews() {
		Config.isHideFooter = true;
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		vehicleBrankList = (XListView) findViewById(R.id.vehicle_brank_list);
		vehicleBrankList.setXListViewListener(this);
		//不设置上拉加载无效
		vehicleBrankList.setPullLoadEnable(true);
		
		
		letterIndex = (TextView) findViewById(R.id.dialog);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		
		choiceBrankBack = (Button) findViewById(R.id.choice_vechile_back);
		choiceBrankBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CarBrankListActivity.this.finish();
			}
		});

		sideBar.setTextView(letterIndex);   //选中某个拼音索引   提示框显示
		
		characterParser = new CharacterParser().getInstance();
		comparator = new PinyinComparator();
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					vehicleBrankList.setSelection(position);
				}
			}
		});
		
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		progressDialog = ProgressDialog.show(CarBrankListActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		progressDialog.setCancelable(true);
		myHandler = new MyHandler();
		new Thread(new NetThread.GetDataThread(myHandler, Config.BaseUrl + "base/car_brand", GET_BRANK)).start();
	}
	
	
	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<BrankModel> filledData(String [] date){
		List<BrankModel> mSortList = new ArrayList<BrankModel>();
		for(int i=0; i<date.length; i++){
			BrankModel sortModel = new BrankModel();
			sortModel.setVehicleBrank(date[i]);
			Log.e("表情库--->",date[i]);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setVehicleLetter(sortString.toUpperCase());
			}else{
				sortModel.setVehicleLetter("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
		
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<BrankModel> filterDateList = new ArrayList<BrankModel>();
		
		
		//编辑框的内容为空的时候
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = brankModelList;
		}else{
			//匹配某些类型的品牌
			filterDateList.clear();
			for(BrankModel sortModel : brankModelList){
				String name = sortModel.getVehicleBrank();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, comparator);
		adapter.updateListView(filterDateList);
	}
	@Override  //下拉刷新
	public void onRefresh() {
		new Thread(new NetThread.GetDataThread(myHandler, Config.BaseUrl + "base/car_brand", GET_BRANK)).start();
		Log.e("下拉刷新","下拉刷新");
		myHandler.postDelayed(new Runnable() {
			public void run() {
				onLoad();
			}
		}, 2000);
	}
	@Override   //上拉加载
	public void onLoadMore() {
	}
	
	private void onLoad() {
		//获取当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String temp = sdf.format(new Date());
		String date = temp.substring(5, 16);
		vehicleBrankList.stopRefresh();
		vehicleBrankList.stopLoadMore();
		vehicleBrankList.setRefreshTime(date);
	}
	@Override
	public void PullUp() {
		// TODO Auto-generated method stub
		
	}
	
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case GET_BRANK:
				progressDialog.dismiss();
				String brankData = msg.obj.toString();
//				ContentValues contentValues = new ContentValues();
//				contentValues.put("Title", "brank");
//				contentValues.put("Content", "brankContent");
//				
//				dBExcute.InsertDB(CarBrankListActivity.this, contentValues, Config.TB_Base);  //存储到数据库
//				DBHelper dBHelper = new DBHelper(CarBrankListActivity.this);
//				SQLiteDatabase read = dBHelper.getReadableDatabase();
//				
//				Cursor cursor = read.rawQuery("select * from " + Config.TB_Base + " where Title = ?",new String[]{"brank"});
//				if(cursor.moveToNext()){
//					System.out.println("title=" + cursor.getString(cursor.getColumnIndex("Title")));
//					System.out.println("content=" + cursor.getString(cursor.getColumnIndex("Content")));
//				}
				Log.e("获取到的车辆品牌",brankData);
				
				try {
					JSONArray jsonArray = new JSONArray(brankData);
					int arrayLength = jsonArray.length();
					StringBuffer sb = new StringBuffer();
					for(int i = 0 ; i < arrayLength ; i ++){
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						if(i < arrayLength){
							sb.append(jsonObj.get("name")+",");
						}else{
							sb.append(jsonObj.get("name"));
						}
						//获取logo  TODO
					}
					brankTemp = sb.toString().split(",");
					Log.e("brankTemp",brankTemp.length+"");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(brankTemp != null && brankTemp.length >0){
					brankModelList = filledData(brankTemp);
				}else{
					//查询数据库 TODO
				}
				break;
			default:
				return;
			}
//			//添加数据(模拟)
//			brankModelList = filledData(getResources().getStringArray(R.array.date));
			//排序
			Collections.sort(brankModelList, comparator);
			
			adapter = new BrankAdapter(CarBrankListActivity.this, brankModelList);
			vehicleBrankList.setAdapter(adapter);
			super.handleMessage(msg);
			
		}
	}
	protected void onDestroy() {
		Config.isHideFooter = false;
		super.onDestroy();
	}
	protected void onPause() {
		Config.isHideFooter = false;
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Config.isHideFooter = true;
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Config.isHideFooter = true;
		super.onResume();
	}
}
