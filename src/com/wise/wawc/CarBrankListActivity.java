package com.wise.wawc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.wise.data.BrankModel;
import com.wise.data.CharacterParser;
import com.wise.service.BrankAdapter;
import com.wise.service.ClearEditText;
import com.wise.service.PinyinComparator;
import com.wise.service.SideBar;
import com.wise.service.SideBar.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * 车辆型号
 * @author 王庆文
 */
public class CarBrankListActivity extends Activity {
	private ClearEditText mClearEditText;   //自定义搜索栏
	private ListView vehicleBrankList = null;   //显示车的品牌
	private TextView letterIndex = null;    //字母索引选中提示框
	private SideBar sideBar = null;         //右侧字母索引栏
	
	private CharacterParser characterParser;   //将汉字转成拼音
	private List<BrankModel> brankModelList;    //车辆品牌集合
	
	private PinyinComparator comparator;      //根据拼音排序
	
	private BrankAdapter adapter = null;
	
	//组件
	private Button choiceBrankBack = null;
	private Intent parentIntent = null;
	private int code = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brank_list);
		parentIntent = getIntent();
		code = parentIntent.getIntExtra("code", 0);
		//初始化控件
		initViews();
		
		
		vehicleBrankList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Log.e("click Index",arg2+"");
				String beank = ((BrankModel)adapter.getItem(arg2)).getVehicleBrank();
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
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		vehicleBrankList = (ListView) findViewById(R.id.vehicle_brank_list);
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
		
		
		//添加数据(模拟)
		brankModelList = filledData(getResources().getStringArray(R.array.date));
		//排序
		Collections.sort(brankModelList, comparator);
		for(int i = 0 ; i < brankModelList.size() ; i++){
		    System.out.println(brankModelList.get(i).toString());
		}
		adapter = new BrankAdapter(this, brankModelList);
		vehicleBrankList.setAdapter(adapter);
		
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
	
}
