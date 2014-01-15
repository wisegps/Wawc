package com.wise.sql;

import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBExcute {	
	/**
	 * 向数据库中插入记录
	 * @param values
	 */
	public void InsertDB(Context context, ContentValues values ,String table){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insert(table, null, values);
		Log.e("插入数据","插入数据");
		db.close();
		dbHelper.close();
	}
	/**
	 * 更新基础数据表
	 * @param context
	 * @param values
	 * @param Title
	 */
	public void UpdateDB(Context context,ContentValues values,String Title){
	    DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(Constant.TB_Base, values, "Title=?", new String[] {Title});
        db.close();
        dbHelper.close();
	}
	/**
	 * 更新需要Cust_id的基础表
	 * @param context
	 * @param values
	 * @param Title
	 * @param Cust_id
	 */
	public void UpdateBDCustID(Context context,ContentValues values,String Title,String Cust_id){
	    DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(Constant.TB_Base, values, "Title=? and Cust_id=?", new String[] {Title,Cust_id});
        db.close();
        dbHelper.close();
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
		dbHelper.close();
	}
	
	/**
	 * 删除记录
	 * @param id
	 */
	public void DeleteDB(Context context,String table,String whereClause, String[] whereArgs){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(table, whereClause, whereArgs);
		db.close();
	}
	/**
	 * 更新汽车数据
	 * @param id
	 */
	public void updataVehilce(Context context,String tableName,ContentValues values,String whereClause,String[] whereArgs){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.update(tableName, values, whereClause, whereArgs);
		Log.e("更改数据库","更改数据库");
		db.close();
	}
}
