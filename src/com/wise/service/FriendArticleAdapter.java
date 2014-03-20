package com.wise.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.Article;
import com.wise.extend.FaceConversionUtil;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.MyAdapter.UserImageAdapter;
import com.wise.service.MyAdapter.UserImageAdapter.UserImageHolder;
import com.wise.sql.DBExcute;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.PicActivity;
import com.wise.wawc.R;
import com.wise.wawc.VehicleFriendActivity;

import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class FriendArticleAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater layoutInflater;
	public static boolean isClick = false;
	private List<Article> articleList = null;
	
	private StringBuffer sb = null;
	//private OnClickListeners listener = null;
	private ProgressDialog myDialog = null;
	private MyHandler myHandler = null;
	private DBExcute dbExcute= null;
	private FriendHomeActivity friendHomeActivity = null;
	private static final int articleFavorite = 2;
	private LinearLayout linearLayout;   //点击评论显示输入框
	private Bitmap bitmap = null;
	private int blogId = 0;
	ViewHolder viewHolder;
	
	public FriendArticleAdapter(Context context,LinearLayout linearLayout,List<Article> articleList){
		this.context = context;
		this.linearLayout = linearLayout;
		this.articleList = processData(articleList);
		layoutInflater = LayoutInflater.from(context);
		//listener = new OnClickListeners(0);
		myHandler = new MyHandler();
		dbExcute = new DBExcute();
		friendHomeActivity = new FriendHomeActivity();
	}
	public int getCount() {
		return articleList.size();
	}
	public Object getItem(int position) {
		return articleList.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.friend_article_adapter, null);
			viewHolder.publishTime = (TextView) convertView.findViewById(R.id.friend_article_publish_time);
			viewHolder.articleContent = (TextView) convertView.findViewById(R.id.friend_article_content);
			viewHolder.data = (TextView) convertView.findViewById(R.id.friend_article_data);
			viewHolder.userImage = (GridView) convertView.findViewById(R.id.friend_home_image);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		// 动态显示用户发表的图片
		List<Bitmap> smallImageList = new ArrayList<Bitmap>();
		List<Bitmap> threeSmallImageList = new ArrayList<Bitmap>();
		for (int i = 0; i < articleList.get(position).getImageList().size(); i++) {
			Map<String, String> imageMap = articleList.get(position).getImageList().get(i);
			// 判断小图是否存在sd卡 /点击小图的时候再判断是否存在sd卡
			String smallImage = imageMap.get("small_pic").substring(imageMap.get("small_pic").lastIndexOf("/"));
			Bitmap smallBitmap = imageIsExist(Constant.VehiclePath + smallImage,imageMap.get("small_pic"));
			if (i <= 2) {
				threeSmallImageList.add(smallBitmap);
			}
			smallImageList.add(smallBitmap);
		}
		viewHolder.userImage.setAdapter(new UserImageAdapter(threeSmallImageList, position));
		viewHolder.userImage.setFocusable(false);
		String str = articleList.get(position).getCreate_time();
		String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
		viewHolder.articleContent.setText(articleList.get(position).getContent());
		
		String data = getTime(createTime).substring(0,getTime(createTime).indexOf("t"));
		String time = getTime(createTime).substring(getTime(createTime).indexOf("t") + 1);
		viewHolder.publishTime.setText(time);
		String section = getSectionForPosition(position);
		if(position == getPositionForSection(section)){
			viewHolder.data.setVisibility(View.VISIBLE);
			viewHolder.data.setText(this.articleList.get(position).getData());
		}else{
			viewHolder.data.setText("");
		}
		return convertView;
	}
	
	/**
	 * 根据ListView的当前位置获取文章对象hashCode值
	 */
	public String getSectionForPosition(int position) {
		return this.articleList.get(position).getData();
	}
	
	public int getPositionForSection(String section) {
		for (int i = 0; i < getCount(); i++) {
			String str = this.articleList.get(i).getData();
			if (str.equals(section)) {
				return i;
			}
		}
		return -1;
	}
	
	class ViewHolder{
		public TextView publishTime;
		public TextView data = null;
		public TextView articleContent = null;
		public GridView userImage;
	}
	
