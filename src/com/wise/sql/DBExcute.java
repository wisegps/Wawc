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
		System.out.println("插入成功");
	}
	/**
	 * 删除表
	 * @param context
	 * @param sql
	 */
	public void DeleteDB(Context context,String sql){
	    DBHelper dbHelper = new DBHelper(context);
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    db.execSQL(sql);
	    db.close();
	    dbHelper.close();
	}
	/**
	 * 更新数据库
	 * @param context
	 * @param values
	 * @param where
	 * @param args
	 * @param Table
	 */
	public void UpdateDB(Context context,ContentValues values,String where, String[] args,String Table){
	    DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(Table, values, where, args);
        db.close();
        dbHelper.close();
        System.out.println("更新数据库");
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
		if(cursor != null){
			cursor.close();
			db.close();
		}
		return adressDatas;
	}
	
	/**
	 * 分页查询（车友圈）
	 */
		public List<Article> getArticlePageDatas(Context context,String sql,String[] whereClause,List<Article> articleData){
			DBHelper dbHelper = new DBHelper(context);
			SQLiteDatabase db = null;
				db = dbHelper.getReadableDatabase();
				Cursor cursor = db.rawQuery(sql, whereClause);
				while(cursor.moveToNext()){
					articleData.add(parseDBDatas(cursor));
				}
				if(cursor != null){
					cursor.close();
				}
				db.close();  
				return articleData;
		}
		
		/**
		 * 分类查询车友圈文章  TODO
		 */
		public List<Article> getArticleTypeList(Context context,String sql,String[] whereClause,List<Article> articleData){
			if(articleData == null){
	            System.out.println("车友圈null");
			}else{
			    System.out.println("车友圈不null");
			}
		    DBHelper dbHelper = new DBHelper(context);
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, whereClause);
			while(cursor.moveToNext()){
				int blog_id = cursor.getInt(cursor.getColumnIndex("Blog_id"));
				//通过文章类型中的blog_id 在文章表中查询文章详细信息
				SQLiteDatabase reader = dbHelper.getReadableDatabase();
				Cursor cursors = reader.rawQuery("select * from " + Constant.TB_VehicleFriend + " where Blog_id=?", new String[]{String.valueOf(blog_id)});
				if(cursors.moveToNext()){
					articleData.add(parseDBDatas(cursors));
				}
			}
			if(cursor != null){
				cursor.close();
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
		if(cursor != null){
			cursor.close();
		}
		reader.close();
		update.close();
	}
	public void updateArticlePraises(Context context,String tableName,int whereValue,String praisesUser,int cust_id){
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase reader = dbHelper.getReadableDatabase();
		SQLiteDatabase update = dbHelper.getWritableDatabase();
		Cursor cursor = reader.rawQuery("select * from " + tableName + " where Blog_id=?", new String[]{String.valueOf(whereValue)});
		String content = "";
		String newContent = "";
		while(cursor.moveToNext()){
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
		if(cursor != null){
			cursor.close();
		}
		reader.close();
		update.close();
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
		if(cursor != null){
			cursor.close();
		}
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
        if(cursor != null){
        	cursor.close();
        }
        db.close();
        return totalPage;
    }
	
	public Article parseDBDatas(Cursor cursor){
		Article article = null;
		try {
			JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex("Content")));
			article = new Article();
			article.setBlog_id(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Blog_id"))));
			article.setUserLogo(cursor.getString(cursor.getColumnIndex("UserLogo")));
			if(jsonObject.opt("update_time") != null){
				article.setUpdateTime(jsonObject.getString("update_time"));
			}else{
				article.setUpdateTime(jsonObject.getString("create_time"));
			}
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
			
//			用户发表的图片
			Map<String,String> imageListTemp = null;
			List<Map<String,String>> imageList = new ArrayList<Map<String,String>>();
			if(!"[]".equals(jsonObject.getString("pics"))){
				JSONArray json = new JSONArray(jsonObject.getString("pics"));
				for(int j = 0 ; j < json.length() ; j ++){
					JSONObject jsonObj = json.getJSONObject(j);
					imageListTemp = new HashMap<String, String>();
					imageListTemp.put(Constant.smallImage, jsonObj.getString("small_pic"));
					imageListTemp.put(Constant.bigImage, jsonObj.getString("big_pic"));
					imageList.add(imageListTemp);
					
				}
			}
			if(!"[]".equals(jsonObject.getString("praises"))){
				JSONArray json = new JSONArray(jsonObject.getString("praises"));
//				List<String> parisesList = new ArrayList<String>();
				Map<String,String> parisesList = new HashMap<String,String>();
				for(int k = 0 ; k < json.length(); k ++){
//					parisesList.add(json.getJSONObject(k).getString("name"));
					parisesList.put(json.getJSONObject(k).getString("cust_id"), json.getJSONObject(k).getString("name"));
				}
				article.setPraisesList(parisesList);
			}
			article.setImageList(imageList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return article;
	}
	
	public String selectIllegal(Context context){
		String jsonData = null;
		DBHelper helper = new DBHelper(context);
		SQLiteDatabase reader = helper.getReadableDatabase();
		Cursor cursor = reader.rawQuery("select * from " + Constant.TB_IllegalCity, new String[]{});
		if(cursor.moveToFirst()){
			jsonData = cursor.getString(cursor.getColumnIndex("json_data"));
		}
		if(cursor != null){
			cursor.close();
		}
		reader.close();
		return jsonData;
	}
	/**
	 * 刷新评论  赞
	 * @param whereValues
	 * @param updateTime
	 * @param comments
	 * @param praises
	 * @param TbName
	 * @param context
	 */
	public void updataComment(String whereValues,String updateTime,String comments,String praises,String TbName,Context context) {
		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase reader = dbHelper.getReadableDatabase();
		SQLiteDatabase update = dbHelper.getWritableDatabase();
		Cursor cursor = reader.rawQuery("select * from " + TbName + " where Blog_id=?", new String[]{String.valueOf(whereValues)});
		String content = "";
		String newContent = "";
		
		while(cursor.moveToNext()){
			content = cursor.getString(cursor.getColumnIndex("Content"));
		}
		try {
			//用来存储赞相关数据
			JSONObject jsonObject = new JSONObject(content);
			JSONArray jsonArrayPraises = jsonObject.getJSONArray("praises");  //  数据库原本赞
			JSONArray praisesArray = praisesArray = new JSONArray(praises);   //新增赞
			jsonObject.remove("praises");
			jsonObject.put("praises", praisesArray);
			
			JSONArray jsonArrayComments = jsonObject.getJSONArray("comments");
			JSONArray commentsJsonArray = new JSONArray(comments);
			jsonObject.remove("comments");
			jsonObject.put("comments", commentsJsonArray);
			
			
			if(jsonObject.opt("update_time") != null){
				jsonObject.remove("updata_time");
				jsonObject.put("update_time", updateTime);
			}else{
				jsonObject.put("update_time", updateTime);
			}
			String newContents = jsonObject.toString().replaceAll("\\\\", "");
			ContentValues values = new ContentValues();
			values.put("Content", newContents);
			update.update(TbName, values, "Blog_id=?", new String[]{String.valueOf(whereValues)});
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(cursor != null){
			cursor.close();
		}
		reader.close();
		update.close();
	}
}
