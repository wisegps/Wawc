package com.wise.wawc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 车辆行程
 * 
 * @author honesty
 */
public class TravelMapActivity extends Activity {
    private static final String TAG = "TravelMapActivity";
    private static final int get_data = 1;

    WawcApplication app;
    MapView mMapView = null;
    MapController mMapController = null;
    List<Overlay> overlays;
    ProgressDialog Dialog = null; // progress
    int device = 3;

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
        setContentView(R.layout.activity_travel_map);
        mMapView = (MapView) findViewById(R.id.mv_travel_map);
        mMapView.setBuiltInZoomControls(true);
        mMapView.regMapViewListener(app.mBMapManager, mkMapViewListener);
        mMapController = mMapView.getController();
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
                (int) (116.404 * 1E6));
        mMapController.setCenter(point);// 设置地图中心点
        mMapController.setZoom(12);// 设置地图zoom级别
        overlays = mMapView.getOverlays();

        // ImageView iv_activity_car_home_search =
        // (ImageView)findViewById(R.id.iv_activity_car_home_search);
        // iv_activity_car_home_search.setOnClickListener(onClickListener);
        ImageView iv_activity_travel_back = (ImageView) findViewById(R.id.iv_activity_travel_back);
        iv_activity_travel_back.setOnClickListener(onClickListener);
        Intent intent = getIntent();
        String StartTime = intent.getStringExtra("StartTime");
        String StopTime = intent.getStringExtra("StopTime");

        StartTime = "2014-01-02 11:28:37";
        StopTime = "2014-01-02 12:28:37";
        try {
            String url = Constant.BaseUrl + "device/" + device
                    + "/gps_data?auth_code=" + Variable.auth_code
                    + "&start_time=" + URLEncoder.encode(StartTime, "UTF-8")
                    + "&end_time=" + URLEncoder.encode(StopTime, "UTF-8");
            new Thread(new NetThread.GetDataThread(handler, url, get_data))
                    .start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_car_home_search:
                Dialog = ProgressDialog.show(TravelMapActivity.this,
                        getString(R.string.note),
                        getString(R.string.travel_map_urrent), true);
                mMapView.getCurrentMap();
                break;
            case R.id.iv_activity_travel_back:
                finish();
                break;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case get_data:
                jsonData(msg.obj.toString());
                break;

            default:
                break;
            }
        }
    };
    
    private void jsonData(String result){
        try {
            
            Symbol palaceSymbol = new Symbol();//创建样式  
            Symbol.Color palaceColor = palaceSymbol.new Color();
            palaceColor.red = 0;//设置颜色的红色分量  
            palaceColor.green = 0;//设置颜色的绿色分量  
            palaceColor.blue = 255;//设置颜色的蓝色分量  
            palaceColor.alpha = 126;//设置颜色的alpha值  
            palaceSymbol.setLineSymbol(palaceColor, 7);
            

            GraphicsOverlay palaceOverlay = new GraphicsOverlay(mMapView);
            overlays.add(palaceOverlay);

            Geometry geometry = new Geometry();
            Geometry[] geometries;
            
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                if(jsonArray.length() == (i + 1)){
                    break;
                }
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String Lat = jsonObject.getString("lat");
                String Lon = jsonObject.getString("lon");
                //JSONObject jsonObject1 = jsonArray.getJSONObject(i + 1);
                //String Lat1 = jsonObject1.getString("lat");
                //String Lon1 = jsonObject1.getString("lon");
                
                GeoPoint geoPoint1 = new GeoPoint(GetSystem.StringToInt(Lat),GetSystem.StringToInt(Lon));
                mMapController.setCenter(geoPoint1);
                //GeoPoint geoPoint2 = new GeoPoint(GetSystem.StringToInt(Lat1),GetSystem.StringToInt(Lon1));   
                
                
            }
            //geometry.setPolyLine(new GeoPoint[]{geoPoint1,geoPoint2});
            
            
            Graphic palaceGraphic = new Graphic(geometry, palaceSymbol);
            palaceOverlay.setData(palaceGraphic);
            mMapView.refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    MKMapViewListener mkMapViewListener = new MKMapViewListener() {
        @Override
        public void onMapMoveFinish() {}
        @Override
        public void onMapLoadFinish() {}
        @Override
        public void onMapAnimationFinish() {}

        @Override
        public void onGetCurrentMap(Bitmap arg0) {
            if (Dialog != null) {
                Dialog.dismiss();
            }
            Log.d(TAG, "截图完毕");
            Intent intent = new Intent(TravelMapActivity.this,
                    NewArticleActivity.class);
            intent.putExtra("bitmap", arg0);
            startActivity(intent);
            // 分享界面接受
            // Intent intent=getIntent();
            // if(intent!=null)
            // {
            // bitmap=intent.getParcelableExtra("bitmap");
            // imageview.setImageBitmap(bitmap);
            // }
        }
        @Override
        public void onClickMapPoi(MapPoi arg0) {}
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.destroy();
    }
}