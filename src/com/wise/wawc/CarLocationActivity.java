package com.wise.wawc;

import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.data.CarData;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.Variable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 车辆位置
 * 
 * @author honesty
 */
public class CarLocationActivity extends Activity {
    private static final String TAG = "CarLocationActivity";
    MapView mMapView = null;
    MapController mMapController = null;
    PopupWindow mPopupWindow;
    List<Overlay> overlays;
    LinearLayout ll_activity_car_location_bottom;
    CarData carData;
    double Lat, Lon;
    GeoPoint geoPoint;

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
        setContentView(R.layout.activity_car_location);
        TextView tv_car_name = (TextView) findViewById(R.id.tv_car_name);
        mMapView = (MapView) findViewById(R.id.mv_car_location);
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6),
                (int) (116.404 * 1E6));
        mMapController.setCenter(point);// 设置地图中心点
        mMapController.setZoom(12);// 设置地图zoom级别
        overlays = mMapView.getOverlays();
        mMapView.regMapViewListener(WawcApplication.getInstance().mBMapManager, mkMapViewListener);
        // TODO 修改
        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);
        carData = Variable.carDatas.get(index);
        tv_car_name.setText(carData.getObj_name());
        Lat = Double.valueOf(carData.getLat());
        Lon = Double.valueOf(carData.getLon());
        Log.d(TAG, "carData.getLat() = " + carData.getLat());
        geoPoint = new GeoPoint((int) (Lat * 1E6), (int) (Lon * 1E6));
        OverlayItem item1 = new OverlayItem(geoPoint, "item1", "item1");
        Drawable mark = getResources().getDrawable(
                R.drawable.body_icon_location);
        item1.setMarker(mark);
        item1.setAnchor(OverlayItem.ALING_CENTER);
        OverlayCar overlayCar = new OverlayCar(mark, mMapView);
        overlays.add(overlayCar);
        overlayCar.addItem(item1);
        mMapController.setCenter(geoPoint);

        ImageView iv_activity_car_location_back = (ImageView) findViewById(R.id.iv_activity_car_location_back);
        iv_activity_car_location_back.setOnClickListener(onClickListener);
        ImageView iv_activity_car_location_share = (ImageView) findViewById(R.id.iv_activity_car_location_share);
        iv_activity_car_location_share.setOnClickListener(onClickListener);
        Button bt_activity_car_location_periphery = (Button) findViewById(R.id.bt_activity_car_location_periphery);
        bt_activity_car_location_periphery.setOnClickListener(onClickListener);
        Button bt_activity_car_location_travel = (Button) findViewById(R.id.bt_activity_car_location_travel);
        bt_activity_car_location_travel.setOnClickListener(onClickListener);
        Button bt_activity_car_location_findCar = (Button) findViewById(R.id.bt_activity_car_location_findCar);
        bt_activity_car_location_findCar.setOnClickListener(onClickListener);
        ll_activity_car_location_bottom = (LinearLayout) findViewById(R.id.ll_activity_car_location_bottom);

    }
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_car_location_back:
                finish();
                break;
            case R.id.iv_activity_car_location_share:
                Toast.makeText(CarLocationActivity.this,
                        R.string.travel_map_urrent, Toast.LENGTH_LONG).show();
                boolean isCurrent = mMapView.getCurrentMap();
                System.out.println("isCurrent = " + isCurrent);
                break;
            case R.id.bt_activity_car_location_periphery:
                ShowPop();
                break;
            case R.id.bt_activity_car_location_findCar:
                GeoPoint pt1 = new GeoPoint((int) (Variable.Lat * 1E6),
                        (int) (Variable.Lon * 1E6));
                GetSystem.FindCar(CarLocationActivity.this, pt1, geoPoint, "起始",
                        "结束");
                break;
            case R.id.bt_activity_car_location_travel:// 车辆行程
                CarLocationActivity.this.startActivity(new Intent(
                        CarLocationActivity.this, TravelActivity.class));
                break;
            case R.id.tv_item_car_location_oil:
                ToSearchMap(getString(R.string.oil_station));
                break;
            case R.id.tv_item_car_location_Parking:
                ToSearchMap(getString(R.string.parking));
                break;
            case R.id.tv_item_car_location_4s:
                ToSearchMap(getString(R.string.four_s));
                break;
            case R.id.tv_item_car_location_specialist:
                ToSearchMap(getString(R.string.specialist));
                break;
            case R.id.tv_item_car_location_automotive_beauty:
                ToSearchMap(getString(R.string.automotive_beauty));
                break;
            case R.id.tv_item_car_location_wash:
                ToSearchMap(getString(R.string.wash));
                break;
            }
        }
    };
    private void ToSearchMap(String keyWord) {
        mPopupWindow.dismiss();
        Intent intent = new Intent(CarLocationActivity.this,
                SearchMapActivity.class);
        intent.putExtra("keyWord", keyWord);
        startActivity(intent);
    }
    /**
     * 弹出popupwindow
     */
    private void ShowPop() {
        int Height = ll_activity_car_location_bottom.getMeasuredHeight();
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popunwindwow = mLayoutInflater.inflate(R.layout.item_car_location,
                null);
        mPopupWindow = new PopupWindow(popunwindwow, LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(
                findViewById(R.id.bt_activity_car_location_periphery),
                Gravity.BOTTOM, 0, Height);
        TextView tv_item_car_location_oil = (TextView) popunwindwow
                .findViewById(R.id.tv_item_car_location_oil);
        tv_item_car_location_oil.setOnClickListener(onClickListener);
        TextView tv_item_car_location_Parking = (TextView) popunwindwow
                .findViewById(R.id.tv_item_car_location_Parking);
        tv_item_car_location_Parking.setOnClickListener(onClickListener);
        TextView tv_item_car_location_4s = (TextView) popunwindwow
                .findViewById(R.id.tv_item_car_location_4s);
        tv_item_car_location_4s.setOnClickListener(onClickListener);
        TextView tv_item_car_location_specialist = (TextView) popunwindwow
                .findViewById(R.id.tv_item_car_location_specialist);
        tv_item_car_location_specialist.setOnClickListener(onClickListener);
        TextView tv_item_car_location_automotive_beauty = (TextView) popunwindwow
                .findViewById(R.id.tv_item_car_location_automotive_beauty);
        tv_item_car_location_automotive_beauty
                .setOnClickListener(onClickListener);
        TextView tv_item_car_location_wash = (TextView) popunwindwow
                .findViewById(R.id.tv_item_car_location_wash);
        tv_item_car_location_wash.setOnClickListener(onClickListener);
    }

    class OverlayCar extends ItemizedOverlay<OverlayItem> {
        public OverlayCar(Drawable arg0, MapView arg1) {
            super(arg0, arg1);
        }
        @Override
        protected boolean onTap(int arg0) {
            return true;
        }
        @Override
        public boolean onTap(GeoPoint arg0, MapView arg1) {
            super.onTap(arg0, arg1);
            System.out.println("MapView的点击事件");
            return false;
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.destroy();
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    };
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
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
            System.out.println("截取成功");
            GetSystem.saveImageSD(arg0, Constant.picPath, Constant.ShareImage);
            String imagePath = Constant.picPath + Constant.ShareImage;
            String url = "http://api.map.baidu.com/geocoder?location="
                    + carData.getLat() + "," + carData.getLon()
                    + "&coord_type=bd09ll&output=html";
            StringBuffer sb = new StringBuffer();
            sb.append("【位置】");
            sb.append(carData.getAdress());
            sb.append("," + carData.getAdress());
            sb.append("," + url);
            GetSystem.share(CarLocationActivity.this, sb.toString(), imagePath,
                    (float) Lat, (float) Lon,"位置");
        }
        @Override
        public void onClickMapPoi(MapPoi arg0) {}
    };
}