package com.wise.sql;

import java.util.ArrayList;
import java.util.List;

import com.wise.data.AdressData;
import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	
	
	//分页查询
	public List<AdressData> getPageDatas(Context context,String sql,String[] whereClause,int excuteCode,List<AdressData> adressData){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = null;
		//查询
		if(excuteCode == 2){
			db = dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(sql, whereClause);
			while(cursor.moveToNext()){
				AdressData adrDatas = new AdressData(); 
				adrDatas.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				adrDatas.setAdress(cursor.getString(cursor.getColumnIndex("address")));
				adrDatas.setName(cursor.getString(cursor.getColumnIndex("name")));
				adrDatas.setPhone(cursor.getString(cursor.getColumnIndex("tel")));
				adrDatas.setLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat"))));
				adrDatas.setLon(Double.parseDouble(cursor.getString(cursor.getColumnIndex("lon"))));
				adressData.add(adrDatas);
			}
		}else if(excuteCode == 1){
			//  TODO 刷新
		}
	
		db.close();
		return adressData;
	}
	
	/**
	 * 查询数据总量
	 * @return
	 */
	public int getTotalCount(String tableName,Context context){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from " + tableName + ";";
		Cursor cursor = db.rawQuery(sql, new String[]{});
		int totalPage = cursor.getCount();
		db.close();
		return totalPage;
	}
}
