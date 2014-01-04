package com.wise.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wise.data.Article;
import com.wise.extend.DBHelper;
import com.wise.pubclas.Config;

public class DBOperation {
	private DBHelper dBHelper;
	public DBOperation(Context context) {
		dBHelper = new DBHelper(context, Config.DBName, null, Config.DBVersion);
	}
	
	public void newArticle(Object[] obj){
		SQLiteDatabase db = dBHelper.getWritableDatabase();
		try {
				db.execSQL("insert into "+ Config.articleTable+" (publish_user, publish_time,publish_content,article_comment_id)values(?, ?, ?, ?)",
						new Object[]{obj[0],obj[1],obj[2],obj[3]});
//						new Object[]{"没伴奏、就清唱","2014年01月03日15:46","出现交通事故不要慌",0});
		  }  catch (SQLException e)  {
			  e.printStackTrace();
		 } 
	}
	
	public void newComment(Object[] obj){
		SQLiteDatabase db = dBHelper.getWritableDatabase();
		try {
			db.execSQL("insert into "+ Config.commentTable+" (comment_user, comment_time,comment_content,admire_person,comment_article_id)values(?, ?, ?, ?,?)",
//					new Object[]{"没伴奏、就清唱","2014年01月03日15:46","出现交通事故不要慌",1,0});
					new Object[]{obj[0],obj[1],obj[2],obj[3],obj[4]});
		  }  catch (SQLException e)  {
			  e.printStackTrace();
		 } 
	}
	
	public void articleImage(List<Object[]> objList){
		SQLiteDatabase db = dBHelper.getWritableDatabase();
		try {
			for(Object[] obj : objList){
			db.execSQL("insert into "+ Config.imageTable+" (image_name, image_article_id)values(?, ?)",
					new Object[]{obj[0],obj[1]});
			}
		  }  catch (SQLException e)  {
			  e.printStackTrace();
		 } 
	}
	public List<Article> selectArticle(int start,int pageSize){
//		create table article_tb(article_id integer primary key autoincrement, publish_user varchar(30),publish_time varchar(20), publish_content text,article_comment_id integer)
		SQLiteDatabase db = dBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from article_tb order by article_id limit ?, ?", new String[]{String.valueOf(0), String.valueOf(10)});
		List<Article> Articlelist = new ArrayList<Article>();
		while (cursor.moveToNext()){ //如果移动成功，才返回真
			Article article = new Article();
			article.setArticle_id(cursor.getInt(cursor.getColumnIndex("article_id")));
			article.setPublish_user(cursor.getString(cursor.getColumnIndex("publish_user")));
			article.setPublish_time(cursor.getString(cursor.getColumnIndex("publish_time")));
			article.setPublish_content(cursor.getString(cursor.getColumnIndex("publish_content")));
			article.setArticle_comment_id(cursor.getInt(cursor.getColumnIndex("article_comment_id")));
			Articlelist.add(article);
		}
		return Articlelist;
	}
}
