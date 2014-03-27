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
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 车辆行程
 * 
 * @author honesty
 */
public class TravelMapActivity extends Activity {
    private static final String TAG = "TravelMapActivity";
    private static final int get_data = 1;

    MapView mMapView = null;
    MapController mMapController = null;
    List<Overlay> overlays;
    ProgressDialog Dialog = null; // progress
    int device = 3;
    Intent intent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WawcApplication app = (WawcApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            app.mBMapManager.init(WawcApplication.strKey,null);
        }
        setContentView(R.layout.activity_travel_map);
        ImageView iv_activity_travel_share = (ImageView)findViewById(R.id.iv_activity_travel_share);
        iv_activity_travel_share.setOnClickListener(onClickListener);
        mMapView = (MapView) findViewById(R.id.mv_travel_map);
        mMapView.setBuiltInZoomControls(true);
        mMapView.regMapViewListener(app.mBMapManager, mkMapViewListener);
        mMapController = mMapView.getController();
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
                (int) (116.404 * 1E6));
        mMapController.setCenter(point);// 设置地图中心点
        mMapController.setZoom(12);// 设置地图zoom级别
        overlays = mMapView.getOverlays();

        TextView tv_travel_startPlace = (TextView)findViewById(R.id.tv_travel_startPlace);
        TextView tv_travel_stopPlace = (TextView)findViewById(R.id.tv_travel_stopPlace);
        TextView tv_travel_startTime = (TextView)findViewById(R.id.tv_travel_startTime);
        TextView tv_travel_stopTime = (TextView)findViewById(R.id.tv_travel_stopTime);
        TextView tv_travel_spacingDistance = (TextView)findViewById(R.id.tv_travel_spacingDistance);
        TextView tv_travel_averageOil = (TextView)findViewById(R.id.tv_travel_averageOil);
        TextView tv_travel_oil = (TextView)findViewById(R.id.tv_travel_oil);
        TextView tv_travel_speed = (TextView)findViewById(R.id.tv_travel_speed);
        TextView tv_travel_cost = (TextView)findViewById(R.id.tv_travel_cost);
        
        ImageView iv_activity_travel_back = (ImageView) findViewById(R.id.iv_activity_travel_back);
        iv_activity_travel_back.setOnClickListener(onClickListener);
        intent = getIntent();
        tv_travel_startPlace.setText(intent.getStringExtra("Start_place"));
        tv_travel_stopPlace.setText(intent.getStringExtra("End_place"));
        tv_travel_startTime.setText(intent.getStringExtra("StartTime").substring(10, 16));
        tv_travel_stopTime.setText(intent.getStringExtra("StopTime").substring(10, 16));
        String str = "共"+ intent.getStringExtra("SpacingDistance") + "公里\\" + intent.getStringExtra("SpacingTime");
        tv_travel_spacingDistance.setText(str);
        tv_travel_averageOil.setText(intent.getStringExtra("AverageOil"));
        tv_travel_oil.setText(intent.getStringExtra("Oil"));
        tv_travel_speed.setText(intent.getStringExtra("Speed"));
        tv_travel_cost.setText(intent.getStringExtra("Cost"));
        
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
            case R.id.iv_activity_travel_back:
                finish();
                break;
            case R.id.iv_activity_travel_share:
                Toast.makeText(TravelMapActivity.this, R.string.travel_map_urrent, Toast.LENGTH_LONG).show();
                boolean isCurrent = mMapView.getCurrentMap();
                System.out.println("isCurrent = " + isCurrent);
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
            GeoPoint startGeoPoint = null;
            GeoPoint stopGeoPoint = null;
            JSONArray jsonArray = new JSONArray(result);
            GeoPoint[] geoPoints = new GeoPoint[jsonArray.length()];
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String Lat = jsonObject.getString("lat");
                String Lon = jsonObject.getString("lon");
                
                GeoPoint geoPoint = new GeoPoint(GetSystem.StringToInt(Lat),GetSystem.StringToInt(Lon));
                geoPoints[i] = geoPoint;
                mMapController.setCenter(geoPoint);  
                if(i == 0){
                    startGeoPoint = geoPoint;
                }else{
                    stopGeoPoint = geoPoint;
                }
            }
            //创建样式 
            Symbol palaceSymbol = new Symbol(); 
            Symbol.Color palaceColor = palaceSymbol.new Color();
            palaceColor.red = 0;//设置颜色的红色分量  
            palaceColor.green = 0;//设置颜色的绿色分量  
            palaceColor.blue = 255;//设置颜色的蓝色分量  
            palaceColor.alpha = 126;//设置颜色的alpha值  
            palaceSymbol.setLineSymbol(palaceColor, 7);
            
            Geometry geometry = new Geometry();
            geometry.setPolyLine(geoPoints);
            Graphic palaceGraphic = new Graphic(geometry, palaceSymbol);
            
            //将自绘图形添加到地图中
            GraphicsOverlay palaceOverlay = new GraphicsOverlay(mMapView);
            overlays.add(palaceOverlay);
            palaceOverlay.setData(palaceGraphic);
            
            
            
            if(startGeoPoint != null){
                Drawable start= getResources().getDrawable(R.drawable.body_icon_outset);
                ItemizedOverlay startItemizedOverlay = new ItemizedOverlay<OverlayItem>(start, mMapView);
                overlays.add(startItemizedOverlay);
                OverlayItem overlayItem = new OverlayItem(startGeoPoint, "", "");
                overlayItem.setAnchor(OverlayItem.ALING_CENTER);
                startItemizedOverlay.addItem(overlayItem);
                if(stopGeoPoint != null){
                    Drawable stop= getResources().getDrawable(R.drawable.body_icon_end);
                    ItemizedOverlay stopItemizedOverlay = new ItemizedOverlay<OverlayItem>(stop, mMapView);
                    overlays.add(stopItemizedOverlay);
                    OverlayItem overlayItem1 = new OverlayItem(stopGeoPoint, "", "");
                    overlayItem1.setAnchor(OverlayItem.ALING_CENTER);
                    stopItemizedOverlay.addItem(overlayItem1);
                }
            }
            
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
            System.out.println("截取成功");
            GetSystem.saveImageSD(arg0,Constant.picPath, Constant.ShareImage,50);
            String imagePath = Constant.picPath + Constant.ShareImage;
            StringBuffer sb = new StringBuffer();
            sb.append("【行程】");
            sb.append(intent.getStringExtra("StartTime").substring(5, 16));
            sb.append(" 从" + intent.getStringExtra("Start_place"));
            sb.append("到" + intent.getStringExtra("End_place"));
            sb.append("，共行驶" + intent.getStringExtra("SpacingDistance"));
            sb.append("公里，耗时" + intent.getStringExtra("SpacingTime"));
            sb.append("，" + intent.getStringExtra("Oil"));
            sb.append("，" + intent.getStringExtra("Cost"));
            sb.append("，" + intent.getStringExtra("AverageOil"));
            sb.append("，" + intent.getStringExtra("Speed"));
            System.out.println(sb.toString());
            GetSystem.share(TravelMapActivity.this, sb.toString(), imagePath,0,0,"行程","");
        }
        @Override
        public void onClickMapPoi(MapPoi arg0) {}
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.destroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}