package com.wise.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBExcute {	
	/**
	 * 向数据库中插入记录
	 * @param values
	 */
	public void InsertDB(Context context, ContentValues values ,String table){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insert(table, null, values);
		db.close();
	}
	/**
	 * 更新记录
	 * @param sql
	 */
	public void UpdateDB(Context context,String sql){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}
	/**
	 * 删除记录
	 * @param id
	 */
	public void DeleteDB(Context context,String sql){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}
}