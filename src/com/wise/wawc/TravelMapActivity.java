package com.wise.wawc;

import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
/**
 * 车辆行程
 * @author honesty
 */
public class TravelMapActivity extends Activity{
	private static final String TAG = "TravelMapActivity";
	
	WawcApplication app;
	MapView mMapView = null;
	MapController mMapController = null;
	List<Overlay> overlays;
	ProgressDialog Dialog = null;    //progress
	
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
		mMapView.regMapViewListener(app.mBMapManager, mkMapViewListener);
		mMapController=mMapView.getController(); 
		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		mMapController.setCenter(point);//设置地图中心点  
		mMapController.setZoom(12);//设置地图zoom级别  
		overlays = mMapView.getOverlays();
		
		//ImageView iv_activity_car_home_search = (ImageView)findViewById(R.id.iv_activity_car_home_search);
		//iv_activity_car_home_search.setOnClickListener(onClickListener);
		ImageView iv_activity_travel_back = (ImageView)findViewById(R.id.iv_activity_travel_back);
		iv_activity_travel_back.setOnClickListener(onClickListener);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_car_home_search:
				Dialog = ProgressDialog.show(TravelMapActivity.this,getString(R.string.note),getString(R.string.travel_map_urrent),true);
				mMapView.getCurrentMap();
				break;
			case R.id.iv_activity_travel_back:
				finish();
				break;
			}
		}
	};
	
	MKMapViewListener mkMapViewListener = new MKMapViewListener() {		
		@Override
		public void onMapMoveFinish() {}		
		@Override
		public void onMapLoadFinish() {}		
		@Override
		public void onMapAnimationFinish() {}		
		@Override
		public void onGetCurrentMap(Bitmap arg0) {
			if(Dialog != null){
				Dialog.dismiss();
			}
			Log.d(TAG, "截图完毕");
			Intent intent = new Intent(TravelMapActivity.this, NewArticleActivity.class);
			intent.putExtra("bitmap", arg0);
			startActivity(intent);
			//分享界面接受
//			Intent intent=getIntent();
//	        if(intent!=null)
//	        {
//	            bitmap=intent.getParcelableExtra("bitmap");
//	            imageview.setImageBitmap(bitmap);
//	        }
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