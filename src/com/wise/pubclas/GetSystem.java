package com.wise.pubclas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
	 * 检测网络
	 * @param context
	 * @return
	 */
	public static boolean checkNetWorkStatus(Context context){
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            result = true;
        } else {
            result = false;
        }
        return result;
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
	/**
	 * 调用百度地图导航
	 * @param mActivity
	 * @param pt1
	 * @param pt2
	 * @param str1
	 * @param str2
	 */
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
	/**
	 * 获取版本信息，判断时候有更新
	 * @param context
	 * @param 包名称
	 * @return versionName，版本名称，如1.2
	 */
	public static String GetVersion(Context context,String packString) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(packString, 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 从服务器读取图片
	 * @param src
	 * @return
	 */
	public static Bitmap getBitmapFromURL(String Path) {
        try {
            URL url = new URL(Path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	/**
	 * 保存图片在sd卡上
	 * @param bitmap
	 * @param name 图片名称a.jpg
	 */
	public static void saveImageSD(Bitmap bitmap,String name){
        File file = new File(Constant.BasePath);
        if(!file.exists()){            
            file.mkdirs();// 创建文件夹  
        }        
        String fileName = Constant.BasePath + name;  
        FileOutputStream b = null;
        try {  
            b = new FileOutputStream(fileName);  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                b.flush();  
                b.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
    }
	public static void displayBriefMemory(Context mContext) {    
	    Log.e("tag","内存"+Runtime.getRuntime().totalMemory()/1024/1024 + "M"); 
    }
}