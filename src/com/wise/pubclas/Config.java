package com.wise.pubclas;

import java.util.List;
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
	/**
	 * 故障推送
	 */
	public static boolean faultPush = true;
	/**
	 * 车务提醒
	 */
	public static boolean remaindPush = true;
	
	/**
	 * 默认定位中心
	 */
	public static String defaultCenter = "车辆位置";
	/**
	 * 使用百度地图的密钥
	 */
	public static final String BDMapKey = "zwIFsm9hVHYmroq923Psz3xv";
	/**
	 * SharedPreferences数据共享名称
	 */
	public static final String sharedPreferencesName = "userData";
	
	public static final String DefaultCity = "DefaultCity";
}
