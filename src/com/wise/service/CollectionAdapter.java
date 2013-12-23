package com.wise.service;

import com.baidu.mapapi.navi.BaiduMapNavigation;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.wawc.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 我的收藏自定义迭代器
 */
public class CollectionAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private ImageView call;
	private TextView tel;
	private View navigation = null;
	
	//天安门坐标
	double mLat1 = 39.915291; 
   	double mLon1 = 116.403857; 
   	//百度大厦坐标
   	double mLat2 = 40.056858;   
   	double mLon2 = 116.308194;
	public CollectionAdapter(Context context){
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}
	public int getCount() {
		return 10;
	}
	public Object getItem(int position) {
		return null;
	}
	public long getItemId(int position) {
		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.collection_list, null);
		call = (ImageView) convertView.findViewById(R.id.collection_call);
		tel = (TextView) convertView.findViewById(R.id.collection_tel);
		navigation = convertView.findViewById(R.id.navigation);
		navigation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//TODO   切换到导航页面
				startNavi();
			}
		});
		
		
		call.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String temp = tel.getText().toString();
				String phoneno = temp.substring(temp.indexOf("：")+1).trim();
				Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneno));
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	   public void startNavi(){		
			int lat = (int) (mLat1 *1E6);
		   	int lon = (int) (mLon1 *1E6);   	
		   	GeoPoint pt1 = new GeoPoint(lat, lon);
			lat = (int) (mLat2 *1E6);
		   	lon = (int) (mLon2 *1E6);
		    GeoPoint pt2 = new GeoPoint(lat, lon);
		    // 构建 导航参数
	        NaviPara para = new NaviPara();
	        para.startPoint = pt1;
	        para.startName= "从这里开始";
	        para.endPoint  = pt2;
	        para.endName   = "到这里结束";
	        
	        try {
	        	BaiduMapNavigation.openBaiduMapNavi(para, (Activity)context);
				 
			} catch (BaiduMapAppNotSupportNaviException e) {
				e.printStackTrace();
				  AlertDialog.Builder builder = new AlertDialog.Builder(context);
				  builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
				  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						 dialog.dismiss();
					}
				});
				  builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						BaiduMapNavigation.GetLatestBaiduMapApp((Activity)context);
					}
				  });
				  builder.create().show();
				 }
			}
}
