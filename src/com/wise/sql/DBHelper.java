package com.wise.sql;

import com.wise.pubclas.Config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	private static final int VERSION = 1;
	private static final String DB_NAME = "DB_Wiwc";
	//基础表
	private static final String CREATE_TB_Base = "create table " + Config.TB_Base + "(_id integer primary key autoincrement,Title text,Content text)";
	//车友圈文章
	private static final String CREATE_TB_VehicleFriend = "create table " + Config.TB_VehicleFriend + "(_id integer primary key autoincrement,FriendID int,Content text)";
	//爱车故障
	private static final String CREATE_TB_Faults = "create table " + Config.TB_Faults + "(_id integer primary key autoincrement,CarID int,Content text)";
	//行程记录
	private static final String CREATE_TB_TripList = "create table " + Config.TB_TripList + "(_id integer primary key autoincrement,CarID int,Date text,Content text)";
	//行程记录
	private static final String CREATE_TB_Trip = "create table " + Config.TB_Trip + "(_id integer primary key autoincrement,TripID int,Content text)";
	//我的爱车
	private static final String CREATE_TB_Vehicle = "create table " + Config.TB_Vehicle + "(_id integer primary key autoincrement,CarID int,Content text)";
	//我的终端
	private static final String CREATE_TB_Devices = "create table " + Config.TB_Devices + "(_id integer primary key autoincrement,DeviceID int,Content text)";
	//我的收藏
	private static final String CREATE_TB_Collection = "create table " + Config.TB_Collection + "(_id integer primary key autoincrement,Content text)";
	
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
		db.execSQL(CREATE_TB_TripList);
		db.execSQL(CREATE_TB_Trip);
		db.execSQL(CREATE_TB_Vehicle);
		db.execSQL(CREATE_TB_Devices);
		db.execSQL(CREATE_TB_Collection);
		Log.e("创建表成功","创建表成功");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
