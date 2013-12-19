package com.wise.pubclas;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

public class GetSystem {
	/**
	 * 获取gps状态
	 * @param mContext
	 * @return
	 */
	public static boolean GPSSettings(Context mContext) {
		LocationManager alm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
			//Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
			//mContext.startActivity(myIntent);
		}
	}
	/**
	 * 获取指定月份
	 * @param Month 2013-12
	 * @param number 上个月填-1 ,下个月填1
	 * @return
	 */
	public static String GetNextMonth(String Month,int number){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Calendar nowDate = Calendar.getInstance();
			nowDate.setTime(sdf.parse(Month));
			nowDate.add(Calendar.MONTH, number);
			String Date = sdf.format(nowDate.getTime());
			return Date;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 获取指定天
	 * @param Date 2013-12-01
	 * @param number 前一天填-1 ,后一天填1
	 * @return
	 */
	public static String GetNextData(String Date,int number){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar nowDate = Calendar.getInstance();
			nowDate.setTime(sdf.parse(Date));
			nowDate.add(Calendar.DATE, number);
			String newDate = sdf.format(nowDate.getTime());
			return newDate;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void FindCar(Activity mActivity,GeoPoint pt1,GeoPoint pt2,String str1,String str2) {
		NaviPara para = new NaviPara();
		para.startPoint = pt1; // 起点坐标
		para.startName = str1;
		para.endPoint = pt2; // 终点坐标
		para.endName = str2;
		try {
			// 调起百度地图客户端导航功能,参数this为Activity。
			BaiduMapNavigation.openBaiduMapNavi(para, mActivity);
		} catch (BaiduMapAppNotSupportNaviException e) {
			// 在此处理异常
			e.printStackTrace();
		}
	}
}