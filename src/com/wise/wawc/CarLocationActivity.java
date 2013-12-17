package com.wise.wawc;

import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.os.Bundle;
/**
 * 车辆位置
 * @author honesty
 */
public class CarLocationActivity extends Activity{
	WawcApplication app;
	MapView mMapView = null;
	MapController mMapController = null;
	List<Overlay> overlays;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_location);
		app = (WawcApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(WawcApplication.strKey,null);
        }
        setContentView(R.layout.activity_car_location);
		mMapView=(MapView)findViewById(R.id.mv_car_location);  
		mMapView.setBuiltInZoomControls(true);
		mMapController=mMapView.getController(); 
		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		mMapController.setCenter(point);//设置地图中心点  
		mMapController.setZoom(12);//设置地图zoom级别  
		overlays = mMapView.getOverlays();
	}
}