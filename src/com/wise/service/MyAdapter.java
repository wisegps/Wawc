package com.wise.service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.data.Article;
import com.wise.extend.FaceConversionUtil;
import com.wise.list.XListView;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.wawc.ArticleDetailActivity;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.R;
import com.wise.wawc.VehicleFriendActivity;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	private ImageView saySomething;
	private ImageView userHead = null;
	private View view;
	private Activity activity;
	public static boolean isClick = false;
	private TextView articel_user_name;  //点击查看详细信息
	private TextView tv_article_content;
	private TextView publish_time;
	private Bitmap bitmap = null;
	private int commentUserId = 0;
	private int blogId = 0;
	private int position = 0;
	private ImageView favorite = null;
	private MyHandler myHandler = null;
	private ImageView favoriteStart = null;
	private TextView favoriteUser = null;
	private View line = null;
	private TableLayout tablelayout = null;
	private StringBuffer sb = null;
	private static final int articleFavorite = 123;
	private static final int refreshList = 555;
	private ProgressDialog myDialog = null;
	private XListView listView = null;
	private List<Article> articleList = null;
	private DBExcute dbExcute = null;
	int padding = 40;
	private int selection = 0;
	public MyAdapter(Activity activity,View v,List<Article> articleList,XListView listView){
		inflater=LayoutInflater.from(activity);
		this.view = v;
		this.activity = activity;
		this.articleList = articleList;
		myHandler = new MyHandler();
		dbExcute = new DBExcute();
		this.listView = listView;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		this.position = position;
		convertView = inflater.inflate(R.layout.article_adapter, null);
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
		line = convertView.findViewById(R.id.article_adapter_line);
		//文章评论布局
		tablelayout = (TableLayout) convertView.findViewById(R.id.tablelayout);
		LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.user_image);
		
//		if(articleList.get(position).getImageList().size() == 0 && articleList.get(position).getCommentList().size() == 0 && articleList.get(position).getPraisesList().size() == 0){
//			tablelayout.setVisibility(View.GONE);
//		}else{
//			tablelayout.setVisibility(View.VISIBLE);
//		}
		for (int i = 0; i < smallImageList.size(); i++) {
			if(i < 3){
				Bitmap bitmap = smallImageList.get(i);
				if(bitmap == null){   //显示转圈圈加载
					selection = position;
					ProgressBar progressBar = new ProgressBar(activity);
					progressBar.setVisibility(View.VISIBLE);
					linearLayout.addView(progressBar,i,new LinearLayout.LayoutParams(Variable.articleAdapterImageWidth, Variable.articleAdapterImageWidth));
				}else{
					ImageView imageView = new ImageView(activity);
					imageView.setImageBitmap(smallImageList.get(i));
					imageView.setPadding(0, 0,10, 0);
					linearLayout.addView(imageView,i,new LinearLayout.LayoutParams(Variable.articleAdapterImageWidth, Variable.articleAdapterImageWidth));
				}
			}
		}
		//动态添加用户发表的图片
