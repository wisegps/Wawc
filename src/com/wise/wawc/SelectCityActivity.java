package com.wise.wawc;

import java.util.ArrayList;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 选择城市
 * 
 * @author honesty
 */
public class SelectCityActivity extends Activity implements
		MKOfflineMapListener {
	ListView lv_activity_select_city;
	MKOfflineMap mkOfflineMap;
	ArrayList<CityData> cityParentDatas; // 一级城市列表
	ArrayList<CityData> cityChildDatas; // 子级城市列表
	ArrayList<CityData> cityChooseDatas; // 选中省下城市列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WawcApplication app = (WawcApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(WawcApplication.strKey, null);
		}
		setContentView(R.layout.activity_select_city);

		MapView mMapView = new MapView(this);
		MapController mMapController = mMapView.getController();
		mkOfflineMap = new MKOfflineMap();
		mkOfflineMap.init(mMapController, this);

		lv_activity_select_city = (ListView) findViewById(R.id.lv_activity_select_city);
		GetCityList();
	}

	private void GetCityList() {
		cityParentDatas = new ArrayList<CityData>();
		cityChildDatas = new ArrayList<CityData>();
		ArrayList<MKOLSearchRecord> records2 = mkOfflineMap
				.getOfflineCityList();
		if (records2 != null) {
			for (int i = 1; i < records2.size(); i++) {
				MKOLSearchRecord r = records2.get(i);
				CityData cityData = new CityData();
				cityData.setCityID(r.cityID);
				cityData.setCityName(r.cityName);
				int CityStatus = 0;

				ArrayList<MKOLSearchRecord> records3 = r.childCities;// 查询子节点
				if (records3 != null) {
					CityStatus = 1;
					for (MKOLSearchRecord r1 : records3) {
						CityData cityChildData = new CityData();
						cityChildData.setCityParentID(r.cityID);
						cityChildData.setCityID(r1.cityID);
						cityChildData.setCityName(r1.cityName);
						cityChildData.setCityStatus(0);
						cityChildDatas.add(cityChildData);
					}
				}
				cityData.setCityStatus(CityStatus);
				cityParentDatas.add(cityData);
			}
		}
		cityChooseDatas = cityParentDatas;
		lv_activity_select_city.setAdapter(new AllCityAdapter(cityChooseDatas));
		lv_activity_select_city
				.setOnItemClickListener(onAllCityItemClickListener);
	}

	OnItemClickListener onAllCityItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			CityData cityChooseData = cityChooseDatas.get(arg2);
			if (cityChooseData.getCityID() == -1) {// 返回省份
				cityChooseDatas = cityParentDatas;
				lv_activity_select_city.setAdapter(new AllCityAdapter(
						cityChooseDatas));
			} else {
				if (cityChooseData.getCityStatus() == 1) {// 显示子级
					cityChooseDatas = new ArrayList<CityData>();
					// 第一个为省份，点击可以返回
					CityData cityChildData = new CityData();
					cityChildData.setCityParentID(cityChooseData.getCityID());
					cityChildData.setCityID(-1);
					cityChildData.setCityName(cityChooseData.getCityName());
					cityChildData.setCityStatus(2);
					cityChooseDatas.add(cityChildData);

					for (CityData cityData : cityChildDatas) {
						if (cityData.getCityParentID() == cityChooseData
								.getCityID()) {
							cityChooseDatas.add(cityData);
						}
					}
					lv_activity_select_city.setAdapter(new AllCityAdapter(
							cityChooseDatas));
				} else {
					if(cityChooseData.getCityParentID() != 0){
						for(CityData cityData : cityParentDatas){
							if(cityData.getCityID() == cityChooseData.getCityParentID()){
								System.out.println(cityData.getCityName() + "/" + cityChooseData.getCityName());
								break;
							}
						}
					}else{
						System.out.println(cityChooseData.getCityName());
					}					
				}
			}
		}
	};

	private class AllCityAdapter extends BaseAdapter {
		ArrayList<CityData> citys;
		LayoutInflater mInflater;

		public AllCityAdapter(ArrayList<CityData> citys) {
			this.citys = citys;
			mInflater = LayoutInflater.from(SelectCityActivity.this);
		}

		@Override
		public int getCount() {
			return citys.size();
		}

		@Override
		public Object getItem(int position) {
			return citys.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.item_select_city, null);
				holder = new ViewHolder();
				holder.tv_item_select_city = (TextView) convertView
						.findViewById(R.id.tv_item_select_city);
				holder.iv_item_select_city = (ImageView) convertView
						.findViewById(R.id.iv_item_select_city);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CityData cityData = citys.get(position);
			holder.tv_item_select_city.setText(cityData.getCityName());
			if (cityData.getCityStatus() == 0) {
				holder.iv_item_select_city.setImageBitmap(null);
			} else if (cityData.getCityStatus() == 1) {
				holder.iv_item_select_city.setImageBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.detail_arrow_down));
			} else {
				holder.iv_item_select_city.setImageBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.icon_arrow_up));
			}
			return convertView;
		}

		private class ViewHolder {
			TextView tv_item_select_city;
			ImageView iv_item_select_city;
		}
	}

	private class CityData {
		int CityParentID;
		int CityID;
		int CityStatus;
		String CityName;

		public int getCityParentID() {
			return CityParentID;
		}

		public void setCityParentID(int cityParentID) {
			CityParentID = cityParentID;
		}

		public int getCityID() {
			return CityID;
		}

		public void setCityID(int cityID) {
			CityID = cityID;
		}

		public String getCityName() {
			return CityName;
		}

		public void setCityName(String cityName) {
			CityName = cityName;
		}

		/**
		 * 0,选择，1可展开,2可收起
		 * 
		 * @return
		 */
		public int getCityStatus() {
			return CityStatus;
		}

		/**
		 * 0,选择，1可展开,2可收起
		 * 
		 * @param cityStatus
		 */
		public void setCityStatus(int cityStatus) {
			CityStatus = cityStatus;
		}

		@Override
		public String toString() {
			return "CityData [CityParentID=" + CityParentID + ", CityID="
					+ CityID + ", CityStatus=" + CityStatus + ", CityName="
					+ CityName + "]";
		}
		
	}

	@Override
	public void onGetOfflineMapState(int arg0, int arg1) {}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (cityChooseDatas.get(0).getCityStatus() == 2) {
				cityChooseDatas = cityParentDatas;
				lv_activity_select_city.setAdapter(new AllCityAdapter(
						cityChooseDatas));
			}else{
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}