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
	private ImageView LvComment;
	private List<Article> articleList = null;
	
	private StringBuffer sb = null;
	private OnClickListeners listener = null;
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
		this.articleList = articleList;
		layoutInflater = LayoutInflater.from(context);
		listener = new OnClickListeners(0);
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
			viewHolder.favoriteStart = (ImageView) convertView.findViewById(R.id.friend_article_praises_star);
			viewHolder.favoriteUser = (TextView)convertView.findViewById(R.id.friend_article_praises_user);
			viewHolder.addFavorite = (ImageView) convertView.findViewById(R.id.friend_article_praises);
			viewHolder.commentLayout = (LinearLayout) convertView.findViewById(R.id.friend_home_article_comment_layout);
			viewHolder.line = convertView.findViewById(R.id.friend_article_line);
			//  TODO
			viewHolder.twoCommentLayout = (LinearLayout) convertView.findViewById(R.id.friend_home_two_comment_ll);
			viewHolder.totalComment = (TextView) convertView.findViewById(R.id.friend_home_comment_total_tv);
			viewHolder.oneCommentName = (TextView) convertView.findViewById(R.id.friend_home__one_comment_name_tv);
			viewHolder.oneCommentContent = (TextView) convertView.findViewById(R.id.friend_home__one_comment_content_tv);
			viewHolder.twoCommentName = (TextView) convertView.findViewById(R.id.friend_home_two_comment_name_tv);
			viewHolder.twoCommentContent = (TextView) convertView.findViewById(R.id.friend_home_two_comment_content_tv);
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
		viewHolder.addFavorite.setOnClickListener(new OnClickListeners(position));
		String str = articleList.get(position).getCreate_time();
		String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
		viewHolder.articleContent.setText(articleList.get(position).getContent());
		
		
		
		
		String data = getTime(createTime).substring(0,getTime(createTime).indexOf("t"));
		String time = getTime(createTime).substring(getTime(createTime).indexOf("t") + 1);
		
		//  TODO
		viewHolder.publishTime.setText(time);
		viewHolder.data.setText(data);
		//评论
		int size = articleList.get(position).getCommentList().size();
		viewHolder.commentLayout.setVisibility(View.VISIBLE);
		viewHolder.twoCommentLayout.setVisibility(View.VISIBLE);
		viewHolder.line.setVisibility(View.VISIBLE);
		if(size == 0 || articleList.get(position).getCommentList() == null){
			viewHolder.commentLayout.setVisibility(View.GONE);
			viewHolder.line.setVisibility(View.GONE);
		}else if(size == 1){
			viewHolder.twoCommentLayout.setVisibility(View.GONE);
			viewHolder.totalComment.setVisibility(View.GONE);
			viewHolder.oneCommentName.setText(articleList.get(position).getCommentList().get(0)[0]+":");
			viewHolder.oneCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(0)[1]));
		}else{
			viewHolder.twoCommentLayout.setVisibility(View.VISIBLE);
			for (int i = 0; i < size; i++) {
				if (i == articleList.get(position).getCommentList().size() - 1) {
					viewHolder.oneCommentName.setText(articleList.get(position).getCommentList().get(i)[0]+":");
					viewHolder.oneCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(i)[1]));
				} else if (i == articleList.get(position).getCommentList().size() - 2) {
					viewHolder.twoCommentName.setText(articleList.get(position).getCommentList().get(i)[0]+":");
					viewHolder.twoCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(i)[1]));
				}
			}
			if(articleList.get(position).getCommentList().size() > 2){
				viewHolder.totalComment.setVisibility(View.VISIBLE);
				viewHolder.totalComment.setText("共" + articleList.get(position).getCommentList().size() + "条评论");
			}
		}
		
		//动态添加用户的评论
