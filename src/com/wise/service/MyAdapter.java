package com.wise.service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.resource.Resource;
import com.wise.data.Article;
import com.wise.extend.FaceConversionUtil;
import com.wise.list.XListView;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.FaceAdapter.ViewHolder;
import com.wise.sql.DBExcute;
import com.wise.wawc.ArticleDetailActivity;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.PicActivity;
import com.wise.wawc.R;
import com.wise.wawc.VehicleFriendActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 车友圈文章列表
 * @author 王庆文
 */
public class MyAdapter extends BaseAdapter{
    private static final String TAG = "MyAdapter";
	private LayoutInflater inflater;
	private Activity activity;
	public static boolean isClick = false;
	
	private int commentUserId = 0;
	private int blogId = 0;
	private int position = 0;
	private StringBuffer sb = null;
	private static final int articleFavorite = 123;
	private static final int refreshList = 555;
	private ProgressDialog myDialog = null;
	private List<Article> articleList = null;
	private DBExcute dbExcute = null;
	int padding = 40;
	private int selection = 0;
	private int screenWidth = 0;
	private int imageWidth = 0;
	Map<String,String> favoriteMap; //点赞集合
	MyHandler myHandler = null;
	View view;
	XListView listView = null;
	
	int chickIndex1 = 0;
	//图片布局类
	public MyAdapter(Activity activity,View v,List<Article> articleList,XListView listView){
		inflater=LayoutInflater.from(activity);
		this.view = v;
		this.activity = activity;
		this.articleList = articleList;
		myHandler = new MyHandler();
		dbExcute = new DBExcute();
		this.listView = listView;
		//计算需要显示多大尺寸的图片
		GetSystem.getScreenInfor(activity);
	}
	public int getCount() {
		return articleList.size();
	}
	public Object getItem(int position) {
		return null;
	}
	//用于获取评论者的id
	public long getItemId(int position) {
		return commentUserId;
	}
	@SuppressLint("ResourceAsColor")
	public View getView(final int position, View convertView, ViewGroup parent) {
		this.position = position;
		ViewHolder viewHolder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.article_adapter, null);
			viewHolder = new ViewHolder(); 
			viewHolder.favoriteStart = (ImageView) convertView.findViewById(R.id.article_praises_star);
			viewHolder.favoriteUser = (TextView) convertView.findViewById(R.id.article_praises_user);
			viewHolder.saySomething = (ImageView) convertView.findViewById(R.id.list_say_somthing);
			viewHolder.userHead = (ImageView) convertView.findViewById(R.id.head_article);
			viewHolder.articel_user_name = (TextView) convertView.findViewById(R.id.article_user_name);
			viewHolder.tv_article_content = (TextView) convertView.findViewById(R.id.tv_article_content);
			viewHolder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
			viewHolder.favorite = (ImageView) convertView.findViewById(R.id.favorite);
			viewHolder.line = convertView.findViewById(R.id.article_adapter_line);
			viewHolder.articlePraisesLayout = (TableRow) convertView.findViewById(R.id.article_praises_layout);
			viewHolder.userImageLayout = (GridView) convertView.findViewById(R.id.user_image);
			viewHolder.totalComment = (TextView) convertView.findViewById(R.id.my_vehicle_comment_total_tv);
			
			viewHolder.commentLayout = (LinearLayout) convertView.findViewById(R.id.article_comment_layout);
			
			viewHolder.oneCommentName = (TextView)convertView.findViewById(R.id.my_vehicle_one_comment_name_tv);
			viewHolder.oneCommentContent = (TextView)convertView.findViewById(R.id.my_vehicle_one_comment_content_tv);
			viewHolder.twoCommentName = (TextView)convertView.findViewById(R.id.my_vehicle_two_comment_name_tv);
			viewHolder.twoCommentContent = (TextView)convertView.findViewById(R.id.my_vehicle_two_comment_content_tv);
			
			viewHolder.twoCommentLayout = (LinearLayout) convertView.findViewById(R.id.my_vehicle_two_comment_ll);
			viewHolder.allCommentLayout = (TableLayout) convertView.findViewById(R.id.vehicle_friend_comment_tl);
			viewHolder.oneCommentLayout = (LinearLayout) convertView.findViewById(R.id.my_vehicle_one_comment_ll);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		//   评论
		int size = articleList.get(position).getCommentList().size();
		viewHolder.commentLayout.setVisibility(View.VISIBLE);
		viewHolder.oneCommentLayout.setVisibility(View.VISIBLE);
		viewHolder.twoCommentLayout.setVisibility(View.VISIBLE);
		viewHolder.line.setVisibility(View.VISIBLE);
		viewHolder.allCommentLayout.setVisibility(View.VISIBLE);
		viewHolder.totalComment.setVisibility(View.GONE);
		
