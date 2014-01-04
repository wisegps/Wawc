package com.wise.extend;
import com.wise.pubclas.Config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 数据库操作类
 * @author Mr.Wang
 */
public class DBHelper extends SQLiteOpenHelper {

	private Context context;
	private String DBName;
	private int DBVersion;
	public DBHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	@SuppressLint("NewApi")
	public DBHelper(Context context, String name, CursorFactory factory,int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String article_tb_sql = "create table "+ Config.articleTable+"(article_id integer primary key autoincrement, publish_user varchar(30),publish_time varchar(20), publish_content text,article_comment_id integer)";
			String comment_tb_sql = "create table "+ Config.commentTable+"(comment_id integer primary key autoincrement, comment_user varchar(30),comment_time varchar(20), comment_content text, admire_person integer, comment_article_id integer)";
			String image_tb_sql = "create table "+ Config.imageTable+"(image_id integer primary key autoincrement, image_name varchar,image_article_id integer)";
			
			db.execSQL(article_tb_sql);
			db.execSQL(comment_tb_sql);
			db.execSQL(image_tb_sql);
			Log.e("创建表成功","创建表成功");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
