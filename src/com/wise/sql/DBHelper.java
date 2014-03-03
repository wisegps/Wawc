package com.wise.sql;

import com.wise.pubclas.Constant;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper{
	private static final int VERSION = 1;
	private static final String DB_NAME = "DB_Wiwc";
	//基础表
	private static final String CREATE_TB_Base = "create table " + Constant.TB_Base + "(_id integer primary key autoincrement,Cust_id text,Title text,Content text)";
	//车友圈文章
	private static final String CREATE_TB_VehicleFriend = "create table " + Constant.TB_VehicleFriend + "(_id integer primary key autoincrement,Cust_id int,FriendID int,Blog_id int,UserLogo text ,Content text)";
	//车友圈文章类型
	private static final String CREATE_TB_VehicleFriendType = "create table " + Constant.TB_VehicleFriendType + "(_id integer primary key autoincrement,Type_id int,Blog_id int)";
	//爱车故障
	private static final String CREATE_TB_Faults = "create table " + Constant.TB_Faults + "(_id integer primary key autoincrement,DeviceID text,fault_id int,fault_code text,fault_desc text,create_time text)";
	//行程统计
	private static final String CREATE_TB_TripTotal = "create table " + Constant.TB_TripTotal + "(_id integer primary key autoincrement,device_id text,tDate text,Content text)";
	//行程记录
	private static final String CREATE_TB_TripList = "create table " + Constant.TB_TripList + "(_id integer primary key autoincrement,device_id text,tDate text,Content text)";
	//行程记录
	private static final String CREATE_TB_Trip = "create table " + Constant.TB_Trip + "(_id integer primary key autoincrement,Device_id text,tDate text,Content text)";
	//我的爱车
	private static final String CREATE_TB_Vehicle = "create table " + Constant.TB_Vehicle + "(_id integer primary key autoincrement,Cust_id text,obj_id int,obj_name text,car_brand text,car_series text,car_type text,engine_no text,frame_no text,insurance_company text,insurance_date text,annual_inspect_date text,maintain_company text,maintain_last_mileage text,maintain_next_mileage text,buy_date text,reg_no text,vio_location text)";
	//我的终端
	private static final String CREATE_TB_Devices = "create table " + Constant.TB_Devices + "(_id integer primary key autoincrement,Cust_id text,DeviceID int,Content text)";
	//我的收藏
	private static final String CREATE_TB_Collection = "create table " + Constant.TB_Collection + "(_id integer primary key autoincrement,Cust_id text,favorite_id text,name text,address text,tel text,lon text,lat text)";
	//我的违章
	private static final String CREATE_TB_Traffic = "create table " + Constant.TB_Traffic + "(_id integer primary key autoincrement,obj_id text,Car_name text,create_time text,action text,location text,score int,fine int)";
	//我的账户
	private static final String CREATE_TB_Account = "create table " + Constant.TB_Account + "(_id integer primary key autoincrement,cust_id text,Consignee text,Adress text,Phone text,annual_inspect_date text,change_date text)";
	//我的消息
	private static final String CREATE_TB_Sms = "create table " + Constant.TB_Sms + "(_id integer primary key autoincrement,cust_id text,noti_id int,msg_type int,content text,rcv_time text,lat text,lon text,status text)";
	
	private static final String CREATE_TB_IllegalCity = "create table " + Constant.TB_IllegalCity + "(_id integer primary key autoincrement,json_data text)";
	public DBHelper(Context context){
		super(context,DB_NAME,null,VERSION);
	}
	public DBHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TB_Base);
		db.execSQL(CREATE_TB_VehicleFriend);
		db.execSQL(CREATE_TB_Faults);
		db.execSQL(CREATE_TB_TripTotal);
		db.execSQL(CREATE_TB_TripList);
		db.execSQL(CREATE_TB_Trip);
		db.execSQL(CREATE_TB_Vehicle);
		db.execSQL(CREATE_TB_Devices);
		db.execSQL(CREATE_TB_Collection);
		db.execSQL(CREATE_TB_Traffic);
		db.execSQL(CREATE_TB_VehicleFriendType);
		db.execSQL(CREATE_TB_Account);
		db.execSQL(CREATE_TB_Sms);
		db.execSQL(CREATE_TB_IllegalCity);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
