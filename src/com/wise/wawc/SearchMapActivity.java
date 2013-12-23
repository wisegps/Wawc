package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.data.AdressData;
import com.wise.extend.AdressAdapter;
import com.wise.pubclas.Config;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 搜索结果
 * @author honesty
 */
public class SearchMapActivity extends Activity {
	WawcApplication app;
	MapView mMapView = null;
	MapController mMapController = null;
	List<Overlay> overlays;
	MKSearch mkSearch;
	List<AdressData> adressDatas = new ArrayList<AdressData>();
	ListView lv_activity_search_map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (WawcApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(WawcApplication.strKey, null);
		}
		setContentView(R.layout.activity_search_map);
		mMapView = (MapView) findViewById(R.id.mv_search_map);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		GeoPoint point = new GeoPoint((int) (Config.Lat * 1E6),(int) (Config.Lon * 1E6));
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(12);// 设置地图zoom级别
		overlays = mMapView.getOverlays();
		lv_activity_search_map = (ListView) findViewById(R.id.lv_activity_search_map);
		Intent intent = getIntent();
		String keyWord = intent.getStringExtra("keyWord");
		mkSearch = new MKSearch();
		mkSearch.init(app.mBMapManager, mkSearchListener);
		mkSearch.poiSearchNearBy(keyWord, point, 5000);
	}
	MKSearchListener mkSearchListener = new MKSearchListener() {
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {}
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {}
		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {}
		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,int arg2) {}
		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			if (error == MKEvent.ERROR_RESULT_NOT_FOUND) {
				Toast.makeText(SearchMapActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_LONG).show();
				return;
			} else if (error != 0 || res == null) {
				Toast.makeText(SearchMapActivity.this, "搜索出错啦..",
						Toast.LENGTH_LONG).show();
				return;
			}
			// 将poi结果显示到地图上
			PoiOverlay poiOverlay = new PoiOverlay(SearchMapActivity.this,mMapView);
			poiOverlay.setData(res.getAllPoi());
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(poiOverlay);
			mMapView.refresh();
			for(MKPoiInfo mkPoiInfo : res.getAllPoi()){				
				AdressData adressData = new AdressData();
				adressData.setName(mkPoiInfo.address);
				adressData.setAdress("地址："+mkPoiInfo.name);
				adressData.setPhone("电话："+mkPoiInfo.phoneNum);
				adressDatas.add(adressData);
			}
			AdressAdapter adressAdapter = new AdressAdapter(SearchMapActivity.this, adressDatas);
			lv_activity_search_map.setAdapter(adressAdapter);
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {}
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {}
		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {}
		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {}
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.destroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}
}