//	class OnClickListeners implements OnClickListener{
//		int position = 0;
//		OnClickListeners(int position){
//			this.position = position;
//		}
//		public void onClick(View v) {
//			switch(v.getId()){
//			}
//		}
//	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case articleFavorite:
				String result = msg.obj.toString();
				try {
					JSONObject jsonObject = new JSONObject(result);
					if(Integer.valueOf(jsonObject.getString("status_code")) == 0){
						
						//更新数据库
						dbExcute.updateArticlePraises(context, Constant.TB_VehicleFriend, blogId, Variable.cust_name, Integer.valueOf(Variable.cust_id));
						
						VehicleFriendActivity vehicleFriendActivity = new VehicleFriendActivity();
						//更新列表
						List<Article> oldArticlList = vehicleFriendActivity.getArticleDataList();
						oldArticlList.clear();
						vehicleFriendActivity.setArticleDataList(oldArticlList);
						List<Article> newArticlList = FriendArticleAdapter.this.dbExcute.getArticlePageDatas(context, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(0),String.valueOf(Constant.start1 + Constant.pageSize)}, vehicleFriendActivity.getArticleDataList());
						Variable.articleList = newArticlList;
						vehicleFriendActivity.setArticleDataList(newArticlList);
						FriendArticleAdapter.this.refreshDates(newArticlList);
						myDialog.dismiss();
						
						Toast.makeText(context, "点赞成功", 0).show();
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	private Bitmap imageIsExist(String path,final String loadUrl) {
		File file = new File(path);
		if(file.exists()){
			bitmap = BitmapFactory.decodeFile(path);
		}else{
			new Thread(new Runnable() {
				public void run() {
					Bitmap tempBitmap = GetSystem.getBitmapFromURL(loadUrl);
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						File imagePath = new File(Constant.VehiclePath);
						if(!imagePath.exists()){
							imagePath.mkdir();
						}
						if(tempBitmap != null){
							createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),bitmap);
						}
					}
				}
			}).start();
		}
		return bitmap;
	}
	
	public void createImage(String fileName,Bitmap bitmap){
		FileOutputStream b = null;
		try {  
            b = new FileOutputStream(fileName);  
            Log.e("------------->" , fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                b.flush();  
                b.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
	}
	
	 class UserImageAdapter extends BaseAdapter{
		 List<Bitmap> smallImageList;
		 int indexId = 0;
		 UserImageAdapter(List<Bitmap> smallImageList,int indexId){
			 this.smallImageList = smallImageList;
			 this.indexId = indexId;
		 }
		public int getCount() {
			return smallImageList.size();
		}
		public Object getItem(int position) {
			return smallImageList.get(position);
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			UserImageHolder userImageHolder = null; 
			if(convertView == null){
				userImageHolder = new UserImageHolder();
				convertView = layoutInflater.inflate(R.layout.user_image_item, null);
				userImageHolder.userImage = (ImageView) convertView.findViewById(R.id.one_image);
				userImageHolder.userImage.setLayoutParams(new LinearLayout.LayoutParams(Variable.smallImageReqWidth, Variable.smallImageReqWidth));
				convertView.setTag(userImageHolder);
			}else{
				userImageHolder = (UserImageHolder) convertView.getTag();
			}
			
			
			Bitmap bitmap = smallImageList.get(position);
			if(bitmap == null){   //显示临时图片
				Bitmap im = BitmapFactory.decodeResource(context.getResources(), R.drawable.article);
				userImageHolder.userImage.setImageBitmap(im);
//				userImageHolder.userImage.setBackgroundResource(R.drawable.article_comment_bg);
			}else{
				userImageHolder.userImage.setImageBitmap(smallImageList.get(position));
				userImageHolder.userImage.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						//查看大图
						Intent intent = new Intent(context,PicActivity.class);
						intent.putExtra("article", articleList.get(indexId));
						context.startActivity(intent);
					}
				});
			}
			return convertView;
		}
		
		class UserImageHolder{
			ImageView userImage = null;
		}
	 }
	 public String getTime(String time){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(new Date());
			String time1 =  MyAdapter.transform(time);
		     if(Integer.parseInt(time1.substring(0,4)) < Integer.parseInt(currentTime.substring(0,4))){
		    	 return time1.substring(0, 10) + "t" + time1.substring(11, 16);
		     }else{
		    	 if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time1.substring(8,10))) == 1){
		    		 return "昨天t" + time1.substring(11, 16);
		    	 }else if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time1.substring(8,10))) == 2){
		    		 return "前天t" + time1.substring(11, 16);
		    	 }else if((Integer.parseInt(currentTime.substring(8,10)) == Integer.parseInt(time1.substring(8,10)))){
		    		 return "今天t" + time1.substring(11, 16);
		    	 }
		    	 return time1.substring(5, 10) + "t" + time1.substring(11, 16);
		     }
		}
	
	public  SpannableString getFaceImage(String faceContent){
		 return FaceConversionUtil.getInstace().getExpressionString(context, faceContent);
	 }
	public void refreshDates(List<Article> articleList){ 
		this.articleList = processData(articleList);
		this.notifyDataSetChanged();
	}
	
	public List<Article> processData(List<Article> articleList){
		for(int i = 0 ; i < articleList.size() ; i ++){
			String str = articleList.get(i).getCreate_time();
			String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
			String data = getTime(createTime).substring(0,getTime(createTime).indexOf("t"));
			articleList.get(i).setData(data);
		}
		return articleList;
	}
}
