package com.wise.wawc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.data.AdressData;
import com.wise.extend.AdressAdapter;
import com.wise.extend.AdressAdapter.OnCollectListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 搜索结果
 * @author honesty
 */
public class SearchMapActivity extends Activity {
    private final int getIsCollect = 1;
	WawcApplication app;
	MapView mMapView = null;
	MapController mMapController = null;
	List<Overlay> overlays;
	MKSearch mkSearch;
	List<AdressData> adressDatas = new ArrayList<AdressData>();
	ListView lv_activity_search_map;
	OverlayCar overlayCar;
	GeoPoint point;//当前位置
	
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
		lv_activity_search_map = (ListView) findViewById(R.id.lv_activity_search_map);
		lv_activity_search_map.setOnItemClickListener(onItemClickListener);
		ImageView iv_activity_search_map_back = (ImageView)findViewById(R.id.iv_activity_search_map_back);
		iv_activity_search_map_back.setOnClickListener(onClickListener);
		TextView tv_activity_search_map_title = (TextView)findViewById(R.id.tv_activity_search_map_title);
		mMapView = (MapView) findViewById(R.id.mv_search_map);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		point = new GeoPoint((int) (Variable.Lat * 1E6),(int) (Variable.Lon * 1E6));
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(12);// 设置地图zoom级别
		overlays = mMapView.getOverlays();
		
		Intent intent = getIntent();
		String keyWord = intent.getStringExtra("keyWord");
		tv_activity_search_map_title.setText(keyWord);
		//搜索关键字
		mkSearch = new MKSearch();
		mkSearch.init(app.mBMapManager, mkSearchListener);
		mkSearch.poiSearchNearBy(keyWord, point, 5000);
		//显示自己位置
		Drawable mark= getResources().getDrawable(R.drawable.ic_launcher);
		overlayCar = new OverlayCar(mark, mMapView);
		overlays.add(overlayCar);
		OverlayItem item = new OverlayItem(point, "item2", "item2");
		overlayCar.addItem(item);
	}
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.iv_activity_search_map_back:
				finish();
				break;

			default:
				break;
			}
		}
	};
	
	Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case getIsCollect:
                System.out.println(msg.obj.toString());
                break;

            default:
                break;
            }
        }
	    
	};
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			GeoPoint point = new GeoPoint((int)(adressDatas.get(arg2).getLat() * 1e6),(int)(adressDatas.get(arg2).getLon() * 1e6));
			mMapController.setCenter(point);// 设置地图中心点
		}
	};
	
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
				Toast.makeText(SearchMapActivity.this, R.string.search_result_not_found,Toast.LENGTH_SHORT).show();
				return;
			} else if (error != 0 || res == null) {
				Toast.makeText(SearchMapActivity.this, R.string.search_error,Toast.LENGTH_SHORT).show();
				return;
			}
			String str ="";
			for(MKPoiInfo mkPoiInfo : res.getAllPoi()){
				int distance = (int) DistanceUtil.getDistance(point, mkPoiInfo.pt);
				AdressData adressData = new AdressData();
				adressData.setName(mkPoiInfo.name);
				adressData.setAdress(mkPoiInfo.address);
				adressData.setPhone(mkPoiInfo.phoneNum);
				adressData.setLat(mkPoiInfo.pt.getLatitudeE6()/1e6);
				adressData.setLon(mkPoiInfo.pt.getLongitudeE6()/1e6);
				adressData.setDistance(distance);
				adressDatas.add(adressData);
				str = str + mkPoiInfo.name + ",";
			}
			System.out.println(str);
			String url = Constant.BaseUrl + "favorite/is_collect?auth_code=" + Variable.auth_code + 
			        "&names=" + str + "&cust_id=" + Variable.cust_id;
			new Thread(new NetThread.GetDataThread(handler, url, getIsCollect)).start();
			Collections.sort(adressDatas, new Comparator());
			final AdressAdapter adressAdapter = new AdressAdapter(SearchMapActivity.this, adressDatas,SearchMapActivity.this);
			adressAdapter.setOnCollectListener(new OnCollectListener() {                
                @Override
                public void OnCollect(int index) {
                    adressDatas.get(index).setIs_collect(true);
                    adressAdapter.notifyDataSetChanged();
                }
            });
			lv_activity_search_map.setAdapter(adressAdapter);
			for(int i = 0; i < adressDatas.size() ; i++){
				GeoPoint point1 = new GeoPoint((int) (adressDatas.get(i).getLat() * 1E6),(int) (adressDatas.get(i).getLon() * 1E6));
				OverlayItem item = new OverlayItem(point1, "item2", "item2");
				overlayCar.addItem(item);
			}
			mMapView.refresh();
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
	
	class OverlayCar extends ItemizedOverlay<OverlayItem>{
		public OverlayCar(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}
		@Override
		protected boolean onTap(int arg0) {
			System.out.println("item onTap:" + arg0);
			if(arg0 != 0){
				lv_activity_search_map.setSelection(arg0-1);
			}
			return super.onTap(arg0);
		}
	}
	
	class Comparator implements java.util.Comparator<AdressData>{
		@Override
		public int compare(AdressData lhs, AdressData rhs) {
			int m1 = lhs.getDistance();
			int m2 = rhs.getDistance();
			int result = 0 ;
			if(m1>m2){
				result = 1;
			}
			if (m1<m2) {
				result = -1;
			}
			return result;
		}		
	}
	
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