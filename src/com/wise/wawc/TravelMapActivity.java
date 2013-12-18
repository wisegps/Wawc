package com.wise.wawc;

import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
/**
 * 车辆行程
 * @author honesty
 */
public class TravelMapActivity extends Activity{
	WawcApplication app;
	MapView mMapView = null;
	MapController mMapController = null;
	List<Overlay> overlays;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (WawcApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(WawcApplication.strKey,null);
        }
		setContentView(R.layout.activity_travel_map);
		mMapView=(MapView)findViewById(R.id.mv_travel_map);  
		mMapView.setBuiltInZoomControls(true);
		mMapController=mMapView.getController(); 
		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		mMapController.setCenter(point);//设置地图中心点  
		mMapController.setZoom(12);//设置地图zoom级别  
		overlays = mMapView.getOverlays();
		
		ImageView iv_activity_car_home_search = (ImageView)findViewById(R.id.iv_activity_car_home_search);
		iv_activity_car_home_search.setOnClickListener(onClickListener);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_car_home_search:
				TravelMapActivity.this.startActivity(new Intent(TravelMapActivity.this, ShareLocationActivity.class));
				break;
			}
		}
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.destroy();
	}
}