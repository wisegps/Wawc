package com.wise.pubclas;

import java.util.List;

import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.wise.data.Article;
import com.wise.data.CarData;
/**
 * 变量
 * @author honesty
 */
public class Variable {
    /**
     * auth_code
     */
    public static String auth_code;
    /**
     * cust_id
     */
    public static String cust_id;
    /**
     * 用户名称
     */
    public static String cust_name;
    /**
     * 当前位置
     */
    public static String Adress = "";
    /**
     * 当前定位城市
     */
    public static String City = "";
    /**
     * 当前经度
     */
    public static double Lat = 0;
    /**
     * 当前未读
     */
    public static double Lon = 0;
    /**
     * 车辆数据
     */

    public static boolean againstPush = true;

    public static boolean faultPush = true;

    public static boolean remaindPush = true;

    public static String defaultCenter = "车辆位置";
    
    public static List<CarData> carDatas;
    
    public static List<Article> articleList;
    
    public static int smallImageReqWidth = 0;
    public static int margins = 0;
    public static String MscKey = "5281f227";
    
    
    public static int distance = 10;  //附近车友距离（公里）
    
    
    public static  int articleAdapterImageWidth = 150;
    public static  int articleAdapterImageHeight = 150;
}