//		TableLayout table = (TableLayout) convertView.findViewById(R.id.user_image);
//		TableRow row = new TableRow(activity);
//		for(int i = 0; i < smallImageList.size() ; i ++){
//			ImageView t = new ImageView(activity);
//			t.setClickable(true);
//			t.setId(i);
//			t.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					Intent intent = new Intent(activity,ImageActivity.class);
//					intent.putExtra("position", position);
//					activity.startActivity(intent);
//				}
//			});
//			t.setImageBitmap(smallImageList.get(i));
//			row.addView(t);
//			if((i%3 + 1) == 3){
//				table.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//				row = new TableRow(activity);
//			}else if(i == (smallImageList.size() - 1)){
//				table.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//			}
//		}
		
		LinearLayout commentLayout = (LinearLayout) convertView.findViewById(R.id.article_comment_layout);
		//动态添加用户的评论
		if(articleList.get(position).getCommentList().size() == 0){
			line.setVisibility(View.GONE);
			commentLayout.setVisibility(View.GONE);
		}else{
			line.setVisibility(View.VISIBLE);
			commentLayout.setVisibility(View.VISIBLE);
			for(int i = 0 ; i < articleList.get(position).getCommentList().size() ; i ++){
				LinearLayout oneComment = new LinearLayout(activity);
				oneComment.setOrientation(LinearLayout.HORIZONTAL);
				TextView commentName = new TextView(activity);
			    TextView commentContent = new TextView(activity);
				String[] commentStr = articleList.get(position).getCommentList().get(i);
				commentName.setText(commentStr[0] + ":");
				oneComment.addView(commentName, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(activity, commentStr[1]);
				commentContent.setText(spannableString);
				oneComment.addView(commentContent, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				commentLayout.addView(oneComment, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
		}
		
		favoriteStart = (ImageView) convertView.findViewById(R.id.article_praises_star);
		favoriteUser = (TextView) convertView.findViewById(R.id.article_praises_user);

		
		if(articleList.get(position).getPraisesList() != null){
			if(articleList.get(position).getPraisesList().size() != 0){
				sb = new StringBuffer();
				for(int f = 0 ; f < articleList.get(position).getPraisesList().size(); f ++){
					sb.append(articleList.get(position).getPraisesList().get(f) + ",");
				}
				favoriteStart.setVisibility(View.VISIBLE);
				favoriteUser.setText(sb.toString());
			}else{
				favoriteStart.setVisibility(View.GONE);
			}
		}else{
			favoriteStart.setVisibility(View.GONE);
		}
		
		saySomething = (ImageView) convertView.findViewById(R.id.list_say_somthing);
		userHead = (ImageView) convertView.findViewById(R.id.head_article);
		articel_user_name = (TextView) convertView.findViewById(R.id.article_user_name);
		tv_article_content = (TextView) convertView.findViewById(R.id.tv_article_content);
		publish_time = (TextView) convertView.findViewById(R.id.publish_time);
		String str = articleList.get(position).getCreate_time();
		String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
		
		publish_time.setText(getTime(createTime));
		articel_user_name.setText(articleList.get(position).getName());
		tv_article_content.setText(articleList.get(position).getContent());
		
		saySomething.setOnClickListener(new MyClickListener(position));
		favorite = (ImageView) convertView.findViewById(R.id.favorite);
		favorite.setOnClickListener(new MyClickListener(position));
		userHead.setOnClickListener(new MyClickListener(position));
		articel_user_name.setOnClickListener(new MyClickListener(position));
		//设置用户头像   TODO
		//TODO
		Bitmap userIcons = imageIsExist(Constant.userIconPath + articleList.get(position).getCust_id() + ".jpg",articleList.get(position).getUserLogo(),4,articleList.get(position).getCust_id());
		if(userIcons == null){
			userHead.setBackgroundResource(R.drawable.ic_launcher);
		}else{
			Bitmap user = BitmapFactory.decodeFile(Constant.userIconPath + articleList.get(position).getCust_id()+".jpg");
			userHead.setImageBitmap(user);
		}
		return convertView;
	}
	
	//判断图片是否存在SD卡   TODO
	private Bitmap imageIsExist(String path,final String loadUrl,final int action,final int custId) {
		File file = new File(path);
		Log.e("图片路径：",path);
		Log.e("图片：",file.exists()+"");
		if(file.exists()){
			bitmap = BitmapFactory.decodeFile(path);
			Log.e("本地存在图片-----------","本地存在图片-----------");
			return bitmap;
		}
		else{
			new Thread(new Runnable() {
				public void run() {
					bitmap = GetSystem.getBitmapFromURL(loadUrl);
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
	                        	//  TODO
	                        	createImage(Constant.userIconPath + custId + ".jpg",bitmap);
	                        }
	                    }
					}else{
					    Log.e("MyAdapter", "图片为空");
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
		Log.e("创建图片","创建图片");
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
					activity.startActivity(new Intent(activity,ArticleDetailActivity.class));
					break;
				case R.id.favorite:
					blogId = articleList.get(chickIndex).getBlog_id();
					myDialog = ProgressDialog.show(activity, "提示","数据提交中...");
					myDialog.setCancelable(true);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("name",Variable.cust_name));
					params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
					new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + articleList.get(chickIndex).getBlog_id()+"/praise?auth_code=" + Variable.auth_code, params, articleFavorite)).start();
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
							Log.e("---->",Constant.start + "");
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
		Log.e("列表刷新","列表刷新");
	}
}