//				LinearLayout commentLayout = (LinearLayout) convertView.findViewById(R.id.friend_article_comment_layout);
//				for(int i = 0 ; i < articleList.get(position).getCommentList().size() ; i ++){
//					LinearLayout oneComment = new LinearLayout(context);
//					oneComment.setOrientation(LinearLayout.HORIZONTAL);
//					TextView commentName = new TextView(context);
//					commentName.setTextColor(Color.parseColor("#3b5197"));
//					commentName.setPadding(Variable.margins, Variable.margins, 0, 0);
//				    TextView commentContent = new TextView(context);
//				    commentContent.setTextColor(Color.parseColor("#313131"));
//				    commentContent.setPadding(Variable.margins, Variable.margins, 0, 0);
//					String[] commentStr = articleList.get(position).getCommentList().get(i);
//					commentName.setText(commentStr[0] + ":");
//					oneComment.addView(commentName, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//					SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, commentStr[1]);
//					commentContent.setText(spannableString);
//					oneComment.addView(commentContent, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//					commentLayout.addView(oneComment, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//				}
				//点赞者
				if(articleList.get(position).getPraisesList() != null){
					if(articleList.get(position).getPraisesList().size() != 0){
						sb = new StringBuffer();
						Iterator iter = articleList.get(position).getPraisesList().entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String val = (String) entry.getValue();
							sb.append(val+",");
							if(Variable.cust_name.equals(val)){
								viewHolder.favoriteStart.setBackgroundResource(R.drawable.body_icon_heart_press);
							}
						}
						viewHolder.favoriteStart.setVisibility(View.VISIBLE);
						viewHolder.favoriteUser.setText(sb.toString());
					}else{
						viewHolder.favoriteStart.setVisibility(View.GONE);
					}
				}else{
					viewHolder.favoriteStart.setVisibility(View.GONE);
				}
		LvComment = (ImageView) convertView.findViewById(R.id.friend_article_comment);
		LvComment.setOnClickListener(new OnClickListeners(position));
		return convertView;
	}
	
	class ViewHolder{
		public TextView publishTime;
		public ImageView favoriteStart = null;
		public TextView favoriteUser = null;
		public TextView data = null;
		public ImageView addFavorite = null;
		public TextView articleContent = null;
		public LinearLayout commentLayout;
		public View line;
		public LinearLayout twoCommentLayout;
		public TextView totalComment;
		public TextView oneCommentName;
		public TextView oneCommentContent;
		public TextView twoCommentName;
		public TextView twoCommentContent;
		public GridView userImage;
	}
	
	
	class OnClickListeners implements OnClickListener{
		int position = 0;
		OnClickListeners(int position){
			this.position = position;
		}
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.friend_article_comment:
				FriendHomeActivity.blogId = articleList.get(this.position).getBlog_id();
				//编辑框不可见，设置为可见
				Log.e("onClick",String.valueOf(isClick));
				if(!isClick){
					isClick = true;
					linearLayout.setVisibility(View.VISIBLE);
				//编辑框可见，设置为不可见	
				}else if(isClick){
					isClick = false;
					linearLayout.setVisibility(View.GONE);
				}
				break;
			case R.id.friend_article_praises:  //  点赞
				FriendArticleAdapter.this.blogId = articleList.get(this.position).getBlog_id();
				myDialog = ProgressDialog.show(context, "提示","数据提交中...");
				myDialog.setCancelable(true);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name",Variable.cust_name));
				params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
				new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + blogId +"/praise?auth_code=" + Variable.auth_code, params, articleFavorite)).start();
				Log.e("点赞url:",Constant.BaseUrl + "blog/" + blogId +"/praise?auth_code=" + Variable.auth_code);
				break;
			}
		}
	}
	
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
						Log.e("---->",Constant.start + "");
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
		 UserImageAdapter(){}
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
						Intent intent = new Intent(context,ImageActivity.class);
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
		Log.e("好友主页迭代器：" , articleList.size()+"");
		this.articleList = articleList;
		this.notifyDataSetChanged();
	}
}
