package com.wise.service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import com.wise.data.Article;
import com.wise.extend.FaceConversionUtil;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.wawc.ArticleDetailActivity;
import com.wise.wawc.FriendHomeActivity;
import com.wise.wawc.ImageActivity;
import com.wise.wawc.R;
import com.wise.wawc.VehicleFriendActivity;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 车友圈文章列表
 * @author 王庆文
 */
public class MyAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private ImageView saySomething;
	private ImageView userHead;
	private View view;
	private Context context;
	public static boolean isClick = false;
	private TextView articel_user_name;  //点击查看详细信息
	private TextView tv_article_content;
	private TextView publish_time;
	private Bitmap bitmap = null;
	private int commentUserId = 0;
	
	private List<Article> articleList = null;
	
	int padding = 40;
	public MyAdapter(Context context,View v,List<Article> articleList){
		inflater=LayoutInflater.from(context);
		this.view = v;
		this.context = context;
		this.articleList = articleList;
		
	}
	public int getCount() {
//		return 10;
		return articleList.size();
	}
	public Object getItem(int position) {
		return null;
	}
	//用于获取评论者的id
	public long getItemId(int position) {
		return commentUserId;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.article_adapter, null);
		TextView articleCommentUser = (TextView) convertView.findViewById(R.id.article_comment_user);
		TextView articleCommentContent = (TextView) convertView.findViewById(R.id.article_comment_content);
		List<Bitmap> smallImageList = new ArrayList<Bitmap>();
		for(int i = 0 ; i < articleList.get(position).getImageList().size() ; i ++){
			Map<String,String> imageMap = articleList.get(position).getImageList().get(i);
			//判断小图是否存在sd卡 /点击小图的时候再判断是否存在sd卡
			String smallImage = imageMap.get("big_pic").substring(imageMap.get("big_pic").lastIndexOf("/"));
			Bitmap smallBitmap = imageIsExist(Constant.VehiclePath + smallImage,imageMap.get("small_pic"));
			smallImageList.add(smallBitmap);
		}
		//动态添加用户发表的图片
		TableLayout table = (TableLayout) convertView.findViewById(R.id.user_image);
		TableRow row = new TableRow(context);
		for(int i = 0; i < smallImageList.size() ; i ++){
			ImageView t = new ImageView(context);
			t.setClickable(true);
			t.setId(i);
			t.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					context.startActivity(new Intent(context,ImageActivity.class));
				}
			});
			t.setImageBitmap(smallImageList.get(i));
			row.addView(t);
			if((i%3 + 1) == 3){
				table.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				row = new TableRow(context);
			}else if(i == (smallImageList.size() - 1)){
				table.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
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
		userHead.setOnClickListener(new MyClickListener(position));
		articel_user_name.setOnClickListener(new MyClickListener(position));
		
		String content = "测试[可爱]";  //模拟评论
	    SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, content);
	    articleCommentUser.setText("张三:");
	    articleCommentContent.setText(spannableString);
		return convertView;
	}
	private Bitmap imageIsExist(String path,final String loadUrl) {
		File file = new File(path);
		if(file.exists()){
			bitmap = BitmapFactory.decodeFile(path);
			Log.e("本地存在","本地存在");
		}else{
			Log.e("服务器获取","服务器获取");
			new Thread(new Runnable() {
				public void run() {
					bitmap = GetSystem.getBitmapFromURL(loadUrl);
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						File imagePath = new File(Constant.VehiclePath);
						if(!imagePath.exists()){
							imagePath.mkdir();
						}
						createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),bitmap);
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
	
	public String getTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////	     String currentTime = "2014-06-21 18:28:14";
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
					Log.e("点击的索引：" ,chickIndex + "");
					VehicleFriendActivity.blogId = articleList.get(chickIndex).getBlog_id();
					//编辑框不可见，设置为可见
					Log.e("onClick",String.valueOf(isClick));
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
					context.startActivity(new Intent(context,FriendHomeActivity.class));
					break;
				case R.id.article_user_name:   //点击进入文章的详细介绍
					Log.e("进入文章详情","进入文章详情");
					context.startActivity(new Intent(context,ArticleDetailActivity.class));
					break;
				case 1:
//					context.startActivity(new Intent(context,ImageActivity.class));
					break;
				}
			}
	    }
	public void refreshDates(List<Article> articleList){ 
		this.articleList = articleList;
		this.notifyDataSetChanged();
	}
}
