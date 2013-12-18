package com.wise.pubclas;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	public String GetNextMonth(String Month,int number){
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
}