package com.wise.service;

import java.util.List;

import com.baidu.mapapi.navi.BaiduMapNavigation;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.data.AdressData;
import com.wise.data.CarData;
import com.wise.pubclas.Variable;
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
	private TextView addressTV = null;
	private TextView nameTV = null;
	private View navigation = null;
	List<AdressData> adrDataList = null;
	
	//天安门坐标
	double currentLat = Variable.Lat; 
   	double currentLon = Variable.Lon; 
   	//百度大厦坐标
   	double goToLat = 0d;   
   	double goToLon = 0d;
	public CollectionAdapter(Context context,List<AdressData> adrDataList){
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.adrDataList = adrDataList;  
	}
	public int getCount() {
		return this.adrDataList.size();
	}
	public Object getItem(int position) {
		return this.adrDataList.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.collection_list, null);
		call = (ImageView) convertView.findViewById(R.id.collection_iv_call_phone);
		addressTV = (TextView) convertView.findViewById(R.id.collection_tv_address);
		tel = (TextView) convertView.findViewById(R.id.collection_tv_tel);
		nameTV = (TextView) convertView.findViewById(R.id.collection_tv_name);
		addressTV.setText(this.adrDataList.get(position).getAdress());
		tel.setText(this.adrDataList.get(position).getPhone());
		nameTV.setText(this.adrDataList.get(position).getName());
		
		goToLat = this.adrDataList.get(position).getLat();
		goToLon = this.adrDataList.get(position).getLon();
		
		navigation = convertView.findViewById(R.id.collection_iv_go_here);
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
	//刷新数据
	public void refish(List<AdressData> adrDataList){
		this.adrDataList = adrDataList;
		CollectionAdapter.this.notifyDataSetChanged();
	}
	public void startNavi(){		
			int lat = (int) (currentLat *1E6);
		   	int lon = (int) (currentLon *1E6);   	
		   	GeoPoint pt1 = new GeoPoint(lat, lon);
			lat = (int) (goToLat *1E6);
		   	lon = (int) (goToLon *1E6);
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
