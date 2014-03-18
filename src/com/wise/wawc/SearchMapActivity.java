package com.wise.wawc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.sharesdk.framework.ShareSDK;
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
import com.wise.data.CarData;
import com.wise.extend.AdressAdapter;
import com.wise.extend.AdressAdapter.OnCollectListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
 * 
 * @author honesty
 */
public class SearchMapActivity extends Activity {
    private static final String TAG = "SearchMapActivity";
    private final int getIsCollect = 1;
    private final int get4s = 2;
    
    MapView mMapView = null;
    MapController mMapController = null;
    List<Overlay> overlays;
    MKSearch mkSearch;
    List<AdressData> adressDatas = new ArrayList<AdressData>();
    ListView lv_activity_search_map;
    AdressAdapter adressAdapter;

    OverlayCar overlayCar;
    GeoPoint point;// 当前位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WawcApplication app = (WawcApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(WawcApplication.strKey, null);
        }
        setContentView(R.layout.activity_search_map);
        ShareSDK.initSDK(this);
        lv_activity_search_map = (ListView) findViewById(R.id.lv_activity_search_map);
        lv_activity_search_map.setOnItemClickListener(onItemClickListener);
        adressAdapter = new AdressAdapter(SearchMapActivity.this, adressDatas,
                SearchMapActivity.this);
        lv_activity_search_map.setAdapter(adressAdapter);
        adressAdapter.setOnCollectListener(new OnCollectListener() {
            @Override
            public void OnCollect(int index) {
                adressDatas.get(index).setIs_collect(true);
                adressAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnShare(int index) {
                AdressData adressData = adressDatas.get(index);
                String url = "http://api.map.baidu.com/geocoder?location="
                        + adressData.getLat() + "," + adressData.getLon()
                        + "&coord_type=bd09ll&output=html";
                StringBuffer sb = new StringBuffer();
                sb.append("【地点】");
                sb.append(adressData.getName());
                sb.append("," + adressData.getAdress());
                sb.append("," + adressData.getPhone());
                sb.append("," + url);
                GetSystem.share(SearchMapActivity.this, sb.toString(), "",
                        (float) adressData.getLat(),
                        (float) adressData.getLon(),"地点",url);
            }
        });

        ImageView iv_activity_search_map_back = (ImageView) findViewById(R.id.iv_activity_search_map_back);
        iv_activity_search_map_back.setOnClickListener(onClickListener);
        TextView tv_activity_search_map_title = (TextView) findViewById(R.id.tv_activity_search_map_title);
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
        if (keyWord.equals(getResources().getString(R.string.four_s))) {
            // 4S店数据去自己服务器读取
            if (Variable.carDatas != null || Variable.carDatas.size() > 0) {
                for (CarData carData : Variable.carDatas) {
                    if (carData.isCheck()) {
                        String car_brand = carData.getCar_brand();
                        SharedPreferences preferences = getSharedPreferences(
                                Constant.sharedPreferencesName,
                                Context.MODE_PRIVATE);
                        String City = preferences.getString(
                                Constant.LocationCity, "深圳");
                        try {
                            String url = Constant.BaseUrl + "base/dealer?city="
                                    + URLEncoder.encode(City, "UTF-8")
                                    + "&brand=" + URLEncoder.encode(car_brand, "UTF-8")
                                    + "&lon=" + Variable.Lon + "&lat="
                                    + Variable.Lat + "&cust_id="
                                    + Variable.cust_id;
                            new Thread(new NetThread.GetDataThread(handler,
                                    url, get4s)).start();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        } else {
            // 搜索关键字
            Log.d(TAG, keyWord + "Variable.Lat = " + Variable.Lat + " , Variable.Lon = " + Variable.Lon);
            mkSearch = new MKSearch();
            mkSearch.init(WawcApplication.getInstance().mBMapManager, mkSearchListener);
            mkSearch.poiSearchNearBy(keyWord, point, 50000);
        }
        // 显示自己位置
        Drawable mark = getResources().getDrawable(R.drawable.body_icon_location2);
        overlayCar = new OverlayCar(mark, mMapView);
        overlays.add(overlayCar);
        
        Drawable markMe = getResources().getDrawable(R.drawable.body_icon_outset);
        OverlayCar overlayMe = new OverlayCar(markMe, mMapView);
        overlays.add(overlayMe);
        OverlayItem item = new OverlayItem(point, "item2", "item2");
        item.setAnchor(OverlayItem.ALING_CENTER);
        overlayMe.addItem(item);
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

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case getIsCollect:
                System.out.println(msg.obj.toString());
                jsonCollect(msg.obj.toString());
                adressAdapter.notifyDataSetChanged();
                break;
            case get4s:
                jsonDealAdress(msg.obj.toString());
                adressAdapter.notifyDataSetChanged();
                break;
            }
        }

    };

    private void jsonDealAdress(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {//TODO 
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AdressData adressData = new AdressData();
                adressData.setAdress(jsonObject.getString("address"));
                adressData.setName(jsonObject.getString("name"));
                adressData.setPhone(jsonObject.getString("tel"));
                adressData.setLat(jsonObject.getDouble("lat"));
                adressData.setLon(jsonObject.getDouble("lon"));
                adressData.setDistance(jsonObject.getInt("distance"));
                if (jsonObject.getString("is_collect").equals("1")) {
                    // 收藏
                    adressData.setIs_collect(true);
                } else {
                    // 未收藏
                    adressData.setIs_collect(false);
                }
                adressDatas.add(adressData);

                GeoPoint point = new GeoPoint((int) (adressData.getLat() * 1E6),(int) (adressData.getLon() * 1E6));
                OverlayItem item = new OverlayItem(point, "item2", "item2");
                overlayCar.addItem(item);
            }
            mMapView.refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            GeoPoint point = new GeoPoint(
                    (int) (adressDatas.get(arg2).getLat() * 1e6),
                    (int) (adressDatas.get(arg2).getLon() * 1e6));
            mMapController.setCenter(point);// 设置地图中心点
        }
    };

    /**
     * 解析返回的数据
     * 
     * @param result
     */
    private void jsonCollect(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                for (int j = 0; j < adressDatas.size(); j++) {
                    if (adressDatas.get(j).getName().equals(name)) {
                        adressDatas.get(j).setIs_collect(true);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            Log.d(TAG, "type = " + type + " , error = " + error);
            if (error == MKEvent.ERROR_RESULT_NOT_FOUND) {
                Toast.makeText(SearchMapActivity.this,
                        R.string.search_result_not_found, Toast.LENGTH_SHORT)
                        .show();
                return;
            } else if (error != 0 || res == null) {
                Toast.makeText(SearchMapActivity.this, R.string.search_error,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String str = "";// 用户判断是否已经收藏
            for (MKPoiInfo mkPoiInfo : res.getAllPoi()) {
                int distance = (int) DistanceUtil.getDistance(point,mkPoiInfo.pt);
                AdressData adressData = new AdressData();
                adressData.setName(mkPoiInfo.name);
                adressData.setAdress(mkPoiInfo.address);
                adressData.setPhone(mkPoiInfo.phoneNum);
                adressData.setLat(mkPoiInfo.pt.getLatitudeE6() / 1e6);
                adressData.setLon(mkPoiInfo.pt.getLongitudeE6() / 1e6);
                adressData.setDistance(distance);
                adressDatas.add(adressData);
                str = str + mkPoiInfo.name + ",";
            }
            Log.d(TAG, "str = " + str);
            Collections.sort(adressDatas, new Comparator());// 排序
            adressAdapter.notifyDataSetChanged();
            for (int i = 0; i < adressDatas.size(); i++) {
                GeoPoint point1 = new GeoPoint((int) (adressDatas.get(i)
                        .getLat() * 1E6),
                        (int) (adressDatas.get(i).getLon() * 1E6));
                OverlayItem item = new OverlayItem(point1, "item2", "item2");
                overlayCar.addItem(item);
            }
            mMapView.refresh();
            // 判断是否收藏
            String url;
            try {
                url = Constant.BaseUrl + "favorite/is_collect?auth_code="
                        + Variable.auth_code + "&names="
                        + URLEncoder.encode(str, "UTF-8") + "&cust_id="
                        + Variable.cust_id;
                new Thread(new NetThread.GetDataThread(handler, url,
                        getIsCollect)).start();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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

    class OverlayCar extends ItemizedOverlay<OverlayItem> {
        public OverlayCar(Drawable arg0, MapView arg1) {
            super(arg0, arg1);
        }

        @Override
        protected boolean onTap(int arg0) {
            System.out.println("item onTap:" + arg0);
            if (arg0 != 0) {
                lv_activity_search_map.setSelection(arg0 - 1);
            }
            return super.onTap(arg0);
        }
    }

    class Comparator implements java.util.Comparator<AdressData> {
        @Override
        public int compare(AdressData lhs, AdressData rhs) {
            int m1 = lhs.getDistance();
            int m2 = rhs.getDistance();
            int result = 0;
            if (m1 > m2) {
                result = 1;
            }
            if (m1 < m2) {
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