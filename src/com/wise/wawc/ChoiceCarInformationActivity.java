package com.wise.wawc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.BrankAdapter;
import com.wise.service.ClearEditText;
import com.wise.service.PinyinComparator;
import com.wise.service.SeriesAdapter;
import com.wise.service.SideBar;
import com.wise.service.SideBar.OnTouchingLetterChangedListener;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import com.wise.wawc.ChoiceCarInformationActivity.MyHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 选择车辆品牌
 */
public class ChoiceCarInformationActivity extends Activity implements IXListViewListener{
    private static final String TAG = "CarBrankListActivity";
    /**
	 * 品牌
	 */
	private RelativeLayout carBrankLayout = null;   //车牌
	private ListView carModlesLayout = null;   //车型
	private ListView carTypeLayout = null;   //车款
	private ClearEditText mClearEditText;   //自定义搜索栏
	private XListView vehicleBrankList = null;   //显示车的品牌
	private TextView letterIndex = null;    //字母索引选中提示框
	private SideBar sideBar = null;         //右侧字母索引栏
	
	private CharacterParser characterParser;   //将汉字转成拼音
	private List<BrankModel> brankModelList = new ArrayList<BrankModel>();    //车辆品牌集合
	
	private List<String> brankLogo = null;
	private List<String[]> carSeriesList = new ArrayList<String[]>();
	private List<String[]> carSeriesNameList = new ArrayList<String[]>();
	
	private PinyinComparator comparator;      //根据拼音排序
	
	private BrankAdapter brankAdapter = null;
	private SeriesAdapter seriesAdapter = null;
	
	//组件
	private ImageView choiceBrankBack = null;
	private Intent parentIntent = null;
	private int code = 0;
	
	private MyHandler myHandler = null;
	private ProgressDialog progressDialog;
	private static final int GET_BRANK = 1;
	private static final int GET_SERIES = 3;
	private static final int GET_TYPE = 4;
	private static final int REFRESH_BRANK = 2;
	
	private DBExcute dBExcute = null;
	private DBHelper dbHelper = null;
	private String imageName = "";
	private ImageView back;
	private TextView title = null;
	String carSeriesId;
	String carBrankId;
	String carSeries;
	String carBrank;
	String logoUrl = "";
	private MyThread myThread = null;
	private boolean imageDownload = true;
	
