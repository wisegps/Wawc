package com.wise.wawc;
import java.io.File;
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
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	private List<String> carIdList = new ArrayList<String>(); 
	private String[] brankTemp;
	
	private PinyinComparator comparator;      //根据拼音排序
	
	private BrankAdapter adapter = null;
	
	//组件
	private ImageView choiceBrankBack = null;
	private Intent parentIntent = null;
	private int code = 0;
	
	private MyHandler myHandler = null;
	private ProgressDialog progressDialog;
	private static final int GET_BRANK = 1;
	private static final int REFRESH_BRANK = 2;
	
	private DBExcute dBExcute = null;
	private DBHelper dbHelper = null;
	private String imageName = "";
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brank_list);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		parentIntent = getIntent();
		code = parentIntent.getIntExtra("code", 0);
		
		myHandler = new MyHandler();
		brankLogo = new ArrayList<String>();
		dbHelper = new DBHelper(CarBrankListActivity.this);
		dBExcute = new DBExcute();
		//初始化控件
		initViews();
		
		
		vehicleBrankList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Log.e("click Index",arg2+"");
				String beank = ((BrankModel)adapter.getItem(arg2 - 1)).getVehicleBrank();
				String carId = carIdList.get(arg2 - 1);
				String carLogo = brankLogo.get(arg2 - 1);
				finishCurrentActivity(beank,carId,carLogo);
			}
			
			private void finishCurrentActivity(String brank,String carId,String carLogo) {
				Intent intent = new Intent();
				intent.putExtra("brank", brank);
				intent.putExtra("carId", carId);
				intent.putExtra("carLogo", carLogo);
				if (code == MyVehicleActivity.resultCodeBrank) {
					CarBrankListActivity.this.setResult(
							MyVehicleActivity.resultCodeBrank, intent);
				} else if (code == NewVehicleActivity.newVehicleBrank) {
					CarBrankListActivity.this.setResult(
							NewVehicleActivity.newVehicleBrank, intent);
				}
				CarBrankListActivity.this.finish();
			}
		});
	}

	private void initViews() {
		Constant.isHideFooter = true;
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		vehicleBrankList = (XListView) findViewById(R.id.vehicle_brank_list);
		vehicleBrankList.setXListViewListener(this);
		//不设置上拉加载无效
		vehicleBrankList.setPullLoadEnable(true);
		
		
		letterIndex = (TextView) findViewById(R.id.dialog);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		
		choiceBrankBack = (ImageView) findViewById(R.id.choice_vechile_back);
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
		
		getDate();
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
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/car_brand", REFRESH_BRANK)).start();

		Log.e("下拉刷新","下拉刷新");
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
	
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case GET_BRANK:
				progressDialog.dismiss();
				String brankData = msg.obj.toString();
				if(!"".equals(brankData)){
					ContentValues contentValues = new ContentValues();
					contentValues.put("Title", "carBrank");
					contentValues.put("Content", brankData);
					dBExcute.InsertDB(CarBrankListActivity.this, contentValues, Constant.TB_Base);
					JSONArray jsonArray = null;
					try {
						jsonArray = new JSONArray(brankData);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.e("服务器获取的数据：",brankData);
					parseJSON(jsonArray);
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败，稍后再试", 0).show();
				}
				break;
			case REFRESH_BRANK:
				onLoad();
				String refreshData = msg.obj.toString();
				if(!"".equals(refreshData)){
					ContentValues contentValues = new ContentValues();
					contentValues.put("Title", "carBrank");
					contentValues.put("Content", refreshData);
					//更新数据库  TODO
//					dBExcute.InsertDB(CarBrankListActivity.this, contentValues, Config.TB_Base);
					JSONArray jsonArray = null;
					try {
						jsonArray = new JSONArray(refreshData);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					Log.e("刷新得到服务器获取的数据：",jsonArray.length()+"");
					parseJSON(jsonArray);
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败，稍后再试", 0).show();
				}
				break;
			default:
				return;
			}
			//添加数据(模拟)
//			brankModelList = filledData(getResources().getStringArray(R.array.date));
			
			super.handleMessage(msg);
			
		}
	}
	
	
	
	//数据库存在数据则从数据库获取
	private void getDate() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{"carBrank"});
		 JSONArray jsonArray = null;
		if(cursor.moveToFirst()){
			progressDialog.dismiss();
			try {
				jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("Content")));
				Log.e("数据长度：",jsonArray.length()+"");
				parseJSON(jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/car_brand", GET_BRANK)).start();
		}
	}
	
	//解析json数据
	public void parseJSON(JSONArray jsonArray){

		
		Log.e("数据库数据：",jsonArray.toString());
		try {
			int arrayLength = jsonArray.length();
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < arrayLength ; i ++){
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				carIdList.add(jsonObj.getString("id"));
				if(i < arrayLength){
					sb.append(jsonObj.get("name")+",");
				}else{
					sb.append(jsonObj.get("name"));
				}
				Log.e("品牌：",jsonObj.toString());
				String logoImage;
				try {
					logoImage = jsonObj.getString("url_icon");
				} catch (Exception e) {
					logoImage = "";
					continue;
				}
				brankLogo.add(logoImage);
			}
			brankTemp = sb.toString().split(",");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//  TODO
		if(brankTemp != null && brankTemp.length >0){
			brankModelList = filledData(brankTemp);
		}
		
		Collections.sort(brankModelList, comparator);
		
		adapter = new BrankAdapter(CarBrankListActivity.this, brankModelList);
		vehicleBrankList.setAdapter(adapter);
	}
	
	protected void onDestroy() {
		Constant.isHideFooter = false;
		super.onDestroy();
	}
	protected void onPause() {
		Constant.isHideFooter = false;
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Constant.isHideFooter = true;
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Constant.isHideFooter = true;
		super.onResume();
	}
}