		/**
		 * 加载之前  所有控件设置为显示
		 * 没有评论  第一条隐藏  第二条隐藏  总条数隐藏
		 * 一条评论  第一条显示  第二条隐藏  总条数隐藏
		 * 两条评论    都显示   
		 */
		if(size == 0 || articleList.get(position).getCommentList() == null){
			viewHolder.commentLayout.setVisibility(View.GONE);
			viewHolder.line.setVisibility(View.GONE);
		}
		
		if(size == 1){
			viewHolder.commentLayout.setVisibility(View.VISIBLE);
			viewHolder.line.setVisibility(View.VISIBLE);
			viewHolder.oneCommentLayout.setVisibility(View.VISIBLE);
			viewHolder.oneCommentName.setText(articleList.get(position).getCommentList().get(size - 1)[0]+" : ");
			viewHolder.oneCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(size - 1)[1]));
			viewHolder.twoCommentLayout.setVisibility(View.GONE);
			viewHolder.totalComment.setVisibility(View.GONE);
		}
		
		
		if(size == 2){
			viewHolder.commentLayout.setVisibility(View.VISIBLE);
			viewHolder.line.setVisibility(View.VISIBLE);
			viewHolder.oneCommentLayout.setVisibility(View.VISIBLE);
			viewHolder.twoCommentLayout.setVisibility(View.VISIBLE);
			viewHolder.oneCommentName.setText(articleList.get(position).getCommentList().get(size - 1)[0]+" : ");
			viewHolder.oneCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(size - 1)[1]));
			viewHolder.twoCommentName.setText(articleList.get(position).getCommentList().get(size - 2)[0]+" : ");
			viewHolder.twoCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(size - 2)[1]));
		}
		if(size > 2){
			viewHolder.commentLayout.setVisibility(View.VISIBLE);
			viewHolder.line.setVisibility(View.VISIBLE);
			viewHolder.oneCommentLayout.setVisibility(View.VISIBLE);
			viewHolder.twoCommentLayout.setVisibility(View.VISIBLE);
			viewHolder.oneCommentName.setText(articleList.get(position).getCommentList().get(size - 1)[0]+" : ");
			viewHolder.oneCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(size - 1)[1]));
			viewHolder.twoCommentName.setText(articleList.get(position).getCommentList().get(size - 2)[0]+" : ");
			viewHolder.twoCommentContent.setText(getFaceImage(articleList.get(position).getCommentList().get(size - 2)[1]));
			viewHolder.totalComment.setVisibility(View.VISIBLE);
			viewHolder.totalComment.setText("共" + articleList.get(position).getCommentList().size() + "条评论");
		}
		
		List<Bitmap> threeSmallImageList = new ArrayList<Bitmap>();
		List<Bitmap> smallImageList = new ArrayList<Bitmap>();
		for(int i = 0 ; i < articleList.get(position).getImageList().size() ; i ++){
			Map<String,String> imageMap = articleList.get(position).getImageList().get(i);
			//判断小图是否存在sd卡 /点击小图的时候再判断大图是否存在sd卡  
			String smallImage = imageMap.get("small_pic").substring(imageMap.get("small_pic").lastIndexOf("/") + 1);
			//本地不存在图片  存null  
			Bitmap smallBitmap = imageIsExist(Constant.VehiclePath + smallImage,imageMap.get("small_pic"),3,0);
			smallImageList.add(smallBitmap);
			if(i <= 2){
				threeSmallImageList.add(i, smallBitmap);
			}
		}
		
		//将用户头像url存储起来
		File userIconPath = new File(Constant.userIconPath);
		if(!userIconPath.exists()){
			userIconPath.mkdir();
		}
		
		//   赞     如果没有赞 隐藏赞布局  同时将分割线隐藏  
		if (articleList.get(position).getPraisesList() != null) {
			if (articleList.get(position).getPraisesList().size() != 0) {
				sb = new StringBuffer();
				Iterator iter = articleList.get(position).getPraisesList().entrySet().iterator();
				if (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String val = (String) entry.getValue();
					String str = "";
					if(articleList.get(position).getPraisesList().size() > 1){
						str = "...等共" + articleList.get(position).getPraisesList().size() + "人觉得赞";
					}else{
						str = "";
					}
					sb.append(val + str);
				}
				viewHolder.favoriteStart.setVisibility(View.VISIBLE);
				viewHolder.articlePraisesLayout.setVisibility(View.VISIBLE);
				viewHolder.favoriteUser.setText(sb.toString());
			} else {
				viewHolder.articlePraisesLayout.setVisibility(View.GONE);
				viewHolder.line.setVisibility(View.GONE);
			}
		}else{
			viewHolder.articlePraisesLayout.setVisibility(View.GONE);
			viewHolder.line.setVisibility(View.GONE);
		}
		boolean isNull1 = false;
		boolean isNull2 = false;
		if(articleList.get(position).getCommentList() != null){
			if(articleList.get(position).getCommentList().size() == 0){
				isNull1 = true;
			}
		}else{
			isNull1 = true;
		}
		
		if(articleList.get(position).getPraisesList() != null){
			if(articleList.get(position).getPraisesList().size() == 0){
				isNull2 = true;
			}
		}else{
			isNull2 = true;
		}
		if(isNull2 && isNull1){
			viewHolder.allCommentLayout.setVisibility(View.GONE);
			isNull1 = false;
			isNull2 = false;
		}
		viewHolder.userImageLayout.setAdapter(new UserImageAdapter(threeSmallImageList,position));

		String str = articleList.get(position).getCreate_time();
		String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
		
		viewHolder.publish_time.setText(getTime(createTime));
		viewHolder.articel_user_name.setText(articleList.get(position).getName());
		viewHolder.tv_article_content.setText(articleList.get(position).getContent());
		viewHolder.saySomething.setOnClickListener(new MyClickListener(position));
		viewHolder.favorite.setOnClickListener(new MyClickListener(position));
		viewHolder.userHead.setOnClickListener(new MyClickListener(position));
		viewHolder.articel_user_name.setOnClickListener(new MyClickListener(position));
		//设置用户头像   
		Bitmap userIcons = imageIsExist(Constant.userIconPath + articleList.get(position).getCust_id() + ".jpg",articleList.get(position).getUserLogo(),4,articleList.get(position).getCust_id());
		if(userIcons == null){
			viewHolder.userHead.setImageBitmap(getBitmap(R.drawable.body_icon_help));   //  使用缓存
		}else{
			//  使用缓存
			viewHolder.userHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(getBitmap(Constant.userIconPath + articleList.get(position).getCust_id()+".jpg")));   
		}
		return convertView;
	}
	
	//判断图片是否存在SD卡   
	private Bitmap imageIsExist(final String path,final String loadUrl,final int action,final int custId) {
		File file = new File(path);
		if(file.exists()){
//			return getBitmap(path);  //  使用缓存
			return BitmapFactory.decodeFile(path);
		}else{
			new Thread(new Runnable() {
				public void run() {
					Bitmap bitmap = GetSystem.getBitmapFromURL(loadUrl);
					if(bitmap != null){
					    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	                        File imagePath = new File(Constant.VehiclePath);
	                        if(!imagePath.exists()){
	                            imagePath.mkdir();
	                        }
	                        if(action == 3){
	                        	createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),bitmap);
	                        }
	                        if(action == 4){
	                        	createImage(Constant.userIconPath + custId + ".jpg",bitmap);
	                        }
	                    }
					}
				}
			}).start();
			return null;
		}
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
		Message msg = new Message();
		msg.what = refreshList;
		myHandler.sendMessage(msg);
	}
	
	public static String getTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(new Date());
		String time1 =  transform(time);
		System.out.println(time + "," + time1);
	     if(Integer.parseInt(time1.substring(0,4)) < Integer.parseInt(currentTime.substring(0,4))){
	    	 return time1.substring(0, 16);
	     }else{
	    	 if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time1.substring(8,10))) == 1){
	    		 return "昨天" + time1.substring(11, 16);
	    	 }else if((Integer.parseInt(currentTime.substring(8,10)) - Integer.parseInt(time1.substring(8,10))) == 2){
	    		 return "前天" + time1.substring(11, 16);
	    	 }else if((Integer.parseInt(currentTime.substring(8,10)) == Integer.parseInt(time1.substring(8,10)))){
	    		 return time1.substring(11, 16);
	    	 }
	    	 return time1.substring(5, 16);
	     }
	}
	
	
		//转换时区
	    public static String transform(String from){
	        String to = "";
	        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        //本地时区
	        Calendar nowCal = Calendar.getInstance();
	        TimeZone localZone = nowCal.getTimeZone();
	        //设定SDF的时区为本地
	        simple.setTimeZone(localZone);

	        SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        //设置 DateFormat的时间区域为GMT
	        simple1.setTimeZone(TimeZone.getTimeZone("GMT"));

	        //把字符串转化为Date对象，然后格式化输出这个Date
	        Date fromDate = new Date();
	        try {
	            //时间string解析成GMT时间
	            fromDate = simple1.parse(from);
	            //GMT时间转成当前时区的时间
	            to = simple.format(fromDate);
	        } catch (ParseException e1) {
	            e1.printStackTrace();
	        }
	        return to;
	    }
	    
	    class MyClickListener implements OnClickListener{
	    	int index = 0 ;
	    	MyClickListener(int chickIndex){
	    		this.index = chickIndex;
	    	}
			public void onClick(View v) {
				switch(v.getId()){

				case R.id.list_say_somthing:
					VehicleFriendActivity.blogId = articleList.get(index).getBlog_id();
					//编辑框不可见，设置为可见
					if(!isClick){
						isClick = true;
						view.setVisibility(View.VISIBLE);
					//编辑框可见，设置为不可见	
					}else if(isClick){
						isClick = false;
						view.setVisibility(View.GONE);
					}
					break;
				case R.id.head_article:   //点击用户头像 进入好友主页
					Intent intent = new Intent(activity,FriendHomeActivity.class);
					intent.putExtra("cust_id", String.valueOf(articleList.get(index).getCust_id()));
					intent.putExtra("user_logo", String.valueOf(articleList.get(index).getUserLogo()));
					intent.putExtra("user_name", String.valueOf(articleList.get(index).getName()));
					activity.startActivity(intent);
					break;
				case R.id.article_user_name:   //点击进入文章的详细介绍
					Intent articleDetailIntent = new Intent(activity,ArticleDetailActivity.class);
					articleDetailIntent.putExtra("article", articleList.get(index));
					activity.startActivity(articleDetailIntent);
					break;
				case R.id.favorite:
					boolean hasFavorite = true;
					//判断当前登录用户是否已经赞过
					favoriteMap = articleList.get(index).getPraisesList();
					if(favoriteMap != null){
						Iterator iter = favoriteMap.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String userName = (String) entry.getValue();
							String userId = (String) entry.getKey();
							//已经赞过  不许再赞  TODO
							if(Variable.cust_id.equals(userId)){
								hasFavorite = false;
							}
						}
					}
					if(hasFavorite){
						blogId = articleList.get(index).getBlog_id();
						myDialog = ProgressDialog.show(activity, "提示","数据提交中...");
						myDialog.setCancelable(true);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("name",Variable.cust_name));
						params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
						new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + articleList.get(index).getBlog_id()+"/praise?auth_code=" + Variable.auth_code, params, articleFavorite)).start();
					}else{
						hasFavorite = true;
						Toast.makeText(activity,"已经赞过了", 0).show();
					}
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
							dbExcute.updateArticlePraises(activity, Constant.TB_VehicleFriend, blogId, Variable.cust_name, Integer.valueOf(Variable.cust_id));
							VehicleFriendActivity vehicleFriendActivity = new VehicleFriendActivity();
							//更新列表
							List<Article> oldArticlList = vehicleFriendActivity.getArticleDataList();
							oldArticlList.clear();
							vehicleFriendActivity.setArticleDataList(oldArticlList);
							List<Article> newArticlList = MyAdapter.this.dbExcute.getArticlePageDatas(activity, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize)}, vehicleFriendActivity.getArticleDataList());
							Variable.articleList = newArticlList;
							vehicleFriendActivity.setArticleDataList(newArticlList);
							MyAdapter.this.refreshDates(newArticlList);
							myDialog.dismiss();
							Toast.makeText(activity, "点赞成功", 0).show();
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case refreshList:
					new UserImageAdapter().notifyDataSetChanged();
					MyAdapter.this.notifyDataSetChanged();
					break;
				}
			}
	    }
	public void refreshDates(List<Article> articleList){
		this.articleList = articleList;
		this.notifyDataSetChanged();
	}
	//屏蔽List  item 点击事件（避免点击变色 影响ui）
	public boolean isEnabled(int position) {
		return false;
	}
	
	 class ViewHolder {
		 public TextView articel_user_name;  //点击查看详细信息
		 public TextView tv_article_content;
		 public TextView publish_time;
		 public ImageView favorite = null;
		 public ImageView saySomething;
		 public ImageView userHead = null;
		 public ImageView favoriteStart = null;
		 public TextView favoriteUser = null;
		 public View line = null;   //分割线
		 public TableRow articlePraisesLayout = null;   //点赞者
		 public GridView userImageLayout = null;
		 public UserImageAdapter userImageAdapter = null;
		 //暂时显示两条评论
		 public TextView oneCommentName = null;
		 public TextView oneCommentContent = null;
		 public TextView twoCommentName = null;
		 public TextView twoCommentContent = null;
		 public TextView totalComment = null;
		 public LinearLayout twoCommentLayout = null;
		 public LinearLayout oneCommentLayout = null;
		 public LinearLayout commentLayout = null; 
		 public TableLayout allCommentLayout = null;
	 }
	 
	 
	 //得到缓存图片
	 public Bitmap getBitmap(Object obj){
		 BitmapCache bitmapCache = BitmapCache.getInstance();
		 Bitmap image = null;
		 if(obj instanceof String){//  对象  内存卡上面的图片
			 String path = (String) obj;
			 if(bitmapCache.getBitmap(path) == null){
				 BitmapDrawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(path));
				 bitmapCache.putBitmap(path, drawable);
			 }
			 image = bitmapCache.getBitmap(path).getBitmap();
		 }
		 if(obj instanceof Integer){  // 资源文件中的图片
			 int resId = (Integer) obj;
			 if(bitmapCache.getBitmap(resId) == null){
				 BitmapDrawable drawable = new BitmapDrawable(BitmapFactory.decodeResource(activity.getResources(), resId));
				 bitmapCache.putBitmap(resId, drawable);
			 }
			 image = bitmapCache.getBitmap(resId).getBitmap();
		 }
		 return image;
	 }

	 //得到缓存控件
	 public View getView(int hashCode, View view){
		 String name = String.valueOf(hashCode);
		 ViewCache viewCache = ViewCache.getInstance();
		 if(viewCache.getView(name) == null){
			 viewCache.putView(name, view);
		 }
		 return viewCache.getView(name);
	 }
	 public  SpannableString getFaceImage(String faceContent){
		 return FaceConversionUtil.getInstace().getExpressionString(activity, faceContent);
	 }
	 
	 /**
	  * 显示图片列表
	  * @author Mr.Wang
	  *
	  */
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
				convertView = inflater.inflate(R.layout.user_image_item, null);
				userImageHolder.userImage = (ImageView) convertView.findViewById(R.id.one_image);
				userImageHolder.userImage.setLayoutParams(new LinearLayout.LayoutParams(Variable.smallImageReqWidth, Variable.smallImageReqWidth));
				convertView.setTag(userImageHolder);
			}else{
				userImageHolder = (UserImageHolder) convertView.getTag();
			}
			
			
			Bitmap bitmap = smallImageList.get(position);
			if(bitmap == null){   //显示临时图片
				Bitmap im = BitmapFactory.decodeResource(activity.getResources(), R.drawable.article);
				userImageHolder.userImage.setImageBitmap(im);
//				userImageHolder.userImage.setBackgroundResource(R.drawable.article_comment_bg);
			}else{
				userImageHolder.userImage.setImageBitmap(smallImageList.get(position));
				userImageHolder.userImage.setLayoutParams(new LinearLayout.LayoutParams(Variable.smallImageReqWidth, Variable.smallImageReqWidth));
				userImageHolder.userImage.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						//查看大图
						Intent intent = new Intent(activity,PicActivity.class);
						intent.putExtra("article", articleList.get(indexId));
						activity.startActivity(intent);
					}
				});
			}
			return convertView;
		}
		
		class UserImageHolder{
			ImageView userImage = null;
		}
	 }
}