	public static final String carBrankTitle = "carBrank";  //数据库基础表车辆品牌的标题字段
	public static final String carSeriesTitle = "carSeries";  //数据库基础表车辆款式的标题字段
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.choice_car_information);
		parentIntent = getIntent();
		code = parentIntent.getIntExtra("code", 0);
		
		myHandler = new MyHandler();
		brankLogo = new ArrayList<String>();
		dbHelper = new DBHelper(ChoiceCarInformationActivity.this);
		dBExcute = new DBExcute();
		//初始化控件
		initViews();
	}

	private void initViews() {
		carBrankLayout = (RelativeLayout) findViewById(R.id.choice_car_brank);  
		vehicleBrankList = (XListView) findViewById(R.id.vehicle_brank_list);   //  车牌
		carModlesLayout = (ListView) findViewById(R.id.choice_car_modles);    //车型
		carTypeLayout = (ListView) findViewById(R.id.choice_car_type);      //车款
		back = (ImageView) findViewById(R.id.choice_vechile_information_back);
		title = (TextView) findViewById(R.id.choice_vechile_information_title);
		
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(code == 3 || code == NewVehicleActivity.newVehicleBrank){
						if(carBrankLayout.getVisibility() == View.VISIBLE){
							ChoiceCarInformationActivity.this.finish();
						}else if(carModlesLayout.getVisibility() == View.VISIBLE){
							carModlesLayout.setVisibility(View.GONE);
							title.setText(R.string.choice_brank);
							carBrankLayout.setVisibility(View.VISIBLE);
						}else if(carTypeLayout.getVisibility() == View.VISIBLE){
							carTypeLayout.setVisibility(View.GONE);
							title.setText(R.string.choice_series);
							carModlesLayout.setVisibility(View.VISIBLE);
						}
				}else if(code == 12 || code == NewVehicleActivity.newVehicleSeries){
					if(carModlesLayout.getVisibility() == View.VISIBLE){
						ChoiceCarInformationActivity.this.finish();
					}
					if(carTypeLayout.getVisibility() == View.VISIBLE){
						carTypeLayout.setVisibility(View.GONE);
						title.setText(R.string.choice_series);
						carModlesLayout.setVisibility(View.VISIBLE);
					}
				}else if(code == 14 || code == NewVehicleActivity.newVehicleType){
					ChoiceCarInformationActivity.this.finish();
				}
			}
		});
		
		
		//选择品牌页面
		vehicleBrankList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				BrankModel brankModel = (BrankModel) vehicleBrankList.getItemAtPosition(arg2);
				//  TODO
				carBrank = brankModel.getVehicleBrank();
				carBrankId = brankModel.getBrankId();
				logoUrl = brankModel.getLogoUrl();
				Log.e("品牌id:",carBrankId);
				Log.e("品牌:",carBrank);
				//点击品牌列表   选择车型
				progressDialog = ProgressDialog.show(ChoiceCarInformationActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
				progressDialog.setCancelable(true);
				getDate(carBrankTitle + carBrankId, Constant.BaseUrl + "base/car_series?pid=" + carBrankId,GET_SERIES);
			}
		});
		//选择车型页面
		carModlesLayout.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//点击车型   选择车款
				String[] str = (String[]) carModlesLayout.getItemAtPosition(arg2);
				carSeriesId = str[0];
				carSeries = str[1];
				progressDialog = ProgressDialog.show(ChoiceCarInformationActivity.this,getString(R.string.dialog_title),getString(R.string.dialog_message));
				progressDialog.setCancelable(true);
				getDate(carSeriesTitle + carSeriesId, Constant.BaseUrl + "base/car_type?pid=" + carSeriesId,GET_TYPE);
			}
		});
		//选择车款页面
		carTypeLayout.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//点击返回我的爱车  并将相关数据显示出来
				Intent intent = new Intent();
				intent.putExtra("brank", carBrank);
				intent.putExtra("brankId", carBrankId);
				intent.putExtra("series", carSeries);
				intent.putExtra("seriesId", carSeriesId);
				intent.putExtra("typeId", carSeriesNameList.get(arg2)[0]);
				intent.putExtra("type", carSeriesNameList.get(arg2)[1]);
				intent.putExtra("logo", logoUrl);
				ChoiceCarInformationActivity.this.setResult(code, intent);
				ChoiceCarInformationActivity.this.finish();
			}
		});
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		vehicleBrankList.setXListViewListener(this);
		vehicleBrankList.setPullLoadEnable(false);
		
		
		letterIndex = (TextView) findViewById(R.id.dialog);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		
		choiceBrankBack = (ImageView) findViewById(R.id.choice_vechile_back);
		sideBar.setTextView(letterIndex);   //选中某个拼音索引   提示框显示
		characterParser = new CharacterParser().getInstance();
		comparator = new PinyinComparator();
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			public void onTouchingLetterChanged(String s) {
				int position = brankAdapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					vehicleBrankList.setSelection(position);
				}
			}
		});
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
		progressDialog = ProgressDialog.show(ChoiceCarInformationActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
		progressDialog.setCancelable(true);
		myHandler = new MyHandler();
		//获取车牌   
		if(code == 3 || code == NewVehicleActivity.newVehicleBrank){
			Log.e("点击选择品牌","点击选择品牌");
			getDate(carBrankTitle,Constant.BaseUrl + "base/car_brand",GET_BRANK);
		}else if(code == 12 || code == NewVehicleActivity.newVehicleSeries){
			carBrankId = getIntent().getStringExtra("brankId");
			carBrank = getIntent().getStringExtra("carBrank");
			logoUrl = getIntent().getStringExtra("logo");
			getDate(carBrankTitle + carBrankId, Constant.BaseUrl + "base/car_series?pid=" + carBrankId,GET_SERIES);
			Log.e("点击车型","点击车型");
		}else if(code == 14 || code == NewVehicleActivity.newVehicleType){
			carBrankId = getIntent().getStringExtra("brankId");
			carBrank = getIntent().getStringExtra("carBrank");
			carSeriesId = getIntent().getStringExtra("seriesId");
			carSeries = getIntent().getStringExtra("series");
			logoUrl = getIntent().getStringExtra("logo");
			Log.e("点击车款","点击车款");
			getDate(carSeriesTitle + carSeriesId, Constant.BaseUrl + "base/car_type?pid=" + carSeriesId,GET_TYPE);
		}
	}
	
	//处理服务器返回的数据
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch(msg.what){
			case GET_BRANK:
				String brankData = msg.obj.toString();
				//存到数据库
				insertDatabases(carBrankTitle,brankData,ChoiceCarInformationActivity.this);
				if(!"".equals(brankData)){
					ContentValues contentValues = new ContentValues();
					contentValues.put("Title", carBrankTitle);
					contentValues.put("Content", brankData);
					dBExcute.InsertDB(ChoiceCarInformationActivity.this, contentValues, Constant.TB_Base);
					JSONArray jsonArray = null;
					try {
						jsonArray = new JSONArray(brankData);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					parseJSON(jsonArray,GET_BRANK);
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败，稍后再试", 0).show();
				}
				break;
			case REFRESH_BRANK:
				onLoad();
				String refreshData = msg.obj.toString();
				if(!"".equals(refreshData)){
					SQLiteDatabase db = dbHelper.getReadableDatabase();
					Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{"carBrank"});
					if(cursor.moveToFirst()){
						db.delete(Constant.TB_Base, "Title = ?", new String[]{"carBrank"});
						}
					ContentValues contentValues = new ContentValues();
					contentValues.put("Title", carBrankTitle);
					contentValues.put("Content", refreshData);
					//更新数据库  
					dBExcute.InsertDB(ChoiceCarInformationActivity.this, contentValues, Constant.TB_Base);
					JSONArray jsonArray = null;
					try {
						jsonArray = new JSONArray(refreshData);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					parseJSON(jsonArray,REFRESH_BRANK);
				}else{
					Toast.makeText(getApplicationContext(), "获取数据失败，稍后再试", 0).show();
				}
				Log.e(TAG,"imageDownload = " + imageDownload);
				//  TODO
				imageDownload = true;
				new Thread(new Runnable() {
					public void run() {
						myThread.run();
					}
				}).start();
				
				break;
			case GET_SERIES:   //车型
				String seriesData = msg.obj.toString();
				JSONArray jsonArray = null;
				if(!"[]".equals(seriesData)){
					try {
						jsonArray = new JSONArray(seriesData);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				insertDatabases(carBrankTitle + carBrankId,seriesData,ChoiceCarInformationActivity.this);
				
				parseJSON(jsonArray,GET_SERIES);
				
				break;
			case GET_TYPE:   //车款
				String resultType = msg.obj.toString();
				JSONArray jsonType = null;
				try {
					jsonType = new JSONArray(msg.obj.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//更新数据库  
				insertDatabases(carSeriesTitle + carSeriesId,resultType,ChoiceCarInformationActivity.this);
				parseJSON(jsonType,GET_TYPE);
				break;
			case 38:
				brankAdapter.notifyDataSetChanged();
				break;
			default:
				return;
			}
			super.handleMessage(msg);
		}
	}
	
	/**
	 * @param whereValues  查询数据库时搜索条件
	 * @param url   数据库没有数据  服务器获取的地址
	 * @param handlerWhat   服务器获取handler异步处理的标识
	 */
	private void getDate(String whereValues,String url,int handlerWhat) {
		Log.e("title:",whereValues);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		 Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base + " where Title = ?", new String[]{whereValues});
		 JSONArray jsonArray = null;
		if(cursor.moveToFirst()){
			Log.e("数据库数据","数据库数据");
			try {
				jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("Content")));
				parseJSON(jsonArray,handlerWhat);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			Log.e("服务器数据","服务器数据");
			new Thread(new NetThread.GetDataThread(myHandler, url, handlerWhat)).start();
		}
		cursor.close();
		db.close();
	}
	
	//解析json数据
	public void parseJSON(JSONArray jsonArray,int what){
		progressDialog.dismiss();
		switch(what){
		case GET_BRANK:   //解析车牌数据
			List<BrankModel> brankList = null;
			try {
				int arrayLength = jsonArray.length();
				brankList = new ArrayList<BrankModel>();
				for(int i = 0 ; i < arrayLength ; i ++){
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					BrankModel brankModel = new BrankModel();
					brankModel.setVehicleBrank(jsonObj.getString("name"));
					brankModel.setBrankId(jsonObj.getString("id"));
					if(jsonObj.opt("url_icon") != null){
						brankModel.setLogoUrl(jsonObj.getString("url_icon"));
					}else{
						brankModel.setLogoUrl("");
					}
					brankList.add(brankModel);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			brankModelList = filledData(brankList);
			//排序
			Collections.sort(brankModelList, comparator);
			carModlesLayout.setVisibility(View.GONE);
			carTypeLayout.setVisibility(View.GONE);
			title.setText(R.string.choice_brank);
			brankAdapter = new BrankAdapter(ChoiceCarInformationActivity.this, brankModelList);
			vehicleBrankList.setAdapter(brankAdapter);
			
			//刷新品牌logo
			myThread = new MyThread();
			myThread.start();
			break;
		case GET_SERIES:   //解析车型数据
			carSeriesList.clear();
			int jsonLength = jsonArray.length();
			for(int i = 0 ; i < jsonLength ; i ++){
				String[] series = new String[2];
				try {
					series[0] = jsonArray.getJSONObject(i).getString("id");
					series[1] = jsonArray.getJSONObject(i).getString("show_name");
					carSeriesList.add(series);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			//隐藏车牌列表  显示车型列表     
			carBrankLayout.setVisibility(View.GONE);
			carTypeLayout.setVisibility(View.GONE);
			seriesAdapter = new SeriesAdapter(carSeriesList, ChoiceCarInformationActivity.this,1,null);
			carModlesLayout.setAdapter(seriesAdapter);
			title.setText(R.string.choice_series);
			carModlesLayout.setVisibility(View.VISIBLE);
			break;
		case REFRESH_BRANK:   //刷新车牌数据
			
			break;
		case GET_TYPE:   //获取车款
			int jsonTypeLength = jsonArray.length();
			for(int i = 0 ; i < jsonTypeLength ; i ++){
				String[] typeStr = new String[2];
				try {
					typeStr[0] = jsonArray.getJSONObject(i).getString("id");
					typeStr[1] = jsonArray.getJSONObject(i).getString("name");
					carSeriesNameList.add(typeStr);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			carBrankLayout.setVisibility(View.GONE);
			carModlesLayout.setVisibility(View.GONE);
			seriesAdapter = new SeriesAdapter(null,ChoiceCarInformationActivity.this,2,carSeriesNameList);
			seriesAdapter.refresh(2, carSeriesNameList);
			carTypeLayout.setAdapter(seriesAdapter);
			title.setText(R.string.choice_type);
			carTypeLayout.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<BrankModel> filledData(List<BrankModel> brankList){
		for(int i=0; i<brankList.size(); i++){
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(brankList.get(i).getVehicleBrank());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				brankList.get(i).setVehicleLetter(sortString.toUpperCase());
			}else{
				brankList.get(i).setVehicleLetter("#");
			}
		}
		return brankList;
		
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
		brankAdapter.updateListView(filterDateList);
	}
	@Override  //下拉刷新
	public void onRefresh() {
		new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "base/car_brand", REFRESH_BRANK)).start();
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
	protected void onDestroy() {
		super.onDestroy();
	}
	protected void onPause() {
		imageDownload = false;
		super.onPause();
	}
	protected void onRestart() {
		super.onRestart();
	}
	protected void onResume() {
		super.onResume();
	}
	
	//将获取的数据存到数据库
	public static void insertDatabases(String titleName,String content,Context context){
		ContentValues values = new ContentValues();
		values.put("Cust_id", Variable.cust_id);
		values.put("Title", titleName);
		values.put("Content", content);
		DBExcute dBExcute = new DBExcute();
		dBExcute.InsertDB(context, values, Constant.TB_Base);
	}
	
	public void logoImageIsExist(final String imagePath,final String name,final String logoUrl){
		File filePath = new File(imagePath);
		File imageFile = new File(imagePath + name + ".png");
		if(!filePath.exists()){
			filePath.mkdir();
		}
		if(!imageFile.exists()){
			Bitmap bitmap = GetSystem.getBitmapFromURL(Constant.ImageUrl + logoUrl);
			if (bitmap != null) {
				createImage(imagePath + name + ".png", bitmap);
			}
		}
	}
	
	class MyThread extends Thread{
		public void run() {
			Log.e(TAG,"run()");
			for(int i = 0 ; i < brankModelList.size() ; i++){
				if(!"".equals(brankModelList.get(i).getLogoUrl()) && brankModelList.get(i).getLogoUrl() != null){
					if(imageDownload){
						logoImageIsExist(Constant.VehicleLogoPath, brankModelList.get(i).getVehicleBrank(), brankModelList.get(i).getLogoUrl());
					}else{
						continue;
					}
				}
			}
			super.run();
		}
		
		public void reStart(){
			run();
		}
	}
	
	//向SD卡中添加图片
	public void createImage(String fileName,Bitmap bitmap){
		FileOutputStream b = null;
		try {  
            b = new FileOutputStream(fileName);  
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
            Message msg = new Message();
            msg.what = 38;
            myHandler.sendMessage(msg);
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                b.flush();  
                b.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
	}
}
