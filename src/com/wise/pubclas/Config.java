package com.wise.pubclas;

import java.util.List;
import android.graphics.Bitmap;
import com.wise.data.CarData;

public class Config {
    /**
     * 服务器地址："http://wiwc.api.wisegps.cn/"
     */
    public static String BaseUrl = "http://wiwc.api.wisegps.cn/";
	/**
	 * 获取版本信息用到
	 */
	public static String PackageName = "com.wise.wawc";
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
	 * 定位城市，获取天气油价
	 */
	public static final String LocationCity = "LocationCity";
	/**
	 * 城市编码
	 */
	public static final String LocationCityCode = "LocationCityCode";
	/**
	 * 省份
	 */
	public static final String LocationProvince = "LocationProvince";
	/**
	 * QQ登录返回的数据
	 */
	public static String qqUserName = "";
	/**
	 * QQ用户登录之后的头像
	 */
	public static Bitmap UserIcon = null;
	/**
	 * auth_code
	 */
	public static String auth_code;
	/**
	 * cust_id
	 */
	public static String cust_id;
	/**
	 * 基础表
	 */
	public static String TB_Base = "TB_Base";
	/**
	 * 车友圈
	 */
	public static String TB_VehicleFriend = "TB_VehicleFriend";
	/**
	 * 爱车故障
	 */
	public static String TB_Faults = "TB_Faults";
	/**
	 * 行程记录
	 */
	public static String TB_TripList = "TB_TripList";
	/**
	 * 单次行程记录详细
	 */
	public static String TB_Trip = "TB_Trip";
	/**
	 * 我的爱车
	 */
	public static String TB_Vehicle = "TB_Vehicle";
	/**
	 * 我的终端
	 */
	public static String TB_Devices = "TB_Devices";
	/**
	 * 我的收藏
	 */
	public static String TB_Collection = "TB_Collection";
	/**
	 * 定位到城市后广播
	 */
	public static String A_City = "com.wise.wawc.city";
}
