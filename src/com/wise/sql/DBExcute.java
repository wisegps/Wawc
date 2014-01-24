package com.wise.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.AdressData;
import com.wise.data.Article;
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
	public List<AdressData> getPageDatas(Context context,String sql,String[] whereClause){
	    List<AdressData> adressDatas = new ArrayList<AdressData>();
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, whereClause);
		while(cursor.moveToNext()){
			AdressData adrDatas = new AdressData(); 
			adrDatas.set_id(cursor.getInt(cursor.getColumnIndex("favorite_id")));
			adrDatas.setAdress(cursor.getString(cursor.getColumnIndex("address")));
			adrDatas.setName(cursor.getString(cursor.getColumnIndex("name")));
			adrDatas.setPhone(cursor.getString(cursor.getColumnIndex("tel")));
			adrDatas.setLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex("lat"))));
			adrDatas.setLon(Double.parseDouble(cursor.getString(cursor.getColumnIndex("lon"))));
			adressDatas.add(adrDatas);
		}
		cursor.close();
		db.close();
		return adressDatas;
	}
	
	/**
	 * 分页查询（车友圈）
	 */
		public List<Article> getArticlePageDatas(Context context,String sql,String[] whereClause,List<Article> articleData){
			DBHelper dbHelper = new DBHelper(context);
			SQLiteDatabase db = null;
				db = dbHelper.getWritableDatabase();
				Cursor cursor = db.rawQuery(sql, whereClause);
				while(cursor.moveToNext()){
					try {
						JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex("Content")));
						Article article = new Article();
						article.setBlog_id(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Blog_id"))));
						article.setCity(jsonObject.getString("city"));
						article.setName(jsonObject.getString("name"));
						List<String[]> comments = new ArrayList<String[]>();
						if(!"[]".equals(jsonObject.getString("comments"))){
							String commentjson = jsonObject.getString("comments");
							JSONArray  jsonArrayComment = new JSONArray(commentjson);
							for(int m = 0; m < jsonArrayComment.length() ; m ++){
								String[] commentList = new String[2];
								JSONObject jsonObjectComment = jsonArrayComment.getJSONObject(m);
								commentList[0] =  jsonObjectComment.getString("name");
								commentList[1] =  jsonObjectComment.getString("content");
								comments.add(commentList);
							}
						}
						article.setCommentList(comments);
						article.setContent(jsonObject.getString("content"));
						article.setCreate_time(jsonObject.getString("create_time"));
						article.setCust_id(cursor.getInt(cursor.getColumnIndex("Cust_id")));
//						
//						//用户发表的图片
						Map<String,String> imageListTemp = null;
						List<Map<String,String>> imageList = new ArrayList<Map<String,String>>();
						if(!"[]".equals(jsonObject.getString("pics"))){
							JSONArray json = new JSONArray(jsonObject.getString("pics"));
							for(int j = 0 ; j < json.length() ; j ++){
								JSONObject jsonObj = json.getJSONObject(j);
								imageListTemp = new HashMap<String, String>();
								imageListTemp.put("small_pic", jsonObj.getString("small_pic"));
								imageListTemp.put("big_pic", jsonObj.getString("big_pic"));
								imageList.add(imageListTemp);
							}
						}
						if(!"[]".equals(jsonObject.getString("praises"))){
							JSONArray json = new JSONArray(jsonObject.getString("praises"));
							List<String> parisesList = new ArrayList<String>();
							for(int k = 0 ; k < json.length(); k ++){
								parisesList.add(json.getJSONObject(k).getString("name"));
							}
							article.setPraisesList(parisesList);
						}
						article.setImageList(imageList);
						articleData.add(article);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				db.close();
				return articleData;
		}
		
	/**
 	* 更新车友圈数据
	*/
	public void updateArticleComments(Context context,String tableName,int whereValue,String commentValue,String commentUser,int cust_id){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase reader = dbHelper.getReadableDatabase();
		SQLiteDatabase update = dbHelper.getWritableDatabase();
		Cursor cursor = reader.rawQuery("select * from " + tableName + " where Blog_id=?", new String[]{String.valueOf(whereValue)});
		String content = "";
		String newContent = "";
		if(cursor.moveToFirst()){
			content = cursor.getString(cursor.getColumnIndex("Content"));
		}
		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONArray jsonArray = jsonObject.getJSONArray("comments");
			
			JSONObject newComment = new JSONObject();
			newComment.put("content", commentValue);
			newComment.put("cust_id", cust_id);
			newComment.put("name", commentUser);
			jsonArray.put(newComment);
			
			newContent = jsonObject.toString().replaceAll("\\\\", "");
			ContentValues values = new ContentValues();
			values.put("Content", newContent);
			update.update(tableName, values, "Blog_id=?", new String[]{String.valueOf(whereValue)});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void updateArticlePraises(Context context,String tableName,int whereValue,String praisesUser,int cust_id){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase reader = dbHelper.getReadableDatabase();
		SQLiteDatabase update = dbHelper.getWritableDatabase();
		Cursor cursor = reader.rawQuery("select * from " + tableName + " where Blog_id=? and Cust_id=?", new String[]{String.valueOf(whereValue),Variable.cust_id});
		String content = "";
		String newContent = "";
		if(cursor.moveToNext()){
			content = cursor.getString(cursor.getColumnIndex("Content"));
		}
		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONArray jsonArray = jsonObject.getJSONArray("praises");
			
			JSONObject newPraises = new JSONObject();
			newPraises.put("name", praisesUser);
			newPraises.put("cust_id", cust_id);
			jsonArray.put(newPraises);
			
			newContent = jsonObject.toString().replaceAll("\\\\", "");
			ContentValues values = new ContentValues();
			values.put("Content", newContent);
			update.update(tableName, values, "Blog_id=?", new String[]{String.valueOf(whereValue)});
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
		cursor.close();
		db.close();
		return totalPage;
	}
	/**
	 * 带条件的查询总记录数目
	 * @param context
	 * @param sql
	 * @param whereClause
	 * @return
	 */
	public int getTotalCount(Context context,String sql,String[] whereClause){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, whereClause);
        int totalPage = cursor.getCount();
        cursor.close();
        db.close();
        return totalPage;
    }
}
