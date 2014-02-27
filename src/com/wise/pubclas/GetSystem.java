package com.wise.pubclas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.data.TimeData;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class GetSystem {
    /**
     * 获取gps状态
     * 
     * @param mContext
     * @return
     */
    public static boolean GPSSettings(Context mContext) {
        LocationManager alm = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
            // Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            // mContext.startActivity(myIntent);
        }
    }

    /**
     * 检测网络
     * 
     * @param context
     * @return
     */
    public static boolean checkNetWorkStatus(Context context) {
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * 返回当前月份
     * 
     * @return
     */
    public static TimeData GetNowMonth() {
        Time time = new Time();
        time.setToNow();
        String year = ChangeTime(time.year);
        String month = ChangeTime(time.month + 1);
        String str = year + "-" + month;
        TimeData timeData = new TimeData();
        timeData.setDate(str);
        timeData.setYear(year);
        timeData.setMonth("" + (time.month + 1));
        return timeData;
    }

    /**
     * 调整时间格式
     * 
     * @param 9
     * @return 09
     */
    public static String ChangeTime(int i) {
        String str = null;
        if (i < 10) {
            str = "0" + i;
        } else {
            str = "" + i;
        }
        return str;
    }

    /**
     * 获取指定月份
     * 
     * @param Month 2013-12
     * @param number 上个月填-1 ,下个月填1
     * @return
     */
    public static TimeData GetNextMonth(String Month, int number) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Calendar nowDate = Calendar.getInstance();
            nowDate.setTime(sdf.parse(Month));
            nowDate.add(Calendar.MONTH, number);
            String Date = sdf.format(nowDate.getTime());
            TimeData timeData = new TimeData();
            timeData.setDate(Date);
            timeData.setYear("" + nowDate.get(Calendar.YEAR));
            timeData.setMonth("" + (nowDate.get(Calendar.MONTH) + 1));
            return timeData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取指定天
     * 
     * @param Date 2013-12-01
     * @param number 前一天填-1 ,后一天填1
     * @return
     */
    public static String GetNextData(String Date, int number) {
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
     * 格式化显示时间，今天，昨天，日期
     * @param time 2014-02-10 06:17
     * @return 数组：0 日期，1 时间
     */
    public static String[] formatDateTime(String time) {  
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");   
        if(time==null ||"".equals(time)){  
            return null;  
        }  
        Date date = null;  
        try {  
            date = format.parse(time);  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
          
        Calendar current = Calendar.getInstance();  
          
        Calendar today = Calendar.getInstance();    //今天  
          
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));  
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));  
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));  
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数  
        today.set( Calendar.HOUR_OF_DAY, 0);  
        today.set( Calendar.MINUTE, 0);  
        today.set(Calendar.SECOND, 0);  
          
        Calendar yesterday = Calendar.getInstance();    //昨天  
          
        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));  
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));  
        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);  
        yesterday.set( Calendar.HOUR_OF_DAY, 0);  
        yesterday.set( Calendar.MINUTE, 0);  
        yesterday.set(Calendar.SECOND, 0);  
          
        current.setTime(date);  
        String[] myDate = new String[3];
        if(current.after(today)){  
            myDate[0] = "今天";
            myDate[1] = time.split(" ")[1];
            myDate[2] = "今天";
        }else if(current.before(today) && current.after(yesterday)){  
            myDate[0] = "昨天";
            myDate[1] = time.split(" ")[1];
            myDate[2] = "昨天";
        }else{ 
            myDate[0] = time.substring(0, 10);
            myDate[1] = time.split(" ")[1];
            myDate[2] = time.substring(5, 10);
        }  
        return myDate;
    }
    /**
     * 计算间隔时间
     * @param Second
     * @return
     */
    public static String ProcessTime(int Second){
        if(Second < 60){
            return Second + "秒";
        }else if(Second < 3600){
            return Second/60 + "分钟";
        }else{
            return Second/(60*60) + "小时";
        }
    }

    /**
     * 调用百度地图导航
     * 
     * @param mActivity
     * @param pt1
     * @param pt2
     * @param str1
     * @param str2
     */
    public static void FindCar(Activity mActivity, GeoPoint pt1, GeoPoint pt2,
            String str1, String str2) {
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
            Toast.makeText(mActivity, "未安装百度地图，请安装百度地图后在使用导航功能", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取版本信息，判断时候有更新
     * 
     * @param context
     * @param 包名称
     * @return versionName，版本名称，如1.2
     */
    public static String GetVersion(Context context, String packString) {
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
     * 
     * @param src
     * @return
     */
    public static Bitmap getBitmapFromURL(String Path) {
        try {
            URL url = new URL(Path);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
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
     * 从服务器读取文件
     * 
     * @param url
     * @return
     */
    public static String getStringFromURL(String url) {
        try {
            URL myURL = new URL(url);
            URLConnection httpsConn = (URLConnection) myURL.openConnection();
            httpsConn.setConnectTimeout(20 * 1000);
            httpsConn.setReadTimeout(20 * 1000);
            InputStreamReader insr = new InputStreamReader(
                    httpsConn.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(insr, 1024);
            String data = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                data += line;
            }
            insr.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 保存图片在sd卡上
     * 
     * @param bitmap
     * @param name
     *            图片名称a.png
     */
    public static void saveImageSD(Bitmap bitmap, String path, String name) {
        File file = new File(path);
        System.out.println(path);
        if (!file.exists()) {
            System.out.println("创建文件夹");
            file.mkdirs();// 创建文件夹
        }
        String fileName = path + name;
        System.out.println("fileName = " + fileName);
        FileOutputStream b = null;
        try {
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
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
        Log.e("tag", "内存" + Runtime.getRuntime().totalMemory() / 1024 / 1024
                + "M");
    }
    /**
     * 经纬度格式转换,把服务器得到的string转成int类型
     * @param string 116.000000
     * @return 116000000
     */
    public static int StringToInt(String str) {
        try {
            Double point_doub = Double.parseDouble(str);
            return (int) (point_doub * 1000000);
        } catch (NumberFormatException e) {
            Log.d("GetSystem", "经纬度格式转换异常：NumberFormatException");
            return 0;
        }
    }
    /**
     * 判断是否提醒
     * @param time
     * @return
     */
    public static boolean isTimeOut(String time){
        if(time == null){
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            java.util.Date begin = sdf.parse(time);
            java.util.Date end = sdf.parse(GetNowTime());
            return end.getTime() >= begin.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String GetNowTime() {
        Time time = new Time();
        time.setToNow();
        String year = ChangeTime(time.year);
        String month = ChangeTime(time.month + 1);
        String day = ChangeTime(time.monthDay);
        String minute = ChangeTime(time.minute);
        String hour = ChangeTime(time.hour);
        String sec = ChangeTime(time.second);
        String str = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec;
        return str;
    }
}