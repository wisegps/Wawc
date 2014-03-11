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
	ViewHolder viewHolder;
	MyHandler myHandler = null;
	View view;
	XListView listView = null;
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
			viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.user_image);
			viewHolder.commentLayout = (LinearLayout) convertView.findViewById(R.id.article_comment_layout);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		List<Bitmap> smallImageList = new ArrayList<Bitmap>();
		for(int i = 0 ; i < articleList.get(position).getImageList().size() ; i ++){
			Map<String,String> imageMap = articleList.get(position).getImageList().get(i);
			//判断小图是否存在sd卡 /点击小图的时候再判断大图是否存在sd卡  TODO
			String smallImage = imageMap.get("small_pic").substring(imageMap.get("small_pic").lastIndexOf("/") + 1);
			//本地不存在图片  存null  
			Bitmap smallBitmap = imageIsExist(Constant.VehiclePath + smallImage,imageMap.get("small_pic"),3,0);
			smallImageList.add(i, smallBitmap);
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
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String val = (String) entry.getValue();
					sb.append(val+",");
					if(Variable.cust_name.equals(val)){
						viewHolder.favorite.setBackgroundResource(R.drawable.body_icon_heart_press);
					}
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
		//   评论     如果没有评论   将 评论布局隐藏  同时将分割线隐藏
		if(articleList.get(position).getCommentList() != null){
			if(articleList.get(position).getCommentList().size() != 0){
				for(int i = 0 ; i < articleList.get(position).getCommentList().size() ; i ++){
					LinearLayout oneComment = new LinearLayout(activity);
					oneComment.setOrientation(LinearLayout.HORIZONTAL);
					TextView commentName = new TextView(activity);  //评论者昵称
				    TextView commentContent = new TextView(activity);   //评论内容
					String[] commentStr = articleList.get(position).getCommentList().get(i);
					commentName.setText(commentStr[0] + ":");
					SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(activity, commentStr[1]);
					commentContent.setText(spannableString);
					commentName.setTextColor(R.color.blue);
					commentContent.setTextColor(R.color.common);
					oneComment.addView(commentName, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					oneComment.addView(commentContent, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					viewHolder.commentLayout.addView(oneComment, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				}
			}else{
				viewHolder.commentLayout.setVisibility(View.GONE);
				viewHolder.line.setVisibility(View.GONE);
			}
		}else{
			viewHolder.commentLayout.setVisibility(View.GONE);
			viewHolder.line.setVisibility(View.GONE);
		}
		//用户图片
		
		for (int i = 0; i < smallImageList.size(); i++) {
			if(i < 3){
				Bitmap bitmap = smallImageList.get(i);
				if(bitmap == null){   //显示转圈圈加载
					selection = position;
					ImageView tempImage = new ImageView(activity);
					tempImage.setImageResource(R.drawable.body_nothing_icon);
					tempImage.setPadding(Variable.margins, 0,0, 0);
					viewHolder.linearLayout.addView(tempImage,i,new LinearLayout.LayoutParams(Variable.smallImageReqWidth, Variable.smallImageReqWidth));
				}else{
					ImageView imageView = new ImageView(activity);
					imageView.setImageBitmap(smallImageList.get(i));
					imageView.setPadding(Variable.margins, 0,0, 0);
					viewHolder.linearLayout.addView(imageView,i,new LinearLayout.LayoutParams(Variable.smallImageReqWidth, Variable.smallImageReqWidth));
					imageView.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							//查看大图
							Intent intent = new Intent(activity,ImageActivity.class);
							intent.putExtra("article", articleList.get(position));
							activity.startActivity(intent);
						}
					});
				}
			}
		}

		String str = articleList.get(position).getCreate_time();
		String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
		
		viewHolder.publish_time.setText(getTime(createTime));
		viewHolder.articel_user_name.setText(articleList.get(position).getName());
		viewHolder.tv_article_content.setText(articleList.get(position).getContent());
		
		viewHolder.saySomething.setOnClickListener(new MyClickListener(position));
		viewHolder.favorite.setOnClickListener(new MyClickListener(position));
		viewHolder.userHead.setOnClickListener(new MyClickListener(position));
		viewHolder.articel_user_name.setOnClickListener(new MyClickListener(position));
		//设置用户头像   TODO
		Bitmap userIcons = imageIsExist(Constant.userIconPath + articleList.get(position).getCust_id() + ".jpg",articleList.get(position).getUserLogo(),4,articleList.get(position).getCust_id());
		if(userIcons == null){
			viewHolder.userHead.setBackgroundResource(R.drawable.body_icon_help);
		}else{
			Bitmap user = BitmapFactory.decodeFile(Constant.userIconPath + articleList.get(position).getCust_id()+".jpg");
			viewHolder.userHead.setImageBitmap(BlurImage.getRoundedCornerBitmap(user));
		}
		return convertView;
	}
	
	//判断图片是否存在SD卡   TODO
	private Bitmap imageIsExist(String path,final String loadUrl,final int action,final int custId) {
		File file = new File(path);
		if(file.exists()){
			viewHolder.bitmap = BitmapFactory.decodeFile(path);
			return viewHolder.bitmap;
		}
		else{
			new Thread(new Runnable() {
				public void run() {
					viewHolder.bitmap = GetSystem.getBitmapFromURL(loadUrl);
					if(viewHolder.bitmap != null){
					    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	                        File imagePath = new File(Constant.VehiclePath);
	                        if(!imagePath.exists()){
	                            imagePath.mkdir();
	                        }
	                        if(action == 3){
	                        	createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),viewHolder.bitmap);
	                        }
	                        if(action == 4){
	                        	//  TODO
	                        	createImage(Constant.userIconPath + custId + ".jpg",viewHolder.bitmap);
	                        }
	                    }
					}else{
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
	    	int chickIndex = 0;
	    	MyClickListener(int chickIndex){
	    		this.chickIndex = chickIndex;
	    	}
			public void onClick(View v) {
				switch(v.getId()){

				case R.id.list_say_somthing:
					VehicleFriendActivity.blogId = articleList.get(chickIndex).getBlog_id();
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
					intent.putExtra("cust_id", String.valueOf(articleList.get(chickIndex).getCust_id()));
					activity.startActivity(intent);
					break;
				case R.id.article_user_name:   //点击进入文章的详细介绍
					Intent articleDetailIntent = new Intent(activity,ArticleDetailActivity.class);
					articleDetailIntent.putExtra("article", articleList.get(chickIndex));
					activity.startActivity(articleDetailIntent);
					break;
				case R.id.favorite:
					boolean hasFavorite = true;
					//判断当前登录用户是否已经赞过
					favoriteMap = articleList.get(chickIndex).getPraisesList();
					if(favoriteMap != null){
						Iterator iter = favoriteMap.entrySet().iterator();
						while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							String val = (String) entry.getValue();
							//已经赞过  不许再赞
							if(Variable.cust_name.equals(val)){
								hasFavorite = false;
							}
						}
					}
					if(hasFavorite){
						blogId = articleList.get(chickIndex).getBlog_id();
						myDialog = ProgressDialog.show(activity, "提示","数据提交中...");
						myDialog.setCancelable(true);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("name",Variable.cust_name));
						params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
						new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + articleList.get(chickIndex).getBlog_id()+"/praise?auth_code=" + Variable.auth_code, params, articleFavorite)).start();
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
					MyAdapter.this.notifyDataSetChanged();
					listView.setSelection(selection);
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
		 public Bitmap bitmap = null;
		 public ImageView favorite = null;
		 public ImageView saySomething;
		 public ImageView userHead = null;
		 public ImageView favoriteStart = null;
		 public TextView favoriteUser = null;
		 public View line = null;   //分割线
		 public TableRow articlePraisesLayout = null;   //点赞者
		 public LinearLayout linearLayout;
		 public LinearLayout commentLayout;
	 }
}
