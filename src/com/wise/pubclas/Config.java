package com.wise.pubclas;

import java.util.List;
import android.graphics.Bitmap;
import com.wise.data.CarData;

public class Config {
    public static String BaseUrl = "http://wiwc.api.wisegps.cn/";
	/**
	 * 获取版本信息用到
	 */
	public static String PackageName = "com.wise.wawc";
	/**
	 * 配置文件名称
	 */
	public static String spfName = "com_wise_wawc";
	/**
	 * 当前位置
	 */
	public static String Adress = "";
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
	public static List<CarData> carDatas;
	/**
	 * 违章推送
	 */
	public static boolean againstPush = true;
	public static String againstPush_key ="againstPush";
	/**
	 * 故障推送
	 */
	public static boolean faultPush = true;
	public static String faultPush_key ="faultPush";
	/**
	 * 车务提醒
	 */
	public static boolean remaindPush = true;
	public static String remaindPush_key ="remaindPush";
	
	/**
	 * 默认定位中心
	 */
	public static String defaultCenter = "车辆位置";
	public static String defaultCenter_key ="defaultCenter";
	/**
	 * 使用百度地图的密钥
	 */
	public static final String BDMapKey = "zwIFsm9hVHYmroq923Psz3xv";
	/**
	 * SharedPreferences数据共享名称
	 */
	public static final String sharedPreferencesName = "userData";
	
	public static final String DefaultCity = "DefaultCity";
	/**
	 * QQ登录返回的数据
	 */
	public static String qqUserName = null;
	/**
	 * QQ用户登录之后的头像
	 */
	public static Bitmap UserIcon = null;
	/**
	 * 数据库名称
	 */
	public static String DBName = "com.wise.wawc";
	/**
	 * 数据库版本
	 */
	public static int DBVersion = 1;
	/**
	 * 说说表
	 */
	public static final String articleTable = "article_tb";
	/**
	 * 评论表
	 */
	public static final String commentTable = "comment_tb";
	/**
	 * 说说图片表
	 */
	public static final String imageTable = "image_tb";
}
